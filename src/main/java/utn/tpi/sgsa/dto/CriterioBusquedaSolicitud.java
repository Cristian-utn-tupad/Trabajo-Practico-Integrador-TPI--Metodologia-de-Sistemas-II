package utn.tpi.sgsa.dto;

import utn.tpi.sgsa.domain.EstadoSolicitud;
import utn.tpi.sgsa.domain.Solicitud;
import utn.tpi.sgsa.domain.TipoSolicitud;

/**
 * Refactoring: Introduce Parameter Object para encapsular los criterios de filtrado.
 */
public record CriterioBusquedaSolicitud(
        TipoSolicitud tipo,
        EstadoSolicitud estado,
        String legajoEstudiante
) {
    public boolean coincideCon(Solicitud solicitud) {
        if (tipo != null && solicitud.getTipo() != tipo) {
            return false;
        }
        if (estado != null && solicitud.getEstado() != estado) {
            return false;
        }
        if (legajoEstudiante != null && !legajoEstudiante.trim().isEmpty()
                && !solicitud.getEstudiante().getLegajo().equalsIgnoreCase(legajoEstudiante.trim())) {
            return false;
        }
        return true;
    }
}
