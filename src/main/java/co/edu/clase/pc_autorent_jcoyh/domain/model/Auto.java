package co.edu.clase.pc_autorent_jcoyh.domain.model;

import co.edu.clase.pc_autorent_jcoyh.domain.exception.AutoException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.MensajesError;

public class Auto {
    private final Long id;
    private final String placa;
    private final String marca;
    private final String modelo;
    private final int anio;

    private double precioPorDia;
    private double deposito;
    private Double kilometraje;
    private String estado;


    public Auto(Long id, 
                String placa, 
                String marca, 
                String modelo, 
                int anio, 
                double precioPorDia, 
                double deposito, 
                Double kilometraje, 
                String estado) {
                    
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precioPorDia = precioPorDia;
        this.deposito = deposito;
        this.kilometraje = kilometraje != null 
                        ? kilometraje 
                        : 0.0;
        this.estado = estado != null 
                    ? estado 
                    : EstadosNegocio.AUTO_DISPONIBLE;

    }

    private void validarYActualizarKilometraje(Double kilometrajeNuevo) {
        if (kilometrajeNuevo < this.kilometraje) {
            throw new AutoException(
                String.format(MensajesError.AUTO_KM_MENOR, kilometrajeNuevo, this.kilometraje)
            );
        }

        this.kilometraje = kilometrajeNuevo;
    }

    public void alquilarAuto() {
        if (!EstadosNegocio.AUTO_DISPONIBLE.equals(this.estado)) {
            throw new AutoException(
                String.format(MensajesError.AUTO_NO_DISPONIBLE, this.placa, this.estado)
            );
        }

        this.estado = EstadosNegocio.AUTO_ALQUILADO;
    }

    public void liberarDeAlquiler() {

        this.estado = EstadosNegocio.AUTO_DISPONIBLE;
    }

    public void registrarRetornoAuto(Double kilometrajeFinalContrato) {
        validarYActualizarKilometraje(kilometrajeFinalContrato);

        this.estado = EstadosNegocio.AUTO_DISPONIBLE;
    }

    public void ingresarAMantenimiento() {
        if (EstadosNegocio.AUTO_MANTENIMIENTO.equals(this.estado)) {
            throw new AutoException(
                String.format(MensajesError.AUTO_MANTENIMIENTO_DUPLICADO, this.placa)
            );
        }
        if (!EstadosNegocio.AUTO_DISPONIBLE.equals(this.estado)) {
            throw new AutoException(
                String.format(MensajesError.AUTO_MANTENIMIENTO_ESTADO_INVALIDO, this.placa, this.estado)
            );
        }

        this.estado = EstadosNegocio.AUTO_MANTENIMIENTO;
    }

    public void salirDeMantenimiento(Double kilometrajeRetorno) {
        if (!EstadosNegocio.AUTO_MANTENIMIENTO.equals(this.estado)) {
            throw new AutoException(
                String.format(MensajesError.AUTO_MANTENIMIENTO_SALIDA_INVALIDA, this.placa)
            );
        }
        registrarRetornoAuto(kilometrajeRetorno);
    }

    public void desactivarAuto() {
        if (EstadosNegocio.AUTO_ALQUILADO.equals(this.estado)) {
            throw new AutoException(
                String.format(MensajesError.AUTO_BAJA_CONTRATO_ACTIVO, this.placa)
            );
        }

        this.estado = EstadosNegocio.AUTO_INACTIVO;
    }


    public Long getId() { 
        return id; 
    }
    
    public String getPlaca() { 
        return placa; 
    }
    
    public String getMarca() { 
        return marca; 
    }
    
    public String getModelo() { 
        return modelo; 
    }
    
    public int getAnio() { 
        return anio; 
    }
    
    public double getPrecioPorDia() { 
        return precioPorDia; 
    }
    
    public double getDeposito() { 
        return deposito; 
    }
    
    public Double getKilometraje() { 
        return kilometraje; 
    }
    
    public String getEstado() { 
        return estado; 
    }
}
