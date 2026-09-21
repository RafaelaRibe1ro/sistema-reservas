#!/bin/bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE bd_usuario;
    CREATE DATABASE bd_reserva;
    CREATE DATABASE bd_auth;
EOSQL
