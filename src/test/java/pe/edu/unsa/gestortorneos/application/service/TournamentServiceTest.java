package pe.edu.unsa.gestortorneos.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsa.gestortorneos.domain.exception.BusinessConflictException;
import pe.edu.unsa.gestortorneos.domain.exception.ResourceNotFoundException;
import pe.edu.unsa.gestortorneos.domain.model.Tournament;
import pe.edu.unsa.gestortorneos.domain.model.TournamentStatus;
import pe.edu.unsa.gestortorneos.domain.repository.TournamentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de Servicio con Mockito - TournamentService")
class TournamentServiceTest {

    @Mock
    private TournamentRepository repository;

    @InjectMocks
    private TournamentService service;

    @Test
    @DisplayName("create() debe persistir un torneo planificado y retornarlo")
    void shouldCreateTournamentSuccessfully() {
        // Arrange
        String name = "Copa Arequipa";
        LocalDate start = LocalDate.of(2027, 4, 1);
        LocalDate end = LocalDate.of(2027, 4, 30);
        Tournament expectedTournament = new Tournament(1L, name, start, end, TournamentStatus.PLANNED);

        when(repository.save(any(Tournament.class))).thenReturn(expectedTournament);

        // Act
        Tournament result = service.create(name, start, end);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Copa Arequipa", result.getName());
        assertEquals(TournamentStatus.PLANNED, result.getStatus());
        verify(repository, times(1)).save(any(Tournament.class));
    }

    @Test
    @DisplayName("findById() debe retornar el torneo cuando existe")
    void shouldReturnTournamentWhenFound() {
        // Arrange
        Long id = 10L;
        Tournament tournament = new Tournament(id, "Torneo Clausura", LocalDate.of(2027, 8, 1), LocalDate.of(2027, 8, 31), TournamentStatus.PLANNED);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        // Act
        Tournament result = service.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Torneo Clausura", result.getName());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById() debe lanzar ResourceNotFoundException cuando el torneo no existe")
    void shouldThrowResourceNotFoundExceptionWhenTournamentDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            service.findById(nonExistentId)
        );
        assertEquals("Torneo no encontrado: 999", exception.getMessage());
        verify(repository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("findAll() debe retornar la lista de torneos del repositorio")
    void shouldReturnAllTournaments() {
        // Arrange
        Tournament t1 = new Tournament(1L, "Torneo 1", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 15), TournamentStatus.PLANNED);
        Tournament t2 = new Tournament(2L, "Torneo 2", LocalDate.of(2027, 2, 1), LocalDate.of(2027, 2, 15), TournamentStatus.ACTIVE);
        when(repository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<Tournament> result = service.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Torneo 1", result.get(0).getName());
        assertEquals("Torneo 2", result.get(1).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("activate() debe cambiar el estado a ACTIVE y guardar el torneo")
    void shouldActivateTournamentSuccessfully() {
        // Arrange
        Long id = 1L;
        Tournament plannedTournament = new Tournament(id, "Torneo Verano", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31), TournamentStatus.PLANNED);
        when(repository.findById(id)).thenReturn(Optional.of(plannedTournament));
        when(repository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = service.activate(id);

        // Assert
        assertNotNull(result);
        assertEquals(TournamentStatus.ACTIVE, result.getStatus(), "El estado debe haber cambiado a ACTIVE");
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(plannedTournament);
    }

    @Test
    @DisplayName("activate() debe lanzar ResourceNotFoundException y NO guardar si el ID no existe")
    void shouldThrowResourceNotFoundExceptionAndNeverSaveWhenActivatingNonExistentTournament() {
        // Arrange
        Long nonExistentId = 404L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.activate(nonExistentId));
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("activate() debe lanzar BusinessConflictException y NO guardar si el torneo ya está ACTIVE")
    void shouldThrowBusinessConflictExceptionAndNeverSaveWhenActivatingAlreadyActiveTournament() {
        // Arrange
        Long id = 2L;
        Tournament activeTournament = new Tournament(id, "Torneo Activo", LocalDate.of(2027, 2, 1), LocalDate.of(2027, 2, 28), TournamentStatus.ACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(activeTournament));

        // Act & Assert
        assertThrows(BusinessConflictException.class, () -> service.activate(id));
        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("finish() debe cambiar el estado a FINISHED y guardar el torneo cuando está ACTIVE")
    void shouldFinishTournamentSuccessfully() {
        // Arrange
        Long id = 1L;
        Tournament activeTournament = new Tournament(id, "Torneo Otoño", LocalDate.of(2027, 4, 1), LocalDate.of(2027, 4, 30), TournamentStatus.ACTIVE);
        when(repository.findById(id)).thenReturn(Optional.of(activeTournament));
        when(repository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tournament result = service.finish(id);

        // Assert
        assertNotNull(result);
        assertEquals(TournamentStatus.FINISHED, result.getStatus(), "El estado debe haber cambiado a FINISHED");
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(activeTournament);
    }

    @Test
    @DisplayName("finish() debe lanzar BusinessConflictException y NO guardar si el torneo aún está PLANNED")
    void shouldThrowBusinessConflictExceptionAndNeverSaveWhenFinishingPlannedTournament() {
        // Arrange
        Long id = 3L;
        Tournament plannedTournament = new Tournament(id, "Torneo Planificado", LocalDate.of(2027, 3, 1), LocalDate.of(2027, 3, 31), TournamentStatus.PLANNED);
        when(repository.findById(id)).thenReturn(Optional.of(plannedTournament));

        // Act & Assert
        assertThrows(BusinessConflictException.class, () -> service.finish(id));
        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("finish() debe lanzar ResourceNotFoundException y NO guardar si el torneo no existe")
    void shouldThrowResourceNotFoundExceptionAndNeverSaveWhenFinishingNonExistentTournament() {
        // Arrange
        Long nonExistentId = 500L;
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.finish(nonExistentId));
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }
}
