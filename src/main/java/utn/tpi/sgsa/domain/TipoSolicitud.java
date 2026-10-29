package utn.tpi.sgsa.domain;

import utn.tpi.sgsa.exception.ReglaNegocioException;

public enum TipoSolicitud {
    EQUIV("EQUIV", "Equivalencia de Materia", Prioridad.MEDIA),
    CERT("CERT", "Certificado Academico", Prioridad.BAJA),
    EXCEP("EXCEP", "Excepcion de Correlatividad", Prioridad.ALTA),
    BAJA("BAJA", "Baja Temporal de Materia", Prioridad.MEDIA);

    private final String codigo;
    private final String descripcion;
    private final Prioridad prioridadBase;

    TipoSolicitud(String codigo, String descripcion, Prioridad prioridadBase) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.prioridadBase = prioridadBase;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Prioridad calcularPrioridad(boolean esUrgente) {
        return esUrgente ? Prioridad.ALTA : prioridadBase;
    }

    public static TipoSolicitud desdeCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El tipo de solicitud no puede estar vacio.");
        }
        for (TipoSolicitud tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo.trim())) {
                return tipo;
            }
        }
        throw new ReglaNegocioException("Tipo de solicitud invalido: " + codigo);
    }
}
