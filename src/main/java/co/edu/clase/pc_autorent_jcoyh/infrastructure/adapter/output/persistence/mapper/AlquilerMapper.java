package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.mapper;

import co.edu.clase.pc_autorent_jcoyh.domain.model.Alquiler;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.entity.AlquilerEntity;
import org.springframework.stereotype.Component;

@Component
public class AlquilerMapper {

    // Convierte de la Base de Datos (Entity) hacia el Negocio Puro (Domain)
    public Alquiler toDomain(AlquilerEntity entity) {
        if (entity == null) return null;
        
        Alquiler domain = new Alquiler();
        domain.setId(entity.getId());
        domain.setClienteId(entity.getClienteId());
        domain.setAutoId(entity.getAutoId());
        domain.setFechaReserva(entity.getFechaReserva());
        domain.setFechaInicio(entity.getFechaInicio());
        domain.setFechaFin(entity.getFechaFin());
        domain.setCantidadDias(entity.getCantidadDias());
        domain.setValorTotal(entity.getValorTotal());
        domain.setKilometrajeInicial(entity.getKilometrajeInicial());
        domain.setKilometrajeFinal(entity.getKilometrajeFinal());
        domain.setEstado(entity.getEstado());
        
        return domain;
    }

    // Convierte del Negocio Puro (Domain) hacia la Base de Datos (Entity)
    public AlquilerEntity toEntity(Alquiler domain) {
        if (domain == null) return null;
        
        return new AlquilerEntity(
            domain.getId(),
            domain.getClienteId(),
            domain.getAutoId(),
            domain.getFechaReserva(),
            domain.getFechaInicio(),
            domain.getFechaFin(),
            domain.getCantidadDias(),
            domain.getValorTotal(),
            domain.getKilometrajeInicial(),
            domain.getKilometrajeFinal(),
            domain.getEstado()
        );
    }
}
