package utn.tpi.sgsa.repository;

import utn.tpi.sgsa.domain.AreaResponsable;
import java.util.List;
import java.util.Optional;

public interface AreaResponsableRepository {
    Optional<AreaResponsable> buscarPorCodigo(String codigo);
    List<AreaResponsable> listarTodas();
}
