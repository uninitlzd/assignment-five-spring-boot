# Deployment Guide

This guide will help you deploy the application using Docker Compose.

## Prerequisites

- Docker and Docker Compose installed on your system
- Git (optional, for cloning the repository)

## Steps

1. **Clone the Repository** (if applicable)
   ```
   git clone <repository-url>
   cd <project-directory>
   add application-secret.properties in src/main/resources/ (the value redacted in assignment submission comment, please kindly check!)
   ```

2. **Review and Modify Environment Variables** (if needed)
   - Check the `environment` section in the `docker-compose.yml` file
   - Modify any variables as necessary for your deployment environment

3. **Build and Start the Services**
   ```
   docker-compose up -d
   ```
   This command will build the application image and start all services defined in the docker-compose.yml file.

4. **Verify Services**
   - Application: http://localhost:3001
   - Mailpit Web UI: http://localhost:8025

5. **Database Connection**
   - The PostgreSQL database is accessible on localhost:5433
   - Database Name: assignment_five
   - Username: postgres
   - Password: postgres

## Service Details

### Application (app)
- Built from the Dockerfile in the current directory
- Exposed on port 3001 (mapped to internal port 8080)
- Depends on PostgreSQL and Mailpit services
- Configured to use PostgreSQL and Mailpit for email

### PostgreSQL (postgres)
- Uses PostgreSQL 14 Alpine image
- Exposed on port 5433 (mapped to internal port 5432)
- Data persisted in a named volume: postgres_data

### Mailpit (mailpit)
- Used for email testing
- SMTP server runs on port 1025
- Web UI accessible on port 8025

## Volume Management
- `postgres_data`: Persists PostgreSQL data
- `maven-repo`: (Defined but not used in the provided compose file)

## Stopping the Services
To stop all services:
```
docker-compose down
```

To stop and remove all data (including volumes):
```
docker-compose down -v
```

## Troubleshooting
- If services fail to start, check the logs:
  ```
  docker-compose logs
  ```
- Ensure all required ports are available on your host machine
- Verify network connectivity between services if you encounter connection issues

## Maintenance
- Regularly update your Docker images:
  ```
  docker-compose pull
  docker-compose up -d
  ```
- Monitor disk usage, especially for the PostgreSQL volume

This guide provides a basic deployment workflow. Adjust as necessary for your specific environment and requirements.
