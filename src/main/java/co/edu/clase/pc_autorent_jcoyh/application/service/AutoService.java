package co.edu.clase.pc_autorent_jcoyh.application.service;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.RegistrarAutoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AutoRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.ActualizarMantenimientoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.DarBajaAutoUseCase;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import java.util.Optional;

public class AutoService implements RegistrarAutoUseCase, ActualizarMantenimientoUseCase, DarBajaAutoUseCase {
    
    private final AutoRepositoryPort autoRepositoryPort;
    private final AlquilerRepositoryPort alquilerRepositoryPort;

    public AutoService(AutoRepositoryPort autoRepositoryPort, AlquilerRepositoryPort alquilerRepositoryPort) {
        this.autoRepositoryPort = autoRepositoryPort;
        this.alquilerRepositoryPort = alquilerRepositoryPort;
    }

    @Override
    public Auto registrar(Auto auto) {
        // VALIDACIÓN: Verificar si la placa ya se encuentra en el catálogo
        Optional<Auto> autoExistente = autoRepositoryPort.buscarPorPlaca(auto.getPlaca());
        if (autoExistente.isPresent()) {
            Auto encontrado = autoExistente.get();
            if (!"INACTIVO".equalsIgnoreCase(encontrado.getEstado())) {
                throw new RuntimeException("El vehículo con placa " + encontrado.getPlaca() + 
                " ya se encuentra registrado y ACTIVO en el sistema con el ID: " + encontrado.getId());
            }
        }
        
        // Forzar a que JPA e H2 generen un ID secuencial nuevo
        auto.setId(null);
        // Autos nacen en estado DISPONIBLE
        auto.setEstado("DISPONIBLE");
        return autoRepositoryPort.guardar(auto);
    }

    // IMPLEMENTACIÓN DEL CASO DE USO DE MANTENIMIENTO
    @Override
    public Auto actualizar(String placa, boolean enMantenimiento, int kilometraje) {
        // 1. Buscar el vehículo en el catálogo H2 por su placa
        Auto auto = autoRepositoryPort.buscarPorPlaca(placa)
            .orElseThrow(() -> new RuntimeException("El vehículo con placa " + placa + " no se encuentra registrado en el sistema."));

        // 2. Verificar si tiene un alquiler ACTIVO
        boolean tieneAlquilerActivo = alquilerRepositoryPort.listarTodos().stream()
            .anyMatch(alquiler -> alquiler.getAutoId().equals(auto.getId()) && "ACTIVO".equalsIgnoreCase(alquiler.getEstado()));

        if (tieneAlquilerActivo) {
            throw new RuntimeException("Operación Denegada: El vehículo con placa " + placa + 
                " no puede ingresar ni salir de MANTENIMIENTO debido a que cuenta con un contrato de alquiler ACTIVO vigente.");
        }

        // 3. Cambio de estado en Arquitectura Hexagonal
        if (enMantenimiento) {
            auto.ingresarAMantenimiento();
        } else {
            auto.salirDeMantenimiento(kilometraje); // Valida internamente el kilometraje
        }

        // 4. Graba en la Base y regresa el JSON
        return autoRepositoryPort.guardar(auto);
    }

    // IMPLEMENTACIÓN DEL CASO DE USO: DAR DE BAJA AUTO
    @Override
    public Auto darDeBaja(Long id) {
        // 1. Buscar el vehículo en H2 por su ID
        Auto auto = autoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("El vehículo solicitado con ID " + id + " no se encuentra registrado en el sistema."));

        // 2. Verificar contratos activos vigentes
        boolean tieneAlquilerActivo = alquilerRepositoryPort.listarTodos().stream()
            .anyMatch(alquiler -> alquiler.getAutoId().equals(auto.getId()) && "ACTIVO".equalsIgnoreCase(alquiler.getEstado()));

        if (tieneAlquilerActivo) {
            throw new RuntimeException("Operación Denegada: El vehículo con placa " + auto.getPlaca() + 
                " no puede ser desactivado debido a que cuenta con un contrato de alquiler ACTIVO en curso.");
        }

        // 3. Cambio de estado en Arquitectura Hexagonal
        auto.desactivar();

        // 4. Guardar en la BD
        return autoRepositoryPort.guardar(auto);
    }

}
