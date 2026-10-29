package utn.tpi.sgsa.repository;

import utn.tpi.sgsa.domain.Solicitud;
import utn.tpi.sgsa.dto.CriterioBusquedaSolicitud;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository {
    int generarSiguienteId();
    void guardar(Solicitud solicitud);
    Optional<Solicitud> buscarPorId(int id);
    List<Solicitud> listarTodas();
    List<Solicitud> buscarPorCriterio(CriterioBusquedaSolicitud criterio);
}
