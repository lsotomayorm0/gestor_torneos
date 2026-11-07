package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.edu.unsa.gestortorneos.domain.model.Team;
import pe.edu.unsa.gestortorneos.domain.repository.TeamRepository;
import pe.edu.unsa.gestortorneos.infrastructure.persistence.mapper.TeamMapper;

@Repository
public class TeamRepositoryAdapter implements TeamRepository {
    private final JpaTeamRepository jpa;
    public TeamRepositoryAdapter(JpaTeamRepository jpa) { this.jpa = jpa; }
    public Team save(Team t) { return TeamMapper.toDomain(jpa.save(TeamMapper.toEntity(t))); }
    public Optional<Team> findById(Long id) { return jpa.findById(id).map(TeamMapper::toDomain); }
    public List<Team> findAll() { return jpa.findAll().stream().map(TeamMapper::toDomain).toList(); }
    public boolean existsByNameIgnoringCaseAndOuterSpaces(String name) {
        return name != null && jpa.existsByNormalizedName(TeamMapper.normalize(name));
    }
}
