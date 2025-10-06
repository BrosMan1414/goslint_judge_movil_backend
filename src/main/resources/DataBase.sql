-- ================================
-- Definición de tipos ENUM
-- ================================
CREATE TYPE resultado_envio AS ENUM (
  'Accepted',
  'WrongAnswer',
  'TimeLimitExceeded',
  'RuntimeError',
  'CompilationError'
);

CREATE TYPE tipo_retro AS ENUM ('juez', 'ia');

-- ================================
-- Tabla equipos
-- ================================
CREATE TABLE IF NOT EXISTS equipos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email_contacto VARCHAR(150) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  puntaje INT DEFAULT 0,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Tabla maratones
-- ================================
CREATE TABLE IF NOT EXISTS maratones (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  fecha_inicio TIMESTAMP NOT NULL,
  fecha_fin TIMESTAMP NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Relación equipos ↔ maratones
-- ================================
CREATE TABLE IF NOT EXISTS equipos_maratones (
  id SERIAL PRIMARY KEY,
  equipo_id INT NOT NULL REFERENCES equipos(id) ON DELETE CASCADE,
  maraton_id INT NOT NULL REFERENCES maratones(id) ON DELETE CASCADE,
  UNIQUE(equipo_id, maraton_id)
);

-- ================================
-- Tabla problemas
-- ================================
CREATE TABLE IF NOT EXISTS problemas (
  id SERIAL PRIMARY KEY,
  maraton_id INT NOT NULL REFERENCES maratones(id) ON DELETE CASCADE,
  titulo VARCHAR(150) NOT NULL,
  enunciado TEXT NOT NULL,
  limite_tiempo INT NOT NULL,
  limite_memoria INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Tabla envíos (códigos enviados por equipos)
-- ================================
CREATE TABLE IF NOT EXISTS envios (
  id SERIAL PRIMARY KEY,
  equipo_id INT NOT NULL REFERENCES equipos(id) ON DELETE CASCADE,
  problema_id INT NOT NULL REFERENCES problemas(id) ON DELETE CASCADE,
  lenguaje VARCHAR(50) NOT NULL,
  codigo TEXT NOT NULL,
  resultado resultado_envio,
  tiempo_ejecucion FLOAT,
  memoria_usada INT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Tabla retroalimentaciones
-- ================================
CREATE TABLE IF NOT EXISTS retroalimentaciones (
  id SERIAL PRIMARY KEY,
  envio_id INT NOT NULL REFERENCES envios(id) ON DELETE CASCADE,
  tipo tipo_retro NOT NULL,
  comentario TEXT NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Tabla notificaciones
-- ================================
CREATE TABLE IF NOT EXISTS notificaciones (
  id SERIAL PRIMARY KEY,
  equipo_id INT NOT NULL REFERENCES equipos(id) ON DELETE CASCADE,
  mensaje VARCHAR(255) NOT NULL,
  leido BOOLEAN DEFAULT FALSE,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Índices útiles
-- ================================
CREATE INDEX idx_envios_equipo ON envios(equipo_id);
CREATE INDEX idx_envios_problema ON envios(problema_id);
CREATE INDEX idx_problemas_maraton ON problemas(maraton_id);
CREATE INDEX idx_retroalimentaciones_envio ON retroalimentaciones(envio_id);
