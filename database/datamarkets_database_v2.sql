-- =============================================================================
-- DataMarkets — Esquema MySQL v2
-- Base de datos: datamarkets (utf8mb4_unicode_ci)
-- Importar desde phpMyAdmin con la base "datamarkets" ya creada y seleccionada.
-- Usuario demo: demo@datamarkets.app / Test1234!
-- =============================================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

SET FOREIGN_KEY_CHECKS = 0;

DROP VIEW IF EXISTS v_seguimiento_usuario;
DROP VIEW IF EXISTS v_cotizacion_actual;

DROP TABLE IF EXISTS configuracion_usuario;
DROP TABLE IF EXISTS noticias;
DROP TABLE IF EXISTS seguimiento;
DROP TABLE IF EXISTS cotizaciones;
DROP TABLE IF EXISTS activos;
DROP TABLE IF EXISTS sesiones_usuario;
DROP TABLE IF EXISTS usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- usuarios — cuentas (contraseña con bcrypt)
-- -----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(120) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    creado_en        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Contraseña: Test1234! (hash bcrypt generado con cost 10)
INSERT INTO usuarios (nombre, email, password_hash) VALUES
(
    'Usuario demo',
    'demo@datamarkets.app',
    '$2b$10$pdXE5EYPE4LJQPWjKuJlDuEChaoEPaNmzKta2qqnM/el8cL8qE48i'
);

-- -----------------------------------------------------------------------------
-- sesiones_usuario — tokens devueltos al cliente tras login/registro (API REST)
-- -----------------------------------------------------------------------------
CREATE TABLE sesiones_usuario (
    id_sesion    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT UNSIGNED NOT NULL,
    token        VARCHAR(64) NOT NULL,
    creado_en    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_en    DATETIME NOT NULL,
    CONSTRAINT fk_sesiones_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uq_sesiones_token (token),
    KEY idx_sesiones_usuario (id_usuario),
    KEY idx_sesiones_expira (expira_en)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- activos — catálogo (IDs externos alineados con CoinGecko / símbolos bursátiles)
-- -----------------------------------------------------------------------------
CREATE TABLE activos (
    id_activo        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_externo       VARCHAR(64) NOT NULL COMMENT 'ID CoinGecko o símbolo API',
    simbolo          VARCHAR(32) NOT NULL,
    nombre           VARCHAR(255) NOT NULL,
    tipo             ENUM('cripto','accion','etf','divisa','materia_prima') NOT NULL,
    imagen_url       VARCHAR(512) DEFAULT NULL,
    UNIQUE KEY uq_activos_id_externo (id_externo),
    KEY idx_activos_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO activos (id_externo, simbolo, nombre, tipo, imagen_url) VALUES
('bitcoin', 'btc', 'Bitcoin', 'cripto', 'https://assets.coingecko.com/coins/images/1/large/bitcoin.png'),
('ethereum', 'eth', 'Ethereum', 'cripto', 'https://assets.coingecko.com/coins/images/279/large/ethereum.png'),
('solana', 'sol', 'Solana', 'cripto', 'https://assets.coingecko.com/coins/images/4128/large/solana.png'),
('cardano', 'ada', 'Cardano', 'cripto', 'https://assets.coingecko.com/coins/images/975/large/cardano.png'),
('ripple', 'xrp', 'XRP', 'cripto', 'https://assets.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png'),
('apple', 'AAPL', 'Apple Inc.', 'accion', NULL),
('microsoft', 'MSFT', 'Microsoft Corporation', 'accion', NULL),
('tesla', 'TSLA', 'Tesla Inc.', 'accion', NULL),
('spy', 'SPY', 'SPDR S&P 500 ETF Trust', 'etf', NULL),
('gld', 'GLD', 'SPDR Gold Shares', 'etf', NULL),
('eurusd', 'EURUSD', 'Euro / Dólar estadounidense', 'divisa', NULL);

-- -----------------------------------------------------------------------------
-- cotizaciones — histórico por activo (relleno desde APIs en producción)
-- -----------------------------------------------------------------------------
CREATE TABLE cotizaciones (
    id_cotizacion    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_activo        INT UNSIGNED NOT NULL,
    precio           DECIMAL(24, 8) NOT NULL,
    moneda           VARCHAR(8) NOT NULL DEFAULT 'EUR',
    cambio_24h       DECIMAL(24, 8) DEFAULT NULL,
    variacion_pct_24h DECIMAL(12, 4) DEFAULT NULL,
    max_24h          DECIMAL(24, 8) DEFAULT NULL,
    min_24h          DECIMAL(24, 8) DEFAULT NULL,
    capitalizacion   BIGINT UNSIGNED DEFAULT NULL,
    capturado_en     DATETIME NOT NULL,
    CONSTRAINT fk_cotizaciones_activo
        FOREIGN KEY (id_activo) REFERENCES activos (id_activo)
        ON DELETE CASCADE ON UPDATE CASCADE,
    KEY idx_cotizaciones_activo_tiempo (id_activo, capturado_en)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Una cotización de ejemplo por activo (precios orientativos para desarrollo)
INSERT INTO cotizaciones (id_activo, precio, moneda, cambio_24h, variacion_pct_24h, max_24h, min_24h, capitalizacion, capturado_en)
SELECT
    a.id_activo,
    v.precio,
    'EUR',
    v.ch,
    v.vp,
    v.mx,
    v.mn,
    v.cap,
    UTC_TIMESTAMP()
FROM activos a
JOIN (
    SELECT 'bitcoin' AS e,  95000.00000000 AS precio, 1200.00 AS ch,  1.2800 AS vp, 96500.00000000 AS mx, 93000.00000000 AS mn, 1900000000000 AS cap UNION ALL
    SELECT 'ethereum',     3200.00000000, 45.00, 1.4200, 3250.00000000, 3100.00000000, 380000000000 UNION ALL
    SELECT 'solana',       180.00000000,  3.20,  1.8100, 185.00000000, 175.00000000, 85000000000 UNION ALL
    SELECT 'cardano',      0.85000000,    0.02,  2.1000, 0.87000000, 0.82000000, 30000000000 UNION ALL
    SELECT 'ripple',       2.10000000,    0.05,  2.4500, 2.15000000, 2.05000000, 120000000000 UNION ALL
    SELECT 'apple',        210.00000000,  1.50,  0.7200, 212.00000000, 207.00000000, 3200000000000 UNION ALL
    SELECT 'microsoft',    420.00000000,  2.10,  0.5000, 425.00000000, 415.00000000, 3100000000000 UNION ALL
    SELECT 'tesla',        250.00000000, -3.00, -1.1800, 258.00000000, 245.00000000, 800000000000 UNION ALL
    SELECT 'spy',          520.00000000,  0.80,  0.1500, 522.00000000, 517.00000000, NULL UNION ALL
    SELECT 'gld',          210.00000000,  0.40,  0.1900, 211.00000000, 208.00000000, NULL UNION ALL
    SELECT 'eurusd',       1.08000000,    0.0020, 0.1800, 1.08500000, 1.07500000, NULL
) AS v ON v.e = a.id_externo;

-- -----------------------------------------------------------------------------
-- seguimiento — favoritos (único por usuario + activo)
-- -----------------------------------------------------------------------------
CREATE TABLE seguimiento (
    id_seguimiento   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario       INT UNSIGNED NOT NULL,
    id_activo        INT UNSIGNED NOT NULL,
    anadido_en       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seguimiento_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_seguimiento_activo
        FOREIGN KEY (id_activo) REFERENCES activos (id_activo)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uq_seguimiento_usuario_activo (id_usuario, id_activo),
    KEY idx_seguimiento_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Demo: usuario sigue Bitcoin y Ethereum
INSERT INTO seguimiento (id_usuario, id_activo)
SELECT u.id_usuario, a.id_activo
FROM usuarios u
CROSS JOIN activos a
WHERE u.email = 'demo@datamarkets.app'
  AND a.id_externo IN ('bitcoin', 'ethereum');

-- -----------------------------------------------------------------------------
-- noticias — caché local (relación opcional con activo)
-- -----------------------------------------------------------------------------
CREATE TABLE noticias (
    id_noticia       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_activo        INT UNSIGNED DEFAULT NULL,
    titulo           VARCHAR(512) NOT NULL,
    descripcion      TEXT,
    fuente           VARCHAR(255) DEFAULT NULL,
    fecha_publicacion DATETIME DEFAULT NULL,
    url              VARCHAR(2048) NOT NULL,
    imagen_url       VARCHAR(1024) DEFAULT NULL,
    guardado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_noticias_activo
        FOREIGN KEY (id_activo) REFERENCES activos (id_activo)
        ON DELETE SET NULL ON UPDATE CASCADE,
    UNIQUE KEY uq_noticias_url (url(768)),
    KEY idx_noticias_fecha (fecha_publicacion),
    KEY idx_noticias_activo (id_activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO noticias (id_activo, titulo, descripcion, fuente, fecha_publicacion, url, imagen_url)
VALUES (
    (SELECT id_activo FROM activos WHERE id_externo = 'bitcoin' LIMIT 1),
    'Mercados observan movimientos en criptoactivos',
    'Resumen orientativo para desarrollo de la app DataMarkets.',
    'DataMarkets Demo',
    UTC_TIMESTAMP(),
    'https://example.com/noticias/demo-bitcoin-1',
    NULL
);

-- -----------------------------------------------------------------------------
-- configuracion_usuario — preferencias (moneda, tema, notificaciones, refresco)
-- -----------------------------------------------------------------------------
CREATE TABLE configuracion_usuario (
    id_usuario            INT UNSIGNED NOT NULL PRIMARY KEY,
    moneda                CHAR(3) NOT NULL DEFAULT 'EUR',
    tema                  ENUM('claro','oscuro','sistema') NOT NULL DEFAULT 'sistema',
    notificaciones        TINYINT(1) NOT NULL DEFAULT 1,
    intervalo_refresco_seg INT UNSIGNED NOT NULL DEFAULT 300,
    actualizado_en        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_config_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO configuracion_usuario (id_usuario, moneda, tema, notificaciones, intervalo_refresco_seg)
SELECT id_usuario, 'EUR', 'sistema', 1, 300 FROM usuarios WHERE email = 'demo@datamarkets.app';

-- -----------------------------------------------------------------------------
-- Vista: último precio por activo (útil para el backend)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_cotizacion_actual AS
SELECT
    c.id_activo,
    a.id_externo,
    a.simbolo,
    a.nombre,
    a.tipo,
    c.precio,
    c.moneda,
    c.cambio_24h,
    c.variacion_pct_24h,
    c.max_24h,
    c.min_24h,
    c.capitalizacion,
    c.capturado_en
FROM cotizaciones c
INNER JOIN (
    SELECT id_activo, MAX(id_cotizacion) AS id_cotizacion_max
    FROM cotizaciones
    GROUP BY id_activo
) u ON u.id_cotizacion_max = c.id_cotizacion
INNER JOIN activos a ON a.id_activo = c.id_activo;

-- -----------------------------------------------------------------------------
-- Vista: favoritos del usuario con cotización actual
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_seguimiento_usuario AS
SELECT
    s.id_seguimiento,
    s.id_usuario,
    s.id_activo,
    s.anadido_en,
    a.id_externo,
    a.simbolo,
    a.nombre,
    a.tipo,
    a.imagen_url,
    v.precio              AS precio_actual,
    v.moneda,
    v.cambio_24h,
    v.variacion_pct_24h,
    v.max_24h,
    v.min_24h,
    v.capturado_en        AS ultima_cotizacion
FROM seguimiento s
INNER JOIN activos a ON a.id_activo = s.id_activo
LEFT JOIN v_cotizacion_actual v ON v.id_activo = s.id_activo;
