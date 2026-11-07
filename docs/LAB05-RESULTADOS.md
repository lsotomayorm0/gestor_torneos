# Laboratorio 05 · Resultados de ejecución y cobertura

**Proyecto** Gestor de torneos  
**Módulo** Inscripción de equipos en torneos  
**Entorno** Java 21, Maven, JUnit Jupiter, Mockito, Spring Boot y H2  
**Comando** `.\mvnw.cmd clean verify`  
**Ejecución final** 2026-11-06T23:17:55-05:00 (01:46 min)

## Resultados de Surefire

| Suite | Ejecutadas | Fallos | Errores | Omitidas | Estado |
| --- | ---: | ---: | ---: | ---: | --- |
| `RegistrationServiceIntegrationTest` | 1 | 0 | 0 | 0 | Pasó |
| `RegistrationServiceTest` | 8 | 0 | 0 | 0 | Pasó |
| `RegistrationTest` | 4 | 0 | 0 | 0 | Pasó |
| `TournamentRegistrationTest` | 3 | 0 | 0 | 0 | Pasó |
| `GestortorneosApplicationTests` | 1 | 0 | 0 | 0 | Pasó |
| `RegistrationRepositoryAdapterTest` | 3 | 0 | 0 | 0 | Pasó |
| `RegistrationRepositoryIntegrationTest` | 2 | 0 | 0 | 0 | Pasó |
| **Total** | **22** | **0** | **0** | **0** | **Pasó** |

Se añadieron 21 ejecuciones de pruebas. El total incluye la prueba de arranque preexistente, que ahora usa H2 en memoria con el perfil `test`.

## Comprobación de regresión

Se retiró temporalmente la restricción que impide inscribirse cuando el torneo deja de estar planificado. La prueba `onlyPlannedTournamentsAcceptRegistrations` falló porque esperaba una `BusinessConflictException`. Se restauró la regla y luego se ejecutó la suite completa con éxito. El registro de control y el registro final se incluyen por separado en el ZIP de evidencia.

## Cobertura global de JaCoCo

| Métrica | Cubierto | Total | Cobertura |
| --- | ---: | ---: | ---: |
| Instrucciones | 609 | 1260 | 48.3% |
| Ramas | 25 | 48 | 52.1% |
| Líneas | 115 | 192 | 59.9% |
| Métodos | 70 | 131 | 53.4% |
| Clases | 29 | 39 | 74.4% |

### Líneas cubiertas en clases del flujo de inscripción

| Clase | Cubiertas | Total | Cobertura |
| --- | ---: | ---: | ---: |
| `Registration` | 14 | 14 | 100.0% |
| `RegistrationRepositoryAdapter` | 4 | 4 | 100.0% |
| `RegistrationService` | 12 | 12 | 100.0% |
| `Tournament` | 27 | 27 | 100.0% |

La cobertura global incluye las capas y módulos ajenos al flujo elegido. El reporte navegable permite revisar el detalle por clase. Tras extraer el ZIP de evidencia, abre `jacoco/index.html`.
