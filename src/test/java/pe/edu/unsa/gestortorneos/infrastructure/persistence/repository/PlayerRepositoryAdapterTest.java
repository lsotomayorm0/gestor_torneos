package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsa.gestortorneos.domain.model.Player;
import pe.edu.unsa.gestortorneos.infrastructure.persistence.entity.PlayerEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerRepositoryAdapterTest {
    @Mock private JpaPlayerRepository jpa;
    @InjectMocks private PlayerRepositoryAdapter players;

    @Test
    void mapsPlayerToJpaAndReturnsGeneratedId() {
        when(jpa.save(any(PlayerEntity.class))).thenReturn(new PlayerEntity(42L, "Ana Ruiz", 22, 20L));

        Player result = players.save(new Player("Ana Ruiz", 22, 20L));

        ArgumentCaptor<PlayerEntity> submitted = ArgumentCaptor.forClass(PlayerEntity.class);
        verify(jpa).save(submitted.capture());
        PlayerEntity entity = submitted.getValue();
        assertAll(
            () -> assertNull(entity.id),
            () -> assertEquals("Ana Ruiz", entity.fullName),
            () -> assertEquals(22, entity.age),
            () -> assertEquals(20L, entity.teamId),
            () -> assertEquals(42L, result.getId()),
            () -> assertEquals("Ana Ruiz", result.getFullName()),
            () -> assertEquals(22, result.getAge()),
            () -> assertEquals(20L, result.getTeamId())
        );
    }

    @Test
    void returnsEmptyWhenJpaDoesNotFindPlayer() {
        when(jpa.findById(42L)).thenReturn(Optional.empty());

        assertTrue(players.findById(42L).isEmpty());
    }

    @Test
    void mapsTheRowsForTheRequestedTeam() {
        when(jpa.findByTeamId(20L)).thenReturn(List.of(new PlayerEntity(42L, "Ana Ruiz", 22, 20L)));

        List<Player> result = players.findByTeamId(20L);

        assertEquals(1, result.size());
        assertEquals(42L, result.getFirst().getId());
        assertEquals(20L, result.getFirst().getTeamId());
    }
}
