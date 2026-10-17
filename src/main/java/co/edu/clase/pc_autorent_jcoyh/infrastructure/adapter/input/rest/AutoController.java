package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.input.rest;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.RegistrarAutoUseCase;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.ActualizarMantenimientoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.DarBajaAutoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AutoRepositoryPort;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/autos")
public class AutoController {

    private final RegistrarAutoUseCase registrarAutoUseCase;
    private final AutoRepositoryPort autoRepositoryPort;
    private final ActualizarMantenimientoUseCase actualizarMantenimientoUseCase;
    private final DarBajaAutoUseCase darBajaAutoUseCase;

    // Inyección a través del constructor
    public AutoController(RegistrarAutoUseCase registrarAutoUseCase, 
                        AutoRepositoryPort autoRepositoryPort,
                        ActualizarMantenimientoUseCase actualizarMantenimientoUseCase,
                        DarBajaAutoUseCase darBajaAutoUseCase) {
        this.registrarAutoUseCase = registrarAutoUseCase;
        this.autoRepositoryPort = autoRepositoryPort;
        this.actualizarMantenimientoUseCase = actualizarMantenimientoUseCase;
        this.darBajaAutoUseCase = darBajaAutoUseCase;
    }

    @PostMapping
    public ResponseEntity<Auto> registrarAuto(@RequestBody Auto auto) {
        Auto autoRegistrado = registrarAutoUseCase.registrar(auto);
        return new ResponseEntity<>(autoRegistrado, HttpStatus.CREATED);
    }

    @PutMapping("/mantenimiento")
    public ResponseEntity<Auto> actualizarMantenimiento(@RequestBody Map<String, Object> payload) {

        String placa = payload.get("placa").toString().trim();
        boolean enMantenimiento = Boolean.parseBoolean(payload.get("enMantenimiento").toString());
        int kilometraje = Integer.parseInt(payload.get("kilometraje").toString());

        Auto autoMutado = actualizarMantenimientoUseCase.actualizar(placa, enMantenimiento, kilometraje);
        return ResponseEntity.ok(autoMutado);
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Auto> darDeBajaAuto(@PathVariable Long id) {

        Auto autoDesactivado = darBajaAutoUseCase.darDeBaja(id);
        return ResponseEntity.ok(autoDesactivado);
    }

    // Icluído al final para interacción con la página index_v2.html
    @GetMapping
    public ResponseEntity<List<Auto>> listarTodos() {
        return ResponseEntity.ok(autoRepositoryPort.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auto> buscarPorId(@PathVariable Long id) {
        return autoRepositoryPort.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}