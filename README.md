A simple app to manage users.

## Prerequisite

- Java 21
- Node.js 22.x + npm
- Angular 18.2.0
- Maven 3.9
- Docker

## Command to start the entire application

- docker compose build
- docker compose up

## Start only the BE

- docker compose build backend
- docker compose up backend

## Start only the FE

- docker compose build frontend
- docker compose up frontend

## Database ##
- url: http://localhost:8081/
- driver Postgresql
- server: db
- credentials for DB: admin/admin
- database name: appusersdb
- table: users