# Laboratorio 05 · Jugadores

**Integrante** Álvaro

**Módulo** Jugadores

**Identificador** Amarillo

Las pruebas se concentran en el dominio de Jugadores, su repositorio y su servicio de aplicación. El equipo sirve como dato de prueba para comprobar las reglas de registro y filtrado.

## Ejecución y reportes

Con Java 21, ejecuta `.\mvnw.cmd clean verify` desde la raíz en Windows o `./mvnw clean verify` en Linux o macOS. Surefire escribe los resultados por clase en `target/surefire-reports/`. Abre `target/site/jacoco/index.html` para revisar la cobertura de JaCoCo. [Resumen de la ejecución de entrega](LAB05-RESULTADOS.md).

JUnit Jupiter ejecuta las pruebas unitarias y de integración. Mockito simula las interfaces de repositorio y permite verificar sus llamadas. Las pruebas de integración usan el perfil `test` con H2 y revierten las transacciones después de cada caso.

## Casos de prueba

| Capa y método | Caso | Datos | Resultado esperado |
| --- | --- | --- | --- |
| Dominio · constructor de `Player` | Límites válidos y normalización | Edades 16 y 60, nombre con espacios exteriores, equipo 20 | Crea el jugador, recorta el nombre y conserva edad y equipo. |
| Dominio · constructor de `Player` | Límites inválidos | Edades 15 y 61 | Lanza `DomainException`. |
| Dominio · constructor de `Player` | Nombre obligatorio | Nulo, vacío y solo espacios | Lanza `DomainException`. |
| Dominio · constructor de `Player` | Equipo obligatorio | ID de equipo nulo | Lanza `DomainException`. |
| Repositorio · `save` | Mapeo con un doble de JPA | Ana Ruiz, edad 22, equipo 20, ID generado 42 | Envía los campos a JPA y devuelve el jugador con el ID generado. |
| Repositorio · `findById` | Jugador inexistente con un doble de JPA | ID 42 ausente | Devuelve un resultado vacío. |
| Repositorio · `findByTeamId` | Mapeo de filas con un doble de JPA | Equipo 20, jugador 42 | Devuelve el jugador mapeado para el equipo solicitado. |
| Repositorio · `save` y `findById` | Lectura real en H2 | Ana Ruiz, edad 22, equipo 20 | Genera un ID y recupera todos los datos por ese ID. |
| Repositorio · `findByTeamId` | Filtrado real en H2 | Dos jugadores del equipo 20, uno del 21. Consultas por 20 y 99. | Devuelve solo los dos del equipo 20 y una lista vacía para 99. |
| Servicio · `register` | Alta con mocks | Equipo 20 existente, nombre con espacios, edad 22 | Verifica el equipo antes de guardar y envía el jugador validado. |
| Servicio · `register` | Equipo inexistente con mocks | Equipo 20 ausente | Lanza `ResourceNotFoundException` y no guarda. |
| Servicio · `register` | Edad inválida con mocks | Edad 15 | Lanza `DomainException` sin consultar los repositorios. |
| Servicio · `findById` | Búsqueda con mocks | Jugador 42 presente o ausente | Devuelve el jugador o lanza `ResourceNotFoundException`, respectivamente. |
| Servicio · `findByTeamId` | Listado con mocks | Equipo 20 presente o ausente | Consulta los jugadores después de verificar el equipo. Si falta el equipo, lanza `ResourceNotFoundException`. |
| Servicio · `register`, `findById` y `findByTeamId` | Flujo completo con H2 | Jugadores en dos equipos persistidos | Registra y recupera al jugador por ID, y excluye al jugador de otro equipo. |
| Servicio · `register` | Equipo inexistente con H2 | ID `Long.MAX_VALUE` | Rechaza el alta con `ResourceNotFoundException`. |

Para verificar que la prueba de edad detecta una regresión, se permitió temporalmente la edad de 15 años y se ejecutó el caso de edades inválidas. Después se restauró el límite original y se ejecutó la suite completa.

## Seguimiento

La rama `lab05/jugadores` parte de `main`. El [tablero Kanban de Jugadores](https://github.com/users/Yojancs/projects/2/views/2) muestra los [cuatro issues del módulo](https://github.com/Yojancs/gestor_torneos/issues?q=is%3Aissue+label%3AJugadores) con su etiqueta amarilla **Jugadores**. Cada commit referencia el issue correspondiente.
