package utn.tpi.sgsa.domain;

import utn.tpi.sgsa.exception.ReglaNegocioException;

public class AreaResponsable {
    private final String codigo;
    private final String nombreArea;
    private final String nombreResponsable;
    private final String emailContacto;

    public AreaResponsable(String codigo, String nombreArea, String nombreResponsable, String emailContacto) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El codigo de area no puede estar vacio.");
        }
        if (nombreArea == null || nombreArea.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del area no puede estar vacio.");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.nombreArea = nombreArea.trim();
        this.nombreResponsable = nombreResponsable != null ? nombreResponsable.trim() : "";
        this.emailContacto = emailContacto != null ? emailContacto.trim() : "";
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public String formatearResumen() {
        return nombreArea + " (" + nombreResponsable + ")";
    }
}
