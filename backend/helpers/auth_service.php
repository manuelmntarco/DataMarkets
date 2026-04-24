<?php

declare(strict_types=1);

/**
 * Hash bcrypt (alineado con el esquema: password_hash en PHP) y sesiones API.
 */

const SESSION_TOKEN_BYTES = 32;
const SESSION_TTL_DAYS = 30;

function hash_password_plain(string $plain): string
{
    return password_hash($plain, PASSWORD_BCRYPT);
}

function issue_session_token(PDO $pdo, int $idUsuario): string
{
    $token = bin2hex(random_bytes(SESSION_TOKEN_BYTES));
    $expira = (new DateTimeImmutable('+' . SESSION_TTL_DAYS . ' days'))->format('Y-m-d H:i:s');

    $stmt = $pdo->prepare(
        'INSERT INTO sesiones_usuario (id_usuario, token, expira_en) VALUES (?, ?, ?)'
    );
    $stmt->execute([$idUsuario, $token, $expira]);

    return $token;
}

/**
 * @param array{id_usuario:int|string,nombre:string,email:string} $row
 * @return array{id_usuario:int,nombre:string,email:string,token:string}
 */
function usuario_payload(array $row, string $token): array
{
    return [
        'id_usuario' => (int) $row['id_usuario'],
        'nombre' => $row['nombre'],
        'email' => $row['email'],
        'token' => $token,
    ];
}

function get_bearer_token(): ?string
{
    $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['Authorization'] ?? null;
    if ($authHeader === null && function_exists('apache_request_headers')) {
        $headers = apache_request_headers();
        if (is_array($headers)) {
            foreach ($headers as $name => $value) {
                if (strtolower((string) $name) === 'authorization') {
                    $authHeader = (string) $value;
                    break;
                }
            }
        }
    }

    if ($authHeader === null) {
        return null;
    }

    if (!preg_match('/^Bearer\s+([A-Za-z0-9]+)$/', trim((string) $authHeader), $matches)) {
        return null;
    }

    return $matches[1];
}

function require_auth_user_id(PDO $pdo): int
{
    $token = get_bearer_token();
    if ($token === null) {
        json_response(401, ['error' => 'Token de sesión requerido']);
    }

    $stmt = $pdo->prepare(
        'SELECT id_usuario
         FROM sesiones_usuario
         WHERE token = ? AND expira_en > UTC_TIMESTAMP()
         LIMIT 1'
    );
    $stmt->execute([$token]);
    $row = $stmt->fetch();

    if ($row === false) {
        json_response(401, ['error' => 'Token inválido o expirado']);
    }

    return (int) $row['id_usuario'];
}
