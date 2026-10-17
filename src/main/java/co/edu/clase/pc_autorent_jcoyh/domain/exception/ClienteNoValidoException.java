package co.edu.clase.pc_autorent_jcoyh.domain.exception;

public class ClienteNoValidoException extends RuntimeException {
    public ClienteNoValidoException(String mensaje) {
        super(mensaje);
    }
}
