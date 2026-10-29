# Trabajo Práctico Integrador (TPI) — Metodología de Sistemas II
## Sistema de Gestión de Solicitudes Académicas (SGSA)

Proyecto desarrollado para la cátedra de **Metodología de Sistemas II** (Tecnicatura Universitaria en Programación - UTN).

### Descripción del Sistema Base
El **Sistema de Gestión de Solicitudes Académicas (SGSA)** permite registrar pedidos académicos de estudiantes (equivalencias, certificados, excepciones de correlatividad y bajas), clasificarlos automáticamente por prioridad según su tipo y urgencia, asignar un área administrativa responsable, gestionar el ciclo de vida de sus estados, realizar búsquedas multicriterio y emitir un reporte general con indicadores resumidos.

### Tecnologías Utilizadas
- **Lenguaje:** Java 21 LTS.
- **Gestor de Construcción:** Apache Maven (`pom.xml`).
- **Persistencia:** Archivo plano estructurado (`CSV`).
- **Interfaz:** Consola interactiva (CLI) y modo demostración automatizado (`--demo`).

### Compilación y Ejecución con Maven
```bash
mvn clean compile
mvn exec:java -Dexec.args="--demo"
```
