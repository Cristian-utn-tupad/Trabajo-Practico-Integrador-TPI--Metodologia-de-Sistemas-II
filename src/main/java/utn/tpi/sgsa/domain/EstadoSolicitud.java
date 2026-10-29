package utn.tpi.sgsa.domain;

import utn.tpi.sgsa.exception.ReglaNegocioException;

public enum EstadoSolicitud {
    PENDIENTE(1, "PENDIENTE", false),
    EN_REVISION(2, "EN_REVISION", false),
    APROBADA(3, "APROBADA", true),
    RECHAZADA(4, "RECHAZADA", true);

    private final int codigo;
    private final String etiqueta;
    private final boolean esFinal;

    EstadoSolicitud(int codigo, String etiqueta, boolean esFinal) {
        this.codigo = codigo;
        this.etiqueta = etiqueta;
        this.esFinal = esFinal;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean esFinal() {
        return esFinal;
    }

    public boolean requiereResponsableAsignado() {
        return this == APROBADA || this == RECHAZADA;
    }

    public boolean requiereMotivoObligatorio() {
        return this == RECHAZADA;
    }

    public static EstadoSolicitud desdeCodigo(int codigo) {
        for (EstadoSolicitud estado : values()) {
            if (estado.codigo == codigo) {
                return estado;
            }
        }
        throw new ReglaNegocioException("Codigo de estado invalido: " + codigo);
    }
}
