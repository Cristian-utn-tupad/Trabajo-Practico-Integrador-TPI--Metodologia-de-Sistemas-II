package utn.tpi.sgsa.service;

import utn.tpi.sgsa.domain.AreaResponsable;
import utn.tpi.sgsa.domain.Solicitud;

public class ConsolaEmailNotificador implements Notificador {

    @Override
    public void notificarRegistro(Solicitud solicitud) {
        System.out.println("[NOTIF EMAIL -> " + solicitud.getEstudiante().getEmail()
                + "] Solicitud #" + solicitud.getId() + " registrada con exito.");
    }

    @Override
    public void notificarAsignacion(Solicitud solicitud, AreaResponsable area) {
        System.out.println("[NOTIF EMAIL -> " + area.getEmailContacto()
                + "] Nueva solicitud #" + solicitud.getId() + " asignada a su area.");
    }

    @Override
    public void notificarCambioEstado(Solicitud solicitud) {
        System.out.println("[NOTIF EMAIL -> " + solicitud.getEstudiante().getEmail()
                + "] Su solicitud #" + solicitud.getId() + " cambio de estado.");
    }
}
