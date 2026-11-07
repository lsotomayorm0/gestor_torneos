package pe.edu.unsa.gestortorneos.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unsa.gestortorneos.domain.exception.ResourceNotFoundException;
import pe.edu.unsa.gestortorneos.domain.model.Player;
import pe.edu.unsa.gestortorneos.domain.model.Team;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlayerServiceIntegrationTest {
    @Autowired private PlayerService players;
    @Autowired private TeamService teams;

    @Test
    void registersAndReadsPlayersUsingRealRepositories() {
        Team home = teams.create("Halcones", "Arequipa");
        Team away = teams.create("Pumas", "Cusco");
        players.register("Luis Soto", 16, away.getId());

        Player saved = players.register("  Ana Ruiz  ", 22, home.getId());

        assertNotNull(saved.getId());
        Player found = players.findById(saved.getId());
        assertEquals("Ana Ruiz", found.getFullName());
        assertEquals(22, found.getAge());
        assertEquals(home.getId(), found.getTeamId());
        assertEquals(java.util.List.of(saved.getId()),
            players.findByTeamId(home.getId()).stream().map(Player::getId).toList());
    }

    @Test
    void refusesToRegisterForAnUnknownTeam() {
        assertThrows(ResourceNotFoundException.class,
            () -> players.register("Ana Ruiz", 22, Long.MAX_VALUE));
    }
}
