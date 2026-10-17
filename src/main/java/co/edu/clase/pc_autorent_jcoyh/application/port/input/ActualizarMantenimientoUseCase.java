package co.edu.clase.pc_autorent_jcoyh.application.port.input;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;

public interface ActualizarMantenimientoUseCase {
    Auto actualizar(String placa, boolean enMantenimiento, int kilometraje);
    
}
