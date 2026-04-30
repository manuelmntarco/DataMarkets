<?php

declare(strict_types=1);

/**
 * Activos disponibles. Añadido al backend por Manuel
 * GET: lista todos los activos del catálogo
 *
 * Requiere autenticación (Bearer token) para mantener
 * coherencia con el resto del backend.
 */

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/helpers/http.php';
require_once dirname(__DIR__, 2) . '/helpers/auth_service.php';

header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    json_response(405, ['error' => 'Metodo no permitido']);
}

try {
    $pdo = get_pdo();
} catch (PDOException) {
    json_response(500, ['error' => 'Error de base de datos']);
}

// Verificar token (mantiene coherencia con el resto del backend)
require_auth_user_id($pdo);

// Consulta todos los activos del catálogo
$stmt = $pdo->prepare(
    'SELECT id_activo, id_externo, simbolo, nombre, tipo, imagen_url
     FROM activos
     ORDER BY tipo, nombre'
);
$stmt->execute();
$items = $stmt->fetchAll();

json_response(200, ['items' => $items]);