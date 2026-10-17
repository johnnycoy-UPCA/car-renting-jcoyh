package co.edu.clase.pc_autorent_jcoyh.domain.model;

import co.edu.clase.pc_autorent_jcoyh.domain.exception.AlquilerNoValidoException;
import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;

public class Alquiler {
    private Long id;
    private Long clienteId;
    private Long autoId;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cantidadDias;
    private double valorTotal;
    private Integer kilometrajeInicial; // Antes era int, lo tuve que cambiar a Integer para que acepte null
    private Integer kilometrajeFinal;   // Antes era int, lo tuve que cambiar a Integer para que acepte null
    private String estado; // "ACTIVO", "FINALIZADO"

    public Alquiler() {
        this.fechaReserva = LocalDate.now();
    }

    // Configurar alquiler e ingresar el kilometraje de entrega
    public void calcularYConfigurarAlquiler(double precioPorDia, LocalDate fechaInicioDigitada) {
        if (this.cantidadDias <= 0) {
            throw new AlquilerNoValidoException("La cantidad de días debe ser mayor que cero.");
        }
        if (fechaInicioDigitada == null) {
            throw new AlquilerNoValidoException("La fecha de inicio es obligatoria.");
        }

        if (fechaInicioDigitada.isBefore(LocalDate.now())) {
            throw new AlquilerNoValidoException(
                "Operación Denegada: La fecha de inicio (" + fechaInicioDigitada + ") no puede anteior a la fecha de hoy: " + LocalDate.now()
            );
        }

        this.valorTotal = precioPorDia * this.cantidadDias;
        this.fechaInicio = fechaInicioDigitada;
        this.fechaFin = this.fechaInicio.plusDays(this.cantidadDias);

        if (fechaInicioDigitada.isAfter(LocalDate.now())) {
            this.estado = "PENDIENTE"; // Es una RESERVA
            this.kilometrajeInicial = null; // Se especificará al momento de iniciar el Alquiler
        } else {
            this.estado = "ACTIVO";    // El kilometraje se ingresará desde el Service
        }
    }

    public void iniciarReserva(int kilometrajeActualAuto) {
        if (!"PENDIENTE".equals(this.estado)) {
            throw new AlquilerNoValidoException("Operación Denegada: Solo se pueden iniciar contratos que estén en estado PENDIENTE.");
        }
        this.kilometrajeInicial = kilometrajeActualAuto;
        this.estado = "ACTIVO";
    }

    public void procesarDevolucionConReliquidacion(LocalDate fechaEntregaReal, int kilometrajeFinalReal, double precioPorDia) {
        if (!"ACTIVO".equals(this.estado)) {
            throw new AlquilerNoValidoException("Operación Denegada: Solo se pueden finalizar alquileres en estado ACTIVO.");
        }
        if ("FINALIZADO".equals(this.estado)) {
            throw new AlquilerNoValidoException("No se puede finalizar dos veces el mismo alquiler.");
        }
        if (this.kilometrajeInicial != null && kilometrajeFinalReal < this.kilometrajeInicial) {
            throw new AlquilerNoValidoException("Inconsistencia Mecánica: El kilometraje final (" + kilometrajeFinalReal +
                ") no puede ser menor que el inicial (" + this.kilometrajeInicial + ").");
        }

        this.fechaFin = fechaEntregaReal;
        
        // long diasCalculados = ChronoUnit.DAYS.between(this.fechaInicio, this.fechaFin);
        long diasCalculados = this.fechaFin.toEpochDay() - this.fechaInicio.toEpochDay();
        this.cantidadDias = (diasCalculados <= 0) ? 1 : (int) diasCalculados; // Mínimo cobro de 1 día
        
        // Reliquidación del total
        this.valorTotal = precioPorDia * this.cantidadDias;
        this.kilometrajeFinal = kilometrajeFinalReal;
        this.estado = "FINALIZADO";
    }

    public void cancelar() {
/*        if ("FINALIZADO".equals(this.estado)) {
            throw new AlquilerNoValidoException(
                "Operación Denegada: No se puede cerrar un contrato de alquiler que ya ha sido FINALIZADO."
            );
        }
        if ("CANCELADO".equals(this.estado)) {
            throw new AlquilerNoValidoException(
                "Operación Denegada: El contrato de alquiler ya se encuentra en estado CANCELADO."
            );
        }
*/        if (!"ACTIVO".equals(this.estado) && !"PENDIENTE".equals(this.estado)) {
            throw new AlquilerNoValidoException(
                "Operación Denegada: Solo se pueden cancelar contratos en estado ACTIVO o PENDIENTE. El contrato se encuentra " + this.estado
            );
        }

        this.estado = "CANCELADO";
    }

    // Verificar que las fechas no se crucen
    public boolean seCruzaConPeriodo(LocalDate nuevaFechaInicio, int nuevosDias) {
        if ("CANCELADO".equalsIgnoreCase(this.estado) || "FINALIZADO".equalsIgnoreCase(this.estado)) {
            return false; // Si está finalizado o cancelado, no genera bloqueo
        }

        LocalDate nuevoFin = nuevaFechaInicio.plusDays(nuevosDias);
        boolean seCruzaConFechaInicio = (nuevaFechaInicio.isBefore(this.fechaFin.plusDays(1)) || nuevaFechaInicio.isEqual(this.fechaFin.plusDays(1)));
        boolean seCruzaConFechaFin = (nuevoFin.isAfter(this.fechaInicio) || nuevoFin.isEqual(this.fechaInicio));
        boolean bloqueoPorEstado = "ACTIVO".equalsIgnoreCase(this.estado) || "PENDIENTE".equalsIgnoreCase(this.estado);

        return seCruzaConFechaInicio && seCruzaConFechaFin && bloqueoPorEstado;
    }


    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getAutoId() { return autoId; }
    public void setAutoId(Long autoId) { this.autoId = autoId; }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getCantidadDias() { return cantidadDias; }
    public void setCantidadDias(int cantidadDias) { this.cantidadDias = cantidadDias; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public Integer getKilometrajeInicial() { return kilometrajeInicial; }
    public void setKilometrajeInicial(Integer kilometrajeInicial) { this.kilometrajeInicial = kilometrajeInicial; }

    public Integer getKilometrajeFinal() { return kilometrajeFinal; }
    public void setKilometrajeFinal(Integer kilometrajeFinal) { this.kilometrajeFinal = kilometrajeFinal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
