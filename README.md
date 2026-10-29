# Trabajo Práctico Integrador (TPI) — Metodología de Sistemas II
## Sistema de Gestión de Solicitudes Académicas (SGSA)

Repositorio oficial del **Trabajo Práctico Integrador (TPI)** de **Metodología de Sistemas II** (Tecnicatura Universitaria en Programación — UTN).

Este proyecto demuestra la evolución profesional de un sistema existente desde una versión inicial con problemas reales de diseño (`estado-inicial`) hacia una arquitectura limpia, cohesiva, testeable y alineada con **SOLID** y **Clean Code** (`estado-refactorizado` / `estadorefactorizado`), manteniendo intacto su comportamiento externo.

---

## 1. Dominio y Funcionalidades del Sistema
El **Sistema de Gestión de Solicitudes Académicas (SGSA)** permite administrar el ciclo de vida de trámites estudiantiles dentro de una facultad:
1. **Registrar solicitud académica:** Valida datos del estudiante y del pedido, y calcula automáticamente la prioridad (`ALTA`, `MEDIA`, `BAJA`) según el tipo de trámite (`EQUIV`, `CERT`, `EXCEP`, `BAJA`) y si reviste carácter urgente.
2. **Asignar área responsable:** Vincula la solicitud con un área administrativa (`A01` Departamento de Alumnos, `A02` Secretaría Académica, `A03` Dirección de Carrera TUP) y transiciona automáticamente el estado de `PENDIENTE` a `EN_REVISION`.
3. **Cambiar estado de solicitud:** Gestiona la resolución (`APROBADA` o `RECHAZADA`) controlando invariantes de negocio (prohibición de modificar solicitudes cerradas, exigencia de área responsable previa y obligatoriedad de motivo en caso de rechazo).
4. **Buscar y filtrar solicitudes:** Permite realizar consultas combinadas por tipo de trámite, estado actual y legajo del estudiante.
5. **Generar reporte general:** Emite un listado detallado con historial de auditoría y un cuadro estadístico por estado y prioridad.

---

## 2. Tecnologías y Arquitectura
- **Lenguaje:** Java 21 LTS.
- **Construcción y Gestión del Proyecto:** Apache Maven (`pom.xml`).
- **Testing:** JUnit Jupiter 5 (`junit-jupiter` 5.10.2) + Maven Surefire Plugin.
- **Persistencia:** Repositorio en archivo CSV (`CsvSolicitudRepository`) e implementación en memoria (`InMemorySolicitudRepository`) intercambiable mediante inversión de dependencias (`SolicitudRepository`).
- **Estructura del proyecto Maven (`estado-refactorizado`):**
  ```text
  ├── pom.xml
  ├── docs/
  │   ├── 01-seleccion-y-descripcion-sistema-base.md
  │   ├── diagnostico-deuda-tecnica.md
  │   ├── decisiones-tecnicas-y-refactoring.md
  │   ├── evidencias-pruebas.md
  │   ├── code-review-checklist.md
  │   └── 08-conclusiones-y-aprendizaje.md
  └── src/
      ├── main/java/utn/tpi/sgsa/
      │   ├── Main.java                                  # Punto de entrada CLI y modo --demo
      │   ├── domain/
      │   │   ├── Solicitud.java                         # Entidad rica con reglas de transicion
      │   │   ├── Estudiante.java                        # Entidad encapsulada con validacion
      │   │   ├── AreaResponsable.java                   # Entidad de area administrativa
      │   │   ├── TipoSolicitud.java                     # Enum con regla de calculo de prioridad (OCP)
      │   │   ├── EstadoSolicitud.java                   # Enum con invariantes de transicion
      │   │   └── Prioridad.java                         # Enum de prioridades
      │   ├── dto/
      │   │   ├── DatosRegistroSolicitud.java            # Parameter Object para creacion (record)
      │   │   └── CriterioBusquedaSolicitud.java         # Parameter Object para filtrado (record)
      │   ├── exception/
      │   │   └── ReglaNegocioException.java             # Excepcion de dominio explicita
      │   ├── repository/
      │   │   ├── SolicitudRepository.java               # Abstraccion de persistencia (DIP/ISP)
      │   │   ├── AreaResponsableRepository.java         # Abstraccion de catalogo de areas
      │   │   ├── InMemorySolicitudRepository.java       # Implementacion en memoria
      │   │   ├── InMemoryAreaResponsableRepository.java # Catalogo de areas en memoria
      │   │   └── CsvSolicitudRepository.java            # Implementacion persistente en archivo CSV
      │   └── service/
      │       ├── GestionSolicitudesService.java         # Servicio coordinador de casos de uso (SRP)
      │       ├── GeneradorReporteSolicitudes.java       # Generador de reportes y estadisticas
      │       ├── Notificador.java                       # Abstraccion de notificaciones
      │       └── ConsolaEmailNotificador.java           # Notificador por consola
      └── test/java/utn/tpi/sgsa/
          └── GestionSolicitudesServiceTest.java         # Suite de 9 pruebas automatizadas JUnit 5
  ```

---

## 3. Instalación, Compilación y Uso con Maven

### Requisitos previos
- **JDK 21** y **Apache Maven 3.8+** instalados y disponibles en el `PATH`.

### Compilar, ejecutar tests y empaquetar el proyecto
```bash
mvn clean install
```

### Ejecutar únicamente la suite de pruebas automatizadas (JUnit 5)
```bash
mvn test
```

### Ejecutar el escenario de demostración (`--demo`)
```bash
mvn exec:java -Dexec.args="--demo"
```
*(O bien usando el JAR generado tras `mvn package`: `java -jar target/sgsa-tpi-metodologias2-1.0.0.jar --demo`)*

### Ejecutar el menú interactivo por consola
```bash
mvn exec:java
```

---

## 4. Documentación Técnica (`/docs`) y Tags de Hitos en Git

En la carpeta [`/docs`](./docs) se encuentra toda la evidencia documental requerida por la cátedra para las **8 Etapas obligatorias**:
- [`docs/01-seleccion-y-descripcion-sistema-base.md`](./docs/01-seleccion-y-descripcion-sistema-base.md): Descripción del dominio, alcance, actores, entidades, operaciones funcionales, tecnología, versión inicial (`estado-inicial`), enlace al repositorio y evidencia de ejecución (Etapa 1).
- [`docs/diagnostico-deuda-tecnica.md`](./docs/diagnostico-deuda-tecnica.md): Análisis de Calidad Externa vs. Interna y catálogo de **10 Code Smells** con evidencia, impacto y prioridad (Etapas 2 y 3).
- [`docs/decisiones-tecnicas-y-refactoring.md`](./docs/decisiones-tecnicas-y-refactoring.md): Justificación de aplicación de principios **SOLID**, **Clean Code** y los **5 Refactorings** seguros aplicados (Etapas 4 y 5).
- [`docs/evidencias-pruebas.md`](./docs/evidencias-pruebas.md): Comparativa de salida antes/después y resultados de la suite de pruebas automatizadas JUnit 5 (Etapa 6).
- [`docs/code-review-checklist.md`](./docs/code-review-checklist.md): Checklist profesional de Code Review, observaciones detectadas, correcciones y criterios de aceptación (Etapa 7).
- [`docs/08-conclusiones-y-aprendizaje.md`](./docs/08-conclusiones-y-aprendizaje.md): Reflexión sobre decisiones tomadas, dificultades, límites, mejoras futuras y articulación con los **5 Módulos** de la asignatura (Etapa 8).

### Tags del Repositorio
| Tag | Descripción del Hito |
|---|---|
| `estado-inicial` | Entrega del sistema base funcional con deuda técnica y code smells deliberados. |
| `etapa-diagnostico` / `etapadiagnostico` | Incorporación del diagnóstico de calidad interna/externa y catálogo de code smells. |
| `etapa-refactoring` | Código refactorizado aplicando SOLID, Clean Code y refactorings seguros. |
| `etapa-pruebas` | Incorporación y verificación de la suite de pruebas automatizadas JUnit 5 (`9/9 OK`). |
| `etapa-docs` | Documentación técnica completa, checklist de Code Review y README actualizado. |
| `estado-refactorizado` / `estadorefactorizado` | Versión final evolucionada, verificada y revisada técnicamente. |
