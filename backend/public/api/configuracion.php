<?php

declare(strict_types=1);

/**
 * Configuracion de usuario
 * - GET: obtener preferencias del usuario autenticado
 * - PUT JSON: actualizar moneda, tema, notificaciones, intervalo_refresco_seg
 */

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/helpers/http.php';
require_once dirname(__DIR__, 2) . '/helpers/auth_service.php';

header('Access-Control-Allow-Methods: GET, PUT, OPTIONS');
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
    $config = get_or_create_config($pdo, $idUsuario);
    json_response(200, ['configuracion' => $config]);
}

if ($method === 'PUT') {
    $data = read_json_body();
    $updates = build_update_payload($data);
    if ($updates === []) {
        json_response(400, ['error' => 'Debes enviar al menos un campo actualizable']);
    }

    get_or_create_config($pdo, $idUsuario);

    $sets = [];
    $params = [];
    foreach ($updates as $field => $value) {
        $sets[] = $field . ' = ?';
        $params[] = $value;
    }
    $params[] = $idUsuario;

    $sql = 'UPDATE configuracion_usuario SET ' . implode(', ', $sets) . ' WHERE id_usuario = ?';
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);

    $config = get_or_create_config($pdo, $idUsuario);
    json_response(200, [
        'mensaje' => 'Configuracion actualizada',
        'configuracion' => $config,
    ]);
}

json_response(405, ['error' => 'Metodo no permitido']);

function get_or_create_config(PDO $pdo, int $idUsuario): array
{
    $stmt = $pdo->prepare(
        'SELECT id_usuario, moneda, tema, notificaciones, intervalo_refresco_seg, actualizado_en
         FROM configuracion_usuario
         WHERE id_usuario = ?
         LIMIT 1'
    );
    $stmt->execute([$idUsuario]);
    $row = $stmt->fetch();

    if ($row === false) {
        $insert = $pdo->prepare(
            "INSERT INTO configuracion_usuario (id_usuario, moneda, tema, notificaciones, intervalo_refresco_seg)
             VALUES (?, 'EUR', 'sistema', 1, 300)"
        );
        $insert->execute([$idUsuario]);

        $stmt->execute([$idUsuario]);
        $row = $stmt->fetch();
        if ($row === false) {
            json_response(500, ['error' => 'No se pudo cargar la configuracion']);
        }
    }

    return [
        'id_usuario' => (int) $row['id_usuario'],
        'moneda' => (string) $row['moneda'],
        'tema' => (string) $row['tema'],
        'notificaciones' => (int) $row['notificaciones'] === 1,
        'intervalo_refresco_seg' => (int) $row['intervalo_refresco_seg'],
        'actualizado_en' => (string) $row['actualizado_en'],
    ];
}

/**
 * @param array<string, mixed> $input
 * @return array<string, string|int>
 */
function build_update_payload(array $input): array
{
    $allowed = ['moneda', 'tema', 'notificaciones', 'intervalo_refresco_seg'];
    $updates = [];

    foreach ($input as $key => $_value) {
        if (!in_array($key, $allowed, true)) {
            json_response(400, ['error' => 'Campo no permitido: ' . $key]);
        }
    }

    if (array_key_exists('moneda', $input)) {
        $moneda = strtoupper(trim((string) $input['moneda']));
        if (!preg_match('/^[A-Z]{3}$/', $moneda)) {
            json_response(400, ['error' => 'moneda debe tener formato ISO de 3 letras']);
        }
        $updates['moneda'] = $moneda;
    }

    if (array_key_exists('tema', $input)) {
        $tema = trim((string) $input['tema']);
        $temasValidos = ['claro', 'oscuro', 'sistema'];
        if (!in_array($tema, $temasValidos, true)) {
            json_response(400, ['error' => 'tema debe ser claro, oscuro o sistema']);
        }
        $updates['tema'] = $tema;
    }

    if (array_key_exists('notificaciones', $input)) {
        $raw = $input['notificaciones'];
        if (is_bool($raw)) {
            $updates['notificaciones'] = $raw ? 1 : 0;
        } elseif (is_int($raw) && ($raw === 0 || $raw === 1)) {
            $updates['notificaciones'] = $raw;
        } else {
            json_response(400, ['error' => 'notificaciones debe ser booleano o 0/1']);
        }
    }

    if (array_key_exists('intervalo_refresco_seg', $input)) {
        if (!is_int($input['intervalo_refresco_seg'])) {
            json_response(400, ['error' => 'intervalo_refresco_seg debe ser entero']);
        }
        $intervalo = (int) $input['intervalo_refresco_seg'];
        if ($intervalo < 15 || $intervalo > 86400) {
            json_response(400, ['error' => 'intervalo_refresco_seg debe estar entre 15 y 86400']);
        }
        $updates['intervalo_refresco_seg'] = $intervalo;
    }

    return $updates;
}
