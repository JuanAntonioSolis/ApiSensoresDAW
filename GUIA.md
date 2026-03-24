# 🚀 Guía completa: ApiSensoresDAW en Podman + Cloudflare Tunnel
## CasaOS / Debian — Java 25 — Sin abrir puertos del router

---

## 📁 Estructura de archivos en el repositorio

```
ApiSensoresDAW/
├── src/
├── build.gradle
├── settings.gradle
├── gradlew
├── Dockerfile           ← añadir al repo ✅
├── compose.yml          ← añadir al repo ✅
└── .env                 ← NO subir a GitHub ⛔
```

Añade esto al `.gitignore` del proyecto:
```
.env
*.env
```

---

## 🐳 Paso 1: Instalar Podman en Debian/CasaOS

```bash
sudo apt-get update
sudo apt-get install -y podman podman-compose git
```

Verificar:
```bash
podman --version
podman-compose --version
```

---

## 📦 Paso 2: Clonar el repo y preparar archivos

```bash
git clone https://github.com/JuanAntonioSolis/ApiSensoresDAW.git
cd ApiSensoresDAW

# Copiar Dockerfile, compose.yml y .env a esta carpeta
# Editar .env con tus contraseñas reales
nano .env
```

---

## ⚙️ Paso 3: Ajustar application.properties (si es necesario)

Si el proyecto tiene `src/main/resources/application.properties`,
asegúrate de que lea las credenciales desde variables de entorno:

```properties
# Para el perfil de producción (application-prod.properties)
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=false
```

> Si ya tiene valores hardcodeados y no quieres tocar el código,
> las variables de entorno del compose.yml las sobreescriben automáticamente.
> Spring Boot acepta variables de entorno con formato SPRING_DATASOURCE_URL, etc.

---

## 🚢 Paso 4: Construir y levantar los contenedores

```bash
cd ~/ApiSensoresDAW

# Primera vez (construye la imagen, tarda unos minutos)
podman-compose up -d --build

# Ver logs en tiempo real
podman logs -f sensores-app
```

Verificar que la app está corriendo:
```bash
podman ps
curl http://localhost:8080
```

---

## 🌐 Paso 5: Cloudflare Tunnel — Acceso remoto sin abrir puertos

### 5.1 Crear cuenta y túnel en Cloudflare

1. Crear cuenta gratuita en https://dash.cloudflare.com
2. Ir a **Zero Trust** (menú lateral) → **Networks** → **Tunnels**
3. Click **Create a tunnel** → Nombre: `sensores-tunnel`
4. Elegir el conector **Cloudflared**
5. Anotar el **token** que aparece en pantalla

### 5.2 Instalar cloudflared en el servidor

```bash
curl -L \
  https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb \
  -o cloudflared.deb

sudo dpkg -i cloudflared.deb
cloudflared --version
```

### 5.3 Registrar como servicio del sistema

```bash
# Reemplaza TU_TOKEN con el que copiaste en el dashboard
sudo cloudflared service install TU_TOKEN

sudo systemctl enable --now cloudflared
sudo systemctl status cloudflared
```

### 5.4 Configurar el hostname público

En el dashboard de Cloudflare → pestaña **Public Hostname**:

| Campo | Valor |
|---|---|
| Subdomain | `sensores-app` |
| Domain | tu dominio o uno gratuito de Cloudflare |
| Service | `HTTP` |
| URL | `localhost:8080` |

Tus compañeros acceden a `https://sensores-app.tudominio.com` con HTTPS automático. ✅

### 5.5 Opción rápida sin cuenta (URL temporal para demos)

```bash
cloudflared tunnel --url http://localhost:8080
# Genera: https://random-name.trycloudflare.com
```
La URL cambia cada vez que reinicias el comando. Solo para pruebas.

---

## 🔐 Paso 6 (Recomendado): Proteger el acceso con login

Para que solo tu equipo pueda entrar, en Cloudflare Zero Trust:

1. **Access** → **Applications** → **Add an application**
2. Tipo: **Self-hosted**
3. Domain: `sensores-app.tudominio.com`
4. En **Policies** → añadir los emails del equipo
5. Guardar

A partir de ahí cualquier visita verá un login de Cloudflare antes de llegar a tu app.

---

## 🔄 Actualizar la app cuando haya cambios

```bash
cd ~/ApiSensoresDAW
git pull origin main
podman-compose up -d --build app
podman logs -f sensores-app
```

---

## 🛠️ Comandos útiles

```bash
podman ps                                              # estado de contenedores
podman logs -f sensores-app                            # logs de la app
podman logs -f sensores-db                             # logs de MySQL
podman exec -it sensores-app sh                        # entrar al contenedor
podman exec -it sensores-db mysql -u sensores -p       # conectarse a MySQL
podman-compose down                                    # parar todo
podman-compose down -v                                 # parar y borrar BD ⚠️
podman stats                                           # uso de recursos
```

---

## 🐛 Problemas frecuentes

**App no conecta a MySQL (Connection refused)**
La BD tarda en inicializarse. El compose ya usa `healthcheck`, pero si persiste:
```bash
podman ps  # STATUS debe mostrar (healthy) en sensores-db
```

**Imagen eclipse-temurin:25 no disponible**
Java 25 es muy reciente. Si falla el build, prueba con la imagen EA:
```dockerfile
FROM eclipse-temurin:25-ea-jdk AS builder
FROM eclipse-temurin:25-ea-jre
```
O con OpenJDK:
```dockerfile
FROM openjdk:25-jdk AS builder
FROM openjdk:25-jdk-slim
```

**Error de permisos en Podman rootless**
```bash
sudo loginctl enable-linger $USER
```

**El JAR tiene nombre específico y falla el COPY**
```bash
# Busca el nombre exacto tras hacer un build manual:
ls build/libs/
# Ajusta en el Dockerfile:
# COPY --from=builder /app/build/libs/ApiSensoresDAW-0.0.1-SNAPSHOT.jar app.jar
```

---

## 📋 Stack completo

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot + Gradle |
| Base de datos | MySQL 8.0 |
| Contenedor | Podman + podman-compose |
| Acceso remoto | Cloudflare Tunnel (0 puertos abiertos) |
| OS servidor | Debian / CasaOS |
