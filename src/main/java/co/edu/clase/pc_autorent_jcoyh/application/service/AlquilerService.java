package co.edu.clase.pc_autorent_jcoyh.application.service;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.CancelarAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.CrearAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.FinalizarAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.IniciarReservaUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AutoRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.ClienteRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.AlquilerNoValidoException;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;

public class AlquilerService implements CrearAlquilerUseCase, FinalizarAlquilerUseCase, CancelarAlquilerUseCase, IniciarReservaUseCase {

    private final AlquilerRepositoryPort alquilerRepositoryPort;
    private final AutoRepositoryPort autoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    public AlquilerService(AlquilerRepositoryPort alquilerRepositoryPort, 
                        AutoRepositoryPort autoRepositoryPort,
                        ClienteRepositoryPort clienteRepositoryPort) {
        this.alquilerRepositoryPort = alquilerRepositoryPort;
        this.autoRepositoryPort = autoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public Alquiler crear(Long clienteId, Long autoId, int cantidadDias, LocalDate fechaInicio) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(clienteId)
            .orElseThrow(() -> new RuntimeException("El documento ingresado no se encuentra registrado en el sistema."));

        Auto auto = autoRepositoryPort.buscarPorId(autoId)
            .orElseThrow(() -> new RuntimeException("El vehículo solicitado no se encuentra registrado en el sistema."));

        if ("INACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new RuntimeException("Operación denegada: El cliente " + cliente.getNombre() + " se encuentra en estado INACTIVO.");
        }

        boolean seCruzaConAlquilerVigente = alquilerRepositoryPort.listarTodos().stream()
            .anyMatch(alquiler -> alquiler.getAutoId().equals(autoId) && alquiler.seCruzaConPeriodo(fechaInicio, cantidadDias));

        if (seCruzaConAlquilerVigente) {
            Alquiler conflictoenContrato = alquilerRepositoryPort.listarTodos().stream()
                .filter(a -> a.getAutoId().equals(autoId) && a.seCruzaConPeriodo(fechaInicio, cantidadDias))
                // .map(Alquiler::getFechaFin) // Este método no verifica datos, generó warning.
                // .map(alq -> alq.getFechaFin()) // Cambio por la declaración de variable.
                .findFirst()
                .orElse(null);
            
            String rangoEnMensaje = (conflictoenContrato != null)
                ? "del " + conflictoenContrato.getFechaInicio() + " al " + conflictoenContrato.getFechaFin() + "(" + conflictoenContrato.getEstado() + ")"
                : "solicitado";
            
            throw new AlquilerNoValidoException("Operación Denegada: El vehículo con placa " + auto.getPlaca() +
                    " cuenta con un alquiler ACTIVO en la fecha ingresada. " + rangoEnMensaje);
        }
        
        Alquiler alquiler = new Alquiler();
        alquiler.setClienteId(clienteId);
        alquiler.setAutoId(autoId);
        alquiler.setCantidadDias(cantidadDias);
        
        alquiler.calcularYConfigurarAlquiler(auto.getPrecioPorDia(), fechaInicio);

        if ("PENDIENTE".equals(alquiler.getEstado())) {
            auto.setEstado("RESERVADO");
        } else {
            auto.setEstado("ALQUILADO");
            alquiler.setKilometrajeInicial(auto.getKilometraje());
        }

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }

    @Override
    @Transactional
    public Alquiler iniciar(Long alquilerId, int kilometrajeInicialReal) {
        Alquiler alquiler = alquilerRepositoryPort.buscarPorId(alquilerId)
            .orElseThrow(() -> new RuntimeException("Contrato de reserva no encontrado."));

        Auto auto = autoRepositoryPort.buscarPorId(alquiler.getAutoId())
            .orElseThrow(() -> new RuntimeException("Vehículo asociado no encontrado."));

        alquiler.iniciarReserva(kilometrajeInicialReal);

        auto.setEstado("ALQUILADO");

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }

    @Override
    @Transactional 
    public Alquiler finalizar(Long alquilerId, int kilometrajeFinal, LocalDate fechaEntregaReal) {
        Alquiler alquiler = alquilerRepositoryPort.buscarPorId(alquilerId)
            .orElseThrow(() -> new RuntimeException("Contrato de alquiler no encontrado con ID: " + alquilerId));

        Auto auto = autoRepositoryPort.buscarPorId(alquiler.getAutoId())
            .orElseThrow(() -> new RuntimeException("Vehículo asociado al contrato no encontrado."));

        try {
            alquiler.procesarDevolucionConReliquidacion(fechaEntregaReal, kilometrajeFinal, auto.getPrecioPorDia());
            auto.registrarRetornoDeAlquiler(kilometrajeFinal);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error en el vehículo con placa " + auto.getPlaca() + ": " + e.getMessage());
        }

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }

    @Override
    @Transactional 
    public Alquiler cancelar(Long alquilerId) {
        // Buscar ALQUILERES.ID en H2
        Alquiler alquiler = alquilerRepositoryPort.buscarPorId(alquilerId)
            .orElseThrow(() -> new RuntimeException("Contrato de alquiler no encontrado con ID: " + alquilerId));

        // Buscar ALQUILERES.AUTO_ID
        Auto auto = autoRepositoryPort.buscarPorId(alquiler.getAutoId())
            .orElseThrow(() -> new RuntimeException("Vehículo asociado al contrato no encontrado."));

        alquiler.cancelar();

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }
}
