<?php

declare(strict_types=1);

/**
 * Prueba de conexión PDO a la base datamarkets.
 * Navegador: http://localhost/.../backend/public/test-connection.php
 * CLI: php test-connection.php (desde esta carpeta)
 */

require_once dirname(__DIR__) . '/config/database.php';

$isCli = PHP_SAPI === 'cli';

if (!$isCli) {
    header('Content-Type: text/html; charset=utf-8');
}

function out(string $line, bool $isCli): void
{
    echo $isCli ? $line . PHP_EOL : htmlspecialchars($line, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8') . "<br>\n";
}

try {
    $pdo = get_pdo();
    out('Conexión PDO: OK', $isCli);

    $version = $pdo->query('SELECT VERSION() AS v')->fetch();
    out('MySQL: ' . ($version['v'] ?? '?'), $isCli);

    $users = $pdo->query('SELECT COUNT(*) AS c FROM usuarios')->fetch();
    out('Filas en usuarios: ' . (string) ($users['c'] ?? '?'), $isCli);

    out('Base de datos "' . DB_NAME . '" accesible.', $isCli);
} catch (PDOException $e) {
    http_response_code(500);
    out('Error PDO: ' . $e->getMessage(), $isCli);
    exit(1);
}
