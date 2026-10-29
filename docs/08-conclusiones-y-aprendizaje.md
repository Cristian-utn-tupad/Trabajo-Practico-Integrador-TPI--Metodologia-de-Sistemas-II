# Etapa 8: Conclusiones, Aprendizaje y Relación con los Contenidos de la Asignatura

## 1. Reflexión sobre las Decisiones Técnicas Tomadas

La evolución del **Sistema de Gestión de Solicitudes Académicas (SGSA)** desde el tag `estado-inicial` hasta `estado-refactorizado` permitió experimentar en la práctica que **un software que "funciona" externamente no necesariamente posee calidad profesional**. Las decisiones clave que transformaron la mantenibilidad del sistema fueron:

1. **Pasar de un modelo procedural anémico a un modelo de dominio rico:** En lugar de permitir que una clase externa (`SistemaSolicitudes`) manipulara libremente enteros de estado (`s.e = 3`), se encapsularon las reglas de negocio dentro de la entidad `Solicitud` (`asignarResponsable`, `cambiarEstado`) y de los enumerados `EstadoSolicitud` y `TipoSolicitud`.
2. **Desacoplar la infraestructura mediante abstracciones (DIP):** Extraer las interfaces `SolicitudRepository`, `AreaResponsableRepository` y `Notificador` permitió aislar el formato físico del archivo `CSV` y la consola respecto del servicio de aplicación (`GestionSolicitudesService`).
3. **Adoptar Apache Maven y JUnit 5 como red de seguridad:** Automatizar el ciclo de compilación y pruebas (`mvn clean test`) permitió aplicar refactorings estructurales profundos con retroalimentación inmediata en menos de un segundo, garantizando cero regresiones en el comportamiento visible.
4. **Criterio pragmático sobre cuándo NO sobre-ingenierizar (Evitar *Over-engineering*):** Se decidió mantener el diseño simple y expresivo, utilizando enumerados con comportamiento y `record` de Java 21 para los DTOs en lugar de introducir jerarquías artificiales de clases o frameworks pesados que no aportaban valor al alcance del módulo.

---

## 2. Dificultades Encontradas y Cómo se Resolvieron

1. **Romper el estado global estático sin alterar la salida funcional:**  
   En `estado-inicial`, `SistemaSolicitudes` usaba variables `public static` (`lista`, `ultId`, `arch`) accedidas directamente desde `Main`. La dificultad consistió en migrar hacia inyección de dependencias por constructor en `GestionSolicitudesService` asegurando que la numeración de IDs (`101..104`), las notificaciones por consola y el formato exacto del reporte general permanecieran 100% idénticos.
2. **Persistencia CSV y caracteres conflictivos:**  
   Durante la revisión técnica se advirtió que si una descripción o comentario incluía el delimitador `;`, la lectura posterior mediante `split(";")` desplazaba las columnas. Se resolvió encapsulando la sanitización en `CsvSolicitudRepository` y escribiendo el caso de prueba `CP-08` específicamente con `;` dentro de la descripción para prevenir regresiones (análisis de causa raíz - RCA).
3. **Granularidad y trazabilidad en Git:**  
   Para que cada etapa del TPI fuera auditable de forma independiente por la cátedra, se estructuraron commits atómicos y tags específicos (`estado-inicial`, `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas`, `etapa-docs`, `estado-refactorizado`), verificando que en cada hito el proyecto compilara y ejecutara correctamente.

---

## 3. Límites del Trabajo Actual y Mejoras Pendientes (Evolución Futura)

- **Límites actuales:**
  - La persistencia en archivo `CSV` reescribe el archivo completo en cada operación de guardado, lo cual es adecuado para volúmenes pequeños o académicos pero no escala ante acceso concurrente multiusuario.
  - El historial de observaciones se almacena como una cadena de texto concatenada (`String historialObservaciones`) para mantener compatibilidad con el formato de reporte original.
  - La interfaz de usuario se limita a consola (CLI).
- **Mejoras pendientes identificadas:**
  - Implementar un `JdbcSolicitudRepository` (SQLite / PostgreSQL) implementando la misma interfaz `SolicitudRepository` sin tocar una sola línea de `GestionSolicitudesService` (aprovechando OCP y DIP).
  - Modelar el historial como una colección inmutable de objetos de valor `EventoHistorial(LocalDateTime fecha, EstadoSolicitud estado, String detalle)`.
  - Incorporar plugins de análisis estático y cobertura en el ciclo de Maven (por ejemplo, **JaCoCo** para métricas de cobertura de código y **Checkstyle** / **SpotBugs** como *linters* en un pipeline de GitHub Actions).

---

## 4. Relación Integradora con los Contenidos de Metodología de Sistemas II

A continuación se explicita cómo cada uno de los **5 Módulos del programa de la asignatura** fue aplicado y evidenciado en este Trabajo Práctico Integrador:

### Módulo 1: Calidad de Diseño y Construcción (Clean Code + SOLID)
- **Calidad interna vs. externa y Deuda Técnica:** Analizado en la **Etapa 2**, contrastando el correcto funcionamiento del modo `--demo` frente a la rigidez y fragilidad interna de `SistemaSolicitudes`.
- **Code Smells y costo del cambio:** Diagnosticados en la **Etapa 3** mediante una matriz de 10 smells (*God Class*, *Primitive Obsession*, *Long Parameter List*, *Data Clumps*, *Feature Envy*, *Anemic Domain Model*, *Duplicated Code*, *Long Method*, *Poor Naming* y *Swallowed Exceptions*).
- **Principios SOLID y Clean Code:** Aplicados en la **Etapa 4**, dividiendo responsabilidades (SRP), llevando reglas a enumerados extensibles (OCP), garantizando sustitución entre `InMemorySolicitudRepository` y `CsvSolicitudRepository` (LSP), segregando interfaces (ISP) e invirtiendo dependencias (DIP), junto con nombres reveladores de intención y manejo limpio de excepciones (`ReglaNegocioException`).
- **Refactoring seguro y Code Review:** Documentados en las **Etapas 5 y 7**, aplicando *Extract Method*, *Move Method*, *Introduce Parameter Object*, *Replace Type Code with Enum* y *Extract Class*, validados mediante el checklist técnico de revisión.

### Módulo 2: Patrones de Diseño y Decisiones de Arquitectura (con Criterio y Práctica)
- **Patrones vs. Anti-patrones y cuándo NO usar patrones:** Se erradicó el anti-patrón *God Object / Global Static State* presente en `SistemaSolicitudes`. A la vez, se aplicó criterio profesional para no sobre-complicar el diseño con patrones innecesarios donde las características nativas de Java 21 (`enum` polimórficos y `record`) resolvían el problema con mayor claridad.
- **Patrones aplicados en el proyecto:**
  - **Arquitectura en Capas / Clean Architecture ligera:** Separación estricta entre `domain` (núcleo sin dependencias técnicas), `service` (casos de uso), `repository` (adaptadores de datos) y `Main` (interfaz CLI).
  - **Repository Pattern & Adapter:** `SolicitudRepository` abstrae la colección de dominio, mientras que `CsvSolicitudRepository` adapta la lectura/escritura de flujos de texto plano `CSV` al modelo de objetos.
  - **Strategy / Comportamiento encapsulado por tipo y estado:** En lugar de condicionales externos, `TipoSolicitud` encapsula la estrategia de cálculo de prioridad base y `EstadoSolicitud` encapsula las reglas de transición válidas (acercamiento pragmático al patrón *State*/*Strategy*).
  - **Factory Method estático expresivo:** Implementado en `DatosRegistroSolicitud.crear(...)`, `TipoSolicitud.desdeCodigo(...)` y `EstadoSolicitud.desdeCodigo(...)` para encapsular la validación y construcción de objetos.
- **ADR (Architecture Decision Records):** Las decisiones arquitectónicas (separación de persistencia, uso de DTOs inmutables y manejo de excepciones de negocio) quedaron registradas y justificadas en `docs/decisiones-tecnicas-y-refactoring.md`.

### Módulo 3: Verificación y Validación (V&V) con Herramientas
- **Definición de *Done* con calidad y evidencias de V&V:** Ningún refactoring se dio por terminado (*Done*) hasta comprobar que compilaba sin advertencias, superaba el 100% de los tests automatizados y mantenía idéntica la salida del escenario `--demo`.
- **Pirámide de Tests (Unitarias, Integración y Regresión):** Implementada en `GestionSolicitudesServiceTest` con **JUnit 5**:
  - *Pruebas unitarias rápidas en memoria (`CP-01` a `CP-07` y `CP-09`):* Verifican reglas de negocio aisladas usando `InMemorySolicitudRepository` y un doble de prueba (`NotificadorSilencioso`).
  - *Prueba de integración con sistema de archivos (`CP-08`):* Verifica la serialización y deserialización real contra disco en `CsvSolicitudRepository`.
  - *Prueba de humo / regresión end-to-end (`--demo`):* Compara la salida completa del sistema antes y después del refactoring.
- **Gestión de defectos y Causa Raíz (RCA):** Aplicada durante el *Code Review* (Etapa 7) al detectar el riesgo de fuga de descriptores de archivo (resuelto con `try-with-resources`) y la vulnerabilidad ante el carácter `;` en el CSV (resuelta con sanitización y test de regresión).

### Módulo 4: Repositorios, Colaboración y Entrega Continua
- **Buenas prácticas de repositorio y trazabilidad en Git:** Uso de `.gitignore` apropiado para proyectos Java/Maven, `README.md` estructurado con instrucciones de build/ejecución y carpeta `/docs` organizada por etapas.
- **Versionado, Commits Semánticos y Tags de Hitos:** Historial de commits limpio siguiendo *Conventional Commits* (`feat:`, `docs:`, `refactor:`, `test:`) y etiquetado completo de hitos (`estado-inicial`, `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas`, `etapa-docs`, `estado-refactorizado`).
- **Automatización del Build (Base para CI):** La configuración declarativa en `pom.xml` permite que cualquier entorno de Integración Continua ejecute `mvn clean test` o `mvn clean install` como *Quality Gate* automático en un solo comando.

### Módulo 5: Habilidades Blandas + IA Aplicada al Trabajo Profesional
- **Elicitación, Criterios de Aceptación y Comunicación Técnica:** Traducción de los requerimientos del dominio académico en reglas de negocio verificables, criterios de aceptación claros en el checklist de Code Review y redacción técnica estructurada pensada tanto para una defensa ejecutiva (`--demo`) como para una auditoría de código.
- **IA como Copiloto y Uso Ético/Responsable:** Utilización de asistencia de IA generativa como herramienta de par-programación (*copiloto*) para acelerar la generación de código repetitivo y estructurar la documentación, manteniendo siempre la **validación humana y técnica** mediante la ejecución real del compilador de Java 21, la corrida de pruebas en Maven Surefire y la verificación manual de cada regla de negocio y commit del repositorio.
