-- Ejecutar en la base `datamarkets` si ya tenías el esquema v2 sin la tabla de sesiones API.
-- Si importas de cero `datamarkets_database_v2.sql` actualizado, no necesitas este fichero.

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sesiones_usuario (
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
