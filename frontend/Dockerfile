# syntax=docker/dockerfile:1
ARG NODE_VERSION=20-alpine
FROM node:${NODE_VERSION}

ENV NODE_ENV=development
WORKDIR /usr/src/app

COPY package*.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

COPY . .

EXPOSE 4200
CMD ["npm","run","start","--","--host","0.0.0.0","--port","4200","--poll","2000"]
