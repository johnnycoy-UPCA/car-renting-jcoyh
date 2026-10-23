package co.edu.clase.pc_autorent_jcoyh.domain.model;

import co.edu.clase.pc_autorent_jcoyh.domain.exception.AlquilerException;
import co.edu.clase.pc_autorent_jcoyh.domain.exception.MensajesError;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Alquiler {
    private final Long id;
    private final Long clienteId;
    private final Long autoId;
    private final LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cantidadDias;
    private double valorTotal;
    private Double kilometrajeInicial;
    private Double kilometrajeFinal;
    private String estado;

    public Alquiler(Long id, 
                    Long clienteId, 
                    Long autoId, 
                    LocalDate fechaReserva, 
                    LocalDate fechaInicio, 
                    LocalDate fechaFin, 
                    int cantidadDias, 
                    double valorTotal, 
                    Double kilometrajeInicial, 
                    Double kilometrajeFinal, 
                    String estado) {

        this.id = id;
        this.clienteId = clienteId;
        this.autoId = autoId;
        this.fechaReserva = fechaReserva != null 
                            ? fechaReserva 
                            : LocalDate.now();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadDias = cantidadDias;
        this.valorTotal = valorTotal;
        this.kilometrajeInicial = kilometrajeInicial;
        this.kilometrajeFinal = kilometrajeFinal;
        this.estado = estado != null 
                    ? estado 
                    : EstadosNegocio.ALQUILER_PENDIENTE;
    }

    public void calcularYConfigurarAlquiler(double precioPorDia, 
                                            LocalDate fechaInicioDigitada) {

        if (this.cantidadDias <= 0) {
            throw new AlquilerException(MensajesError.ALQUILER_CANTIDAD_DIAS_INVALIDA);
        }
        if (fechaInicioDigitada == null) {
            throw new AlquilerException(MensajesError.ALQUILER_FECHA_INICIO_OBLIGATORIA);
        }

        if (fechaInicioDigitada.isBefore(LocalDate.now())) {
            throw new AlquilerException(
                String.format(MensajesError.ALQUILER_FECHA_PASADA, fechaInicioDigitada)
            );
        }

        this.valorTotal = precioPorDia * this.cantidadDias;
        this.fechaInicio = fechaInicioDigitada;
        this.fechaFin = this.fechaInicio.plusDays(this.cantidadDias);

        if (!fechaInicioDigitada.isAfter(LocalDate.now())) {
            this.estado = EstadosNegocio.ALQUILER_ACTIVO;
        } else {
            this.kilometrajeInicial = null;
        }
    }

    public void iniciarReserva(double kilometrajeActualAuto) {
        if (!EstadosNegocio.ALQUILER_PENDIENTE.equals(this.estado)) {
            throw new AlquilerException(MensajesError.ALQUILER_INICIO_RESERVA_INVALIDO);
        }
        this.kilometrajeInicial = kilometrajeActualAuto;
        this.estado = EstadosNegocio.ALQUILER_ACTIVO;
    }

    public void procesarDevolucion(LocalDate fechaEntregaReal, 
                                Double kilometrajeFinalReal, 
                                double precioPorDia) {

        if (!EstadosNegocio.ALQUILER_ACTIVO.equals(this.estado)) {
            throw new AlquilerException(MensajesError.ALQUILER_DEVOLUCION_ESTADO_INVALIDO);
        }
        if (EstadosNegocio.ALQUILER_FINALIZADO.equals(this.estado)) {
            throw new AlquilerException(MensajesError.ALQUILER_FINALIZAR_DOBLE);
        }
        if (this.kilometrajeInicial != null && kilometrajeFinalReal < this.kilometrajeInicial) {
            throw new AlquilerException(
                String.format(MensajesError.AUTO_KM_MENOR, kilometrajeFinalReal, this.kilometrajeInicial)
            );
        }

        this.fechaFin = fechaEntregaReal;
        
        long diasCalculados = ChronoUnit.DAYS.between(this.fechaInicio, this.fechaFin);
        this.cantidadDias = (diasCalculados <= 0) ? 1 : (int) diasCalculados;
        
        this.valorTotal = precioPorDia * this.cantidadDias;
        this.kilometrajeFinal = kilometrajeFinalReal;
        this.estado = EstadosNegocio.ALQUILER_FINALIZADO;
    }

    public void cancelarAlquiler() {

        if (!EstadosNegocio.ALQUILER_ACTIVO.equals(this.estado) && !EstadosNegocio.ALQUILER_PENDIENTE.equals(this.estado)) {
            throw new AlquilerException(
                String.format(MensajesError.ALQUILER_CANCELACION_INVALIDA, this.estado)
            );
        }

        this.estado = EstadosNegocio.ALQUILER_CANCELADO;
    }

    public boolean seCruzaConPeriodo(LocalDate nuevaFechaInicio, 
                                    int nuevosDias) {
                                        
        if (EstadosNegocio.ALQUILER_CANCELADO.equalsIgnoreCase(this.estado) || 
            EstadosNegocio.ALQUILER_FINALIZADO.equalsIgnoreCase(this.estado)) {
            return false;
        }

        LocalDate nuevoFin = nuevaFechaInicio.plusDays(nuevosDias);
        
        boolean seCruzaConFechas = !nuevaFechaInicio.isAfter(this.fechaFin) && 
                                !nuevoFin.isBefore(this.fechaInicio);

        boolean bloqueoPorEstado = EstadosNegocio.ALQUILER_ACTIVO.equalsIgnoreCase(this.estado) || 
                                EstadosNegocio.ALQUILER_PENDIENTE.equalsIgnoreCase(this.estado);

        return seCruzaConFechas && bloqueoPorEstado;
    }


    public Long getId() { 
        return id; 
    }

    public Long getClienteId() { 
        return clienteId; 
    }

    public Long getAutoId() { 
        return autoId; 
    }

    public LocalDate getFechaReserva() { 
        return fechaReserva; 
    }

    public LocalDate getFechaInicio() { 
        return fechaInicio; 
    }

    public LocalDate getFechaFin() { 
        return fechaFin; 
    }

    public int getCantidadDias() { 
        return cantidadDias; 
    }

    public double getValorTotal() { 
        return valorTotal; 
    }

    public Double getKilometrajeInicial() { 
        return kilometrajeInicial; 
    }

    public Double getKilometrajeFinal() { 
        return kilometrajeFinal; 
    }

    public String getEstado() { 
        return estado; 
    }
}
