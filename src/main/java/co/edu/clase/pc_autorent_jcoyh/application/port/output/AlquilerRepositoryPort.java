package co.edu.clase.pc_autorent_jcoyh.application.port.output;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;

import java.util.Optional;
import java.util.List;

public interface AlquilerRepositoryPort {
    Alquiler guardar(Alquiler alquiler);
    Optional<Alquiler> buscarPorId(Long id);
    List<Alquiler> listarTodos(); // IMPLEMENTADO PARA LA CONSULTA EN FRONT-END index_v2.html
}
