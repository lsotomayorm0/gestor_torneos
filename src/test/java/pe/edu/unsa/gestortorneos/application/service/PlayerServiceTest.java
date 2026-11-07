package pe.edu.unsa.gestortorneos.application.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsa.gestortorneos.domain.exception.DomainException;
import pe.edu.unsa.gestortorneos.domain.exception.ResourceNotFoundException;
import pe.edu.unsa.gestortorneos.domain.model.Player;
import pe.edu.unsa.gestortorneos.domain.model.Team;
import pe.edu.unsa.gestortorneos.domain.repository.PlayerRepository;
import pe.edu.unsa.gestortorneos.domain.repository.TeamRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {
    @Mock private PlayerRepository players;
    @Mock private TeamRepository teams;
    @InjectMocks private PlayerService service;

    @Test
    void verifiesTeamAndSavesTheValidatedPlayer() {
        when(teams.findById(20L)).thenReturn(Optional.of(new Team(20L, "Halcones", "Arequipa")));
        Player saved = new Player(42L, "Ana Ruiz", 22, 20L);
        when(players.save(any(Player.class))).thenReturn(saved);

        Player result = service.register("  Ana Ruiz  ", 22, 20L);

        ArgumentCaptor<Player> submitted = ArgumentCaptor.forClass(Player.class);
        InOrder order = inOrder(teams, players);
        order.verify(teams).findById(20L);
        order.verify(players).save(submitted.capture());
        Player player = submitted.getValue();
        assertAll(
            () -> assertSame(saved, result),
            () -> assertNull(player.getId()),
            () -> assertEquals("Ana Ruiz", player.getFullName()),
            () -> assertEquals(22, player.getAge()),
            () -> assertEquals(20L, player.getTeamId())
        );
    }

    @Test
    void rejectsUnknownTeamBeforeSaving() {
        when(teams.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.register("Ana Ruiz", 22, 20L));

        verifyNoInteractions(players);
    }

    @Test
    void rejectsInvalidAgeBeforeConsultingRepositories() {
        assertThrows(DomainException.class, () -> service.register("Ana Ruiz", 15, 20L));

        verifyNoInteractions(players, teams);
    }

    @Test
    void returnsExistingPlayer() {
        Player player = new Player(42L, "Ana Ruiz", 22, 20L);
        when(players.findById(42L)).thenReturn(Optional.of(player));

        assertSame(player, service.findById(42L));
    }

    @Test
    void rejectsUnknownPlayer() {
        when(players.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(42L));
    }

    @Test
    void listsPlayersAfterCheckingTeam() {
        List<Player> expected = List.of(new Player(42L, "Ana Ruiz", 22, 20L));
        when(teams.findById(20L)).thenReturn(Optional.of(new Team(20L, "Halcones", "Arequipa")));
        when(players.findByTeamId(20L)).thenReturn(expected);

        assertEquals(expected, service.findByTeamId(20L));

        InOrder order = inOrder(teams, players);
        order.verify(teams).findById(20L);
        order.verify(players).findByTeamId(20L);
    }

    @Test
    void rejectsListingForUnknownTeam() {
        when(teams.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByTeamId(20L));

        verifyNoInteractions(players);
    }
}
