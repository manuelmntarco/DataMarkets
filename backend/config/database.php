<?php

declare(strict_types=1);

/**
 * Conexión PDO a MySQL — DataMarkets.
 * XAMPP por defecto: usuario root sin contraseña. Ajusta si tu MySQL usa otra cuenta.
 */

const DB_HOST = '127.0.0.1';
const DB_PORT = 3306;
const DB_NAME = 'datamarkets';
const DB_USER = 'root';
const DB_PASS = '';
const DB_CHARSET = 'utf8mb4';

/**
 * Instancia PDO reutilizable (singleton).
 *
 * @throws PDOException si falla la conexión
 */
function get_pdo(): PDO
{
    static $pdo = null;

    if ($pdo instanceof PDO) {
        return $pdo;
    }

    $dsn = sprintf(
        'mysql:host=%s;port=%d;dbname=%s;charset=%s',
        DB_HOST,
        DB_PORT,
        DB_NAME,
        DB_CHARSET
    );

    $options = [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ];

    $pdo = new PDO($dsn, DB_USER, DB_PASS, $options);

    return $pdo;
}
