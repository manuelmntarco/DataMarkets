# API REST — DataMarkets (backend PHP)

Base de ejemplo en XAMPP: coloca la carpeta `backend` bajo `htdocs` y ajusta la ruta. Los ejemplos usan `http://localhost/DataMarkets/backend/public/…` (adapta el segmento a tu instalación).

## Autenticación

Todas las respuestas exitosas de usuario devuelven el mismo shape JSON que consume Gson en `Usuario.java`: `id_usuario`, `nombre`, `email`, `token`.

### POST `/api/auth/register.php`

- **Body (JSON):** `nombre` (string), `email` (string), `password` (string, mínimo 8 caracteres).
- **201:** usuario creado; cuerpo: `{ "id_usuario", "nombre", "email", "token" }`.
- **400 / 409 / 405 / 500:** `{ "error": "mensaje" }`.

### POST `/api/auth/login.php`

- **Body (JSON):** `email`, `password`.
- **200:** `{ "id_usuario", "nombre", "email", "token" }`.
- **400 / 401 / 405 / 500:** `{ "error": "mensaje" }`.

Contraseñas: se almacenan con **bcrypt** (`password_hash` / `password_verify` en PHP), coherente con el script MySQL del proyecto.

## Seguimiento (favoritos)

Todos los endpoints de seguimiento requieren cabecera `Authorization: Bearer <token>`.

### GET `/api/seguimiento.php`

- **Descripción:** lista los favoritos del usuario autenticado usando la vista `v_seguimiento_usuario`.
- **200:** `{ "items": [ ... ] }`.
- **401 / 405 / 500:** `{ "error": "mensaje" }`.

### POST `/api/seguimiento.php`

- **Body (JSON):** `id_activo` (int) o `id_externo` (string). Debe venir uno de los dos.
- **201:** `{ "mensaje": "Favorito anadido", "item": { ... } }`.
- **400 / 401 / 404 / 409 / 405 / 500:** `{ "error": "mensaje" }`.

### DELETE `/api/seguimiento.php`

- **Parámetro:** `id_activo` o `id_externo` por query string (recomendado) o en body JSON.
- **Ejemplo:** `/api/seguimiento.php?id_externo=bitcoin`.
- **200:** `{ "mensaje": "Favorito eliminado" }`.
- **400 / 401 / 404 / 405 / 500:** `{ "error": "mensaje" }`.

## Configuración de usuario

Todos los endpoints de configuración requieren cabecera `Authorization: Bearer <token>`.

### GET `/api/configuracion.php`

- **Descripción:** obtiene la configuración del usuario autenticado desde `configuracion_usuario`.
- **Nota:** si el usuario no tiene fila de configuración, el backend la crea automáticamente con valores por defecto (`EUR`, `sistema`, `true`, `300`).
- **200:**
  ```json
  {
    "configuracion": {
      "id_usuario": 1,
      "moneda": "EUR",
      "tema": "sistema",
      "notificaciones": true,
      "intervalo_refresco_seg": 300,
      "actualizado_en": "2026-04-27 09:20:00"
    }
  }
  ```
- **401 / 405 / 500:** `{ "error": "mensaje" }`.

### PUT `/api/configuracion.php`

- **Descripción:** actualiza parcialmente la configuración del usuario autenticado (solo los campos enviados).
- **Body (JSON, opcional por campo):**
  - `moneda`: string de 3 letras en mayúsculas (formato ISO), ejemplo `EUR`, `USD`.
  - `tema`: `claro` | `oscuro` | `sistema`.
  - `notificaciones`: boolean (`true/false`) o entero `0/1`.
  - `intervalo_refresco_seg`: entero entre `15` y `86400`.
- **Campos no permitidos:** cualquier clave fuera de las anteriores devuelve `400`.
- **200:**
  ```json
  {
    "mensaje": "Configuracion actualizada",
    "configuracion": {
      "id_usuario": 1,
      "moneda": "USD",
      "tema": "oscuro",
      "notificaciones": false,
      "intervalo_refresco_seg": 120,
      "actualizado_en": "2026-04-27 09:25:00"
    }
  }
  ```
- **400 / 401 / 405 / 500:** `{ "error": "mensaje" }`.

### Errores de validación típicos (PUT)

- `moneda` inválida: `moneda debe tener formato ISO de 3 letras`.
- `tema` inválido: `tema debe ser claro, oscuro o sistema`.
- `notificaciones` inválido: `notificaciones debe ser booleano o 0/1`.
- `intervalo_refresco_seg` fuera de rango: `intervalo_refresco_seg debe estar entre 15 y 86400`.
- body sin campos actualizables: `Debes enviar al menos un campo actualizable`.

### Base de datos

Tras actualizar el repositorio, vuelve a importar `database/datamarkets_database_v2.sql` o ejecuta en MySQL la creación de la tabla `sesiones_usuario` si tu base ya existía sin ella.
