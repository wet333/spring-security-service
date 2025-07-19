# Environment Setup

This guide explains how to set up the development infrastructure using standalone Docker containers instead of docker-compose. This approach allows you to reuse the same infrastructure components across multiple projects.

## Required Infrastructure Components

### 1. PostgreSQL Database (Global)

Create and run a single PostgreSQL container that will host both your application database and Keycloak database:

```bash
# Create a named volume for persistent data
docker volume create postgres_data_volume

# Run PostgreSQL container
docker run -d \
  --name postgres-db \
  --restart always \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -v postgres_data_volume:/var/lib/postgresql/data \
  postgres:15
```

### 2. Setup PostgreSQL Users and Databases

After the PostgreSQL container is running, create the required databases:

```bash
# Create application database
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE \"resource-db\";"

# Create Keycloak database
docker exec -it postgres-db psql -U postgres -c "CREATE DATABASE keycloak;"

# Create users (optional, for better security)
docker exec -it shared-db psql -U postgres -c "CREATE USER username WITH PASSWORD 'password';"
docker exec -it shared-db psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE \"resource-db\" TO username;"
docker exec -it shared-db psql -U postgres -c "CREATE USER keycloak_user WITH PASSWORD 'password';"
docker exec -it shared-db psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak_user;"
```

### 3. Keycloak Authentication Server

Run Keycloak container (Use the credentials of the user created in the previous step):

```bash
# Run Keycloak container
docker run -d \
  --name keycloak \
  --restart always \
  -e KC_DB=postgres \
  -e KC_DB_URL=jdbc:postgresql://host.docker.internal:5432/keycloak \
  -e KC_DB_USERNAME=keycloak_user \
  -e KC_DB_PASSWORD=password \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -p 9000:8080 \
  quay.io/keycloak/keycloak:latest \
  start-dev
```

## Spring Configurations

When running your Spring application (either locally with `mvn spring-boot:run` or in a container), use these environment variables:

```bash
# Database configuration
export DATABASE_URL=jdbc:postgresql://localhost:5432/postgres-db
export DATABASE_USERNAME=username
export DATABASE_PASSWORD=password

# Keycloak configuration
export KEYCLOAK_ENDPOINTS_BASE=http://localhost:9000
export KEYCLOAK_REALM=#your-realm-name
export KEYCLOAK_CLIENT_ID=#your-client-id
export KEYCLOAK_CLIENT_SECRET=#your-client-secret
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:9000/realms/your-realm-name
```

## Container Management Snnipets

### Start Services
```bash
# Start shared database
docker start postgres-db

# Start Keycloak (wait until database finishes initialization)
docker start keycloak
```

## Network Considerations

This setup uses `host.docker.internal` for container-to-container communication from Keycloak to its database. If you're on Linux and this doesn't work, you can:

1. **Use host networking** (Linux only):
   ```bash
   docker run --network host ...
   ```

2. **Create a custom Docker network**:
   ```bash
   # Create network
   docker network create dev-network
   
   # Add --network dev-network to all container run commands
   # Use container names for internal communication
   ```

## Keycloak Initial Configuration

After starting Keycloak:

1. Access Keycloak admin console at `http://localhost:9000`
2. Login with admin/admin
3. Create a realm
4. Add a new client to that realm and retrieve the configuration data (secret, client-id)