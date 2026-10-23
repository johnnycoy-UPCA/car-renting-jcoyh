package co.edu.clase.pc_autorent_jcoyh.application.service;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.CancelarAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.CrearAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.FinalizarAlquilerUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.IniciarReservaUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AutoRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.ClienteRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.AlquilerException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.NoEncontradoException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.MensajesError;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Cliente;
import co.edu.clase.pc_autorent_jcoyh.domain.model.EstadosNegocio;

import java.time.LocalDate;
 
public class AlquilerService implements CrearAlquilerUseCase, 
                                        FinalizarAlquilerUseCase, 
                                        CancelarAlquilerUseCase, 
                                        IniciarReservaUseCase {

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

    private Auto validarClienteYAuto(Long clienteId, 
                                    Long autoId, 
                                    Long alquilerId, 
                                    LocalDate fechaInicio, 
                                    int cantidadDias) {

        Cliente cliente = clienteRepositoryPort.buscarPorId(clienteId)
                            .orElseThrow(() -> new NoEncontradoException(
                                    String.format(MensajesError.CLIENTE_NO_REGISTRADO, clienteId)
                                )
                            );

        if (EstadosNegocio.CLIENTE_INACTIVO.equalsIgnoreCase(cliente.getEstado())) {
            throw new AlquilerException(
                    String.format(MensajesError.CLIENTE_INACTIVO, cliente.getDocumento())
            );
        }

        Auto auto = autoRepositoryPort.buscarPorId(autoId)
                        .orElseThrow(() -> new NoEncontradoException(
                                String.format(MensajesError.AUTO_NO_REGISTRADO, autoId)
                            )
                        );

        if (!EstadosNegocio.AUTO_DISPONIBLE.equals(auto.getEstado())) {
            throw new AlquilerException(
                    String.format(MensajesError.AUTO_NO_DISPONIBLE, auto.getPlaca(), auto.getEstado())
            );
        }

        boolean seCruzaConAlquilerVigente = alquilerRepositoryPort
                                                .listarTodos()
                                                .stream()
                                                .filter(alq -> alq.getAutoId().equals(auto.getId()))
                                                .filter(alq -> alquilerId == null || !alq.getId().equals(alquilerId))
                                                .anyMatch(alq -> alq.seCruzaConPeriodo(fechaInicio, cantidadDias));

        if (seCruzaConAlquilerVigente) {

            throw new AlquilerException(
                String.format(MensajesError.ALQUILER_FECHAS_CRUZADAS, auto.getPlaca())
            );
        }

        return auto;
    }

    @Override
    public Alquiler crearAlquiler(Alquiler alquiler) {
        Auto auto = validarClienteYAuto(alquiler.getClienteId(), 
                                        alquiler.getAutoId(), 
                                        null, 
                                        alquiler.getFechaInicio(), 
                                        alquiler.getCantidadDias());

        Alquiler nuevoAlquiler = new Alquiler(
                                    null, 
                                    alquiler.getClienteId(), 
                                    alquiler.getAutoId(), 
                                    LocalDate.now(), 
                                    alquiler.getFechaInicio(), 
                                    null, 
                                    alquiler.getCantidadDias(), 
                                    0.0, 
                                    auto.getKilometraje(), 
                                    null, 
                                    EstadosNegocio.ALQUILER_PENDIENTE
                                );
        
        nuevoAlquiler.calcularYConfigurarAlquiler(auto.getPrecioPorDia(), 
                                                alquiler.getFechaInicio());

        if (EstadosNegocio.ALQUILER_ACTIVO.equals(nuevoAlquiler.getEstado())) {
            auto.alquilarAuto();
            autoRepositoryPort.guardar(auto);
        }

        return alquilerRepositoryPort.guardar(nuevoAlquiler);
    }

    @Override
    public Alquiler finalizarAlquiler(Long alquilerId, 
                                    Double kilometrajeFinal, 
                                    LocalDate fechaEntregaReal) {

        Alquiler alquiler = alquilerRepositoryPort.buscarPorId(alquilerId)
                                .orElseThrow(() -> new NoEncontradoException(
                                        String.format(MensajesError.ALQUILER_NO_ENCONTRADO, alquilerId)
                                    )
                                );

        Auto auto = autoRepositoryPort.buscarPorId(alquiler.getAutoId())
                        .orElseThrow(() -> new NoEncontradoException(
                                String.format(MensajesError.AUTO_NO_REGISTRADO, alquiler.getAutoId())
                            )
                        );

        LocalDate fechaFinalizacion = (fechaEntregaReal != null) 
                                    ? fechaEntregaReal 
                                    : LocalDate.now();

        alquiler.procesarDevolucion(fechaFinalizacion, 
                                    kilometrajeFinal, 
                                    auto.getPrecioPorDia());
                                    
        auto.registrarRetornoAuto(kilometrajeFinal);

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }

    @Override
    public Alquiler iniciarReserva(Long alquilerId) {
        Alquiler alquiler = alquilerRepositoryPort.buscarPorId(alquilerId)
                                .orElseThrow(() -> new NoEncontradoException(
                                        String.format(MensajesError.ALQUILER_NO_ENCONTRADO, alquilerId)
                                    )
                                );

        Auto auto = validarClienteYAuto(alquiler.getClienteId(), 
                                        alquiler.getAutoId(), 
                                        alquiler.getId(), 
                                        alquiler.getFechaInicio(), 
                                        alquiler.getCantidadDias());
        
        alquiler.iniciarReserva(auto.getKilometraje());
        auto.alquilarAuto();

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }

    @Override
    public Alquiler cancelarAlquiler(Long alquilerId) {
        Alquiler alquiler = alquilerRepositoryPort.buscarPorId(alquilerId)
                                .orElseThrow(() -> new NoEncontradoException(
                                        String.format(MensajesError.ALQUILER_NO_ENCONTRADO, alquilerId)
                                    )
                                );

        Auto auto = autoRepositoryPort.buscarPorId(alquiler.getAutoId())
                        .orElseThrow(() -> new NoEncontradoException(
                                String.format(MensajesError.AUTO_NO_REGISTRADO, alquiler.getAutoId())
                            )
                        );

        alquiler.cancelarAlquiler();
        auto.liberarDeAlquiler();

        autoRepositoryPort.guardar(auto);
        return alquilerRepositoryPort.guardar(alquiler);
    }
}
