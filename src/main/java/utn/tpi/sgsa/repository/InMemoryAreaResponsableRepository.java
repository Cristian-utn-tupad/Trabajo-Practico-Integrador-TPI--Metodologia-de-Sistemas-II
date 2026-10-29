package utn.tpi.sgsa.repository;

import utn.tpi.sgsa.domain.AreaResponsable;
import java.util.*;

public class InMemoryAreaResponsableRepository implements AreaResponsableRepository {
    private final Map<String, AreaResponsable> areas = new LinkedHashMap<>();

    public InMemoryAreaResponsableRepository() {
        registrar(new AreaResponsable("A01", "Departamento de Alumnos", "Laura Gomez", "alumnos@utn.edu.ar"));
        registrar(new AreaResponsable("A02", "Secretaria Academica", "Carlos Ruiz", "academica@utn.edu.ar"));
        registrar(new AreaResponsable("A03", "Direccion de Carrera TUP", "Martin Perez", "tup@utn.edu.ar"));
    }

    public void registrar(AreaResponsable area) {
        areas.put(area.getCodigo().toUpperCase(), area);
    }

    @Override
    public Optional<AreaResponsable> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(areas.get(codigo.trim().toUpperCase()));
    }

    @Override
    public List<AreaResponsable> listarTodas() {
        return new ArrayList<>(areas.values());
    }
}
