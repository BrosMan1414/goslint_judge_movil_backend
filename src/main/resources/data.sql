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

INSERT INTO equipos (nombre, email_contacto, password_hash, puntaje)
VALUES 
  ('Equipo Alpha', 'alpha@example.com', '$2a$10$abcdefghijklmnopqrstuvAlpha1234567890abcd', 0),
  ('Equipo Beta', 'beta@example.com', '$2a$10$abcdefghijklmnopqrstuvBeta1234567890abcd', 0),
  ('Equipo Gamma', 'gamma@example.com', '$2a$10$abcdefghijklmnopqrstuvGamma1234567890abcd', 0)
ON CONFLICT DO NOTHING;

INSERT INTO maratones (nombre, descripcion, fecha_inicio, fecha_fin)
VALUES 
  ('Maratón Universitaria 2025','Competencia de programación para estudiantes.','2025-10-10 08:00:00','2025-10-10 14:00:00'),
  ('Maratón Otoño 2025','Ronda de desafíos de algoritmos.','2025-10-20 09:00:00','2025-10-20 15:00:00'),
  ('Maratón Invierno 2025','Desafíos avanzados de estructuras de datos.','2025-11-05 09:00:00','2025-11-05 16:00:00')
ON CONFLICT DO NOTHING;

-- Relacionar equipos con la maratón (asumiendo ids 1 y 2 y maratón id 1)
INSERT INTO equipos_maratones (equipo_id, maraton_id)
VALUES 
  (1,1),(2,1),(3,1),
  (1,2),(2,2),(3,2),
  (1,3),(2,3),(3,3)
ON CONFLICT DO NOTHING;

INSERT INTO problemas (maraton_id, titulo, enunciado, limite_tiempo, limite_memoria)
VALUES 
  (1,'Suma de Números','Dado un conjunto de números, imprimir la suma total.',2,256),
  (2,'Factorial','Calcular factorial de N (0<=N<=20).',1,128),
  (3,'Rutas Mínimas','Calcular distancia mínima en un grafo dirigido.',3,512)
ON CONFLICT DO NOTHING;

INSERT INTO envios (equipo_id, problema_id, lenguaje, codigo, resultado, tiempo_ejecucion, memoria_usada, fecha)
VALUES 
  (1,1,'Python','print(sum([int(x) for x in input().split()]))','Accepted',0.45,32,NOW()),
  (2,2,'Java','class Main { public static void main(String[] a){ long f=1; for(int i=1;i<=10;i++) f*=i; System.out.println(f); } }','Accepted',0.80,64,NOW()),
  (3,3,'C++','#include <bits/stdc++.h>\nusing namespace std;int main(){cout<<0;return 0;}','WrongAnswer',1.2,70,NOW())
ON CONFLICT DO NOTHING;

INSERT INTO retroalimentaciones (envio_id, tipo, comentario, fecha)
VALUES 
  (1,'ia','El código es correcto y eficiente. Buen uso de comprensión de listas.', NOW()),
  (2,'juez','Salida correcta para factorial de 10.', NOW()),
  (3,'ia','Considera optimizar el uso de memoria en el grafo.', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO notificaciones (equipo_id, mensaje, fecha)
VALUES 
  (1,'Tu envío en el problema "Suma de Números" fue aceptado 🎉', NOW()),
  (2,'Nuevo problema disponible en Maratón Otoño 2025', NOW()),
  (3,'Revisa la retroalimentación de tu envío en Rutas Mínimas', NOW())
ON CONFLICT DO NOTHING;
