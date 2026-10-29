package utn.tpi.sgsa.domain;

import utn.tpi.sgsa.exception.ReglaNegocioException;
import java.time.LocalDate;

public class Solicitud {
    private static final int LONGITUD_MINIMA_DESCRIPCION = 5;

    private final int id;
    private final Estudiante estudiante;
    private AreaResponsable areaResponsable;
    private final TipoSolicitud tipo;
    private final Prioridad prioridad;
    private EstadoSolicitud estado;
    private final String descripcion;
    private final LocalDate fechaCreacion;
    private String historialObservaciones;

    public Solicitud(int id, Estudiante estudiante, TipoSolicitud tipo, Prioridad prioridad,
                     String descripcion, LocalDate fechaCreacion) {
        this(id, estudiante, null, tipo, prioridad, EstadoSolicitud.PENDIENTE,
                descripcion, fechaCreacion, "Creada en fecha " + fechaCreacion);
    }

    public Solicitud(int id, Estudiante estudiante, AreaResponsable areaResponsable,
                     TipoSolicitud tipo, Prioridad prioridad, EstadoSolicitud estado,
                     String descripcion, LocalDate fechaCreacion, String historialObservaciones) {
        if (estudiante == null) {
            throw new ReglaNegocioException("La solicitud debe tener un estudiante asociado.");
        }
        if (tipo == null) {
            throw new ReglaNegocioException("El tipo de solicitud es obligatorio.");
        }
        if (prioridad == null) {
            throw new ReglaNegocioException("La prioridad de la solicitud es obligatoria.");
        }
        if (estado == null) {
            throw new ReglaNegocioException("El estado de la solicitud es obligatorio.");
        }
        if (descripcion == null || descripcion.trim().length() < LONGITUD_MINIMA_DESCRIPCION) {
            throw new ReglaNegocioException("La descripcion es demasiado corta (minimo 5 caracteres).");
        }
        this.id = id;
        this.estudiante = estudiante;
        this.areaResponsable = areaResponsable;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = estado;
        this.descripcion = descripcion.trim();
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDate.now();
        this.historialObservaciones = historialObservaciones != null ? historialObservaciones : "";
    }

    // Move Method: Comportamiento de negocio reubicado desde SistemaSolicitudes hacia la entidad Solicitud
    public void asignarResponsable(AreaResponsable nuevaArea) {
        if (nuevaArea == null) {
            throw new ReglaNegocioException("El area responsable a asignar no puede ser nula.");
        }
        validarQueNoEsteCerrada("No se puede asignar responsable a una solicitud cerrada.");

        this.areaResponsable = nuevaArea;
        if (this.estado == EstadoSolicitud.PENDIENTE) {
            this.estado = EstadoSolicitud.EN_REVISION;
        }
        agregarObservacion("Asignada a " + nuevaArea.formatearResumen());
    }

    // Move Method: Reglas de transicion de estado encapsuladas dentro de Solicitud
    public void cambiarEstado(EstadoSolicitud nuevoEstado, String comentario) {
        if (nuevoEstado == null) {
            throw new ReglaNegocioException("El nuevo estado no puede ser nulo.");
        }
        validarQueNoEsteCerrada("La solicitud ya se encuentra cerrada y no admite cambios.");

        if (nuevoEstado.requiereResponsableAsignado() && this.areaResponsable == null) {
            throw new ReglaNegocioException("No se puede aprobar o rechazar sin responsable asignado.");
        }
        if (nuevoEstado.requiereMotivoObligatorio() && (comentario == null || comentario.trim().isEmpty())) {
            throw new ReglaNegocioException("Para rechazar una solicitud debe indicar un motivo.");
        }

        this.estado = nuevoEstado;
        if (comentario != null && !comentario.trim().isEmpty()) {
            agregarObservacion("Cambio estado (" + nuevoEstado.getCodigo() + "): " + comentario.trim());
        }
    }

    private void validarQueNoEsteCerrada(String mensajeError) {
        if (this.estado.esFinal()) {
            throw new ReglaNegocioException(mensajeError);
        }
    }

    private void agregarObservacion(String nuevaEntrada) {
        if (this.historialObservaciones == null || this.historialObservaciones.isEmpty()) {
            this.historialObservaciones = nuevaEntrada;
        } else {
            this.historialObservaciones = this.historialObservaciones + " | " + nuevaEntrada;
        }
    }

    public int getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public AreaResponsable getAreaResponsable() {
        return areaResponsable;
    }

    public boolean tieneResponsableAsignado() {
        return areaResponsable != null;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public String getHistorialObservaciones() {
        return historialObservaciones;
    }
}
