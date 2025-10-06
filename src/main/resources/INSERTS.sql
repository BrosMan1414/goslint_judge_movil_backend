-- ================================
-- Datos de prueba para goslint_judge_movil_backend
-- ================================

-- Insertar equipos
INSERT INTO equipos (nombre, email_contacto, password_hash, puntaje)
VALUES 
  ('Equipo Alpha', 'alpha@example.com', 'hash_alpha123', 0),
  ('Equipo Beta', 'beta@example.com', 'hash_beta123', 0);

-- Insertar maratón
INSERT INTO maratones (nombre, descripcion, fecha_inicio, fecha_fin)
VALUES (
  'Maratón Universitaria 2025',
  'Competencia de programación para estudiantes.',
  '2025-10-10 08:00:00',
  '2025-10-10 14:00:00'
);

-- Relacionar equipos con la maratón
INSERT INTO equipos_maratones (equipo_id, maraton_id)
VALUES 
  (1, 1),  -- Equipo Alpha en la maratón
  (2, 1);  -- Equipo Beta en la maratón

-- Insertar problema
INSERT INTO problemas (maraton_id, titulo, enunciado, limite_tiempo, limite_memoria)
VALUES (
  1,
  'Suma de Números',
  'Dado un conjunto de números, imprimir la suma total.',
  2,   -- 2 segundos
  256  -- 256 MB
);

-- Insertar envío
INSERT INTO envios (equipo_id, problema_id, lenguaje, codigo, resultado, tiempo_ejecucion, memoria_usada)
VALUES (
  1, -- Equipo Alpha
  1, -- Problema Suma de Números
  'Python',
  'print(sum([int(x) for x in input().split()]))',
  'Accepted',
  0.45,
  32
);

-- Insertar retroalimentación
INSERT INTO retroalimentaciones (envio_id, tipo, comentario)
VALUES (
  1,
  'ia',
  'El código es correcto y eficiente. Buen uso de comprensión de listas.'
);

-- Insertar notificación
INSERT INTO notificaciones (equipo_id, mensaje)
VALUES (
  1,
  'Tu envío en el problema "Suma de Números" fue aceptado 🎉'
);
