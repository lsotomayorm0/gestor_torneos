package pe.edu.unsa.gestortorneos.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import pe.edu.unsa.gestortorneos.domain.exception.DomainException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @ParameterizedTest
    @ValueSource(ints = {16, 60})
    void acceptsAgeLimitsAndTrimsName(int age) {
        Player player = new Player("  Ana Ruiz  ", age, 20L);

        assertAll(
            () -> assertNull(player.getId()),
            () -> assertEquals("Ana Ruiz", player.getFullName()),
            () -> assertEquals(age, player.getAge()),
            () -> assertEquals(20L, player.getTeamId())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {15, 61})
    void rejectsAgeOutsideAllowedRange(int age) {
        assertThrows(DomainException.class, () -> new Player("Ana Ruiz", age, 20L));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsMissingOrBlankName(String name) {
        assertThrows(DomainException.class, () -> new Player(name, 22, 20L));
    }

    @Test
    void rejectsMissingTeamId() {
        assertThrows(DomainException.class, () -> new Player("Ana Ruiz", 22, null));
    }
}
