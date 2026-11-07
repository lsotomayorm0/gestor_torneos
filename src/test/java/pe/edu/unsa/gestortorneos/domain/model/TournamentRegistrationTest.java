package pe.edu.unsa.gestortorneos.domain.model;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.unsa.gestortorneos.domain.exception.BusinessConflictException;

import static org.junit.jupiter.api.Assertions.*;

class TournamentRegistrationTest {
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        LocalDate start = LocalDate.of(2026, 11, 10);
        tournament = new Tournament("Copa UNSA", start, start.plusDays(2));
    }

    @Test
    void onlyPlannedTournamentsAcceptRegistrations() {
        assertEquals(TournamentStatus.PLANNED, tournament.getStatus());
        assertDoesNotThrow(tournament::ensureRegistrationOpen);

        tournament.activate();
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
        assertThrows(BusinessConflictException.class, tournament::ensureRegistrationOpen);

        tournament.finish();
        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
        assertThrows(BusinessConflictException.class, tournament::ensureRegistrationOpen);
    }

    @Test
    void refusesToFinishAPlannedTournament() {
        assertThrows(BusinessConflictException.class, tournament::finish);
        assertEquals(TournamentStatus.PLANNED, tournament.getStatus());
    }

    @Test
    void refusesToActivateAnActiveOrFinishedTournament() {
        tournament.activate();
        assertThrows(BusinessConflictException.class, tournament::activate);
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());

        tournament.finish();
        assertThrows(BusinessConflictException.class, tournament::activate);
        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
    }
}
