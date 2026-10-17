package co.edu.clase.pc_autorent_jcoyh.domain.model;

public class Cliente {
    private Long id;
    private String nombre;
    private String tipoDocumento;
    private Long documento;
    private String telefono;
    private String email;
    private String estado;

    public Cliente() {
        this.estado = "ACTIVO"; // Nace con este estado
    }

    public Cliente(Long id, String nombre, String tipoDocumento, Long documento, String telefono, String email, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado != null ? estado : "ACTIVO";
    }

    public void desactivar() {
        this.estado = "INACTIVO";
    }


    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Long getDocumento() { return documento; }
    public void setDocumento(Long documento) { this.documento = documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
