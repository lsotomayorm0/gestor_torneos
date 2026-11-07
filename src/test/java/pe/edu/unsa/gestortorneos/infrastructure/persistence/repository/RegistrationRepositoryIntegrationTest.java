package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.unsa.gestortorneos.domain.model.Registration;
import pe.edu.unsa.gestortorneos.domain.repository.RegistrationRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(RegistrationRepositoryAdapter.class)
class RegistrationRepositoryIntegrationTest {
    private static final LocalDateTime REGISTERED_AT = LocalDateTime.of(2026, 11, 6, 10, 30);

    @Autowired private RegistrationRepository registrations;
    @Autowired private JpaRegistrationRepository jpa;
    @Autowired private EntityManager entityManager;

    @Test
    void persistsRegistrationAndFiltersByTournamentAndTeam() {
        Registration first = registrations.save(new Registration(10L, 20L, REGISTERED_AT));
        Registration second = registrations.save(new Registration(10L, 21L, REGISTERED_AT));
        registrations.save(new Registration(11L, 20L, REGISTERED_AT));
        entityManager.flush();
        entityManager.clear();

        List<Registration> results = registrations.findByTournamentId(10L);

        assertAll(
            () -> assertNotNull(first.getId()),
            () -> assertEquals(2, results.size()),
            () -> assertTrue(results.stream().anyMatch(r -> r.getId().equals(first.getId())
                && r.getTournamentId().equals(10L) && r.getTeamId().equals(20L)
                && r.getRegisteredAt().equals(REGISTERED_AT))),
            () -> assertTrue(results.stream().anyMatch(r -> r.getId().equals(second.getId())
                && r.getTournamentId().equals(10L) && r.getTeamId().equals(21L))),
            () -> assertTrue(registrations.existsByTournamentIdAndTeamId(10L, 20L)),
            () -> assertFalse(registrations.existsByTournamentIdAndTeamId(11L, 21L)),
            () -> assertTrue(registrations.findByTournamentId(99L).isEmpty())
        );
    }

    @Test
    void databaseRejectsDuplicateTournamentAndTeam() {
        registrations.save(new Registration(10L, 20L, REGISTERED_AT));
        entityManager.flush();

        assertThrows(DataIntegrityViolationException.class, () -> {
            registrations.save(new Registration(10L, 20L, REGISTERED_AT.plusMinutes(1)));
            jpa.flush();
        });
    }
}
