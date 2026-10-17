package co.edu.clase.pc_autorent_jcoyh.application.port.input;

import java.time.LocalDate;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;

public interface FinalizarAlquilerUseCase {
    Alquiler finalizar(Long alquilerId, int kilometrajeFinal, LocalDate fechaEntregaReal);
}
