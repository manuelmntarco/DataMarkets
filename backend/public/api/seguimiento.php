<?php

declare(strict_types=1);

/**
 * Seguimiento/Favoritos
 * - GET: listar favoritos del usuario autenticado
 * - POST JSON: id_activo o id_externo para anadir favorito
 * - DELETE: id_activo o id_externo (query o JSON) para eliminar favorito
 */

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/helpers/http.php';
require_once dirname(__DIR__, 2) . '/helpers/auth_service.php';

header('Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

try {
    $pdo = get_pdo();
} catch (PDOException) {
    json_response(500, ['error' => 'Error de base de datos']);
}

$idUsuario = require_auth_user_id($pdo);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    $stmt = $pdo->prepare(
        'SELECT id_seguimiento, id_activo, id_externo, simbolo, nombre, tipo, imagen_url,
                precio_actual, moneda, cambio_24h, variacion_pct_24h, max_24h, min_24h,
                ultima_cotizacion, anadido_en
         FROM v_seguimiento_usuario
         WHERE id_usuario = ?
         ORDER BY anadido_en DESC'
    );
    $stmt->execute([$idUsuario]);
    $items = $stmt->fetchAll();

    json_response(200, ['items' => $items]);
}

if ($method === 'POST') {
    $data = read_json_body();
    $idActivo = resolve_activo_id($pdo, $data);

    try {
        $stmt = $pdo->prepare(
            'INSERT INTO seguimiento (id_usuario, id_activo) VALUES (?, ?)'
        );
        $stmt->execute([$idUsuario, $idActivo]);
    } catch (PDOException $e) {
        $sqlState = $e->errorInfo[0] ?? '';
        $mysqlErr = isset($e->errorInfo[1]) ? (int) $e->errorInfo[1] : 0;
        if ($sqlState === '23000' || $mysqlErr === 1062) {
            json_response(409, ['error' => 'El activo ya esta en favoritos']);
        }
        json_response(500, ['error' => 'No se pudo anadir a favoritos']);
    }

    $stmt = $pdo->prepare(
        'SELECT id_seguimiento, id_activo, id_externo, simbolo, nombre, tipo, imagen_url,
                precio_actual, moneda, cambio_24h, variacion_pct_24h, max_24h, min_24h,
                ultima_cotizacion, anadido_en
         FROM v_seguimiento_usuario
         WHERE id_usuario = ? AND id_activo = ?
         LIMIT 1'
    );
    $stmt->execute([$idUsuario, $idActivo]);
    $item = $stmt->fetch();

    json_response(201, [
        'mensaje' => 'Favorito anadido',
        'item' => $item === false ? ['id_activo' => $idActivo] : $item,
    ]);
}

if ($method === 'DELETE') {
    $source = [];
    if (!empty($_GET)) {
        $source = $_GET;
    } else {
        $source = read_json_body();
    }
    $idActivo = resolve_activo_id($pdo, $source);

    $stmt = $pdo->prepare(
        'DELETE FROM seguimiento WHERE id_usuario = ? AND id_activo = ?'
    );
    $stmt->execute([$idUsuario, $idActivo]);

    if ($stmt->rowCount() === 0) {
        json_response(404, ['error' => 'El activo no estaba en favoritos']);
    }

    json_response(200, ['mensaje' => 'Favorito eliminado']);
}

json_response(405, ['error' => 'Metodo no permitido']);

/**
 * @param array<string, mixed> $input
 */
function resolve_activo_id(PDO $pdo, array $input): int
{
    if (isset($input['id_activo'])) {
        $idActivo = (int) $input['id_activo'];
        if ($idActivo <= 0) {
            json_response(400, ['error' => 'id_activo debe ser un entero positivo']);
        }
        return $idActivo;
    }

    if (isset($input['id_externo'])) {
        $idExterno = trim((string) $input['id_externo']);
        if ($idExterno === '') {
            json_response(400, ['error' => 'id_externo no puede estar vacio']);
        }

        $stmt = $pdo->prepare(
            'SELECT id_activo FROM activos WHERE id_externo = ? LIMIT 1'
        );
        $stmt->execute([$idExterno]);
        $row = $stmt->fetch();
        if ($row === false) {
            json_response(404, ['error' => 'Activo no encontrado']);
        }
        return (int) $row['id_activo'];
    }

    json_response(400, ['error' => 'Debes enviar id_activo o id_externo']);
}
