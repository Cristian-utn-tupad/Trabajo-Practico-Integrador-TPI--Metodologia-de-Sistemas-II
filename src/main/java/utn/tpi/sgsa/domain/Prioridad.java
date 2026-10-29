package utn.tpi.sgsa.domain;

public enum Prioridad {
    ALTA(1, "ALTA"),
    MEDIA(2, "MEDIA"),
    BAJA(3, "BAJA");

    private final int codigo;
    private final String etiqueta;

    Prioridad(int codigo, String etiqueta) {
        this.codigo = codigo;
        this.etiqueta = etiqueta;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Prioridad desdeCodigo(int codigo) {
        for (Prioridad p : values()) {
            if (p.codigo == codigo) {
                return p;
            }
        }
        throw new IllegalArgumentException("Codigo de prioridad invalido: " + codigo);
    }
}
