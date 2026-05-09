# AGENTS.md

## API Documentation Policy

UrbanCore uses Springdoc OpenAPI and Swagger UI to document the Spring Boot REST API.

Whenever an agent or developer creates, modifies, renames, deprecates, or deletes a REST endpoint, they must update the API documentation in the same change.

This includes:

- Controller-level `@Tag` annotations.
- Endpoint-level `@Operation` descriptions.
- `@ApiResponses` and expected HTTP status codes.
- `@Parameter` documentation for path and query parameters.
- Request and response DTO schemas (`@Schema`).
- Security requirements such as Bearer JWT authentication (`@SecurityRequirement`).
- Public/private access notes in operation descriptions.
- Role requirements such as `ROLE_CITIZEN` or `ROLE_ADMIN`.
- Error response documentation referencing `ApiErrorResponse`.

## Required checks before completing backend endpoint work

Before considering endpoint work complete, verify:

1. The endpoint appears correctly in Swagger UI (`/swagger-ui/index.html`).
2. The endpoint has a clear `summary` and `description`.
3. Request bodies and response bodies are documented with `@Content` / `@Schema`.
4. Query params and path params are documented with `@Parameter`.
5. Authentication and role requirements are documented via `@SecurityRequirement` and operation description text.
6. Error responses (`400`, `401`, `403`, `404`, `409`, `500`) are documented.
7. DTO examples in `@Schema` annotations are still accurate.
8. `/v3/api-docs` generates without errors (validate via `curl http://localhost:8080/v3/api-docs` or browser).

## Documentation must stay close to code

Do not maintain a separate manual endpoint list that can easily become outdated.

The source of truth for API documentation should be:

- Spring controllers (`@RestController` classes).
- Request/response DTOs with `@Schema` annotations.
- OpenAPI annotations (`@Tag`, `@Operation`, `@ApiResponses`).
- Shared error response classes (`ApiErrorResponse`, `FieldErrorResponse`).

## Error response contract

All endpoints return errors using the `ApiErrorResponse` format:

```json
{
  "timestamp": "2026-04-16T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_FAILED",
  "message": "Description is required",
  "path": "/api/incidents",
  "fieldErrors": [],
  "traceId": "9f5b0d9d2a"
}
```

## When in doubt

If an endpoint changes behavior, permissions, parameters, request body, response body, status codes, or error format, update the OpenAPI documentation immediately.

## Quick reference

- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI config: `src/main/java/com/urbancore/urbancore_api/config/OpenApiConfig.java`
- Error response DTOs: `src/main/java/com/urbancore/urbancore_api/dtos/ApiErrorResponse.java`, `FieldErrorResponse.java`
- Global exception handler: `src/main/java/com/urbancore/urbancore_api/controllers/GlobalExceptionHandler.java`
