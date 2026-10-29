# Etapa 7: Revisión Técnica Profesional (Code Review)

## 1. Checklist Técnico de Revisión de Código

| ID | Categoría | Criterio de Evaluación | Estado Inicial (`estado-inicial`) | Estado Final (`estado-refactorizado`) |
|---|---|---|---|---|
| **CR-01** | **Legibilidad y Clean Code** | ¿Los nombres de clases, métodos y variables revelan claramente su intención sin abreviaturas crípticas? | ❌ Rechazado (`Est`, `Sol`, `t`, `p`, `e`, `reg`, `cambEst`) | ✅ Aprobado (`Estudiante`, `Solicitud`, `registrarSolicitud`) |
| **CR-02** | **Encapsulamiento y Dominio** | ¿Las entidades protegen su estado interno (atributos `private`) y validan sus invariantes de negocio? | ❌ Rechazado (campos `public` mutables y clases anémicas) | ✅ Aprobado (atributos `private final` y métodos ricos en `Solicitud`) |
| **CR-03** | **Tamaño y Cohesión de Métodos** | ¿Los métodos son breves, tienen un solo nivel de abstracción y menos de 4 parámetros? | ❌ Rechazado (`reg` con 7 parámetros y >45 líneas) | ✅ Aprobado (uso de `DatosRegistroSolicitud` y métodos extraídos) |
| **CR-04** | **Tipado y Constantes** | ¿Se evitan números mágicos y códigos de texto sueltos mediante el uso de `enum` o constantes con nombre? | ❌ Rechazado (`1..4` para estados y `"EQUIV"` harcodeado) | ✅ Aprobado (`EstadoSolicitud`, `Prioridad`, `TipoSolicitud`) |
| **CR-05** | **Principios SOLID (SRP / DIP)** | ¿La lógica de negocio está separada de la persistencia CSV y de la interfaz de consola? | ❌ Rechazado (`SistemaSolicitudes` acoplado a `FileWriter` y `System.out`) | ✅ Aprobado (inyección de `SolicitudRepository` y `Notificador`) |
| **CR-06** | **Manejo de Errores** | ¿Las excepciones se tratan adecuadamente sin bloques `catch` vacíos que oculten fallos? | ❌ Rechazado (`catch (Exception ex) {}` vacío en `guardar`/`cargar`) | ✅ Aprobado (`ReglaNegocioException` con causa encadenada y `try-with-resources`) |
| **CR-07** | **Pruebas y Verificación** | ¿Existen pruebas automatizadas JUnit 5 reproducibles con `mvn test` que verifiquen casos felices y reglas de borde? | ❌ Rechazado (sin pruebas automatizadas aislables) | ✅ Aprobado (`GestionSolicitudesServiceTest` con 9 casos JUnit 5) |
| **CR-08** | **Trazabilidad en Git** | ¿Los commits son atómicos, descriptivos y cuentan con los tags de hitos requeridos? | ⚠️ En progreso | ✅ Aprobado (commits semánticos y tags de etapa) |

---

## 2. Registro de Observaciones, Feedback y Correcciones Aplicadas

Durante la revisión técnica intermedia del código refactorizado se detectaron y corrigieron las siguientes observaciones antes de otorgar la aprobación final:

1. **Observación #1 — Cierre seguro de recursos de archivo en `CsvSolicitudRepository`:**
   - *Hallazgo inicial:* En `SistemaSolicitudes.guardar()` y `cargar()`, los flujos `PrintWriter` y `BufferedReader` se cerraban manualmente al final del bloque `try`, pudiendo quedar abiertos ante una excepción intermedia, además de depender de la codificación por defecto del sistema operativo.
   - *Feedback técnico:* Utilizar `try-with-resources` explicitando `StandardCharsets.UTF_8` y propagar cualquier `IOException` envuelta en `ReglaNegocioException`.
   - *Corrección aplicada:* Implementado en `CsvSolicitudRepository.persistirEnArchivo()` y `cargarDesdeArchivo()`.

2. **Observación #2 — Sanitización de caracteres delimitadores en persistencia CSV:**
   - *Hallazgo inicial:* Si la descripción o el comentario de una solicitud contenían el carácter `;`, el `split(";")` al recargar el archivo corrompía las columnas.
   - *Feedback técnico:* Asegurar que el método `sanitizarTexto()` reemplace `;` por `,` y agregar una aserción explícita en el test `CP-08` con punto y coma en la descripción.
   - *Corrección aplicada:* Verificado y cubierto en `GestionSolicitudesServiceTest.testPersistenciaCsvConservaDatos()`.

3. **Observación #3 — Uso de `EnumMap` en lugar de múltiples contadores locales:**
   - *Hallazgo inicial:* En `generarReporte()` se declaraban 7 variables enteras (`cPend`, `cRev`, `cAprob`, `cRech`, `pAlta`, `pMedia`, `pBaja`) actualizadas con cadenas de `if/else`.
   - *Feedback técnico:* Reemplazar los contadores sueltos por `EnumMap<EstadoSolicitud, Integer>` y `EnumMap<Prioridad, Integer>` en `GeneradorReporteSolicitudes`.
   - *Corrección aplicada:* Aplicado en `GeneradorReporteSolicitudes.generarReporteGeneral()`.

---

## 3. Criterios de Aceptación Técnica Cumplidos
- [x] Construcción limpia con **Apache Maven (`mvn clean install`)** en **Java 21 LTS**.
- [x] **100% de las pruebas automatizadas superadas** (`9/9 Tests run, 0 Failures` con JUnit 5).
- [x] Salida del escenario `--demo` (`mvn exec:java -Dexec.args="--demo"`) libre de regresiones respecto a `estado-inicial`.
- [x] Cobertura completa de los 5 principios SOLID y de los 5 refactorings catalogados.
