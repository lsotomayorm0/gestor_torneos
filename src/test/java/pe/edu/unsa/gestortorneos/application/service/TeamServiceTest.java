package pe.edu.unsa.gestortorneos.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.unsa.gestortorneos.domain.exception.BusinessConflictException;
import pe.edu.unsa.gestortorneos.domain.exception.DomainException;
import pe.edu.unsa.gestortorneos.domain.exception.ResourceNotFoundException;
import pe.edu.unsa.gestortorneos.domain.model.Team;
import pe.edu.unsa.gestortorneos.domain.repository.TeamRepository;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository repository;

    @InjectMocks
    private TeamService service;

    private Team savedTeam;

    @BeforeEach
    void setUp() {
        savedTeam = new Team(1L, "Arequipa FC", "Arequipa");
    }

    @Test
    @DisplayName("S01 create: flujo exitoso guarda y retorna el equipo")
    void s01_create_flujoExitoso() {
        // Arrange
        when(repository.existsByNameIgnoringCaseAndOuterSpaces("Arequipa FC")).thenReturn(false);
        when(repository.save(any(Team.class))).thenReturn(savedTeam);
        // Act
        Team result = service.create("Arequipa FC", "Arequipa");
        // Assert
        assertSame(savedTeam, result);
        verify(repository).existsByNameIgnoringCaseAndOuterSpaces("Arequipa FC");
        verify(repository).save(any(Team.class));
    }

    @Test
    @DisplayName("S02 create: valida duplicado con el nombre recortado y guarda valores recortados")
    void s02_create_recortaAntesDeValidarYGuardar() {
        // Arrange
        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        when(repository.existsByNameIgnoringCaseAndOuterSpaces("Arequipa FC")).thenReturn(false);
        when(repository.save(captor.capture())).thenReturn(savedTeam);
        // Act
        service.create("  Arequipa FC  ", "  Arequipa  ");
        // Assert
        assertEquals("Arequipa FC", captor.getValue().getName());
        assertEquals("Arequipa", captor.getValue().getCity());
        verify(repository).existsByNameIgnoringCaseAndOuterSpaces("Arequipa FC");
    }

    @Test
    @DisplayName("S03 create: nombre duplicado lanza BusinessConflictException y no guarda")
    void s03_create_nombreDuplicado_lanzaConflicto() {
        // Arrange
        when(repository.existsByNameIgnoringCaseAndOuterSpaces("Arequipa FC")).thenReturn(true);
        // Act
        BusinessConflictException ex = assertThrows(BusinessConflictException.class,
                () -> service.create("Arequipa FC", "Arequipa"));
        // Assert
        assertEquals("Ya existe un equipo con ese nombre", ex.getMessage());
        verify(repository, never()).save(any(Team.class));
    }

    @ParameterizedTest(name = "S04 nombre invalido: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void s04_create_nombreInvalido_lanzaDomainExceptionSinTocarRepositorio(String invalidName) {
        // Act + Assert
        assertThrows(DomainException.class, () -> service.create(invalidName, "Arequipa"));
        verifyNoInteractions(repository);
    }

    @ParameterizedTest(name = "S05 ciudad invalida: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void s05_create_ciudadInvalida_lanzaDomainExceptionSinTocarRepositorio(String invalidCity) {
        // Act + Assert
        assertThrows(DomainException.class, () -> service.create("Arequipa FC", invalidCity));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("S06 findById: id existente retorna el equipo")
    void s06_findById_existente() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(savedTeam));
        // Act
        Team result = service.findById(1L);
        // Assert
        assertSame(savedTeam, result);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("S07 findById: id inexistente lanza ResourceNotFoundException")
    void s07_findById_noEncontrado() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());
        // Act
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(99L));
        // Assert
        assertEquals("Equipo no encontrado: 99", ex.getMessage());
    }

    @Test
    @DisplayName("S08 findAll: retorna los equipos del repositorio")
    void s08_findAll_conEquipos() {
        // Arrange
        List<Team> teams = List.of(savedTeam, new Team(2L, "Cusco FC", "Cusco"));
        when(repository.findAll()).thenReturn(teams);
        // Act
        List<Team> result = service.findAll();
        // Assert
        assertEquals(2, result.size());
        assertSame(teams, result);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("S09 findAll: sin equipos retorna lista vacia")
    void s09_findAll_vacio() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());
        // Act
        List<Team> result = service.findAll();
        // Assert
        assertTrue(result.isEmpty());
    }
}
