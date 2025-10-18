FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi
COPY . .
RUN npm run build -- --configuration=production

FROM nginx:1.25-alpine
WORKDIR /usr/share/nginx/html
RUN apk add --no-cache curl
COPY --from=builder /app/dist/user-management-suite-web ./
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
