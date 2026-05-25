# UrbanCore API

UrbanCore API is a Spring Boot backend for reporting and managing urban incidents. It provides secure REST endpoints, role-based access control, cloud image support, geolocation integration, and AI-powered suggestions to help users create incident reports faster.

## Getting Started 🚀

These instructions will help you run a local copy of the project for development and testing purposes.

Check **Deployment** to learn how to access the live environment and documentation.

### Prerequisites 📋

Things you need before installing and running the project:

- Java 21
- Maven 3.9+ (or use the included Maven Wrapper)
- PostgreSQL
- Firebase project configured for JWT validation
- Cloudinary, Geoapify, and Gemini API credentials

Example:

```bash
java -version
mvn -version
```

### Installation 🔧

A step-by-step guide to get a development environment running.

1) Clone the repository

```bash
git clone <repository-url>
cd urbancore-api
```

2) Configure environment variables

Use one of the following options.

Option A (recommended): IntelliJ IDEA

- Open **Run | Edit Configurations...**
- Select your Spring Boot run configuration
- Add the variables in **Environment variables**
- Save and run the app from IntelliJ

Option B: Terminal session with `export`

`export` makes variables available to commands executed in the same terminal session.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/urbancore
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_cloudinary_key
export CLOUDINARY_API_SECRET=your_cloudinary_secret
export GEOAPIFY_API_KEY=your_geoapify_key
export GEMINI_API_KEY=your_gemini_key
export GEMINI_MODEL=gemini-2.5-flash
```

3) Build the project

```bash
./mvnw clean install
```

4) Run the application

```bash
./mvnw spring-boot:run
```

5) Validate the app is running

```bash
curl http://localhost:8080/v3/api-docs
```

You can now open Swagger UI locally at `http://localhost:8080/swagger-ui/index.html`.

## Running the tests ⚙️

Run the automated tests for this system with:

```bash
./mvnw test
```

## Production Demo Seed 🌱

UrbanCore includes a production-safe demo seed that is disabled by default and only runs when explicitly enabled.

### Enable the seed

Set both variables before starting the application:

```bash
export URBANCORE_SEED_ENABLED=true
export URBANCORE_SEED_MODE=production-demo
```

Then run:

```bash
./mvnw spring-boot:run
```

If either variable is missing or different, the seed does not run.

### Cloudinary image catalog

Seed images are read from:

- `src/main/resources/seed/cloudinary-seed-images.json`

The catalog is grouped by `IncidentCategory` and each image entry must contain:

- `publicId`
- `url`
- `thumbnailUrl`
- `alt`

Each category must have at least one image. In `production-demo` mode, startup fails fast with a clear error if any category is empty.

### Why the seed does not upload images

The seed never uploads files to Cloudinary. It only references existing assets already available in your Cloudinary account. This keeps startup deterministic, avoids accidental media duplication, and prevents unexpected external writes in production environments.

### How idempotency works

Seed idempotency is managed with the `seed_registry` table:

- `id`
- `seed_key`
- `entity_type`
- `entity_id`
- `created_at`

Each seeded entity gets a deterministic `seedKey` (for example `production-demo:incident:inc-001`).
On startup, if a `seedKey` is already registered, that entity is skipped.
This prevents duplicates for cities, users, incidents, images, status history, and planned actions across repeated application starts.

### Adding your real Cloudinary assets

1. Upload or identify the real images in Cloudinary manually.
2. Copy each asset `publicId` and public delivery URL.
3. Add/update entries in `cloudinary-seed-images.json` for each category.
4. Ensure every category has at least one valid image object.

### End-to-end tests 🔩

This backend currently emphasizes API and integration-level tests in the Spring test suite. If your team adds dedicated end-to-end flows, run them after the main test suite.

Example integration test run:

```bash
./mvnw -Dtest=*IntegrationTest test
```

### Code style and quality checks ⌨️

Use Maven lifecycle verification to validate packaging and quality gates before merging:

```bash
./mvnw verify
```

## Deployment 📦

UrbanCore API is deployed on DigitalOcean App Platform.

API URL:

- https://urbancore-api-oeon4.ondigitalocean.app

Swagger UI documentatio URL:

- https://urbancore-api-oeon4.ondigitalocean.app/swagger-ui/index.html

Additional deployment notes:

- Configure all required environment variables in the target environment.
- Ensure database and external providers (Cloudinary, Geoapify, Gemini) are reachable.
- Validate `/v3/api-docs` after each deployment.

## Built With 🛠️

- [Spring Boot](https://spring.io/projects/spring-boot) - Main backend framework
- [Spring Security](https://spring.io/projects/spring-security) - Authentication and authorization
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data access layer
- [PostgreSQL](https://www.postgresql.org/) - Relational database
- [Springdoc OpenAPI](https://springdoc.org/) - API documentation and Swagger UI
- [Spring AI](https://spring.io/projects/spring-ai) - AI integration (Google Gemini)
- [Maven](https://maven.apache.org/) - Dependency and build management

## Authors ✒️

Built with care by Miguel Pujazón Cárdenas.
