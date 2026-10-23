package co.edu.clase.pc_autorent_jcoyh.domain.model;

import co.edu.clase.pc_autorent_jcoyh.domain.exception.ClienteException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.MensajesError;

public class Cliente {
    private final Long id;
    private final String nombre;
    private final String tipoDocumento;
    private final Long documento;
    private String telefono;
    private String email;
    private String estado;

    public Cliente(Long id, 
                String nombre, 
                String tipoDocumento, 
                Long documento, 
                String telefono, 
                String email, 
                String estado) {
                    
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteException(MensajesError.CLIENTE_NOMBRE_OBLIGATORIO);
        }
        if (documento == null || documento <= 0) {
            throw new ClienteException(MensajesError.CLIENTE_DOCUMENTO_OBLIGATORIO);
        }
        if (email == null || !email.contains("@")) {
            throw new ClienteException(String.format(MensajesError.CLIENTE_EMAIL_INVALIDO, email));
        }
  
        this.id = id;
        this.nombre = nombre;
        this.tipoDocumento = (tipoDocumento == null || tipoDocumento.isBlank())
                            ? "CC"
                            : tipoDocumento.trim();
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado != null 
                    ? estado 
                    : EstadosNegocio.CLIENTE_ACTIVO;
    }

    public void desactivarCliente() {
        this.estado = EstadosNegocio.CLIENTE_INACTIVO;
    }

    public  void reactivarCliente() {
        this.estado = EstadosNegocio.CLIENTE_ACTIVO;
    }

    public void actualizarContacto(String nuevoTelefono, String nuevoEmail) {
        if (nuevoEmail == null || !nuevoEmail.contains("@")) {
            throw new ClienteException(
                String.format(MensajesError.CLIENTE_EMAIL_INVALIDO, nuevoEmail)
            );
        }
        this.telefono = nuevoTelefono;
        this.email = nuevoEmail;
    }


    public Long getId() { 
        return id; 
    }

    public String getNombre() { 
        return nombre; 
    }

    public String getTipoDocumento() { 
        return tipoDocumento; 
    }

    public Long getDocumento() { 
        return documento; 
    }

    public String getTelefono() { 
        return telefono; 
    }

    public String getEmail() { 
        return email; 
    }

    public String getEstado() { 
        return estado; 
    }
}
