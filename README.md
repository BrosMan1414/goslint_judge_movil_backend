# Goslint Judge Móvil Backend

Backend Spring Boot para la plataforma de juzgado de problemas de programación orientada a uso móvil. Proporciona:
- Gestión de equipos, maratones e inscripción de equipos a maratones.
- Administración de problemas y envíos (submissions) con resultados.
- Retroalimentaciones (feedback) sobre los envíos (desde juez o IA).
- Notificaciones para los equipos.
- Autenticación básica de equipos (registro + login con hash BCrypt; sin JWT todavía).
- Seed de datos de desarrollo mediante `data.sql`.

> Estado actual: backend funcional con CRUDs principales, endpoints de inscripción, retroalimentaciones y notificaciones. Seguridad abierta (todas las rutas permitidas) salvo hashing de password. Próximo paso sugerido: introducir JWT y migraciones Flyway.

## Stack Tecnológico
- Java 21
- Spring Boot 3 (Web, Data JPA, Validation, Security)
- PostgreSQL
- HikariCP
- Lombok

## Estructura de Paquetes (resumen)
```
controller/          -> Controladores REST
service/ (+impl)     -> Lógica de negocio
repository/          -> Interfaces Spring Data JPA
model/               -> Entidades JPA y enums
dto/request|response -> DTOs para entrada/salida
exception/           -> Manejo centralizado de errores
config/              -> Configuración de seguridad
```

## Preparación del Entorno
1. Asegúrate de tener PostgreSQL ejecutándose y (opcional) un contenedor vía Docker Compose si lo deseas.
2. Crea base de datos (si no existe):
   ```sql
   CREATE DATABASE goslint_judge;
   ```
3. Revisa `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/goslint_judge
       username: postgresql
       password: 1414
     jpa:
       hibernate:
         ddl-auto: update   # cambiar a validate más adelante
       show-sql: true
     sql:
       init:
         mode: always       # ejecuta data.sql (solo dev)
   ```
4. Ejecuta con tu IDE o:
   ```bash
   ./mvnw spring-boot:run
   ```
   (En Windows PowerShell: `mvnw.cmd spring-boot:run`)

## Datos Semilla (data.sql)
`data.sql` inserta 3 registros de cada entidad principal (equipos, maratones, problemas, envíos, retroalimentaciones, notificaciones). Si no quieres el seed en cada arranque cambia `mode: always` a `never`.

## Autenticación
Actualmente:
- Registro: `POST /api/auth/register` (cuerpo: nombre, emailContacto, password)
- Login: `POST /api/auth/login`
- Devuelve un `AuthResponse` con token = null (JWT pendiente).
- Password se almacena en `password_hash` usando BCrypt.

## Endpoints Principales (resumen)
| Recurso | Método | Ruta | Descripción |
|---------|--------|------|-------------|
| Equipos | GET | /api/equipos | Listar equipos |
| Equipos | POST | /api/equipos | Crear equipo (también vía /auth/register) |
| Maratones | POST | /api/maratones | Crear maratón |
| Maratones | POST | /api/maratones/{maratonId}/inscribir/{equipoId} | Inscribir equipo |
| Problemas | POST | /api/problemas | Crear problema |
| Envios | POST | /api/envios | Crear envío |
| Retroalimentaciones | POST | /api/retroalimentaciones | Crear feedback |
| Retroalimentaciones | GET | /api/retroalimentaciones/envio/{envioId} | Listar feedback por envío |
| Notificaciones | POST | /api/notificaciones | Crear notificación |
| Notificaciones | GET | /api/notificaciones/equipo/{equipoId}/noleidas | No leídas |
| Notificaciones | POST | /api/notificaciones/{id}/marcar-leida | Marcar leída |

> También existen endpoints GET/PUT/DELETE estándar en algunas entidades (equipos, problemas, envíos, maratones) según los controladores creados.

## Ejemplos de Prueba (curl)
Ajusta IDs según tu seed actual.

Registrar equipo:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Equipo Test","emailContacto":"test@correo.com","password":"secreto"}'
```

Crear maratón:
```bash
curl -X POST http://localhost:8080/api/maratones \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Maratón Extra","descripcion":"Demo","fechaInicio":"2025-12-01T09:00:00","fechaFin":"2025-12-01T15:00:00"}'
```

Crear problema:
```bash
curl -X POST http://localhost:8080/api/problemas \
  -H "Content-Type: application/json" \
  -d '{"maratonId":1,"titulo":"Suma Simple","enunciado":"Dados dos números...","limiteTiempo":2000,"limiteMemoria":256}'
```

Crear envío:
```bash
curl -X POST http://localhost:8080/api/envios \
  -H "Content-Type: application/json" \
  -d '{"equipoId":1,"problemaId":1,"lenguaje":"java","codigo":"public class Main { public static void main(String[] a){ System.out.println(2+2); } }"}'
```

Crear retroalimentación:
```bash
curl -X POST http://localhost:8080/api/retroalimentaciones \
  -H "Content-Type: application/json" \
  -d '{"envioId":1,"tipo":"ia","comentario":"Buen uso de memoria"}'
```

Crear notificación:
```bash
curl -X POST http://localhost:8080/api/notificaciones \
  -H "Content-Type: application/json" \
  -d '{"equipoId":1,"mensaje":"Tienes un nuevo resultado"}'
```

Listar notificaciones no leídas:
```bash
curl http://localhost:8080/api/notificaciones/equipo/1/noleidas
```

Marcar notificación como leída:
```bash
curl -X POST http://localhost:8080/api/notificaciones/1/marcar-leida
```

## Estrategia de Migraciones (Sugerida)
1. Una vez establecida la estructura, cambiar `ddl-auto` a `validate`.
2. Introducir Flyway:
   - Agregar dependencia `org.flywaydb:flyway-core`.
   - Crear `src/main/resources/db/migration/V1__init.sql` con el DDL.
3. Mover inserts de `data.sql` a `V2__seed.sql` si quieres seed controlado.

## Futuras Mejoras
- JWT (generar token en login y proteger endpoints).
- Roles / autorización granular.
- Paginación y filtros (problemas por maratón, envíos por equipo, etc.).
- Tests unitarios e integración (JUnit + Mockito + Testcontainers).
- Métricas / Actuator.
- Subida de casos de prueba y veredicto automático.

## Troubleshooting Rápido
| Problema | Causa común | Solución |
|----------|-------------|----------|
| 400 validación | Campos faltan en JSON | Revisar DTO correspondiente |
| 404 NotFound | ID inexistente | Verifica que seed o creación previa existe |
| Error enum | Valor no coincide | Usar valor exacto (ej. `ia` / `juez`) |
| Caída al iniciar | Tablas faltantes con ddl-auto=validate | Usar update temporalmente o crear schema manual |

## Licencia
Uso interno / académico (añadir licencia formal si se requiere).

---
**Contacto / Nota:** Este README es un punto de partida; amplíalo con decisiones arquitectónicas y diagramas cuando avances en seguridad y ejecuciones de código.
