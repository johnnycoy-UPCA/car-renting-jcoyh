package co.edu.clase.pc_autorent_jcoyh.application.service;

import co.edu.clase.pc_autorent_jcoyh.application.port.input.RegistrarAutoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.DarBajaAutoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.input.ActualizarMantenimientoUseCase;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AutoRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.application.port.output.AlquilerRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import co.edu.clase.pc_autorent_jcoyh.domain.model.EstadosNegocio;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.AutoException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.NoEncontradoException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.MensajesError;

import java.util.List;

public class AutoService implements RegistrarAutoUseCase, 
                                    ActualizarMantenimientoUseCase, 
                                    DarBajaAutoUseCase {
    
    private final AutoRepositoryPort autoRepositoryPort;
    private final AlquilerRepositoryPort alquilerRepositoryPort;

    public AutoService(AutoRepositoryPort autoRepositoryPort, 
                    AlquilerRepositoryPort alquilerRepositoryPort) {
        this.autoRepositoryPort = autoRepositoryPort;
        this.alquilerRepositoryPort = alquilerRepositoryPort;
    }

    @Override
    public Auto registrarAuto(Auto auto) {

        List<Auto> autosExistentes = autoRepositoryPort.buscarPorPlaca(auto.getPlaca());

        for (Auto encontrado : autosExistentes) {
            if (!EstadosNegocio.AUTO_INACTIVO.equalsIgnoreCase(encontrado.getEstado())) {
                throw new  AutoException(
                    String.format(MensajesError.AUTO_PLACA_REPETIDA, encontrado.getPlaca())
                );
            }
        }

        Auto nuevoAuto = new Auto(
                            null, 
                            auto.getPlaca(), 
                            auto.getMarca(), 
                            auto.getModelo(), 
                            auto.getAnio(), 
                            auto.getPrecioPorDia(), 
                            auto.getDeposito(), 
                            auto.getKilometraje(), 
                            EstadosNegocio.AUTO_DISPONIBLE
                        );

        return autoRepositoryPort.guardar(nuevoAuto);
    }

    @Override
    public Auto actualizarMantenimiento(String placa, 
                                        boolean enMantenimiento, 
                                        Double kilometraje) {

        Auto auto = autoRepositoryPort
                        .buscarPorPlaca(placa)
                        .stream()
                        .filter(a -> !EstadosNegocio.AUTO_INACTIVO.equalsIgnoreCase(a.getEstado()))
                        .findFirst()
                        .orElseThrow(() -> new NoEncontradoException(
                                String.format(MensajesError.AUTO_PLACA_NO_REGISTRADO, placa)
                            )
                        );
                            
        if (enMantenimiento) {
            auto.ingresarAMantenimiento();
        } else {
            auto.salirDeMantenimiento(kilometraje);
        }

        return autoRepositoryPort.guardar(auto);
    }

    @Override
    public Auto darDeBajaAuto(Long id) {
        Auto auto = autoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new NoEncontradoException(
                    String.format(MensajesError.AUTO_NO_REGISTRADO, id)
                )
            );

        boolean tieneAlquilerActivo = alquilerRepositoryPort
                                        .listarTodos()
                                        .stream()
                                        .anyMatch(alquiler -> alquiler.getAutoId().equals(auto.getId()) && 
                                                EstadosNegocio.AUTO_DISPONIBLE.equalsIgnoreCase(alquiler.getEstado())
                                        );

        if (tieneAlquilerActivo) {
            throw new AutoException(
                String.format(MensajesError.AUTO_BAJA_CONTRATO_ACTIVO, auto.getPlaca())
            );
        }

        auto.desactivarAuto();
        return autoRepositoryPort.guardar(auto);
    }
}
