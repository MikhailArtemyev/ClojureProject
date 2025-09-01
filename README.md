# Patient CRUD App (Clojure/ClojureScript + PostgreSQL)

## Overview 

This project is a full-stack application built with Clojure (backend), HTML+JavaScript (frontend), and PostgreSQL (database).
It demonstrates a typical CRUD flow (Create, Read, Update, Delete) for managing patients.

The app includes:
- Viewing a list of patients
- Searching and filtering
- Creating, editing, deleting patients
- Input validation
- Automated tests covering backend, frontend, and integration logic

## Requirements

- [Clojure CLI](https://clojure.org/guides/getting_started) (`clj`)  
- [Docker](https://www.docker.com/) & [docker-compose](https://docs.docker.com/compose/)  
- AWS CLI configured with permissions to push to ECR

## Running Locally

```bash
docker-compose up -d
```
This will start:
- core → Clojure API on port 3000
- web → Nginx frontend on port 8080
- db → PostgreSQL on port 5432
