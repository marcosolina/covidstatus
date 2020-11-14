FROM postgres:9.5

COPY initDb.sql  /docker-entrypoint-initdb.d

ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_USER=postgres