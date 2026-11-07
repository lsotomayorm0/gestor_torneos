package pe.edu.unsa.gestortorneos.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.unsa.gestortorneos.domain.model.Tournament;
import pe.edu.unsa.gestortorneos.domain.model.TournamentStatus;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TournamentRepositoryAdapter.class)
@DisplayName("Pruebas de Repositorio - TournamentRepositoryAdapter (H2)")
class TournamentRepositoryAdapterTest {

    @Autowired
    private TournamentRepositoryAdapter tournamentRepository;

    @Test
    @DisplayName("save() debe persistir un nuevo torneo y asignar un ID autogenerado")
    void shouldSaveNewTournament() {
        // Arrange
        Tournament tournament = new Tournament("Torneo Apertura", LocalDate.of(2027, 3, 1), LocalDate.of(2027, 3, 31));

        // Act
        Tournament saved = tournamentRepository.save(tournament);

        // Assert
        assertNotNull(saved.getId(), "El ID autogenerado no debe ser nulo");
        assertEquals("Torneo Apertura", saved.getName());
        assertEquals(LocalDate.of(2027, 3, 1), saved.getStartDate());
        assertEquals(LocalDate.of(2027, 3, 31), saved.getEndDate());
        assertEquals(TournamentStatus.PLANNED, saved.getStatus());
    }

    @Test
    @DisplayName("save() debe actualizar un torneo existente")
    void shouldUpdateExistingTournament() {
        // Arrange
        Tournament tournament = tournamentRepository.save(
            new Tournament("Copa Verano", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31))
        );
        Long id = tournament.getId();

        // Act
        tournament.activate();
        Tournament updated = tournamentRepository.save(tournament);

        // Assert
        assertEquals(id, updated.getId());
        assertEquals(TournamentStatus.ACTIVE, updated.getStatus());

        Optional<Tournament> found = tournamentRepository.findById(id);
        assertTrue(found.isPresent());
        assertEquals(TournamentStatus.ACTIVE, found.get().getStatus());
    }

    @Test
    @DisplayName("findById() debe retornar el torneo cuando existe")
    void shouldFindByIdWhenExists() {
        // Arrange
        Tournament saved = tournamentRepository.save(
            new Tournament("Copa Oro", LocalDate.of(2027, 5, 1), LocalDate.of(2027, 5, 20))
        );

        // Act
        Optional<Tournament> found = tournamentRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Copa Oro", found.get().getName());
        assertEquals(saved.getStartDate(), found.get().getStartDate());
        assertEquals(saved.getEndDate(), found.get().getEndDate());
        assertEquals(TournamentStatus.PLANNED, found.get().getStatus());
    }

    @Test
    @DisplayName("findById() debe retornar Optional.empty() cuando el ID no existe")
    void shouldReturnEmptyWhenNotFound() {
        // Act
        Optional<Tournament> found = tournamentRepository.findById(9999L);

        // Assert
        assertTrue(found.isEmpty(), "La búsqueda de un ID inexistente debe ser Optional vacía");
    }

    @Test
    @DisplayName("findAll() debe retornar lista vacía si no hay registros")
    void shouldReturnEmptyListWhenNoTournaments() {
        // Act
        List<Tournament> list = tournamentRepository.findAll();

        // Assert
        assertTrue(list.isEmpty(), "Debe retornar lista vacía si no hay torneos");
    }

    @Test
    @DisplayName("findAll() debe retornar todos los torneos guardados")
    void shouldFindAllTournaments() {
        // Arrange
        tournamentRepository.save(new Tournament("Torneo 1", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 10)));
        tournamentRepository.save(new Tournament("Torneo 2", LocalDate.of(2027, 2, 1), LocalDate.of(2027, 2, 10)));

        // Act
        List<Tournament> list = tournamentRepository.findAll();

        // Assert
        assertEquals(2, list.size(), "Debe retornar exactamente los 2 torneos guardados");
    }
}
