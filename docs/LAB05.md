# Laboratorio 05 · Pruebas unitarias y dobles

El módulo elegido es **inscripción de equipos en torneos**. Las pruebas cubren sus invariantes de dominio, el adaptador de persistencia y el servicio de aplicación. El proyecto utiliza Java 21, Maven, JUnit Jupiter, Mockito, H2 y JaCoCo. Spring Boot 4.1.1 gestiona las versiones de JUnit y Mockito.

## Ejecución

En Windows, desde la raíz del repositorio:

```powershell
.\mvnw.cmd clean verify
```

En Linux o macOS:

```bash
./mvnw clean verify
```

- Surefire genera los resultados por clase en `target/surefire-reports/`.
- JaCoCo genera el reporte navegable en `target/site/jacoco/index.html`.
- Los totales de la ejecución de entrega están en [el reporte de resultados](LAB05-RESULTADOS.md).
- Las pruebas unitarias ejecutan clases de dominio o colaboradores simulados. Las de integración activan el perfil `test` y revierten las transacciones de cada caso en H2.
- Maven carga el agente de Mockito al iniciar la JVM de pruebas junto al de JaCoCo. Así evita que Mockito tenga que adjuntarse durante la ejecución.

## Especificación de casos

Los ejemplos usan el torneo **10**, el equipo **20** y la fecha **2026-11-06 10:30** cuando se prueban valores fijos. Las pruebas de integración de servicio crean sus propios registros.

### Dominio

| Clase y método | Caso | Valores de prueba | Resultado esperado |
| --- | --- | --- | --- |
| `Registration` constructor | Inscripción válida | 10, 20, fecha fija | IDs y fecha conservados. ID propio nulo antes de persistir. |
| `Registration` constructor | ID obligatorio | (nulo, 20), (10, nulo) | `DomainException` en ambos casos. |
| `Registration` constructor | Fecha obligatoria | 10, 20, fecha nula | `DomainException`. |
| `Tournament.ensureRegistrationOpen` | Inscripción durante el ciclo de vida | `PLANNED → ACTIVE → FINISHED` | Permitida en `PLANNED` y rechazada en `ACTIVE` y `FINISHED`. |
| `Tournament.finish` | Finalización prematura | `PLANNED` | `BusinessConflictException` y estado conservado. |
| `Tournament.activate` | Reactivación | `ACTIVE`, `FINISHED` | `BusinessConflictException` y estado conservado en ambos casos. |

### Repositorio

| Clase y método | Caso | Valores de prueba | Resultado esperado |
| --- | --- | --- | --- |
| `RegistrationRepositoryAdapter.save` | Mapeo de ida y vuelta con mock de JPA | (10, 20), fecha fija, ID generado 42 | JPA recibe los valores de dominio y el adaptador devuelve los datos con ID 42. |
| `RegistrationRepositoryAdapter.existsByTournamentIdAndTeamId` | Delegación con mock | (10, 20) | Se consulta el par exacto y se devuelve `true`. |
| `RegistrationRepositoryAdapter.findByTournamentId` | Mapeo de resultados con mock | Torneo 10, inscripción 42 | Se devuelve una inscripción con ID, torneo, equipo y fecha correctos. |
| `RegistrationRepositoryAdapter.save/findByTournamentId/existsByTournamentIdAndTeamId` | Persistencia y filtros en H2 | (10, 20), (10, 21), (11, 20). Consultas por torneo 10 y 99, y pares (10, 20) y (11, 21). | Se generan IDs, se conservan los datos, el torneo 10 devuelve dos filas y el 99 ninguna. Solo existe el primero de los pares consultados. |
| `RegistrationRepositoryAdapter.save` | Restricción única en H2 | Dos filas (10, 20) con fechas distintas | `DataIntegrityViolationException` en el segundo guardado. |

### Servicio de aplicación

| Clase y método | Caso | Valores de prueba | Resultado esperado |
| --- | --- | --- | --- |
| `RegistrationService.register` | Inscripción válida con mocks y reloj fijo | Torneo planificado, equipo existente, 15:30 UTC en zona Lima | Verifica duplicados antes de guardar y envía las 10:30 locales al repositorio. Devuelve el objeto persistido. |
| `RegistrationService.register` | Torneo inexistente | ID 10 ausente | `ResourceNotFoundException`, sin consultar equipos ni inscripciones. |
| `RegistrationService.register` | Equipo inexistente | Torneo 10 válido, ID 20 ausente | `ResourceNotFoundException`, sin invocar el repositorio de inscripciones. |
| `RegistrationService.register` | Torneo cerrado | `ACTIVE`, `FINISHED` | `BusinessConflictException`, sin invocar el repositorio de inscripciones. |
| `RegistrationService.register` | Equipo ya inscrito | El mock confirma el par (10, 20) | `BusinessConflictException` y cero llamadas a `save`. |
| `RegistrationService.findByTournamentId` | Listado válido | Torneo 10 existente | Devuelve la lista del repositorio para el torneo 10. |
| `RegistrationService.findByTournamentId` | Listado de torneo inexistente | ID 10 ausente | `ResourceNotFoundException`, sin consultar inscripciones. |
| `RegistrationService.register/findByTournamentId` | Integración de servicio, adaptador y H2 | Un torneo planificado y un equipo real, con intento repetido | Se recupera una inscripción persistida. El intento duplicado se rechaza y el total sigue en uno. |

Se usaron datos deterministas y preparación con `@BeforeEach` cuando la inicialización se comparte. Mockito administra el ciclo de vida de sus dobles y Spring revierte los datos de las pruebas de integración.

## Seguimiento en GitHub

La rama de entrega `development` parte de `main`, donde está el código base del gestor. La rama previa `develop` contiene el esqueleto anterior del proyecto.

Flujo del tablero Kanban: **TO-DO → CURRENT ITERATION → IN PROGRESS → FIX VALIDATION → DONE**.

| Tarea para GitHub Issues | Descripción y criterio de cierre | Etiqueta | Duración estimada |
| --- | --- | --- | ---: |
| Probar el dominio de inscripciones | Verificar los datos obligatorios y las transiciones que controlan el período de inscripción. | `unit test` | 45 min |
| Probar el repositorio de inscripciones | Verificar mapeo y delegación con Mockito, y persistencia, filtros y unicidad con H2. | `unit test` | 60 min |
| Probar el servicio de inscripciones | Usar mocks y un reloj fijo para los casos unitarios. Confirmar el flujo completo con H2. | `unit test` | 75 min |
| Validar y generar los reportes | Ejecutar `clean verify` y guardar la evidencia de Surefire y JaCoCo. | `enhancement` | 30 min |

El laboratorio es individual. Las cuatro tareas se asignan a la cuenta responsable del fork y los mensajes de commit se vinculan con los números de los issues mediante `Fix #número`.

[Tablero Kanban](https://github.com/users/Yojancs/projects/1/views/2) · [Issues del laboratorio](https://github.com/Yojancs/gestor_torneos/issues?q=is%3Aissue)
