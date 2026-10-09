# Imperial QR — Sistema de Pedidos en Mesa y Domicilios del Restaurante Imperial

Servicio web API REST desarrollado con **Spring Boot 3**, **Java 17**, arquitectura en capas con principios **SOLID**, persistencia con **Spring Data JPA** / **PostgreSQL** (compatible en pruebas con H2), migraciones automáticas versionadas con **Flyway**, autenticación mediante **JWT** con control de acceso por roles, contenedorización completa con **Docker & Docker Compose**, y pruebas automatizadas con **JUnit 5**, **Mockito** y **Postman**.

---

## 1. Descripción del Proyecto

El sistema **Imperial QR** moderniza el flujo operativo del Restaurante Imperial:
1. **Pedidos en Mesa vía QR**: Cada mesa cuenta con un código QR único con un identificador UUID v4 no secuencial. El comensal escanea el código desde cualquier navegador móvil sin instalar aplicaciones, consulta la carta digital estructurada por categorías, personaliza ingredientes (quitando ingredientes removibles o agregando extras con costo adicional) y envía la orden directamente a la cocina.
2. **Flujo de Cocina y Servicio**: Los cocineros ven comandas en tiempo real con modificaciones exactas y avanzan los estados (`RECIBIDO` → `EN_PREPARACION` → `LISTO`). Los meseros visualizan los platos listos junto con la mesa de destino y entregan la comida marcándola como `ENTREGADO`.
3. **Cierre de Cuenta y Pagos**: El administrador valida que todos los platos estén entregados o cancelados y genera la orden de pago (subtotal, 8% de impuesto al consumo, 10% de propina voluntaria sugerida). Se aceptan múltiples métodos de pago (Efectivo, Tarjeta, Transferencia, Billeteras Digitales) y pagos divididos.
4. **Encuesta de Satisfacción**: Tras completarse el pago, el cliente califica el servicio y la comida (1 a 5 estrellas), alimentando el reporte de satisfacción gerencial.
5. **Módulo de Domicilios**: Toma de pedidos a domicilio con registro de datos de entrega y asignación de repartidores.

---

## 2. Tecnologías y Arquitectura

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Seguridad**: Spring Security 6 + JJWT (HMAC SHA-256) + BCrypt
- **Persistencia**: Spring Data JPA + Hibernate + PostgreSQL 16 (H2 para pruebas rápidas)
- **Migraciones de BD**: Flyway 9/10
- **Documentación de API**: Springdoc OpenAPI / Swagger UI (OpenAPI 3.0)
- **Contenedorización**: Docker (construcción multietapa) + Docker Compose
- **Pruebas**: JUnit 5, Mockito 5 (mockito-subclass), JaCoCo
- **Arquitectura**: Arquitectura en capas limpia y monolito modular desacoplado:
  - `com.imperial.qr.config`: Configuraciones de seguridad, propiedades y Swagger.
  - `com.imperial.qr.controller`: Controladores REST livianos (solo mapeo HTTP y validaciones).
  - `com.imperial.qr.dto`: Records inmutables para peticiones y respuestas.
  - `com.imperial.qr.service`: Lógica de negocio y orquestación con `@Transactional`.
  - `com.imperial.qr.service.calculo`: Componentes de cálculo de responsabilidad única (SRP: `CalculadoraPrecio`, `CalculadoraTotales`).
  - `com.imperial.qr.service.strategy`: Patrón Strategy para medios de pago (OCP/LSP).
  - `com.imperial.qr.domain.model`: Modelo de dominio rico con validaciones y reglas de negocio.
  - `com.imperial.qr.domain.enums`: Enumeraciones con máquinas de estado (`EstadoDetalle.puedePasarA`).
  - `com.imperial.qr.repository`: Repositorios Spring Data JPA con consultas JPQL especializadas.
  - `com.imperial.qr.security`: Filtro JWT, servicio de token y UserDetails.
  - `com.imperial.qr.exception`: `GlobalExceptionHandler` unificado con `@RestControllerAdvice`.
  - `com.imperial.qr.mapper`: Mappers desacoplados entre entidades y DTOs.

---

## 3. Requisitos Previos

- **Java JDK 17** o superior instalado.
- **Maven 3.8+** (o el runtime de Maven incluido).
- **Docker** y **Docker Compose** (opcional para ejecución en contenedores).

---

## 4. Puesta en Marcha

### Opción A: Ejecución Local con Maven y H2/PostgreSQL

1. Clonar el repositorio y ubicarse en el directorio raíz:
   ```bash
   cd Restaurante-Imperial
   ```

2. Ejecutar las pruebas unitarias:
   ```bash
   mvn clean test
   ```

3. Iniciar la aplicación:
   ```bash
   mvn spring-boot:run
   ```
   La aplicación se iniciará en `http://localhost:8080`.

### Opción B: Ejecución con Docker Compose (API + PostgreSQL)

1. Crear el archivo `.env` a partir de la plantilla:
   ```bash
   cp .env.example .env
   ```

2. Levantar los contenedores en segundo plano:
   ```bash
   docker compose up -d --build
   ```

3. Verificar que los contenedores estén saludables:
   ```bash
   docker compose ps
   ```

4. Detener los servicios:
   ```bash
   docker compose down
   ```

---

## 5. Documentación Interactiva de la API

Una vez iniciada la aplicación, acceda a la consola interactiva Swagger UI:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 6. Usuarios y Credenciales Semilla (Seed Data)

La base de datos se inicializa automáticamente mediante Flyway con las siguientes cuentas de prueba (contraseña para todos: `Imperial123*`):

| Rol | Nombre | Correo Electrónico | Contraseña |
| :--- | :--- | :--- | :--- |
| **ADMIN** | Carlos Méndez | `admin@imperial.com` | `Imperial123*` |
| **COCINERO** | Chef Lin | `cocina@imperial.com` | `Imperial123*` |
| **MESERO** | Andrés Gómez | `mesero@imperial.com` | `Imperial123*` |
| **DOMICILIARIO** | Mateo Silva | `domicilio@imperial.com` | `Imperial123*` |

### Mesas Precargadas y Códigos QR

- **Mesa 1** (Capacidad: 2): `550e8400-e29b-41d4-a716-446655440001`
- **Mesa 2** (Capacidad: 4): `550e8400-e29b-41d4-a716-446655440002`
- **Mesa 3** (Capacidad: 4): `550e8400-e29b-41d4-a716-446655440003`
- **Mesa 4** (Capacidad: 6): `550e8400-e29b-41d4-a716-446655440004`
- **Mesa 5** (Capacidad: 4): `550e8400-e29b-41d4-a716-446655440005`

---

## 7. Pruebas Funcionales en Postman

En la carpeta `postman/` se suministran los artefactos listos para importar:
1. `postman/Imperial_QR_Collection.json`: Colección con 20 peticiones organizadas por carpetas y scripts de aserción automáticos.
2. `postman/Imperial_QR_Environment.json`: Variables de entorno preconfiguradas (`baseUrl`, `codigoQr`, tokens dinámicos).

### Ejecución de Pruebas:
1. Abra Postman e importe ambos archivos.
2. Seleccione el entorno `Imperial QR - Local Environment`.
3. Ejecute la colección mediante el **Collection Runner** para verificar el 100% de los escenarios (PF-01 a PF-14).

---

## 8. Cumplimiento de Reglas de Negocio (RN)

- **RN-01**: Una mesa solo tiene una orden en estado `ABIERTA`; pedidos subsecuentes se incorporan a ella.
- **RN-02**: Solo se quitan ingredientes marcados como removibles y solo se adicionan los marcados como adicionables.
- **RN-03**: Quitar ingredientes no reduce el precio base; agregar ingredientes suma el precio extra configurado.
- **RN-04**: No se pueden ordenar platos o ingredientes no disponibles (HTTP 422).
- **RN-05**: Transiciones estrictas de estado (`RECIBIDO` → `EN_PREPARACION` → `LISTO` → `ENTREGADO`). Cancelación solo en `RECIBIDO`.
- **RN-06**: Restricción de transiciones por rol (cocinero prepara y deja listo; mesero entrega en mesa; repartidor entrega domicilio).
- **RN-07**: No se permiten adiciones a órdenes `CERRADA` o `PAGADA`.
- **RN-08**: El administrador solo cierra la cuenta si todos los platos están `ENTREGADO` o `CANCELADO`.
- **RN-09**: Total = Subtotal + 8% Impoconsumo + 10% Propina voluntaria + Envíos.
- **RN-10**: La orden pasa a `PAGADA` únicamente al cubrirse la totalidad del saldo.
- **RN-11**: Encuesta de satisfacción única por orden y habilitada exclusivamente tras el pago completo.
- **RN-12**: Domicilios no asociados a mesa y con obligatoriedad de datos de entrega.
- **RN-13**: Tratamiento riguroso de datos personales conforme a la Ley 1581 de 2012.
