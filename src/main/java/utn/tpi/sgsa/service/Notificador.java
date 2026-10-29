package utn.tpi.sgsa.service;

import utn.tpi.sgsa.domain.AreaResponsable;
import utn.tpi.sgsa.domain.Solicitud;

public interface Notificador {
    void notificarRegistro(Solicitud solicitud);
    void notificarAsignacion(Solicitud solicitud, AreaResponsable area);
    void notificarCambioEstado(Solicitud solicitud);
}
