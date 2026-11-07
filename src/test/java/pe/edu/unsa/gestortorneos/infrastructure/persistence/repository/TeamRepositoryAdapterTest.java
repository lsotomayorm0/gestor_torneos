package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.unsa.gestortorneos.domain.model.Team;

@DataJpaTest
@ActiveProfiles("test")
@Import(TeamRepositoryAdapter.class)
class TeamRepositoryAdapterTest {

    @Autowired
    private TeamRepositoryAdapter repository;

    @Test
    @DisplayName("R01 save: persiste y asigna id conservando nombre y ciudad")
    void r01_save_asignaIdYConservaDatos() {
        // Arrange
        Team team = new Team("Arequipa FC", "Arequipa");
        // Act
        Team saved = repository.save(team);
        // Assert
        assertNotNull(saved.getId());
        assertEquals("Arequipa FC", saved.getName());
        assertEquals("Arequipa", saved.getCity());
    }

    @Test
    @DisplayName("R02 findById: id existente retorna el equipo")
    void r02_findById_existente() {
        // Arrange
        Team saved = repository.save(new Team("Arequipa FC", "Arequipa"));
        // Act
        Optional<Team> found = repository.findById(saved.getId());
        // Assert
        assertTrue(found.isPresent());
        assertEquals("Arequipa FC", found.get().getName());
        assertEquals("Arequipa", found.get().getCity());
    }

    @Test
    @DisplayName("R03 findById: id inexistente retorna vacio")
    void r03_findById_inexistente() {
        // Act
        Optional<Team> found = repository.findById(9999L);
        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("R04 findAll: sin datos retorna lista vacia (aislamiento entre pruebas)")
    void r04_findAll_vacio() {
        // Act
        List<Team> all = repository.findAll();
        // Assert
        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("R05 findAll: retorna todos los equipos guardados")
    void r05_findAll_conDatos() {
        // Arrange
        repository.save(new Team("Arequipa FC", "Arequipa"));
        repository.save(new Team("Cusco FC", "Cusco"));
        // Act
        List<Team> all = repository.findAll();
        // Assert
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("R06 exists: mismo nombre retorna true")
    void r06_exists_mismoNombre() {
        // Arrange
        repository.save(new Team("Arequipa FC", "Arequipa"));
        // Act + Assert
        assertTrue(repository.existsByNameIgnoringCaseAndOuterSpaces("Arequipa FC"));
    }

    @Test
    @DisplayName("R07 exists: ignora mayusculas y espacios exteriores")
    void r07_exists_ignoraMayusculasYEspacios() {
        // Arrange
        repository.save(new Team("Arequipa FC", "Arequipa"));
        // Act + Assert
        assertTrue(repository.existsByNameIgnoringCaseAndOuterSpaces("  AREQUIPA fc  "));
    }

    @Test
    @DisplayName("R08 exists: nombre inexistente retorna false")
    void r08_exists_inexistente() {
        // Arrange
        repository.save(new Team("Arequipa FC", "Arequipa"));
        // Act + Assert
        assertFalse(repository.existsByNameIgnoringCaseAndOuterSpaces("Cusco FC"));
    }

    @Test
    @DisplayName("R09 save: nombre normalizado repetido viola el indice unico")
    void r09_save_nombreNormalizadoDuplicado_violaIndiceUnico() {
        // Arrange
        repository.save(new Team("Arequipa FC", "Arequipa"));
        Team duplicate = new Team("  arequipa fc  ", "Cusco");
        // Act + Assert
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(duplicate));
    }
}
