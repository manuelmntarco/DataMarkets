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

### Base de datos

Tras actualizar el repositorio, vuelve a importar `database/datamarkets_database_v2.sql` o ejecuta en MySQL la creación de la tabla `sesiones_usuario` si tu base ya existía sin ella.
