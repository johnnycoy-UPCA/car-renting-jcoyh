package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.input.rest;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.CancelarAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.CrearAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.FinalizarAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.IniciarReservaUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final CrearAlquilerUseCase crearAlquilerUseCase;
    private final FinalizarAlquilerUseCase finalizarAlquilerUseCase;
    private final AlquilerRepositoryPort alquilerRepositoryPort;
    private final CancelarAlquilerUseCase cancelarAlquilerUseCase;
    private final IniciarReservaUseCase iniciarReservaUseCase;

    public AlquilerController(CrearAlquilerUseCase crearAlquilerUseCase, 
                            FinalizarAlquilerUseCase finalizarAlquilerUseCase, 
                            AlquilerRepositoryPort alquilerRepositoryPort,
                            CancelarAlquilerUseCase cancelarAlquilerUseCase,
                            IniciarReservaUseCase iniciarReservaUseCase) {
        this.crearAlquilerUseCase = crearAlquilerUseCase;
        this.finalizarAlquilerUseCase = finalizarAlquilerUseCase;
        this.alquilerRepositoryPort = alquilerRepositoryPort;
        this.cancelarAlquilerUseCase = cancelarAlquilerUseCase;
        this.iniciarReservaUseCase = iniciarReservaUseCase;
    }

    @PostMapping
    public ResponseEntity<Alquiler> crearAlquiler(@RequestBody Map<String, Object> payload) {

        // Extraer y convertir los valores del mapa de forma segura
        Long clienteId = Long.valueOf(payload.get("clienteId").toString());
        Long autoId = Long.valueOf(payload.get("autoId").toString());
        int cantidadDias = Integer.parseInt(payload.get("cantidadDias").toString());
        LocalDate fechaInicio = LocalDate.parse(payload.get("fechaInicio").toString());


        Alquiler nuevoAlquiler = crearAlquilerUseCase.crear(clienteId, autoId, cantidadDias, fechaInicio);
        return new ResponseEntity<>(nuevoAlquiler, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Alquiler> iniciarReserva(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> payload) {
        int kilometrajeInicialReal = payload.get("kilometrajeInicial");
        Alquiler alquilerActivado = iniciarReservaUseCase.iniciar(id, kilometrajeInicialReal);
        return ResponseEntity.ok(alquilerActivado);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Alquiler> finalizarAlquiler(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        
        int kilometrajeFinal = Integer.parseInt(payload.get("kilometrajeFinal").toString());

        LocalDate fechaEntregaReal;
        if (payload.containsKey("fechaFin") && payload.get("fechaFin") != null && !payload.get("fechaFin").toString().trim().isEmpty()) {
            fechaEntregaReal = LocalDate.parse(payload.get("fechaFin").toString().trim());
        } else {
            fechaEntregaReal = LocalDate.now();
        }

        Alquiler alquilerFinalizado = finalizarAlquilerUseCase.finalizar(id, kilometrajeFinal, fechaEntregaReal);
        return ResponseEntity.ok(alquilerFinalizado);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Alquiler> cancelarAlquiler(@PathVariable Long id) {
        
        Alquiler alquilerCancelado = cancelarAlquilerUseCase.cancelar(id);
        return ResponseEntity.ok(alquilerCancelado);
    }

    // Icluído al final para interacción con la página index_v2.html
    @GetMapping
    public ResponseEntity<List<Alquiler>> listarTodos() {
        return ResponseEntity.ok(alquilerRepositoryPort.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> buscarPorId(@PathVariable Long id) {
        return alquilerRepositoryPort.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

