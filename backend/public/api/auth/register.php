<?php

declare(strict_types=1);

/**
 * Registro — POST JSON: nombre, email, password
 * Respuesta 201: { id_usuario, nombre, email, token } (compatible con Usuario.java / Gson)
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

$nombre = isset($data['nombre']) ? trim((string) $data['nombre']) : '';
$emailRaw = isset($data['email']) ? trim((string) $data['email']) : '';
$password = isset($data['password']) ? (string) $data['password'] : '';

if ($nombre === '' || mb_strlen($nombre) > 120) {
    json_response(400, ['error' => 'El nombre es obligatorio (máx. 120 caracteres)']);
}

if ($emailRaw === '' || filter_var($emailRaw, FILTER_VALIDATE_EMAIL) === false) {
    json_response(400, ['error' => 'Email no válido']);
}

$email = strtolower($emailRaw);

if (strlen($password) < 8) {
    json_response(400, ['error' => 'La contraseña debe tener al menos 8 caracteres']);
}

try {
    $pdo = get_pdo();
} catch (PDOException $e) {
    json_response(500, ['error' => 'Error de base de datos']);
}

$hash = hash_password_plain($password);

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare(
        'INSERT INTO usuarios (nombre, email, password_hash) VALUES (?, ?, ?)'
    );
    $stmt->execute([$nombre, $email, $hash]);

    $idUsuario = (int) $pdo->lastInsertId();

    $cfg = $pdo->prepare(
        'INSERT INTO configuracion_usuario (id_usuario, moneda, tema, notificaciones, intervalo_refresco_seg)
         VALUES (?, \'EUR\', \'sistema\', 1, 300)'
    );
    $cfg->execute([$idUsuario]);

    $token = issue_session_token($pdo, $idUsuario);

    $row = [
        'id_usuario' => $idUsuario,
        'nombre' => $nombre,
        'email' => $email,
    ];

    $pdo->commit();

    json_response(201, usuario_payload($row, $token));
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    $sqlState = $e->errorInfo[0] ?? '';
    $mysqlErr = isset($e->errorInfo[1]) ? (int) $e->errorInfo[1] : 0;
    if ($sqlState === '23000' || $mysqlErr === 1062) {
        json_response(409, ['error' => 'Ya existe una cuenta con ese email']);
    }
    json_response(500, ['error' => 'No se pudo completar el registro']);
}
