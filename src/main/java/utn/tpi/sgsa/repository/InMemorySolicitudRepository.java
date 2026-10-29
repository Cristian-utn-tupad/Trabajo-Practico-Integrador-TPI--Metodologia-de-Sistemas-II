package utn.tpi.sgsa.repository;

import utn.tpi.sgsa.domain.Solicitud;
import utn.tpi.sgsa.dto.CriterioBusquedaSolicitud;
import java.util.*;
import java.util.stream.Collectors;

public class InMemorySolicitudRepository implements SolicitudRepository {
    private static final int ID_INICIAL_BASE = 100;

    protected final Map<Integer, Solicitud> almacenamiento = new LinkedHashMap<>();
    protected int ultimoId = ID_INICIAL_BASE;

    @Override
    public int generarSiguienteId() {
        ultimoId++;
        return ultimoId;
    }

    @Override
    public void guardar(Solicitud solicitud) {
        almacenamiento.put(solicitud.getId(), solicitud);
        if (solicitud.getId() > ultimoId) {
            ultimoId = solicitud.getId();
        }
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public List<Solicitud> listarTodas() {
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public List<Solicitud> buscarPorCriterio(CriterioBusquedaSolicitud criterio) {
        return almacenamiento.values().stream()
                .filter(criterio::coincideCon)
                .collect(Collectors.toList());
    }
}
