# Laboratorio 05 · Resultados de ejecución y cobertura

**Proyecto** Gestor de torneos

**Módulo** Jugadores

**Entorno** Java 21, Maven, JUnit Jupiter, Mockito, Spring Boot y H2

**Comando** `.\mvnw.cmd clean verify`

**Ejecución final** 2026-11-06T23:26:29-05:00 (01:51 min)

## Resultados de Surefire

| Suite | Ejecutadas | Fallos | Errores | Omitidas | Estado |
| --- | ---: | ---: | ---: | ---: | --- |
| `PlayerServiceIntegrationTest` | 2 | 0 | 0 | 0 | Pasó |
| `PlayerServiceTest` | 7 | 0 | 0 | 0 | Pasó |
| `PlayerTest` | 8 | 0 | 0 | 0 | Pasó |
| `GestortorneosApplicationTests` | 1 | 0 | 0 | 0 | Pasó |
| `PlayerRepositoryAdapterTest` | 3 | 0 | 0 | 0 | Pasó |
| `PlayerRepositoryIntegrationTest` | 2 | 0 | 0 | 0 | Pasó |
| **Total** | **23** | **0** | **0** | **0** | **Pasó** |

Se añadieron 22 ejecuciones de pruebas. El total incluye la prueba de arranque preexistente, que ahora usa H2 en memoria con el perfil `test`.

## Comprobación de regresión

Se permitió temporalmente la edad de 15 años. El caso de edad 15 de `rejectsAgeOutsideAllowedRange` falló porque esperaba una `DomainException`. Se restauró el límite original de 16 años y la suite completa pasó. El registro de control y el registro final se incluyen por separado en el ZIP de evidencia.

## Cobertura global de JaCoCo

| Métrica | Cubierto | Total | Cobertura |
| --- | ---: | ---: | ---: |
| Instrucciones | 442 | 1260 | 35.1% |
| Ramas | 15 | 48 | 31.2% |
| Líneas | 76 | 192 | 39.6% |
| Métodos | 54 | 131 | 41.2% |
| Clases | 26 | 39 | 66.7% |

### Líneas cubiertas en clases del módulo Jugadores

| Clase | Cubiertas | Total | Cobertura |
| --- | ---: | ---: | ---: |
| `Player` | 14 | 14 | 100.0% |
| `PlayerMapper` | 2 | 2 | 100.0% |
| `PlayerRepositoryAdapter` | 4 | 4 | 100.0% |
| `PlayerService` | 7 | 7 | 100.0% |

La cobertura global incluye las capas y módulos ajenos al módulo Jugadores. El reporte navegable permite revisar el detalle por clase. Tras extraer el ZIP de evidencia, abre `jacoco/index.html`.
