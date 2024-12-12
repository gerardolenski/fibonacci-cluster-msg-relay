# FIBONACCI Message Relay

This is a message relay application responsible to deliver new messages located in Task Manager's `outbox table to
message queue.

## Requirements

- Java 21
- Artemis
- PostgreSQL

## Configuration

App can be configured by environment variables:

- `BROKER_URL` - the URI to connect to the Artemis cluster
- `BROKER_USER` - the Artemis user
- `BROKER_PASSWORD` - the Artemis password
- `JOB_QUEUE_NAME` - the name of the destination job queue for each message
- `JMS_SESSION_CACHE_SIZE` - the size of the cache for `SessionConnectionFactory`, by default `5`


- `TOMCAT_PORT` - the port of exposed API, by default `8123`


- `POSTGRES_DATASOURCE_URL` - the URL to the PostgreSQL datasource
- `POSTGRES_USER` - the database user
- `POSTGRES_PASSWORD` - the database password
- `POSTGRESS_POOL_SIZE` - the database connection pool size, by default `5`
- `POSTGRESS_CONNECTION_TIMEOUT` - the database connection timeout, by default `5000` ms


- `LOGGING_LEVEL` - the level of the application logs

The example configuration:

```
BROKER_URL=tcp://localhost:61616
BROKER_PASSWORD=artemis
BROKER_USER=artemis

JOB_QUEUE_NAME=job

POSTGRES_DATASOURCE_URL=jdbc:postgresql://localhost:5432/task_manager
POSTGRES_PASSWORD=postgres
POSTGRES_USER=postgres
```