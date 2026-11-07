package pe.edu.unsa.gestortorneos.domain.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pe.edu.unsa.gestortorneos.domain.exception.DomainException;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTest {
    private static final LocalDateTime REGISTERED_AT = LocalDateTime.of(2026, 11, 6, 10, 30);

    @Test
    void preservesTournamentTeamAndRegistrationTime() {
        Registration registration = new Registration(10L, 20L, REGISTERED_AT);

        assertAll(
            () -> assertNull(registration.getId()),
            () -> assertEquals(10L, registration.getTournamentId()),
            () -> assertEquals(20L, registration.getTeamId()),
            () -> assertEquals(REGISTERED_AT, registration.getRegisteredAt())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"null,20", "10,null"}, nullValues = "null")
    void rejectsMissingTournamentOrTeam(Long tournamentId, Long teamId) {
        assertThrows(DomainException.class, () -> new Registration(tournamentId, teamId, REGISTERED_AT));
    }

    @Test
    void rejectsMissingRegistrationTime() {
        assertThrows(DomainException.class, () -> new Registration(10L, 20L, null));
    }
}
