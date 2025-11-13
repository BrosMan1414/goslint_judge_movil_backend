-- data.sql generado a partir de INSERTS.sql (uso en dev). 
-- NOTA: se ejecuta porque spring.sql.init.mode=always (application.yml)
-- Ajustar a 'never' o eliminar en producción.

-- Limpiar (opcional, si usas update puede fallar por FK; comentar si no quieres truncar)
-- DELETE FROM retroalimentaciones; 
-- DELETE FROM envios; 
-- DELETE FROM problemas; 
-- DELETE FROM equipos_maratones; 
-- DELETE FROM notificaciones; 
-- DELETE FROM maratones; 
-- DELETE FROM equipos;

-- ================================
-- Asegurar inserción correcta en equipos
-- ================================
INSERT INTO equipos (id, nombre, email_contacto, password_hash, puntaje, fecha_creacion)
VALUES
  (1, 'Equipo Alpha', 'alpha@example.com', 'hash_alpha123', 0, '2025-10-02 16:15:05.511172'),
  (2, 'Equipo Beta', 'beta@example.com', 'hash_beta123', 0, '2025-10-02 16:15:05.511172'),
  (5, 'Equipo Gamma', 'gamma@example.com', '$2a$10$abcdefghijklmnopqrstuvGamma1234567890abcd', 0, '2025-11-12 20:26:00.089051'),
  (6, 'Equipo 1', 'equipo1@example.com', 'hash1', 0, '2025-11-12 20:28:31.724969'),
  (7, 'Equipo 2', 'equipo2@example.com', 'hash2', 0, '2025-11-12 20:28:31.724969'),
  (8, 'Equipo 3', 'equipo3@example.com', 'hash3', 0, '2025-11-12 20:28:31.724969')
ON CONFLICT (email_contacto) DO NOTHING;

-- ================================
-- Datos iniciales para la tabla maratones
-- ================================
INSERT INTO maratones (nombre, descripcion, fecha_inicio, fecha_fin)
VALUES
  ('Maratón 1', 'Descripción del Maratón 1', '2025-11-01 10:00:00', '2025-11-01 18:00:00'),
  ('Maratón 2', 'Descripción del Maratón 2', '2025-11-05 10:00:00', '2025-11-05 18:00:00'),
  ('Maratón 3', 'Descripción del Maratón 3', '2025-11-10 10:00:00', '2025-11-10 18:00:00')
ON CONFLICT DO NOTHING;

-- ================================
-- Datos iniciales para la tabla equipos_maratones
-- ================================
-- Vincular equipos a maratones referenciando por claves naturales (email/nombre)
INSERT INTO equipos_maratones (equipo_id, maraton_id)
SELECT e.id, m.id FROM equipos e JOIN maratones m ON m.nombre = 'Maratón 1' WHERE e.email_contacto IN ('equipo1@example.com','equipo2@example.com','equipo3@example.com')
UNION ALL
SELECT e.id, m.id FROM equipos e JOIN maratones m ON m.nombre = 'Maratón 2' WHERE e.email_contacto IN ('equipo1@example.com','equipo2@example.com','equipo3@example.com')
UNION ALL
SELECT e.id, m.id FROM equipos e JOIN maratones m ON m.nombre = 'Maratón 3' WHERE e.email_contacto IN ('equipo1@example.com','equipo2@example.com','equipo3@example.com')
ON CONFLICT DO NOTHING;

-- Problemas, referenciando maratón por nombre para evitar depender de IDs
INSERT INTO problemas (maraton_id, titulo, enunciado, limite_tiempo, limite_memoria)
SELECT m.id, 'Suma de Números', 'Dado un conjunto de números, imprimir la suma total.', 2, 256 FROM maratones m WHERE m.nombre='Maratón 1'
UNION ALL
SELECT m.id, 'Factorial', 'Calcular factorial de N (0<=N<=20).', 1, 128 FROM maratones m WHERE m.nombre='Maratón 2'
UNION ALL
SELECT m.id, 'Rutas Mínimas', 'Calcular distancia mínima en un grafo dirigido.', 3, 512 FROM maratones m WHERE m.nombre='Maratón 3'
ON CONFLICT DO NOTHING;

-- Envios, referenciando por email del equipo y título del problema para robustez
INSERT INTO envios (equipo_id, problema_id, lenguaje, codigo, resultado, tiempo_ejecucion, memoria_usada, fecha)
SELECT e.id, p.id, 'Python', 'print(sum([int(x) for x in input().split()]))', 'Accepted'::resultado_envio, 0.45, 32, NOW()
FROM equipos e, problemas p
WHERE e.email_contacto='equipo1@example.com' AND p.titulo='Suma de Números'
UNION ALL
SELECT e.id, p.id, 'Java', 'class Main { public static void main(String[] a){ long f=1; for(int i=1;i<=10;i++) f*=i; System.out.println(f); } }', 'Accepted'::resultado_envio, 0.80, 64, NOW()
FROM equipos e, problemas p
WHERE e.email_contacto='equipo2@example.com' AND p.titulo='Factorial'
UNION ALL
SELECT e.id, p.id, 'C++', '#include <bits/stdc++.h>\nusing namespace std;int main(){cout<<0;return 0;}', 'WrongAnswer'::resultado_envio, 1.2, 70, NOW()
FROM equipos e, problemas p
WHERE e.email_contacto='equipo3@example.com' AND p.titulo='Rutas Mínimas'
ON CONFLICT DO NOTHING;

-- Retroalimentaciones, enlazando al envio por equipo/problema
INSERT INTO retroalimentaciones (envio_id, tipo, comentario, fecha)
SELECT e.id, 'ia'::tipo_retro, 'El código es correcto y eficiente. Buen uso de comprensión de listas.', NOW()
FROM envios e JOIN equipos eq ON e.equipo_id = eq.id JOIN problemas p ON e.problema_id = p.id
WHERE eq.email_contacto='equipo1@example.com' AND p.titulo='Suma de Números'
UNION ALL
SELECT e.id, 'juez'::tipo_retro, 'Salida correcta para factorial de 10.', NOW()
FROM envios e JOIN equipos eq ON e.equipo_id = eq.id JOIN problemas p ON e.problema_id = p.id
WHERE eq.email_contacto='equipo2@example.com' AND p.titulo='Factorial'
UNION ALL
SELECT e.id, 'ia'::tipo_retro, 'Considera optimizar el uso de memoria en el grafo.', NOW()
FROM envios e JOIN equipos eq ON e.equipo_id = eq.id JOIN problemas p ON e.problema_id = p.id
WHERE eq.email_contacto='equipo3@example.com' AND p.titulo='Rutas Mínimas'
ON CONFLICT DO NOTHING;

-- Notificaciones, referenciando equipo por email para evitar IDs frágiles
INSERT INTO notificaciones (equipo_id, mensaje, fecha)
SELECT e.id, 'Tu envío en el problema "Suma de Números" fue aceptado 🎉', NOW() FROM equipos e WHERE e.email_contacto='equipo1@example.com'
UNION ALL
SELECT e.id, 'Nuevo problema disponible en Maratón Otoño 2025', NOW() FROM equipos e WHERE e.email_contacto='equipo2@example.com'
UNION ALL
SELECT e.id, 'Revisa la retroalimentación de tu envío en Rutas Mínimas', NOW() FROM equipos e WHERE e.email_contacto='equipo3@example.com'
ON CONFLICT DO NOTHING;
