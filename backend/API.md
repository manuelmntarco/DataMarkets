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

### Base de datos

Tras actualizar el repositorio, vuelve a importar `database/datamarkets_database_v2.sql` o ejecuta en MySQL la creación de la tabla `sesiones_usuario` si tu base ya existía sin ella.
