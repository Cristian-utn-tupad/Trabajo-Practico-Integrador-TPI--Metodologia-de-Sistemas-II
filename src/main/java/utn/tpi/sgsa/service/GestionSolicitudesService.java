package utn.tpi.sgsa.service;

import utn.tpi.sgsa.domain.*;
import utn.tpi.sgsa.dto.CriterioBusquedaSolicitud;
import utn.tpi.sgsa.dto.DatosRegistroSolicitud;
import utn.tpi.sgsa.exception.ReglaNegocioException;
import utn.tpi.sgsa.repository.AreaResponsableRepository;
import utn.tpi.sgsa.repository.SolicitudRepository;
import java.time.LocalDate;
import java.util.List;

public class GestionSolicitudesService {
    private final SolicitudRepository solicitudRepository;
    private final AreaResponsableRepository areaRepository;
    private final Notificador notificador;
    private final GeneradorReporteSolicitudes generadorReporte;

    public GestionSolicitudesService(SolicitudRepository solicitudRepository,
                                     AreaResponsableRepository areaRepository,
                                     Notificador notificador,
                                     GeneradorReporteSolicitudes generadorReporte) {
        this.solicitudRepository = solicitudRepository;
        this.areaRepository = areaRepository;
        this.notificador = notificador;
        this.generadorReporte = generadorReporte;
    }

    public Solicitud registrarSolicitud(DatosRegistroSolicitud datos) {
        if (datos == null) {
            throw new ReglaNegocioException("Los datos de registro de la solicitud no pueden ser nulos.");
        }
        Prioridad prioridadCalculada = datos.tipo().calcularPrioridad(datos.esUrgente());
        int nuevoId = solicitudRepository.generarSiguienteId();

        Solicitud nuevaSolicitud = new Solicitud(
                nuevoId,
                datos.estudiante(),
                datos.tipo(),
                prioridadCalculada,
                datos.descripcion(),
                LocalDate.now()
        );

        solicitudRepository.guardar(nuevaSolicitud);
        notificador.notificarRegistro(nuevaSolicitud);
        return nuevaSolicitud;
    }

    public void asignarResponsable(int idSolicitud, String codigoArea) {
        Solicitud solicitud = obtenerSolicitudExistente(idSolicitud);
        AreaResponsable area = obtenerAreaExistente(codigoArea);

        solicitud.asignarResponsable(area);
        solicitudRepository.guardar(solicitud);
        notificador.notificarAsignacion(solicitud, area);
    }

    public void cambiarEstado(int idSolicitud, EstadoSolicitud nuevoEstado, String comentario) {
        Solicitud solicitud = obtenerSolicitudExistente(idSolicitud);

        solicitud.cambiarEstado(nuevoEstado, comentario);
        solicitudRepository.guardar(solicitud);
        notificador.notificarCambioEstado(solicitud);
    }

    public List<Solicitud> buscarSolicitudes(CriterioBusquedaSolicitud criterio) {
        return solicitudRepository.buscarPorCriterio(criterio);
    }

    public String emitirReporteGeneral() {
        List<Solicitud> todas = solicitudRepository.listarTodas();
        return generadorReporte.generarReporteGeneral(todas);
    }

    // Extract Method: Centraliza la obtencion validada evitando duplicacion en asignarResponsable y cambiarEstado
    private Solicitud obtenerSolicitudExistente(int idSolicitud) {
        return solicitudRepository.buscarPorId(idSolicitud)
                .orElseThrow(() -> new ReglaNegocioException("No existe la solicitud #" + idSolicitud));
    }

    private AreaResponsable obtenerAreaExistente(String codigoArea) {
        return areaRepository.buscarPorCodigo(codigoArea)
                .orElseThrow(() -> new ReglaNegocioException("Area responsable no encontrada: " + codigoArea));
    }
}
