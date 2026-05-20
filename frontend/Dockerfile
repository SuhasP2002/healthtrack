# ---- Stage 1: Build React App ----
FROM node:18-alpine AS build

WORKDIR /app

# Copy package files first (better Docker layer caching)
COPY package*.json ./
RUN npm install

# Copy source and build
COPY . .
RUN npm run build

# ---- Stage 2: Serve with Nginx ----
# Nginx is a lightweight web server to serve static React files
FROM nginx:alpine

# Copy built React files to Nginx's web root
COPY --from=build /app/build /usr/share/nginx/html

# Nginx config for React Router (handles /dashboard, /login, etc.)
RUN printf 'server {\n\
    listen 80;\n\
    root /usr/share/nginx/html;\n\
    index index.html;\n\
    location /api {\n\
        proxy_pass http://backend:8080;\n\
        proxy_set_header Host $host;\n\
        proxy_set_header X-Real-IP $remote_addr;\n\
    }\n\
    location / {\n\
        try_files $uri $uri/ /index.html;\n\
    }\n\
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
