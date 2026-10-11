# Microservicio de Autenticación

Registra a los usuarios del sistema (vendedores y administradores) y entrega un token JWT al iniciar sesión.

Forma parte del **Sistema unificado de gestión de pedidos para vendedores de ropa por Instagram** (JVY0101 – Duoc UC). El sistema completo tiene 7 microservicios independientes, cada uno con su propio repositorio y su propia base de datos:

| Microservicio | Repositorio | Puerto | Base de datos |
|---|---|---|---|
| Autenticación | `autenticacion` | 8087 | `autenticacion_db` |
| Catálogo | `catalogo` | 8081 | `catalogo_db` |
| Clientes | `clientes` | 8082 | `clientes_db` |
| Pedidos | `pedidos` | 8083 | `pedidos_db` |
| Pagos | `pagos` | 8084 | `pagos_db` |
| Despachos | `despachos` | 8085 | `despachos_db` |
| Notificaciones | `notificaciones` | 8086 | `notificaciones_db` |

## Tecnologías

Java 21, Spring Boot 4.1, Spring Web MVC, Spring Data JPA / Hibernate, Bean Validation, Lombok, MySQL 8 y Maven (incluido con Maven Wrapper, no es necesario instalarlo).

## Arquitectura en capas

```
src/main/java/cl/duocuc/autenticacion/
├── controller/   -> Endpoints REST (reciben la petición HTTP)
├── service/      -> Reglas de negocio
├── repository/   -> Acceso a datos con JpaRepository
├── model/        -> Entidades JPA (tablas)
├── dto/          -> Objetos de entrada/salida (la password nunca se devuelve)
├── security/     -> JWT, filtro y configuración de Spring Security
└── exception/    -> Manejo centralizado de errores (404, 400, 409)
```

Flujo de una petición: `Controller -> Service -> Repository -> MySQL`.

## Requisitos

- Java 21 (`java -version`)
- MySQL 8 corriendo en `localhost:3306` (XAMPP, Laragon, MySQL Workbench o Docker)
- Git

## 1. Clonar el repositorio

```bash
git clone <URL_DE_ESTE_REPOSITORIO>
cd autenticacion
```

## 2. Configurar la base de datos

No hay que crear la base a mano: la URL usa `createDatabaseIfNotExist=true`, así que `autenticacion_db` se crea sola al iniciar, y Hibernate crea las tablas a partir de las entidades.

Por defecto se conecta con usuario `root` sin contraseña. Si tu MySQL usa otras credenciales, defínelas como variables de entorno antes de ejecutar:

```powershell
# Windows PowerShell
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="tu_password"
```

## 3. Instalar dependencias y compilar con Maven

```powershell
# Windows
.\mvnw.cmd clean install
```

```bash
# Linux / macOS
./mvnw clean install
```

- `clean` borra la carpeta `target/`.
- `install` descarga las dependencias del `pom.xml`, compila, ejecuta las pruebas (con base H2 en memoria) y empaqueta.
- También se puede usar `.\mvnw.cmd clean package` para solo generar el JAR.

El resultado es **`target/app.jar`** (definido con `<finalName>app</finalName>` en el `pom.xml`).

## 4. Ejecutar

```bash
java -jar target/app.jar
```

O directamente sin generar el JAR: `.\mvnw.cmd spring-boot:run`

El servicio queda disponible en **http://localhost:8087**.

## 5. Endpoints

Base: `http://localhost:8087/api/v1`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/v1/auth/registro` | Registra un usuario (público) | 201 / 400 / 409 |
| POST | `/api/v1/auth/login` | Inicia sesión y entrega el token JWT (público) | 200 / 401 |
| GET | `/api/v1/usuarios` | Lista usuarios (requiere token) | 200 / 401 |
| GET | `/api/v1/usuarios/{id}` | Busca por id (requiere token) | 200 / 404 |
| PUT | `/api/v1/usuarios/{id}` | Actualiza (requiere token) | 200 / 404 |
| DELETE | `/api/v1/usuarios/{id}` | Elimina (requiere token) | 204 / 404 |

Ejemplo de cuerpo JSON:

```json
{
  "username": "martin",
  "password": "1234",
  "email": "martin@correo.cl",
  "rol": "ADMIN"
}
```

Códigos de error: `400` datos inválidos, `404` recurso no encontrado, `409` regla de negocio (dato duplicado).

## Uso del token

1. `POST /api/v1/auth/registro` con el JSON de ejemplo.
2. `POST /api/v1/auth/login` con `{"username":"martin","password":"1234"}` → copia el `token`.
3. En las demás peticiones agrega el header `Authorization: Bearer <token>`. El token dura 1 hora.

## Ejecutar con Docker (opcional)

```bash
docker build -t autenticacion:1.0 .
```

El repositorio incluye un `Dockerfile` multi-etapa: Maven compila dentro de Docker y luego Java 21 ejecuta el JAR. Para levantar los 7 microservicios con sus bases, ver el `docker-compose.yml` del proyecto.

## Integrantes

- Martín Meliman
- Ignacio Lapierre
