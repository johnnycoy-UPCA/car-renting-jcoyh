package co.edu.clase.pc_autorent_jcoyh.application.port.output;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import java.util.Optional;
import java.util.List;

public interface AutoRepositoryPort {
    Auto guardar(Auto auto);
    Optional<Auto> buscarPorId(Long id);
    Optional<Auto> buscarPorPlaca(String placa); // IMPLEMENTADO DESPUÉS DE LA PRIMERA PRUEBA
    List<Auto> listarTodos(); // IMPLEMENTADO PARA LA CONSULTA EN FRONT-END index_v2.html
}
