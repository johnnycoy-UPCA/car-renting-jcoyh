package co.edu.clase.pc_autorent_jcoyh.domain.exception;

public class AutoNoDisponibleException extends RuntimeException {
    public AutoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
