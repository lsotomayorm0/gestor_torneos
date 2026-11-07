package pe.edu.unsa.gestortorneos.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsa.gestortorneos.domain.exception.BusinessConflictException;
import pe.edu.unsa.gestortorneos.domain.exception.ResourceNotFoundException;
import pe.edu.unsa.gestortorneos.domain.model.Registration;
import pe.edu.unsa.gestortorneos.domain.model.Team;
import pe.edu.unsa.gestortorneos.domain.model.Tournament;
import pe.edu.unsa.gestortorneos.domain.model.TournamentStatus;
import pe.edu.unsa.gestortorneos.domain.repository.RegistrationRepository;
import pe.edu.unsa.gestortorneos.domain.repository.TeamRepository;
import pe.edu.unsa.gestortorneos.domain.repository.TournamentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    private static final long TOURNAMENT_ID = 10L;
    private static final long TEAM_ID = 20L;
    private static final LocalDate START = LocalDate.of(2026, 11, 10);
    private static final LocalDateTime EXPECTED_TIME = LocalDateTime.of(2026, 11, 6, 10, 30);

    @Mock private RegistrationRepository registrations;
    @Mock private TournamentRepository tournaments;
    @Mock private TeamRepository teams;

    private RegistrationService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-06T15:30:00Z"), ZoneId.of("America/Lima"));
        service = new RegistrationService(registrations, tournaments, teams, clock);
    }

    @Test
    void registersAnEligibleTeamUsingTheInjectedClock() {
        givenTournament(TournamentStatus.PLANNED);
        givenTeam();
        Registration saved = new Registration(42L, TOURNAMENT_ID, TEAM_ID, EXPECTED_TIME);
        when(registrations.save(any(Registration.class))).thenReturn(saved);

        Registration result = service.register(TOURNAMENT_ID, TEAM_ID);

        ArgumentCaptor<Registration> submitted = ArgumentCaptor.forClass(Registration.class);
        InOrder order = inOrder(registrations);
        order.verify(registrations).existsByTournamentIdAndTeamId(TOURNAMENT_ID, TEAM_ID);
        order.verify(registrations).save(submitted.capture());
        assertAll(
            () -> assertSame(saved, result),
            () -> assertNull(submitted.getValue().getId()),
            () -> assertEquals(TOURNAMENT_ID, submitted.getValue().getTournamentId()),
            () -> assertEquals(TEAM_ID, submitted.getValue().getTeamId()),
            () -> assertEquals(EXPECTED_TIME, submitted.getValue().getRegisteredAt())
        );
    }

    @Test
    void rejectsRegistrationForAnUnknownTournament() {
        when(tournaments.findById(TOURNAMENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.register(TOURNAMENT_ID, TEAM_ID));

        verifyNoInteractions(teams, registrations);
    }

    @Test
    void rejectsRegistrationForAnUnknownTeam() {
        givenTournament(TournamentStatus.PLANNED);
        when(teams.findById(TEAM_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.register(TOURNAMENT_ID, TEAM_ID));

        verifyNoInteractions(registrations);
    }

    @ParameterizedTest
    @EnumSource(value = TournamentStatus.class, names = {"ACTIVE", "FINISHED"})
    void rejectsRegistrationWhenTournamentIsClosed(TournamentStatus status) {
        givenTournament(status);
        givenTeam();

        assertThrows(BusinessConflictException.class, () -> service.register(TOURNAMENT_ID, TEAM_ID));

        verifyNoInteractions(registrations);
    }

    @Test
    void rejectsDuplicateWithoutSaving() {
        givenTournament(TournamentStatus.PLANNED);
        givenTeam();
        when(registrations.existsByTournamentIdAndTeamId(TOURNAMENT_ID, TEAM_ID)).thenReturn(true);

        assertThrows(BusinessConflictException.class, () -> service.register(TOURNAMENT_ID, TEAM_ID));

        verify(registrations, never()).save(any(Registration.class));
    }

    @Test
    void listsRegistrationsForAnExistingTournament() {
        givenTournament(TournamentStatus.PLANNED);
        List<Registration> expected = List.of(new Registration(42L, TOURNAMENT_ID, TEAM_ID, EXPECTED_TIME));
        when(registrations.findByTournamentId(TOURNAMENT_ID)).thenReturn(expected);

        assertEquals(expected, service.findByTournamentId(TOURNAMENT_ID));

        verify(registrations).findByTournamentId(TOURNAMENT_ID);
    }

    @Test
    void rejectsListingForAnUnknownTournament() {
        when(tournaments.findById(TOURNAMENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByTournamentId(TOURNAMENT_ID));

        verifyNoInteractions(registrations);
    }

    private void givenTournament(TournamentStatus status) {
        Tournament tournament = new Tournament(TOURNAMENT_ID, "Copa UNSA", START, START.plusDays(2), status);
        when(tournaments.findById(TOURNAMENT_ID)).thenReturn(Optional.of(tournament));
    }

    private void givenTeam() {
        when(teams.findById(TEAM_ID)).thenReturn(Optional.of(new Team(TEAM_ID, "Halcones", "Arequipa")));
    }
}
