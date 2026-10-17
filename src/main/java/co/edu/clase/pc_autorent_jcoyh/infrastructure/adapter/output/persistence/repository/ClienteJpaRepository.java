package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.repository;

import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    List<ClienteEntity> findByDocumento(Long documento);
}
