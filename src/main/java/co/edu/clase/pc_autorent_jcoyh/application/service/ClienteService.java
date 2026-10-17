package co.edu.clase.pc_autorent_jcoyh.application.service;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.RegistrarClienteUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.ClienteRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.ClienteNoValidoException;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;
import java.util.Optional;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.DarBajaClienteUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;

public class ClienteService implements RegistrarClienteUseCase, DarBajaClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final AlquilerRepositoryPort alquilerRepositoryPort;

    public ClienteService(ClienteRepositoryPort clienteRepositoryPort, AlquilerRepositoryPort alquilerRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.alquilerRepositoryPort = alquilerRepositoryPort;
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        // Para validar la duplicidad en el Documento
        Optional<Cliente> clienteExistente = clienteRepositoryPort.buscarPorDocumento(cliente.getDocumento());
        
        if (clienteExistente.isPresent()) {
            Cliente encontrado = clienteExistente.get();
            if (!"INACTIVO".equalsIgnoreCase(encontrado.getEstado())) {
                throw new ClienteNoValidoException("El documento de identificación " + encontrado.getDocumento() + 
                    " ya se encuentra registrado bajo el Cliente ID: " + encontrado.getId());
            }
        }

        cliente.setId(null);
        cliente.setEstado("ACTIVO");
        return clienteRepositoryPort.guardar(cliente);
    }

    // IMPLEMENTACIÓN DEL CASO DE USO: DAR DE BAJA CLIENTE
    @Override
    public Cliente darDeBaja(Long id) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("El cliente solicitado con ID " + id + " no se encuentra registrado en el sistema."));

        // Verificar si tiene alquileres activos vigentes
        boolean tieneAlquilerActivo = alquilerRepositoryPort.listarTodos().stream()
            .anyMatch(a -> a.getClienteId().equals(cliente.getId()) && "ACTIVO".equalsIgnoreCase(a.getEstado()));

        if (tieneAlquilerActivo) {
            throw new RuntimeException("Operación Denegada: El cliente " + cliente.getNombre() + 
                " no puede ser inactivado debido a que posee un contrato de alquiler ACTIVO vigente.");
        }

        cliente.desactivar();
        return clienteRepositoryPort.guardar(cliente);
    }

}
