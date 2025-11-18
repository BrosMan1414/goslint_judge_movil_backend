-- data.sql generado a partir de INSERTS.sql (uso en dev). 
-- NOTA: se ejecuta porque spring.sql.init.mode=always (application.yml)
-- Ajustar a 'never' o eliminar en producción.

-- Limpiar (opcional, si usas update puede fallar por FK; comentar si no quieres truncar)
DELETE FROM retroalimentaciones; 
DELETE FROM envios; 
DELETE FROM problemas; 
DELETE FROM equipos_maratones; 
DELETE FROM notificaciones; 
DELETE FROM maratones; 
DELETE FROM equipos;

-- ================================
-- Asegurar inserción correcta en equipos
-- ================================
INSERT INTO equipos (id, nombre, email_contacto, password_hash, password_original, password_aes, puntaje, fecha_creacion)
VALUES
  (1, 'Equipo Alpha', 'alpha@example.com', 'hash_alpha123', NULL, NULL, 0, '2025-10-02 16:15:05.511172'),
  (2, 'Equipo Beta', 'beta@example.com', 'hash_beta123', NULL, NULL, 0, '2025-10-02 16:15:05.511172'),
  (5, 'Equipo Gamma', 'gamma@example.com', '$2a$10$abcdefghijklmnopqrstuvGamma1234567890abcd', NULL, NULL, 0, '2025-11-12 20:26:00.089051'),
  (6, 'Equipo 1', 'equipo1@example.com', 'hash1', NULL, NULL, 0, '2025-11-12 20:28:31.724969'),
  (7, 'Equipo 2', 'equipo2@example.com', 'hash2', NULL, NULL, 0, '2025-11-12 20:28:31.724969'),
  (8, 'Equipo 3', 'equipo3@example.com', 'hash3', NULL, NULL, 0, '2025-11-12 20:28:31.724969')
ON CONFLICT (email_contacto) DO NOTHING;

-- ================================
-- Datos iniciales para la tabla maratones
-- ================================
INSERT INTO maratones (id, nombre, descripcion, fecha_inicio, fecha_fin, fecha_creacion)
VALUES
  (1, 'Maratón 1', 'Descripción del Maratón 1', '2025-11-01 10:00:00', '2025-11-01 18:00:00', '2025-11-01 09:00:00'),
  (2, 'Maratón 2', 'Descripción del Maratón 2', '2025-11-05 10:00:00', '2025-11-05 18:00:00', '2025-11-05 09:00:00'),
  (3, 'Maratón 3', 'Descripción del Maratón 3', '2025-11-10 10:00:00', '2025-11-10 18:00:00', '2025-11-10 09:00:00')
ON CONFLICT DO NOTHING;

-- ================================
-- Datos iniciales para la tabla equipos_maratones
-- ================================
INSERT INTO equipos_maratones (id, equipo_id, maraton_id)
VALUES
  (1, 6, 1),
  (2, 7, 1),
  (3, 8, 1),
  (4, 6, 2),
  (5, 7, 2),
  (6, 8, 2),
  (7, 6, 3),
  (8, 7, 3),
  (9, 8, 3)
ON CONFLICT DO NOTHING;

-- ================================
-- Problemas
-- ================================
INSERT INTO problemas (id, maraton_id, titulo, enunciado, limite_tiempo, limite_memoria, fecha_creacion)
VALUES
  (1, 1, 'Suma de Números', 'Dado un conjunto de números, imprimir la suma total.', 2, 256, '2025-11-01 10:00:00'),
  (2, 2, 'Factorial', 'Calcular factorial de N (0<=N<=20).', 1, 128, '2025-11-05 10:00:00'),
  (3, 3, 'Rutas Mínimas', 'Calcular distancia mínima en un grafo dirigido.', 3, 512, '2025-11-10 10:00:00')
ON CONFLICT DO NOTHING;

-- ================================
-- Envios
-- ================================
INSERT INTO envios (id, equipo_id, problema_id, lenguaje, codigo, resultado, tiempo_ejecucion, memoria_usada, fecha)
VALUES
  (1, 6, 1, 'Python', 'print(sum([int(x) for x in input().split()]))', 'Accepted', 0.45, 32, '2025-11-12 20:30:00'),
  (2, 7, 2, 'Java', 'class Main { public static void main(String[] a){ long f=1; for(int i=1;i<=10;i++) f*=i; System.out.println(f); } }', 'Accepted', 0.80, 64, '2025-11-12 20:30:00'),
  (3, 8, 3, 'C++', '#include <bits/stdc++.h>\nusing namespace std;int main(){cout<<0;return 0;}', 'WrongAnswer', 1.2, 70, '2025-11-12 20:30:00')
ON CONFLICT DO NOTHING;

-- ================================
-- Retroalimentaciones
-- ================================
INSERT INTO retroalimentaciones (id, envio_id, tipo, comentario, fecha)
VALUES
  (1, 1, 'ia', 'El código es correcto y eficiente. Buen uso de comprensión de listas.', '2025-11-12 20:35:00'),
  (2, 2, 'juez', 'Salida correcta para factorial de 10.', '2025-11-12 20:35:00'),
  (3, 3, 'ia', 'Considera optimizar el uso de memoria en el grafo.', '2025-11-12 20:35:00')
ON CONFLICT DO NOTHING;

-- ================================
-- Notificaciones
-- ================================
INSERT INTO notificaciones (id, equipo_id, mensaje, leido, fecha)
VALUES
  (1, 6, 'Tu envío en el problema "Suma de Números" fue aceptado 🎉', FALSE, '2025-11-12 20:40:00'),
  (2, 7, 'Nuevo problema disponible en Maratón Otoño 2025', FALSE, '2025-11-12 20:40:00'),
  (3, 8, 'Revisa la retroalimentación de tu envío en Rutas Mínimas', FALSE, '2025-11-12 20:40:00')
ON CONFLICT DO NOTHING;
