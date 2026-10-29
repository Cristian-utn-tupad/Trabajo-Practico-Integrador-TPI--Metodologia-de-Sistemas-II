package utn.tpi.sgsa.dto;

import utn.tpi.sgsa.domain.Estudiante;
import utn.tpi.sgsa.domain.TipoSolicitud;

/**
 * Refactoring: Introduce Parameter Object.
 * Reemplaza la lista larga de 7 parametros sueltos en el registro de solicitudes.
 */
public record DatosRegistroSolicitud(
        Estudiante estudiante,
        TipoSolicitud tipo,
        String descripcion,
        boolean esUrgente
) {
    public static DatosRegistroSolicitud crear(
            String legajo,
            String nombreCompleto,
            String email,
            String carrera,
            String codigoTipo,
            String descripcion,
            boolean esUrgente) {
        Estudiante estudiante = new Estudiante(legajo, nombreCompleto, email, carrera);
        TipoSolicitud tipo = TipoSolicitud.desdeCodigo(codigoTipo);
        return new DatosRegistroSolicitud(estudiante, tipo, descripcion, esUrgente);
    }
}
