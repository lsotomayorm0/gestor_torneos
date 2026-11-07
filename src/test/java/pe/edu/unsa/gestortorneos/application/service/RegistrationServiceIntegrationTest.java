package pe.edu.unsa.gestortorneos.application.service;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unsa.gestortorneos.domain.exception.BusinessConflictException;
import pe.edu.unsa.gestortorneos.domain.model.Registration;
import pe.edu.unsa.gestortorneos.domain.model.Team;
import pe.edu.unsa.gestortorneos.domain.model.Tournament;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegistrationServiceIntegrationTest {
    @Autowired private RegistrationService registrations;
    @Autowired private TeamService teams;
    @Autowired private TournamentService tournaments;

    @Test
    void registersAndReadsBackExactlyOneEntryAfterDuplicateAttempt() {
        LocalDate start = LocalDate.of(2026, 11, 10);
        Tournament tournament = tournaments.create("Copa UNSA", start, start.plusDays(2));
        Team team = teams.create("Halcones", "Arequipa");

        Registration saved = registrations.register(tournament.getId(), team.getId());
        List<Registration> found = registrations.findByTournamentId(tournament.getId());

        assertNotNull(saved.getId());
        assertEquals(1, found.size());
        Registration registration = found.getFirst();
        assertAll(
            () -> assertEquals(saved.getId(), registration.getId()),
            () -> assertEquals(tournament.getId(), registration.getTournamentId()),
            () -> assertEquals(team.getId(), registration.getTeamId()),
            () -> assertNotNull(registration.getRegisteredAt())
        );
        assertThrows(BusinessConflictException.class, () -> registrations.register(tournament.getId(), team.getId()));
        assertEquals(1, registrations.findByTournamentId(tournament.getId()).size());
    }
}
