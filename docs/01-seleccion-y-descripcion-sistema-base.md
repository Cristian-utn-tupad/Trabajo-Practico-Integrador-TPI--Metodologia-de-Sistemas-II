# Etapa 1: Selección y Descripción del Sistema Base

## 1. Dominio y Alcance del Sistema
El sistema seleccionado corresponde al caso sugerido por la cátedra: **Sistema de Gestión de Solicitudes Académicas (SGSA)**.

### Dominio del Problema
En el ámbito de una facultad de la Universidad Tecnológica Nacional (UTN), los estudiantes realizan periódicamente trámites administrativos y académicos (pedidos de equivalencias de asignaturas, certificados de alumno regular, excepciones de correlatividad y bajas temporales de materias). Históricamente, cuando estos pedidos se gestionan de forma desarticulada, se producen demoras, pérdida de trazabilidad sobre qué oficina tiene asignado el expediente y resoluciones inconsistentes (por ejemplo, solicitudes rechazadas sin dictamen fundado o aprobadas sin haber pasado por el área competente).

### Alcance del Módulo
El módulo **SGSA** acota su alcance a la gestión integral del ciclo de vida de una solicitud académica dentro de la institución:
- **Incluye:** Registro validado de solicitudes, cálculo automático de prioridad según reglas institucionales, derivación y asignación a un área responsable, control estricto de las transiciones de estado (`PENDIENTE` $\rightarrow$ `EN_REVISION` $\rightarrow$ `APROBADA` / `RECHAZADA`), búsqueda multicriterio, emisión de un reporte general con resumen estadístico y persistencia en archivo estructurado (`CSV`).
- **Excluye (fuera del alcance del prototipo):** Autenticación de usuarios, envío real por servidor SMTP externo (se simula mediante un componente notificador desacoplado) e interfaz gráfica web.

---

## 2. Actores del Sistema

| Actor | Rol en el Dominio | Interacción Principal con el SGSA |
|---|---|---|
| **Estudiante (Solicitante)** | Alumno regular o ingresante de una carrera (ej. TUP, ISI), identificado por legajo, nombre completo y correo institucional. | Origina el pedido académico indicando el tipo de trámite, la descripción del motivo y si reviste carácter de urgencia. Recibe avisos sobre el alta y los cambios de estado de su trámite. |
| **Operador de Mesa de Entradas** | Personal administrativo encargado de recepcionar y clasificar los pedidos. | Registra las solicitudes en el sistema, consulta el padrón mediante filtros (tipo, estado, legajo), asigna el área responsable correspondiente y emite el reporte general de seguimiento. |
| **Área Responsable / Autoridad Académica** | Unidad organizativa (`A01` Departamento de Alumnos, `A02` Secretaría Académica, `A03` Dirección de Carrera TUP) con un responsable a cargo. | Recibe las solicitudes derivadas a su sector (en estado `EN_REVISION`), evalúa la factibilidad académica/reglamentaria y resuelve el trámite cambiándolo a `APROBADA` o `RECHAZADA` (dejando constancia obligatoria del motivo en caso de rechazo). |

---

## 3. Entidades Principales y Funcionalidades Verificables

### Entidades del Sistema Base (`estado-inicial` $\rightarrow$ `estado-refactorizado`)
1. **Solicitud (`Sol` $\rightarrow$ `Solicitud`):** Entidad central que modela el trámite académico, su identificador único incremental (`id` a partir de `101`), fecha de creación, descripción, prioridad calculada, estado actual e historial de auditoría.
2. **Estudiante (`Est` $\rightarrow$ `Estudiante`):** Representa al alumno solicitante con su `legajo`, `nombreCompleto`, `email` y `carrera`.
3. **Área Responsable (`Resp` $\rightarrow$ `AreaResponsable`):** Representa el sector administrativo u académico asignado (`codigo`, `nombreArea`, `nombreResponsable`, `emailContacto`).
4. **Estado y Clasificación (`int e`, `int p`, `String t` $\rightarrow$ `EstadoSolicitud`, `Prioridad`, `TipoSolicitud`):** Modela la taxonomía del trámite (`EQUIV`, `CERT`, `EXCEP`, `BAJA`), su nivel de prioridad (`ALTA`, `MEDIA`, `BAJA`) y las etapas de su ciclo de vida (`PENDIENTE`, `EN_REVISION`, `APROBADA`, `RECHAZADA`).

### Operaciones Funcionales Verificables (Casos de Uso)
1. **CU-01 — Registrar solicitud académica:** Valida que el legajo, nombre, email (`@`) y descripción ($\ge 5$ caracteres) sean válidos, determina automáticamente la prioridad (si es urgente $\rightarrow$ `ALTA`; si no, según el tipo: `EXCEP` $\rightarrow$ `ALTA`, `EQUIV`/`BAJA` $\rightarrow$ `MEDIA`, `CERT` $\rightarrow$ `BAJA`), crea la solicitud en estado `PENDIENTE`, la persiste en CSV y emite notificación al estudiante.
2. **CU-02 — Asignar área responsable:** Verifica que la solicitud exista y no esté cerrada (`APROBADA`/`RECHAZADA`), vincula el área (`A01`, `A02` o `A03`), cambia automáticamente el estado de `PENDIENTE` a `EN_REVISION`, registra el evento en el historial y notifica al área.
3. **CU-03 — Cambiar estado de solicitud:** Verifica las invariantes de negocio (prohibición de alterar solicitudes ya cerradas, obligatoriedad de contar con un área responsable antes de aprobar o rechazar, y exigencia de un motivo no vacío para rechazar), actualiza el estado, anexa la resolución al historial y notifica al estudiante.
4. **CU-04 — Buscar y filtrar solicitudes:** Permite recuperar solicitudes aplicando filtros combinables por tipo de trámite, estado actual y/o número de legajo.
5. **CU-05 — Listar reporte general y estadístico:** Genera un informe consolidado con el detalle completo de cada solicitud, su historial de cambios y los totales sumarizados por estado y por prioridad.

---

## 4. Tecnología Utilizada y Enlace al Repositorio

- **Lenguaje de Programación:** Java 21 LTS (Standard Edition).
- **Gestión de Construcción:** Apache Maven 3.9+ (`pom.xml`).
- **Framework de Pruebas (en versión evolucionada):** JUnit Jupiter 5 (`5.10.2`) ejecutado con `maven-surefire-plugin`.
- **Persistencia:** Archivo de texto plano con formato `CSV` (`solicitudes.csv` / `solicitudes_demo.csv`) e implementación en memoria para pruebas unitarias.
- **Enlace al Repositorio GitHub:**  
  [`https://github.com/Cristian-utn-tupad/Trabajo-Practico-Integrador-TPI--Metodologia-de-Sistemas-II`](https://github.com/Cristian-utn-tupad/Trabajo-Practico-Integrador-TPI--Metodologia-de-Sistemas-II)
  - **Tag de la versión inicial:** `estado-inicial`
  - **Tag de la versión final:** `estado-refactorizado` (y alias `estadorefactorizado`)

---

## 5. Descripción de la Versión Inicial (`estado-inicial`) y Evidencia de Funcionamiento

En el tag `estado-inicial`, el sistema se compone de 5 clases alojadas directamente en el paquete raíz `utn.tpi.sgsa`:
- `Est.java`, `Resp.java` y `Sol.java`: Clases de datos anémicas con atributos públicos.
- `SistemaSolicitudes.java`: Clase monolítica con métodos y listas estáticas que resuelve validación, negocio, reportes y lectura/escritura CSV.
- `Main.java`: Interfaz de consola interactiva y modo de demostración automatizado (`--demo`).

### Evidencia de Ejecución del Sistema Base (`estado-inicial`)

Comando ejecutado:
```bash
git checkout estado-inicial
mvn clean compile exec:java -Dexec.args="--demo"
```

Salida real obtenida por consola:
```text
>>> INICIANDO DEMOSTRACION FUNCIONAL DEL SISTEMA BASE <<<

[NOTIF EMAIL -> ana.martinez@alumno.utn.edu.ar] Solicitud #101 registrada con exito.
[NOTIF EMAIL -> juan.cruz@alumno.utn.edu.ar] Solicitud #102 registrada con exito.
[NOTIF EMAIL -> lucia.f@alumno.utn.edu.ar] Solicitud #103 registrada con exito.
[NOTIF EMAIL -> ana.martinez@alumno.utn.edu.ar] Solicitud #104 registrada con exito.
[NOTIF EMAIL -> tup@utn.edu.ar] Nueva solicitud #101 asignada a su area.
[NOTIF EMAIL -> academica@utn.edu.ar] Nueva solicitud #102 asignada a su area.
[NOTIF EMAIL -> alumnos@utn.edu.ar] Nueva solicitud #103 asignada a su area.
[NOTIF EMAIL -> ana.martinez@alumno.utn.edu.ar] Su solicitud #101 cambio de estado.
[NOTIF EMAIL -> juan.cruz@alumno.utn.edu.ar] Su solicitud #102 cambio de estado.

[BUSQUEDA] Solicitudes del legajo 50123 encontradas: 2

============================================================
        REPORTE GENERAL DE SOLICITUDES ACADEMICAS           
============================================================
#101 | [2026-10-29] | Tipo: EQUIV | Prio: MEDIA | Est: Ana Martinez (50123) | Resp: Direccion de Carrera TUP (Martin Perez)
   Estado: APROBADA | Desc: Solicitud de equivalencia de Programacion I cursada en ISI
   Historial: Creada en fecha 2026-10-29 | Asignada a Direccion de Carrera TUP (Martin Perez) | Cambio estado (3): Se aprueba equivalencia segun resolucion 142/26
------------------------------------------------------------
#102 | [2026-10-29] | Tipo: EXCEP | Prio: ALTA  | Est: Juan Cruz (50456) | Resp: Secretaria Academica (Carlos Ruiz)
   Estado: RECHAZADA | Desc: Excepcion de correlatividad para cursar Metodologia de Sistemas II
   Historial: Creada en fecha 2026-10-29 | Asignada a Secretaria Academica (Carlos Ruiz) | Cambio estado (4): No cumple con el porcentaje minimo de materias aprobadas
------------------------------------------------------------
#103 | [2026-10-29] | Tipo: CERT  | Prio: BAJA  | Est: Lucia Fernandez (50789) | Resp: Departamento de Alumnos (Laura Gomez)
   Estado: EN_REVISION | Desc: Certificado de alumno regular para presentar en el trabajo
   Historial: Creada en fecha 2026-10-29 | Asignada a Departamento de Alumnos (Laura Gomez)
------------------------------------------------------------
#104 | [2026-10-29] | Tipo: BAJA  | Prio: MEDIA | Est: Ana Martinez (50123) | Resp: SIN ASIGNAR
   Estado: PENDIENTE | Desc: Baja temporal de la materia Ingles II por superposicion horaria
   Historial: Creada en fecha 2026-10-29
------------------------------------------------------------
RESUMEN POR ESTADO:
 - Total Solicitudes: 4
 - Pendientes       : 1
 - En Revision      : 1
 - Aprobadas        : 1
 - Rechazadas       : 1
RESUMEN POR PRIORIDAD:
 - Prioridad Alta   : 1
 - Prioridad Media  : 2
 - Prioridad Baja   : 1
============================================================
```
