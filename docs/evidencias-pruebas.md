# Etapa 6: Evidencias de Ejecución y Verificación del Comportamiento

## 1. Estrategia de Verificación Sin Regresiones
Para demostrar que la evolución desde `estado-inicial` hasta `estado-refactorizado` mantuvo intacto el comportamiento externo esperado:
1. **Comparación de Salida Funcional con Maven (`--demo`):** Se ejecutó el mismo escenario de demostración mediante `mvn exec:java -Dexec.args="--demo"` antes y después del refactoring, comprobando que las notificaciones, búsquedas y el reporte general producen exactamente la misma salida funcional.
2. **Suite de Pruebas Automatizadas JUnit 5 (`mvn test`):** Se implementó `GestionSolicitudesServiceTest` con 9 pruebas unitarias y de integración en JUnit Jupiter 5 ejecutadas mediante Maven Surefire.

---

## 2. Evidencia Comparativa del Escenario `--demo` (Antes vs. Después)

Comando ejecutado en ambos estados (`estado-inicial` y `estado-refactorizado`):
```bash
mvn -q exec:java -Dexec.args="--demo"
```

Salida obtenida (idéntica en `estado-inicial` y `estado-refactorizado`):
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

---

## 3. Evidencia de la Suite de Pruebas Automatizadas con Maven y JUnit 5

Comando ejecutado:
```bash
mvn clean test
```

Salida obtenida:
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running utn.tpi.sgsa.GestionSolicitudesServiceTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.087 s -- in utn.tpi.sgsa.GestionSolicitudesServiceTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
