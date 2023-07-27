# Spring One 2023

## Credentials

| Username             | Password |
|----------------------|----------|
| admin@example.com    | password |
| customer@example.com | password |


## Docker Commands

`docker network create springone2023`

`docker run --rm -p 8080:8080 --name client-app --network springone2023 docker.io/library/client-app:0.0.1-SNAPSHOT`

`docker run --rm -p 9000:9000 --name auth-server --network springone2023 docker.io/library/sso:0.0.1-SNAPSHOT`
