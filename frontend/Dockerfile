# syntax=docker/dockerfile:1
ARG NODE_VERSION=22.18.0

FROM node:${NODE_VERSION}-alpine

# Usa un env di sviluppo per assicurarti che le devDependencies siano installate
ENV NODE_ENV=development

WORKDIR /usr/src/app

# Installa le dipendenze (incluso dev) sfruttando la cache
# Nota: rimuoviamo --omit=dev
COPY package*.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

# Copia il resto dei sorgenti
COPY . .

# Facoltativo: esegui come utente non-root
USER node

EXPOSE 4200

# Avvio del dev server Angular su 0.0.0.0:4200
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
