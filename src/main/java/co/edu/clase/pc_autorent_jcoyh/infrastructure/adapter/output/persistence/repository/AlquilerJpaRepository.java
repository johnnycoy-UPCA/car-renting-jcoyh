package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.repository;

import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.entity.AlquilerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlquilerJpaRepository extends JpaRepository<AlquilerEntity, Long> {
    // Hereda automáticamente .save(), .findById() y todas las funciones nativas de JPA
}
