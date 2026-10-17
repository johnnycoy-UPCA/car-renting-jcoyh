package co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.adapter;

import co.edu.clase.pc_autorent_jcoyh.application.port.output.AutoRepositoryPort;
import co.edu.clase.pc_autorent_jcoyh.domain.model.Auto;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.entity.AutoEntity;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.mapper.AutoMapper;
import co.edu.clase.pc_autorent_jcoyh.infrastructure.adapter.output.persistence.repository.AutoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

@Component
public class AutoPersistenceAdapter implements AutoRepositoryPort {

    private final AutoJpaRepository autoJpaRepository;
    private final AutoMapper autoMapper;

    public AutoPersistenceAdapter(AutoJpaRepository autoJpaRepository, AutoMapper autoMapper) {
        this.autoJpaRepository = autoJpaRepository;
        this.autoMapper = autoMapper;
    }

    @Override
    public Auto guardar(Auto auto) {
        AutoEntity entity = autoMapper.toEntity(auto);
        AutoEntity entityGuardada = autoJpaRepository.save(entity);
        return autoMapper.toDomain(entityGuardada);
    }

    @Override
    public Optional<Auto> buscarPorId(Long id) {
        return autoJpaRepository.findById(id).map(autoMapper::toDomain);
    }

    @Override
    public Optional<Auto> buscarPorPlaca(String placa) {
        // return autoJpaRepository.findByPlacaIgnoreCase(placa).map(autoMapper::toDomain);
        return autoJpaRepository.findByPlacaIgnoreCase(placa).stream()
                .map(autoMapper::toDomain)
                .filter(auto -> !"INACTIVO".equalsIgnoreCase(auto.getEstado()))
                .findFirst();
    }

    // Incluído para uso en el Front-End
    public List<Auto> listarTodos() {
        return autoJpaRepository.findAll().stream()
                .map(autoMapper::toDomain)
                .toList();
    }

}
