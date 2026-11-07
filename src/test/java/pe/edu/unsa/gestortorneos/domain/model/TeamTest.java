package pe.edu.unsa.gestortorneos.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import pe.edu.unsa.gestortorneos.domain.exception.DomainException;

class TeamTest {

    private static final String NAME_REQUIRED = "El nombre del equipo es obligatorio";
    private static final String CITY_REQUIRED = "La ciudad es obligatoria";

    private String validName;
    private String validCity;

    @BeforeEach
    void setUp() {
        validName = "Arequipa FC";
        validCity = "Arequipa";
    }

    @Test
    @DisplayName("D01 Constructor (name, city) con valores validos")
    void d01_constructorValido_creaEquipoSinId() {
        // Arrange (setUp)
        // Act
        Team team = new Team(validName, validCity);
        // Assert
        assertNull(team.getId());
        assertEquals("Arequipa FC", team.getName());
        assertEquals("Arequipa", team.getCity());
    }

    @Test
    @DisplayName("D02 Constructor (id, name, city) conserva el id")
    void d02_constructorConId_conservaId() {
        // Act
        Team team = new Team(7L, validName, validCity);
        // Assert
        assertEquals(7L, team.getId());
        assertEquals("Arequipa FC", team.getName());
        assertEquals("Arequipa", team.getCity());
    }

    @Test
    @DisplayName("D03 Recorta espacios exteriores de nombre y ciudad")
    void d03_recortaEspaciosExteriores() {
        // Act
        Team team = new Team("  Arequipa FC  ", "  Arequipa  ");
        // Assert
        assertEquals("Arequipa FC", team.getName());
        assertEquals("Arequipa", team.getCity());
    }

    @Test
    @DisplayName("D04 Conserva los espacios internos del nombre")
    void d04_conservaEspaciosInternos() {
        // Act
        Team team = new Team("Real   Madrid", validCity);
        // Assert
        assertEquals("Real   Madrid", team.getName());
    }

    @ParameterizedTest(name = "D05 nombre invalido: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void d05_nombreInvalido_lanzaDomainException(String invalidName) {
        // Act
        DomainException ex = assertThrows(DomainException.class, () -> new Team(invalidName, validCity));
        // Assert
        assertEquals(NAME_REQUIRED, ex.getMessage());
    }

    @ParameterizedTest(name = "D06 ciudad invalida: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void d06_ciudadInvalida_lanzaDomainException(String invalidCity) {
        // Act
        DomainException ex = assertThrows(DomainException.class, () -> new Team(validName, invalidCity));
        // Assert
        assertEquals(CITY_REQUIRED, ex.getMessage());
    }

    @Test
    @DisplayName("D07 Nombre y ciudad invalidos: se valida primero el nombre")
    void d07_ambosInvalidos_reportaPrimeroElNombre() {
        // Act
        DomainException ex = assertThrows(DomainException.class, () -> new Team(null, null));
        // Assert
        assertEquals(NAME_REQUIRED, ex.getMessage());
    }

    @Test
    @DisplayName("D08 Tener id no evita la validacion del nombre")
    void d08_constructorConIdYNombreNulo_lanzaDomainException() {
        // Act
        DomainException ex = assertThrows(DomainException.class, () -> new Team(5L, null, validCity));
        // Assert
        assertEquals(NAME_REQUIRED, ex.getMessage());
    }
}
