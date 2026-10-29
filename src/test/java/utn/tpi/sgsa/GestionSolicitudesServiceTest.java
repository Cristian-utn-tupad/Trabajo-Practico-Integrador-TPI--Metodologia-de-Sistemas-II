package utn.tpi.sgsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utn.tpi.sgsa.domain.*;
import utn.tpi.sgsa.dto.CriterioBusquedaSolicitud;
import utn.tpi.sgsa.dto.DatosRegistroSolicitud;
import utn.tpi.sgsa.exception.ReglaNegocioException;
import utn.tpi.sgsa.repository.*;
import utn.tpi.sgsa.service.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Verificacion de Comportamiento del Sistema de Solicitudes Academicas (SGSA)")
class GestionSolicitudesServiceTest {

    private GestionSolicitudesService servicio;

    @BeforeEach
    void setUp() {
        servicio = new GestionSolicitudesService(
                new InMemorySolicitudRepository(),
                new InMemoryAreaResponsableRepository(),
                new NotificadorSilencioso(),
                new GeneradorReporteSolicitudes()
        );
    }

    @Test
    @DisplayName("CP-01: Registro de solicitud calcula prioridad base y estado PENDIENTE")
    void testRegistroSolicitudCalculaPrioridadYEstado() {
        Solicitud sol = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "EQUIV", "Pedido de equivalencia Programacion I", false));

        assertEquals(101, sol.getId(), "El primer ID debe ser 101");
        assertEquals(EstadoSolicitud.PENDIENTE, sol.getEstado(), "El estado inicial debe ser PENDIENTE");
        assertEquals(Prioridad.MEDIA, sol.getPrioridad(), "EQUIV no urgente debe tener prioridad MEDIA");
    }

    @Test
    @DisplayName("CP-02: Solicitud urgente fuerza prioridad ALTA sin importar el tipo")
    void testSolicitudUrgenteAsignaPrioridadAlta() {
        Solicitud sol = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50789", "Lucia Fernandez", "lucia@utn.edu.ar", "TUP",
                "CERT", "Certificado urgente para tramite laboral", true));

        assertEquals(Prioridad.ALTA, sol.getPrioridad(), "Cualquier solicitud urgente debe tener prioridad ALTA");
    }

    @Test
    @DisplayName("CP-03: Asignacion de area responsable cambia estado automaticamente a EN_REVISION")
    void testAsignarResponsableCambiaEstadoAEnRevision() {
        Solicitud sol = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "EQUIV", "Pedido de equivalencia Programacion I", false));

        servicio.asignarResponsable(sol.getId(), "A03");

        assertEquals(EstadoSolicitud.EN_REVISION, sol.getEstado(), "Al asignar area debe pasar a EN_REVISION");
        assertTrue(sol.tieneResponsableAsignado(), "Debe tener responsable asignado");
        assertEquals("A03", sol.getAreaResponsable().getCodigo(), "El area debe ser A03");
    }

    @Test
    @DisplayName("CP-04: Regla de negocio impide aprobar o rechazar sin responsable asignado")
    void testImpedirAprobarSinResponsable() {
        Solicitud sol = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "EQUIV", "Pedido de equivalencia Programacion I", false));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.cambiarEstado(sol.getId(), EstadoSolicitud.APROBADA, "Aprobado"),
                "Debe lanzar excepcion al aprobar sin responsable");
    }

    @Test
    @DisplayName("CP-05: Regla de negocio exige motivo obligatorio para rechazar solicitud")
    void testExigirMotivoAlRechazar() {
        Solicitud sol = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50456", "Juan Cruz", "juan@utn.edu.ar", "TUP",
                "EXCEP", "Excepcion de correlatividad", true));
        servicio.asignarResponsable(sol.getId(), "A02");

        assertThrows(ReglaNegocioException.class,
                () -> servicio.cambiarEstado(sol.getId(), EstadoSolicitud.RECHAZADA, "   "),
                "Debe lanzar excepcion al rechazar sin motivo");
    }

    @Test
    @DisplayName("CP-06: Regla de negocio bloquea modificaciones sobre solicitudes cerradas")
    void testBloquearCambiosEnSolicitudCerrada() {
        Solicitud sol = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "EQUIV", "Pedido de equivalencia Programacion I", false));
        servicio.asignarResponsable(sol.getId(), "A03");
        servicio.cambiarEstado(sol.getId(), EstadoSolicitud.APROBADA, "Resolucion 142/26");

        assertThrows(ReglaNegocioException.class,
                () -> servicio.cambiarEstado(sol.getId(), EstadoSolicitud.RECHAZADA, "Intento posterior"),
                "No debe permitir cambiar estado de solicitud ya aprobada");
        assertThrows(ReglaNegocioException.class,
                () -> servicio.asignarResponsable(sol.getId(), "A01"),
                "No debe permitir reasignar area en solicitud cerrada");
    }

    @Test
    @DisplayName("CP-07: Busqueda multicriterio por tipo, estado y legajo filtra correctamente")
    void testBusquedaMulticriterio() {
        servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "EQUIV", "Equivalencia 1", false));
        servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "BAJA", "Baja materia Ingles", false));
        servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50999", "Pedro Gomez", "pedro@utn.edu.ar", "TUP",
                "EQUIV", "Equivalencia 2", false));

        List<Solicitud> porLegajo = servicio.buscarSolicitudes(
                new CriterioBusquedaSolicitud(null, null, "50123"));
        assertEquals(2, porLegajo.size(), "Deben encontrarse 2 solicitudes del legajo 50123");

        List<Solicitud> porTipoYLegajo = servicio.buscarSolicitudes(
                new CriterioBusquedaSolicitud(TipoSolicitud.EQUIV, EstadoSolicitud.PENDIENTE, "50123"));
        assertEquals(1, porTipoYLegajo.size(), "Debe encontrarse 1 solicitud EQUIV pendiente de 50123");
    }

    @Test
    @DisplayName("CP-08: Persistencia CSV conserva integridad de datos tras recarga")
    void testPersistenciaCsvConservaDatos() {
        String archivoTest = "solicitudes_test.csv";
        new File(archivoTest).delete();
        try {
            AreaResponsableRepository areaRepo = new InMemoryAreaResponsableRepository();
            CsvSolicitudRepository csvRepo1 = new CsvSolicitudRepository(archivoTest, areaRepo);
            GestionSolicitudesService servicio1 = new GestionSolicitudesService(
                    csvRepo1, areaRepo, new NotificadorSilencioso(), new GeneradorReporteSolicitudes());

            Solicitud creada = servicio1.registrarSolicitud(DatosRegistroSolicitud.crear(
                    "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                    "EQUIV", "Equivalencia con punto y coma; en texto", false));
            servicio1.asignarResponsable(creada.getId(), "A03");

            // Recargar en una nueva instancia de repositorio leyendo del disco
            CsvSolicitudRepository csvRepo2 = new CsvSolicitudRepository(archivoTest, areaRepo);
            Solicitud recuperada = csvRepo2.buscarPorId(creada.getId()).orElseThrow();

            assertEquals("50123", recuperada.getEstudiante().getLegajo(), "El legajo debe persistir");
            assertEquals(EstadoSolicitud.EN_REVISION, recuperada.getEstado(), "El estado EN_REVISION debe persistir");
            assertEquals("A03", recuperada.getAreaResponsable().getCodigo(), "El area responsable debe persistir");
        } finally {
            new File(archivoTest).delete();
        }
    }

    @Test
    @DisplayName("CP-09: Reporte general conserva estructura y contadores exactos")
    void testGeneracionReporteGeneral() {
        Solicitud s1 = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana@utn.edu.ar", "TUP",
                "EQUIV", "Equivalencia Programacion I", false));
        servicio.asignarResponsable(s1.getId(), "A03");
        servicio.cambiarEstado(s1.getId(), EstadoSolicitud.APROBADA, "Aprobada OK");

        String reporte = servicio.emitirReporteGeneral();
        assertTrue(reporte.contains("Total Solicitudes: 1"), "Reporte debe indicar 1 solicitud total");
        assertTrue(reporte.contains("Aprobadas        : 1"), "Reporte debe indicar 1 aprobada");
        assertTrue(reporte.contains("Prioridad Media  : 1"), "Reporte debe indicar 1 de prioridad media");
    }

    private static class NotificadorSilencioso implements Notificador {
        @Override
        public void notificarRegistro(Solicitud solicitud) {}
        @Override
        public void notificarAsignacion(Solicitud solicitud, AreaResponsable area) {}
        @Override
        public void notificarCambioEstado(Solicitud solicitud) {}
    }
}
