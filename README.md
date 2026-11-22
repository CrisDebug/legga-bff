# LEGGA – Backend for Frontend (BFF)

## 1. Descripción general

El BFF de LEGGA (Backend for Frontend) es el componente encargado de centralizar las solicitudes del frontend Angular y coordinar la comunicación con los microservicios del sistema.

Su objetivo es proporcionar una interfaz de acceso unificada, adaptar los contratos de datos y gestionar el contexto de identidad del usuario autenticado.

El BFF forma parte de una arquitectura basada en microservicios y permite desacoplar el frontend de la implementación interna de los servicios.

## 2. Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje de programación |
| Spring Boot 4.1.2 | Framework backend |
| Spring Web MVC | Desarrollo de API REST |
| Spring Security | Seguridad de solicitudes |
| Spring Boot Actuator | Información y estado del servicio |
| Spring RestClient | Comunicación HTTP entre servicios |
| MapStruct 1.6.3 | Transformación de DTO |
| Maven | Gestión de dependencias y construcción |

## 3. Arquitectura y responsabilidades

El BFF actúa como intermediario entre Angular y los servicios backend de LEGGA.

Sus principales responsabilidades son:

- Recibir solicitudes provenientes del frontend.
- Resolver la identidad del usuario autenticado mediante Azure Easy Auth.
- Obtener el contexto institucional y los permisos funcionales desde MS-Legajos.
- Adaptar solicitudes y respuestas mediante DTO y MapStruct.
- Coordinar consultas y operaciones sobre legajos, categorías y documentos.
- Propagar el contexto de identidad hacia MS-Legajos mediante encabezados HTTP.

### Componentes relacionados

- **Frontend:** Angular.
- **BFF:** Spring Boot.
- **MS-Legajos:** gestión de legajos, categorías y contexto de identidad.
- **MS-Documentos:** consulta y descarga de documentos.
- **Azure Easy Auth:** autenticación e identificación del usuario.
- **Azure Functions – Auditoría:** componente independiente de auditoría.

## 4. Estructura del proyecto

```text
bff-legga/
├── src/
│   └── main/
│       ├── java/cl/legga/bff/
│       │   ├── auth/
│       │   │   └── controller/
│       │   ├── categorias/
│       │   │   ├── client/
│       │   │   ├── controller/
│       │   │   └── dto/
│       │   ├── config/
│       │   ├── documentos/
│       │   │   ├── client/
│       │   │   └── controller/
│       │   ├── identidad/
│       │   │   ├── controller/
│       │   │   ├── model/
│       │   │   ├── resolver/
│       │   │   └── service/
│       │   ├── legajos/
│       │   │   ├── client/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   └── mapper/
│       │   └── security/
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties
│           └── application-prod.properties
├── Dockerfile
├── pom.xml
└── .gitignore
```

## 5. Endpoints disponibles

Base de rutas: `/api/v1`

### 5.1 Autenticación

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/auth/diagnostic` | Diagnóstico de identidad y claims recibidos desde Easy Auth. |

El endpoint permite revisar la decodificación del principal y comprobar la presencia de los identificadores de tenant y usuario.

### 5.2 Categorías

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/categorias` | Consulta categorías disponibles. |

Parámetros opcionales:

- `activa`: permite filtrar por estado.
- `page`: número de página. Valor predeterminado: 0.
- `size`: cantidad de registros. Valor predeterminado: 100.

Ejemplo:

`GET /api/v1/categorias?activa=true&page=0&size=20`

La información se obtiene desde MS-Legajos.

### 5.3 Documentos

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/documentos/legajo/{legajoId}` | Consulta los documentos asociados a un legajo. |
| GET | `/documentos/{id}` | Consulta un documento por identificador. |
| GET | `/documentos/{id}/descarga` | Solicita la generación de un enlace de descarga. |

Los identificadores utilizados corresponden a UUID.

La comunicación se realiza con MS-Documentos.

### 5.4 Identidad

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/identidad/contexto` | Obtiene el contexto del usuario autenticado. |

El flujo de identidad es:

1. El frontend realiza una solicitud al BFF.
2. Azure Easy Auth proporciona la información de identidad.
3. El BFF resuelve los identificadores del usuario y tenant.
4. El BFF consulta el contexto funcional en MS-Legajos.
5. MS-Legajos devuelve información del usuario, institución, perfil y permisos.
6. El BFF entrega el resultado al frontend.

El endpoint no recibe identificadores de usuario enviados directamente desde Angular.

### 5.5 Legajos

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/legajos` | Consulta los legajos del usuario autenticado. |
| GET | `/legajos/{id}` | Consulta el detalle de un legajo. |
| POST | `/legajos` | Crea un nuevo legajo. |

La creación adapta el contrato público recibido desde Angular al contrato requerido por MS-Legajos mediante `LegajoMapper`.

El identificador de categoría se envía como parámetro de consulta `categoriaId`.

Ejemplo de ruta:

`POST /api/v1/legajos?categoriaId={uuid}`

La operación devuelve HTTP 201 cuando se crea correctamente.

## 6. Seguridad e identidad

La gestión de identidad utiliza Azure Easy Auth y el encabezado `X-MS-CLIENT-PRINCIPAL`.

El componente `EasyAuthPrincipalResolver` obtiene los identificadores de identidad necesarios para las operaciones del sistema.

Para las solicitudes dirigidas a MS-Legajos y relacionadas con legajos y categorías, el BFF propaga:

- `X-LEGGA-TENANT-ID`
- `X-LEGGA-OBJECT-ID`

Spring Security administra las reglas de acceso y CORS.

La configuración contempla dos modos:

- **Desarrollo:** habilita determinadas operaciones para facilitar las pruebas.
- **Producción:** requiere autenticación para las operaciones protegidas.

El modo de desarrollo no debe utilizarse como configuración de seguridad para producción.

## 7. Configuración

La configuración se administra mediante archivos de propiedades de Spring Boot y perfiles de ejecución.

Propiedades principales:

| Propiedad | Descripción |
|---|---|
| `legga.cors.allowed-origins` | Orígenes autorizados para solicitudes CORS. |
| `legga.security.dev-mode` | Activa o desactiva el modo de desarrollo. |
| `legga.ms-legajos.base-url` | URL base de MS-Legajos. |
| `legga.ms-documentos.base-url` | URL base de MS-Documentos. |
| `spring.application.name` | Nombre de la aplicación. |
| `server.port` | Puerto HTTP del BFF. |
| `spring.profiles.active` | Perfil activo de Spring. |

Los valores deben configurarse según el entorno de ejecución.

No se deben almacenar credenciales ni secretos directamente en el repositorio.

## 8. Ejecución local

Requisitos:

- JDK 17.
- Maven.
- Acceso a las instancias de MS-Legajos y MS-Documentos.

Configurar las propiedades de conexión y el perfil correspondiente.

Ejecutar:

```bash
mvn clean install
```

Iniciar el servicio:

```bash
mvn spring-boot:run
```

El puerto de ejecución depende de la propiedad `server.port`.

## 9. Estado del desarrollo

El BFF cuenta con los siguientes componentes implementados:

- Controladores REST para autenticación, identidad, categorías, legajos y documentos.
- Clientes HTTP para comunicación con los microservicios.
- Resolución de identidad mediante Easy Auth.
- Adaptación de solicitudes mediante DTO y MapStruct.
- Configuración de seguridad y CORS.
- Configuración diferenciada para desarrollo y producción.
- Dockerfile para empaquetamiento del servicio.

El proyecto se encuentra en desarrollo e integración con los demás componentes de LEGGA.

## 10. Repositorio

GitHub:

https://github.com/CrisDebug/legga-bff

Rama principal: `master`

---

**Proyecto:** LEGGA – Sistema de gestión de legajos institucionales.

**Componente:** Backend for Frontend (BFF).

**Arquitectura:** Microservicios.