# Spring Boot Authentication API

Este proyecto es una API RESTful para manejar autenticación de usuarios, implementada en Java con Spring Boot. Incluye funcionalidades como registro de usuarios, inicio de sesión y manejo de tokens de refresco (JWT).

## 📝 Funcionalidades

- **Registro de usuarios**: Permite crear cuentas con validación de datos.
- **Inicio de sesión**: Autenticación de usuarios utilizando JSON Web Tokens (JWT).
- **Tokens de refresco**: Generación y validación de tokens de acceso y refresh.
- **Seguridad**: Configuración con Spring Security y cifrado de contraseñas (BCrypt).
- **Manejo de errores**: Respuestas claras y consistentes para errores comunes.

---

## 🚀 Tecnologías utilizadas

- **Java 20**: Lenguaje de programación.
- **Spring Boot**: Framework principal.
- **Spring Security**: Para la gestión de autenticación y autorización.
- **JWT (JSON Web Tokens)**: Para manejar sesiones seguras.
- **PostgreSQL**: Base de datos relacional.
- **Lombok**: Para reducir el boilerplate en el código.
- **Maven**: Gestión de dependencias.

---

## 📂 Estructura del proyecto

```plaintext
src/
├── main/
│   ├── java/
│   │   ├── com.example.security/  # Paquete principal
│   │   │   ├── controllers/       # Controladores de la API
│   │   │   ├── models/            # Entidades y DTOs
│   │   │   ├── repositories/      # Acceso a datos
│   │   │   ├── security/          # Configuración de Spring Security
│   │   │   ├── services/          # Lógica de negocio
│   ├── resources/
│       ├── application.properties # Configuración de la aplicación
│       ├── application-dev.properties # Configuración local
