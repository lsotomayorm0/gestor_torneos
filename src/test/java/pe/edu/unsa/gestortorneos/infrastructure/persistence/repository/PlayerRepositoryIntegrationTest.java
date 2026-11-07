package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.unsa.gestortorneos.domain.model.Player;
import pe.edu.unsa.gestortorneos.domain.repository.PlayerRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(PlayerRepositoryAdapter.class)
class PlayerRepositoryIntegrationTest {
    @Autowired private PlayerRepository players;
    @Autowired private EntityManager entityManager;

    @Test
    void persistsAndRecoversPlayerFieldsById() {
        Player saved = players.save(new Player("Ana Ruiz", 22, 20L));
        entityManager.flush();
        entityManager.clear();

        Player found = players.findById(saved.getId()).orElseThrow();

        assertAll(
            () -> assertNotNull(saved.getId()),
            () -> assertEquals(saved.getId(), found.getId()),
            () -> assertEquals("Ana Ruiz", found.getFullName()),
            () -> assertEquals(22, found.getAge()),
            () -> assertEquals(20L, found.getTeamId())
        );
    }

    @Test
    void filtersOutPlayersFromOtherTeams() {
        Player ana = players.save(new Player("Ana Ruiz", 22, 20L));
        Player rosa = players.save(new Player("Rosa Vega", 60, 20L));
        players.save(new Player("Luis Soto", 16, 21L));
        entityManager.flush();
        entityManager.clear();

        List<Long> foundIds = players.findByTeamId(20L).stream().map(Player::getId).sorted().toList();

        assertEquals(List.of(ana.getId(), rosa.getId()).stream().sorted().toList(), foundIds);
        assertTrue(players.findByTeamId(99L).isEmpty());
    }
}
