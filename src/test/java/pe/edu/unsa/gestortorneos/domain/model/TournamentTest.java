package pe.edu.unsa.gestortorneos.domain.model;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.unsa.gestortorneos.domain.exception.BusinessConflictException;
import pe.edu.unsa.gestortorneos.domain.exception.DomainException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Dominio - Tournament")
class TournamentTest {

    @Test
    @DisplayName("Debe crear un torneo correctamente con estado PLANNED por defecto")
    void shouldCreateTournamentWhenDataIsValid() {
        // Arrange
        String name = "Copa UNSA 2027";
        LocalDate startDate = LocalDate.of(2027, 3, 1);
        LocalDate endDate = LocalDate.of(2027, 3, 31);

        // Act
        Tournament tournament = new Tournament(name, startDate, endDate);

        // Assert
        assertNull(tournament.getId(), "El ID debe ser nulo antes de persistir");
        assertEquals("Copa UNSA 2027", tournament.getName());
        assertEquals(startDate, tournament.getStartDate());
        assertEquals(endDate, tournament.getEndDate());
        assertEquals(TournamentStatus.PLANNED, tournament.getStatus(), "El estado inicial debe ser PLANNED");
    }

    @Test
    @DisplayName("Debe recortar los espacios exteriores del nombre del torneo")
    void shouldTrimTournamentName() {
        // Arrange
        String rawName = "   Torneo Interfacultades   ";
        LocalDate startDate = LocalDate.of(2027, 5, 1);
        LocalDate endDate = LocalDate.of(2027, 5, 15);

        // Act
        Tournament tournament = new Tournament(rawName, startDate, endDate);

        // Assert
        assertEquals("Torneo Interfacultades", tournament.getName(), "El nombre debe estar recortado sin espacios externos");
    }

    @Test
    @DisplayName("Debe lanzar DomainException cuando el nombre es nulo")
    void shouldThrowDomainExceptionWhenNameIsNull() {
        // Arrange
        LocalDate startDate = LocalDate.of(2027, 4, 1);
        LocalDate endDate = LocalDate.of(2027, 4, 30);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
            new Tournament(null, startDate, endDate)
        );
        assertEquals("El nombre del torneo es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar DomainException cuando el nombre está vacío o en blanco")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        // Arrange
        LocalDate startDate = LocalDate.of(2027, 4, 1);
        LocalDate endDate = LocalDate.of(2027, 4, 30);

        // Act & Assert
        DomainException exceptionEmpty = assertThrows(DomainException.class, () ->
            new Tournament("", startDate, endDate)
        );
        assertEquals("El nombre del torneo es obligatorio", exceptionEmpty.getMessage());

        DomainException exceptionSpaces = assertThrows(DomainException.class, () ->
            new Tournament("    ", startDate, endDate)
        );
        assertEquals("El nombre del torneo es obligatorio", exceptionSpaces.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar DomainException cuando alguna fecha es nula")
    void shouldThrowDomainExceptionWhenDatesAreNull() {
        // Arrange
        LocalDate validDate = LocalDate.of(2027, 6, 1);

        // Act & Assert (startDate null)
        DomainException ex1 = assertThrows(DomainException.class, () ->
            new Tournament("Torneo Clausura", null, validDate)
        );
        assertEquals("Las fechas del torneo son obligatorias", ex1.getMessage());

        // Act & Assert (endDate null)
        DomainException ex2 = assertThrows(DomainException.class, () ->
            new Tournament("Torneo Clausura", validDate, null)
        );
        assertEquals("Las fechas del torneo son obligatorias", ex2.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar DomainException cuando la fecha final es anterior a la fecha inicial")
    void shouldThrowDomainExceptionWhenEndDateIsBeforeStartDate() {
        // Arrange
        LocalDate startDate = LocalDate.of(2027, 7, 20);
        LocalDate endDate = LocalDate.of(2027, 7, 10);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
            new Tournament("Torneo Invierno", startDate, endDate)
        );
        assertEquals("La fecha final no puede ser anterior a la inicial", exception.getMessage());
    }

    @Test
    @DisplayName("Debe permitir que la fecha de inicio y fin sean el mismo día")
    void shouldAllowTournamentWhenStartAndEndDateAreSameDay() {
        // Arrange
        LocalDate singleDay = LocalDate.of(2027, 8, 15);

        // Act
        Tournament tournament = new Tournament("Torneo Relámpago", singleDay, singleDay);

        // Assert
        assertEquals(singleDay, tournament.getStartDate());
        assertEquals(singleDay, tournament.getEndDate());
    }

    @Test
    @DisplayName("Debe lanzar DomainException cuando el estado es nulo en el constructor completo")
    void shouldThrowDomainExceptionWhenStatusIsNullInFullConstructor() {
        // Arrange
        LocalDate start = LocalDate.of(2027, 9, 1);
        LocalDate end = LocalDate.of(2027, 9, 30);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
            new Tournament(1L, "Liga Primavera", start, end, null)
        );
        assertEquals("El estado es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("Debe transicionar de PLANNED a ACTIVE al invocar activate()")
    void shouldActivatePlannedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));

        // Act
        tournament.activate();

        // Assert
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus(), "El estado debe cambiar a ACTIVE");
    }

    @Test
    @DisplayName("Debe lanzar BusinessConflictException al intentar activar un torneo ya ACTIVE")
    void shouldThrowBusinessConflictExceptionWhenActivatingAlreadyActiveTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        tournament.activate();

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, tournament::activate);
        assertEquals("Solo se puede activar un torneo planificado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar BusinessConflictException al intentar activar un torneo FINISHED")
    void shouldThrowBusinessConflictExceptionWhenActivatingFinishedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        tournament.activate();
        tournament.finish();

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, tournament::activate);
        assertEquals("Solo se puede activar un torneo planificado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe transicionar de ACTIVE a FINISHED al invocar finish()")
    void shouldFinishActiveTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        tournament.activate();

        // Act
        tournament.finish();

        // Assert
        assertEquals(TournamentStatus.FINISHED, tournament.getStatus(), "El estado debe cambiar a FINISHED");
    }

    @Test
    @DisplayName("Debe lanzar BusinessConflictException al intentar finalizar un torneo en estado PLANNED")
    void shouldThrowBusinessConflictExceptionWhenFinishingPlannedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, tournament::finish);
        assertEquals("Solo se puede finalizar un torneo activo", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar BusinessConflictException al intentar finalizar un torneo ya FINISHED")
    void shouldThrowBusinessConflictExceptionWhenFinishingAlreadyFinishedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        tournament.activate();
        tournament.finish();

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, tournament::finish);
        assertEquals("Solo se puede finalizar un torneo activo", exception.getMessage());
    }

    @Test
    @DisplayName("ensureRegistrationOpen() no debe lanzar excepción cuando el torneo está PLANNED")
    void shouldNotThrowWhenEnsuringRegistrationOpenForPlannedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));

        // Act & Assert
        assertDoesNotThrow(tournament::ensureRegistrationOpen, "Las inscripciones deben estar abiertas para torneos en PLANNED");
    }

    @Test
    @DisplayName("ensureRegistrationOpen() debe lanzar BusinessConflictException cuando el torneo está ACTIVE")
    void shouldThrowBusinessConflictExceptionWhenEnsuringRegistrationOpenForActiveTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        tournament.activate();

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, tournament::ensureRegistrationOpen);
        assertEquals("Solo se permiten inscripciones en torneos planificados", exception.getMessage());
    }

    @Test
    @DisplayName("ensureRegistrationOpen() debe lanzar BusinessConflictException cuando el torneo está FINISHED")
    void shouldThrowBusinessConflictExceptionWhenEnsuringRegistrationOpenForFinishedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Copa Universitaria", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31));
        tournament.activate();
        tournament.finish();

        // Act & Assert
        BusinessConflictException exception = assertThrows(BusinessConflictException.class, tournament::ensureRegistrationOpen);
        assertEquals("Solo se permiten inscripciones en torneos planificados", exception.getMessage());
    }
}
