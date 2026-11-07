# Casos de prueba — Módulo Equipos (Team) — Laboratorio 05

## Dominio (TeamTest, JUnit 5)
| ID | Clase/Método | Caso | Valores | Resultado esperado |
|----|--------------|------|---------|--------------------|
| D01 | Team(name, city) | Crear equipo válido | "Arequipa FC", "Arequipa" | id null; name y city iguales |
| D02 | Team(id, name, city) | Crear con id | 7L, "Arequipa FC", "Arequipa" | id = 7 |
| D03 | Team(...) | Recorta espacios exteriores | "  Arequipa FC  ", "  Arequipa  " | "Arequipa FC", "Arequipa" |
| D04 | Team(...) | Conserva espacios internos | "Real   Madrid" | "Real   Madrid" |
| D05 | Team(...) | Nombre inválido | null, "", " ", "   ", "\t" | DomainException "El nombre del equipo es obligatorio" |
| D06 | Team(...) | Ciudad inválida | null, "", " ", "   ", "\t" | DomainException "La ciudad es obligatoria" |
| D07 | Team(...) | Nombre y ciudad inválidos | null, null | DomainException del nombre (se valida primero) |
| D08 | Team(id, ...) | Id presente y nombre nulo | 5L, null, "Arequipa" | DomainException del nombre |

## Servicio (TeamServiceTest, Mockito)
| ID | Clase/Método | Caso | Valores | Resultado esperado |
|----|--------------|------|---------|--------------------|
| S01 | TeamService.create | Flujo exitoso | "Arequipa FC", "Arequipa"; exists=false | Retorna equipo guardado; verify exists y save |
| S02 | TeamService.create | Recorte antes de validar y guardar | "  Arequipa FC  ", "  Arequipa  " | exists("Arequipa FC"); save con valores recortados |
| S03 | TeamService.create | Nombre duplicado | exists=true | BusinessConflictException; save nunca invocado |
| S04 | TeamService.create | Nombre inválido | null, "", " ", "\t" | DomainException; repositorio sin interacciones |
| S05 | TeamService.create | Ciudad inválida | null, "", " ", "\t" | DomainException; repositorio sin interacciones |
| S06 | TeamService.findById | Id existente | 1L | Retorna el equipo |
| S07 | TeamService.findById | Id inexistente | 99L | ResourceNotFoundException "Equipo no encontrado: 99" |
| S08 | TeamService.findAll | Con equipos | 2 equipos | Lista de 2 |
| S09 | TeamService.findAll | Sin equipos | lista vacía | Lista vacía |

## Repositorio (TeamRepositoryAdapterTest, @DataJpaTest + H2)
| ID | Clase/Método | Caso | Valores | Resultado esperado |
|----|--------------|------|---------|--------------------|
| R01 | save | Guardar equipo nuevo | "Arequipa FC", "Arequipa" | id asignado; datos conservados |
| R02 | findById | Id existente | id guardado | Optional con el equipo |
| R03 | findById | Id inexistente | 9999L | Optional vacío |
| R04 | findAll | Sin datos | — | Lista vacía (aislamiento) |
| R05 | findAll | Con datos | 2 equipos | Lista de 2 |
| R06 | existsByNameIgnoringCaseAndOuterSpaces | Mismo nombre | "Arequipa FC" | true |
| R07 | existsByNameIgnoringCaseAndOuterSpaces | Ignora mayúsculas y espacios | "  AREQUIPA fc  " | true |
| R08 | existsByNameIgnoringCaseAndOuterSpaces | Nombre inexistente | "Cusco FC" | false |
| R09 | save | Nombre normalizado repetido | "Arequipa FC" y "  arequipa fc  " | DataIntegrityViolationException (índice único) |
| R10 | existsByNameIgnoringCaseAndOuterSpaces | Nombre nulo (TDD) | null | false, sin excepción |

## Ciclo TDD (R10)
- RED: R10 falla con NullPointerException (normalize hace name.trim()).
- GREEN: se agrega `if (name == null) return false;` en TeamRepositoryAdapter.
- REFACTOR: la guarda se simplifica a una sola expresión; todas las pruebas siguen en verde.
