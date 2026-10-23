package co.edu.clase.pc_autorent_jcoyh.application.service;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.RegistrarClienteUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.DarBajaClienteUseCase;

import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.ClienteRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;
import co.edu.clase.pc_autorent_jcoyh.domain.model.EstadosNegocio;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.ClienteException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.NoEncontradoException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.MensajesError;

import java.util.Optional;

public class ClienteService implements RegistrarClienteUseCase, 
                                        DarBajaClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final AlquilerRepositoryPort alquilerRepositoryPort;

    public ClienteService(ClienteRepositoryPort clienteRepositoryPort, 
                        AlquilerRepositoryPort alquilerRepositoryPort) {

        this.clienteRepositoryPort = clienteRepositoryPort;
        this.alquilerRepositoryPort = alquilerRepositoryPort;
    }

    @Override
    public Cliente registrarCliente(Cliente cliente) {

        Optional<Cliente> clienteExistente = clienteRepositoryPort.buscarPorDocumento(cliente.getDocumento());
        
        if (clienteExistente.isPresent()) {
            Cliente encontrado = clienteExistente.get();
                throw new ClienteException(
                    String.format(MensajesError.CLIENTE_DOCUMENTO_REPETIDO, encontrado.getDocumento())
                );
        }

        Cliente nuevoCliente = new Cliente(
                                    null,
                                    cliente.getNombre(),
                                    cliente.getTipoDocumento(),
                                    cliente.getDocumento(),
                                    cliente.getTelefono(),
                                    cliente.getEmail(),
                                    EstadosNegocio.CLIENTE_ACTIVO
                                );

        return clienteRepositoryPort.guardar(nuevoCliente);
    }

    @Override
    public Cliente darDeBajaCliente(Long id) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new NoEncontradoException(
                    String.format(MensajesError.CLIENTE_NO_REGISTRADO, id)
                )
            );

        boolean tieneAlquilerActivo = alquilerRepositoryPort
                                        .listarTodos()
                                        .stream()
                                        .anyMatch(a -> a.getClienteId().equals(cliente.getId()) && 
                                                EstadosNegocio.ALQUILER_ACTIVO.equalsIgnoreCase(a.getEstado())
                                        );

        if (tieneAlquilerActivo) {
            throw new ClienteException(
                String.format(MensajesError.CLIENTE_ALQUILER_ACTIVO, cliente.getNombre())
            );
        }

        cliente.desactivarCliente();
        return clienteRepositoryPort.guardar(cliente);
    }

}
