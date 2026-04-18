<?php

declare(strict_types=1);

/**
 * Login — POST JSON: email, password
 * Respuesta 200: { id_usuario, nombre, email, token }
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
    json_response(405, ['error' => 'Método no permitido']);
}

$data = read_json_body();

$emailRaw = isset($data['email']) ? trim((string) $data['email']) : '';
$password = isset($data['password']) ? (string) $data['password'] : '';

if ($emailRaw === '') {
    json_response(400, ['error' => 'Email obligatorio']);
}

$email = strtolower($emailRaw);

try {
    $pdo = get_pdo();
} catch (PDOException $e) {
    json_response(500, ['error' => 'Error de base de datos']);
}

$stmt = $pdo->prepare(
    'SELECT id_usuario, nombre, email, password_hash FROM usuarios WHERE email = ? LIMIT 1'
);
$stmt->execute([$email]);
$user = $stmt->fetch();

if ($user === false || !password_verify($password, $user['password_hash'])) {
    json_response(401, ['error' => 'Credenciales incorrectas']);
}

$token = issue_session_token($pdo, (int) $user['id_usuario']);

json_response(200, usuario_payload([
    'id_usuario' => $user['id_usuario'],
    'nombre' => $user['nombre'],
    'email' => $user['email'],
], $token));
