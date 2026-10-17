package co.edu.clase.pc_autorent_jcoyh.application.port.input;

import java.time.LocalDate;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;

public interface CrearAlquilerUseCase {
    Alquiler crear(Long clienteId, Long autoId, int cantidadDias, LocalDate fechaInicio);
}
