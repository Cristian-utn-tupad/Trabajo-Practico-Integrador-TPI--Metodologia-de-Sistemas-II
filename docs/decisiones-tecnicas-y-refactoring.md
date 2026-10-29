# Etapa 4 y 5: Decisiones Técnicas, Principios SOLID y Refactorings Aplicados

## 1. Aplicación de Principios SOLID y Clean Code (Etapa 4)

### Principio de Responsabilidad Única (SRP)
- **Problema inicial (`estado-inicial`):** `SistemaSolicitudes` concentraba validación de dominio, cálculo de prioridades, reglas de transición de estados, persistencia CSV, formateo de reportes y simulación de notificaciones por consola.
- **Solución aplicada (`estado-refactorizado`):** Se dividió el sistema en capas y clases cohesivas con una única razón de cambio dentro de `src/main/java/utn/tpi/sgsa`:
  - `Solicitud`, `Estudiante`, `AreaResponsable`: invariantes y comportamiento de negocio del dominio.
  - `GestionSolicitudesService`: coordinación de casos de uso.
  - `CsvSolicitudRepository` / `InMemorySolicitudRepository`: persistencia de solicitudes.
  - `GeneradorReporteSolicitudes`: formateo y cálculo estadístico del reporte.
  - `ConsolaEmailNotificador`: emisión de notificaciones al usuario/área.

### Principio Abierto/Cerrado (OCP)
- **Problema inicial:** El cálculo de prioridad según el tipo de solicitud (`if (tipo.equals("EXCEP")) ...`) y las reglas según el estado (`if (nuevoEst == 3 || nuevoEst == 4)`) estaban codificados con cadenas de `if/else` y números mágicos en el servicio.
- **Solución aplicada:** Se encapsularon las reglas dentro de los enumerados ricos `TipoSolicitud` (`calcularPrioridad(boolean esUrgente)`) y `EstadoSolicitud` (`esFinal()`, `requiereResponsableAsignado()`, `requiereMotivoObligatorio()`). Agregar un nuevo tipo de trámite sólo requiere añadir una constante en `TipoSolicitud` sin modificar `GestionSolicitudesService`.

### Principio de Sustitución de Liskov (LSP)
- **Solución aplicada:** `CsvSolicitudRepository` extiende `InMemorySolicitudRepository` e implementa `SolicitudRepository` respetando íntegramente el contrato de la superclase e interfaz. Tanto en producción (`CsvSolicitudRepository`) como en pruebas JUnit (`InMemorySolicitudRepository`), `GestionSolicitudesService` opera sin conocer ni alterar la semántica esperada.

### Principio de Segregación de Interfaces (ISP)
- **Solución aplicada:** Se definieron interfaces focalizadas y cohesivas: `SolicitudRepository`, `AreaResponsableRepository` y `Notificador`, evitando una interfaz monolítica de infraestructura y garantizando que cada cliente dependa únicamente de los métodos que realmente invoca.

### Principio de Inversión de Dependencias (DIP)
- **Problema inicial:** `SistemaSolicitudes` dependía directamente de `FileWriter`, `BufferedReader`, variables `static` globales y `System.out.println`.
- **Solución aplicada:** `GestionSolicitudesService` recibe por constructor las abstracciones `SolicitudRepository`, `AreaResponsableRepository` y `Notificador`. Esto permitió inyectar dobles de prueba (`InMemorySolicitudRepository` y `NotificadorSilencioso`) en `GestionSolicitudesServiceTest`.

### Mejoras de Clean Code
- **Nombres reveladores de intención:** Se reemplazaron `Est`, `Resp`, `Sol`, `t`, `p`, `e`, `f`, `reg`, `asig`, `cambEst` por `Estudiante`, `AreaResponsable`, `Solicitud`, `tipo`, `prioridad`, `estado`, `fechaCreacion`, `registrarSolicitud`, `asignarResponsable`, `cambiarEstado`.
- **Manejo explícito de errores:** Se eliminaron los bloques `catch (Exception ex) {}` vacíos y los retornos ambiguos (`null` / `false`), introduciendo la excepción de dominio `ReglaNegocioException` y el uso de `Optional<T>` en los repositorios.

---

## 2. Catálogo de Refactorings Seguros Documentados (Etapa 5)

Se aplicaron **5 refactorings formales** (superando el mínimo de 4 exigidos por la consigna):

### Refactoring 1: Introduce Parameter Object (`DatosRegistroSolicitud` y `CriterioBusquedaSolicitud`)
- **Motivación:** El método `SistemaSolicitudes.reg(...)` recibía 7 parámetros primitivos sueltos (`leg`, `nom`, `mail`, `carr`, `tipo`, `desc`, `esUrg`), configurando los smells *Long Parameter List* y *Data Clumps*.
- **Mecánica aplicada:** Se creó el `record` inmutable `DatosRegistroSolicitud` (que agrupa `Estudiante`, `TipoSolicitud`, `descripcion` y `esUrgente`) y `CriterioBusquedaSolicitud` para el filtrado multicriterio.
- **Resultado:** La firma pasó de `reg(String, String, String, String, String, String, boolean)` a `registrarSolicitud(DatosRegistroSolicitud datos)`.

### Refactoring 2: Move Method / Reubicación de Responsabilidad hacia `Solicitud` y `CriterioBusquedaSolicitud`
- **Motivación:** `SistemaSolicitudes` sufría de *Feature Envy* al manipular directamente los campos `s.e`, `s.resp` y `s.obs` para asignar áreas y cambiar estados.
- **Mecánica aplicada:** Se movieron las validaciones e invariantes de transición a los métodos `Solicitud.asignarResponsable(AreaResponsable)` y `Solicitud.cambiarEstado(EstadoSolicitud, String)`, y la lógica de coincidencia de filtros a `CriterioBusquedaSolicitud.coincideCon(Solicitud)`.
- **Resultado:** Las entidades dejaron de ser estructuras anémicas y protegen sus propias invariantes de negocio.

### Refactoring 3: Extract Method (`obtenerSolicitudExistente`, `escribirDetalleSolicitud`, `escribirResumenEstadistico`)
- **Motivación:** Existía código duplicado para buscar una solicitud por ID y lanzar error en `asig()` y `cambEst()`, además de un método largo en `generarReporte()`.
- **Mecánica aplicada:** Se extrajo `obtenerSolicitudExistente(int idSolicitud)` y `obtenerAreaExistente(String codigoArea)` en `GestionSolicitudesService`, y se descompuso `generarReporteGeneral()` en `escribirEncabezado()`, `escribirDetalleSolicitud()` y `escribirResumenEstadistico()`.
- **Resultado:** Métodos cortos (menos de 15 líneas), con un único nivel de abstracción y cero duplicación.

### Refactoring 4: Replace Type Code with Enum / Class (`Prioridad`, `EstadoSolicitud`, `TipoSolicitud`)
- **Motivación:** El uso de `int p` (`1..3`), `int e` (`1..4`) y `String t` generaba *Primitive Obsession* y *Magic Numbers* repetidos en validaciones y reportes.
- **Mecánica aplicada:** Se crearon los enumerados `Prioridad`, `EstadoSolicitud` y `TipoSolicitud` encapsulando sus etiquetas, códigos de persistencia y comportamiento asociado (`calcularPrioridad`, `esFinal`, `requiereResponsableAsignado`).
- **Resultado:** Seguridad de tipos en tiempo de compilación y eliminación de condicionales repetitivos.

### Refactoring 5: Extract Class (`GeneradorReporteSolicitudes`, `CsvSolicitudRepository`, `ConsolaEmailNotificador`)
- **Motivación:** Desarmar la *God Class* `SistemaSolicitudes` separando la lógica de negocio de la presentación de reportes, el acceso a archivos CSV y el envío de notificaciones.
- **Mecánica aplicada:** Se extrajeron las clases especializadas en los paquetes `repository` y `service`.
- **Resultado:** Alta cohesión interna y bajo acoplamiento entre capas.
