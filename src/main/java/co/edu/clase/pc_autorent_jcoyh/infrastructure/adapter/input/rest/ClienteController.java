package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.input.rest;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.DarBajaClienteUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.RegistrarClienteUseCase;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.clase.pc_autorent_jcoyh.application.port.output.ClienteRepositoryPort;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final RegistrarClienteUseCase registrarClienteUseCase;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final DarBajaClienteUseCase darBajaClienteUseCase;

    public ClienteController(RegistrarClienteUseCase registrarClienteUseCase, ClienteRepositoryPort clienteRepositoryPort, DarBajaClienteUseCase darBajaClienteUseCase) {
        this.registrarClienteUseCase = registrarClienteUseCase;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.darBajaClienteUseCase = darBajaClienteUseCase;
    }

    @PostMapping
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente cliente) {
        Cliente clienteRegistrado = registrarClienteUseCase.registrar(cliente);
        return new ResponseEntity<>(clienteRegistrado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Cliente> darDeBajaCliente(@PathVariable Long id) {
        Cliente clienteDesactivado = darBajaClienteUseCase.darDeBaja(id);
        return ResponseEntity.ok(clienteDesactivado);
    }
    
    // Icluído al final para interacción con la página index_v2.html
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        // Retorna la lista completa mapeada desde H2
        return ResponseEntity.ok(clienteRepositoryPort.listarTodos()); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteRepositoryPort.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}