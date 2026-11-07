package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsa.gestortorneos.domain.model.Registration;
import pe.edu.unsa.gestortorneos.infrastructure.persistence.entity.RegistrationEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationRepositoryAdapterTest {
    private static final LocalDateTime REGISTERED_AT = LocalDateTime.of(2026, 11, 6, 10, 30);

    @Mock private JpaRegistrationRepository jpa;
    @InjectMocks private RegistrationRepositoryAdapter adapter;

    @Test
    void mapsRegistrationToJpaAndReturnsGeneratedId() {
        when(jpa.save(any(RegistrationEntity.class)))
            .thenReturn(new RegistrationEntity(42L, 10L, 20L, REGISTERED_AT));

        Registration result = adapter.save(new Registration(10L, 20L, REGISTERED_AT));

        ArgumentCaptor<RegistrationEntity> submitted = ArgumentCaptor.forClass(RegistrationEntity.class);
        verify(jpa).save(submitted.capture());
        RegistrationEntity entity = submitted.getValue();
        assertAll(
            () -> assertNull(entity.id),
            () -> assertEquals(10L, entity.tournamentId),
            () -> assertEquals(20L, entity.teamId),
            () -> assertEquals(REGISTERED_AT, entity.registeredAt),
            () -> assertEquals(42L, result.getId()),
            () -> assertEquals(10L, result.getTournamentId()),
            () -> assertEquals(20L, result.getTeamId()),
            () -> assertEquals(REGISTERED_AT, result.getRegisteredAt())
        );
    }

    @Test
    void queriesTheExactTournamentAndTeamPair() {
        when(jpa.existsByTournamentIdAndTeamId(10L, 20L)).thenReturn(true);

        assertTrue(adapter.existsByTournamentIdAndTeamId(10L, 20L));

        verify(jpa).existsByTournamentIdAndTeamId(10L, 20L);
    }

    @Test
    void mapsTheRowsReturnedForATournament() {
        when(jpa.findByTournamentId(10L))
            .thenReturn(List.of(new RegistrationEntity(42L, 10L, 20L, REGISTERED_AT)));

        List<Registration> result = adapter.findByTournamentId(10L);

        assertEquals(1, result.size());
        Registration registration = result.getFirst();
        assertAll(
            () -> assertEquals(42L, registration.getId()),
            () -> assertEquals(10L, registration.getTournamentId()),
            () -> assertEquals(20L, registration.getTeamId()),
            () -> assertEquals(REGISTERED_AT, registration.getRegisteredAt())
        );
    }
}
