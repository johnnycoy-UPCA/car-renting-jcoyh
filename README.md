# AutoRent — Sistema de Alquiler de Autos (`pc-autorent-jcoyh`)

## Información General
* **Proyecto:** Sistema de Gestión de Alquiler de Vehículos
* **Entorno de Trabajo Principal:** Visual Studio Code (VS Code) + Maven + H2 Database
* **Entorno de Trabajo Paralelo:** NetBeans
* **Tecnologías Utilizadas:** Java 17, Spring Boot, Spring Data JPA, H2 In-Memory Database, REST Client (VS Code).

---

## Estructura de la Arquitectura Hexagonal
El proyecto sigue estrictamente el principio de inversión de dependencias y aislamiento del núcleo del negocio (*Ports and Adapters*), distribuido bajo la siguiente jerarquía de paquetes:

* **`domain` (El Corazón del Negocio):** Contiene los POJOs puros y las reglas de negocio (`Auto.java`, `Alquiler.java`) completamente agnósticos a Spring Boot, JPA o frameworks, junto con las excepciones del negocio (`AutoNoDisponibleException`, `AlquilerNoValidoException`).
* **`application` (Casos de Uso y Servicios):** Define qué puede hacer el sistema mediante contratos de entrada (`RegistrarAutoUseCase`, `CrearAlquilerUseCase`, `FinalizarAlquilerUseCase`) e interfaces abstractas de salida (`AutoRepositoryPort`, `AlquilerRepositoryPort`), orquestados por servicios puramente Java (`AutoService`, `AlquilerService`).
* **`infrastructure` (Adaptadores y Tecnologías Externas):** 
  * `adapter/input/rest`: Controladores HTTP (`AutoController`, `AlquilerController`) encargados de recibir JSONs y delegar a la aplicación.
  * `adapter/output/persistence`: Entidades físicas JPA (`AutoEntity`, `AlquilerEntity`), repositorios de Spring Data (`AutoJpaRepository`, `AlquilerJpaRepository`), mappers de aislamiento (`AutoMapper`, `AlquilerMapper`) y los adaptadores de base de datos.
  * `config`: Zona franca tecnológica (`BeanConfiguration.java`) que realiza el cableado de inyección explícita en el contenedor de Spring Boot.

---

## Configuración y Despliegue del Sistema
* **Puerto de Escucha:** La aplicación corre de forma aislada en el puerto **`8081`** para evitar colisiones con el desarrollo paralelo de NetBeans (`8080`).
* **URL de Acceso a Consola H2:** `http://localhost:8081/h2-console`
* **Credenciales Relacionales:**
  * **JDBC URL:** `jdbc:h2:mem:autorentdb`
  * **User Name:** `sa`
  * **Password:** *(Vacío)*

---

## Endpoints y Ejemplos de Interacción REST

### 1. Registrar Vehículo
* **Ruta:** `POST /api/autos`
* **Payload de Entrada:**
```json
{
  "placa": "JC-777",
  "marca": "Mazda",
  "modelo": "CX-30",
  "anio": 2026,
  "precioPorDia": 180000.0,
  "deposito": 500000.0,
  "kilometraje": 12500
}
```

### 2. Crear Alquiler
* **Ruta:** `POST /api/alquileres`
* **Payload de Entrada:**
```json
{
  "clienteId": 45,
  "autoId": 1,
  "cantidadDias": 5
}
```

### 3. Finalizar Alquiler
* **Ruta:** `PUT /api/alquileres/{id}/finalizar`
* **Payload de Entrada:**
```json
{
  "kilometrajeFinal": 12850
}
```
