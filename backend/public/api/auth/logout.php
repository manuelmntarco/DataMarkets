<?php

declare(strict_types=1);

/**
 * Logout — POST con Authorization: Bearer <token>
 * Respuesta 200: { mensaje }
 */

require_once dirname(__DIR__, 3) . '/config/database.php';
require_once dirname(__DIR__, 3) . '/helpers/http.php';
require_once dirname(__DIR__, 3) . '/helpers/auth_service.php';

header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    json_response(405, ['error' => 'Metodo no permitido']);
}

$token = get_bearer_token();
if ($token === null) {
    json_response(401, ['error' => 'Token de sesion requerido']);
}

try {
    $pdo = get_pdo();
} catch (PDOException) {
    json_response(500, ['error' => 'Error de base de datos']);
}

$revoked = revoke_session_token($pdo, $token);
if (!$revoked) {
    json_response(401, ['error' => 'Token invalido o expirado']);
}

json_response(200, ['mensaje' => 'Sesion cerrada']);
