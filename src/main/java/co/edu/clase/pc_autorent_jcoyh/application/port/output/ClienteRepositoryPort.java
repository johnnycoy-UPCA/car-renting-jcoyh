package co.edu.clase.pc_autorent_jcoyh.application.port.output;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;

import java.util.Optional;
import java.util.List;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorDocumento(Long documento);
    List<Cliente> listarTodos(); // IMPLEMENTADO PARA LA CONSULTA EN FRONT-END index_v2.html
}
