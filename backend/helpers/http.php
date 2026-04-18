<?php

declare(strict_types=1);

/**
 * Respuestas JSON y lectura del cuerpo para la API REST.
 */

function json_response(int $statusCode, array $payload): never
{
    http_response_code($statusCode);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR);
    exit;
}

/**
 * @return array<string, mixed>
 */
function read_json_body(): array
{
    $raw = file_get_contents('php://input');
    if ($raw === false || $raw === '') {
        return [];
    }

    try {
        $data = json_decode($raw, true, 512, JSON_THROW_ON_ERROR);
    } catch (JsonException) {
        json_response(400, ['error' => 'JSON inválido']);
    }

    return is_array($data) ? $data : [];
}
