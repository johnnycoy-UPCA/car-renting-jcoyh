package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.adapter;

import co.edu.clase.pc_autorent_jcoyh.application.port.output.ClienteRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.entity.ClienteEntity;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.mapper.ClienteMapper;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.repository.ClienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final ClienteMapper clienteMapper;

    public ClientePersistenceAdapter(ClienteJpaRepository clienteJpaRepository, ClienteMapper clienteMapper) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = clienteMapper.toEntity(cliente);
        ClienteEntity entityGuardada = clienteJpaRepository.save(entity);
        return clienteMapper.toDomain(entityGuardada);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteJpaRepository.findById(id).map(clienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(Long documento) {
        return clienteJpaRepository.findByDocumento(documento).stream()
                .map(clienteMapper::toDomain)
                .filter(cliente -> !"INACTIVO".equalsIgnoreCase(cliente.getEstado()))
                .findFirst();
    }

    // Incluído para uso en el Front-End
    public List<Cliente> listarTodos() {
        return clienteJpaRepository.findAll().stream()
                .map(clienteMapper::toDomain)
                .toList();
    }

}
