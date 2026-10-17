package co.edu.clase.pc_autorent_jcoyh.domain.model;

import co.edu.clase.pc_autorent_jcoyh.domain.exception.AutoNoDisponibleException;

public class Auto {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private double precioPorDia;
    private double deposito;      // NUEVO: Valor inicial obligatorio
    private int kilometraje;      // NUEVO: Kilómetros acumulados totales
    private String estado; // "DISPONIBLE", "ALQUILADO", "MANTENIMIENTO"

    public Auto() {}

    public Auto(Long id, String placa, String marca, String modelo, int anio, double precioPorDia, double deposito, int kilometraje, String estado) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precioPorDia = precioPorDia;
        this.deposito = deposito;
        this.kilometraje = kilometraje;
        this.estado = estado;

    }

    private void validarYActualizarKilometraje(int kilometrajeNuevo) {
        if (kilometrajeNuevo < this.kilometraje) {
            throw new AutoNoDisponibleException("Inconsistencia: El kilometraje ingresado (" + kilometrajeNuevo +
                ") no puede ser menor que el kilometraje histórico registrado (" + this.kilometraje + ") del vehículo.");
        }
        this.kilometraje = kilometrajeNuevo;
    }

    public void alquilar() {
        if (!"DISPONIBLE".equals(this.estado)) {
            throw new AutoNoDisponibleException("El auto con placa " + placa + " no está DISPONIBLE.");
        }
        this.estado = "ALQUILADO";
    }

    public void liberar() {
        this.estado = "DISPONIBLE";
    }

    public void registrarRetornoDeAlquiler(int kilometrajeFinalContrato) {
        validarYActualizarKilometraje(kilometrajeFinalContrato);
        liberar();
    }

    public void ingresarAMantenimiento() {
        if ("MANTENIMIENTO".equals(this.estado)) {
            throw new AutoNoDisponibleException("Operación Denegada: El vehículo con placa " + this.placa +
                                                " ya se encuentra en MANTENIMIENTO");
        }
        
        if (!"DISPONIBLE".equals(this.estado)) {
            throw new AutoNoDisponibleException("Operación Denegada: El vehículo con placa " + this.placa + 
                                                " no puede ingresar a mantenimiento porque su estado actual es " + this.estado + ".");
        }
        
        this.estado = "MANTENIMIENTO";
    }

    public void salirDeMantenimiento(int kilometrajeRetorno) {
        if (!"MANTENIMIENTO".equals(this.estado)) {
            throw new AutoNoDisponibleException("Operación Denegada: El vehículo con placa " + this.placa + 
                                                " no se encuentra actualmente en estado de MANTENIMIENTO.");
        }

        validarYActualizarKilometraje(kilometrajeRetorno);

        this.estado = "DISPONIBLE";
    }

    public void desactivar() {
        if ("ALQUILADO".equals(this.estado)) {
            throw new AutoNoDisponibleException("Operación Denegada: El vehículo con placa " + this.placa + 
                " no puede ser dado de baja porque se encuentra bajo un contrato de alquiler ACTIVO vigente.");
        }
        this.estado = "INACTIVO";
    }


    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public double getPrecioPorDia() { return precioPorDia; }
    public void setPrecioPorDia(double precioPorDia) { this.precioPorDia = precioPorDia; }

    public double getDeposito() { return deposito; }
    public void setDeposito(double deposito) { this.deposito = deposito; }

    public int getKilometraje() { return kilometraje; }
    public void setKilometraje(int kilometraje) { this.kilometraje = kilometraje; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
