package utn.tpi.sgsa.domain;

import utn.tpi.sgsa.exception.ReglaNegocioException;

public class Estudiante {
    private final String legajo;
    private final String nombreCompleto;
    private final String email;
    private final String carrera;

    public Estudiante(String legajo, String nombreCompleto, String email, String carrera) {
        validarDatos(legajo, nombreCompleto, email);
        this.legajo = legajo.trim();
        this.nombreCompleto = nombreCompleto.trim();
        this.email = email.trim();
        this.carrera = (carrera == null || carrera.trim().isEmpty()) ? "Sin especificar" : carrera.trim();
    }

    private void validarDatos(String legajo, String nombreCompleto, String email) {
        if (legajo == null || legajo.trim().isEmpty()) {
            throw new ReglaNegocioException("El legajo del estudiante no puede estar vacio.");
        }
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del estudiante no puede estar vacio.");
        }
        if (email == null || !email.contains("@")) {
            throw new ReglaNegocioException("El email del estudiante es invalido.");
        }
    }

    public String getLegajo() {
        return legajo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getCarrera() {
        return carrera;
    }
}
