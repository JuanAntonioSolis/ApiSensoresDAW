#!/bin/bash
# =============================================
# deploy.sh - Script de despliegue completo
# ApiSensoresDAW en Podman + CasaOS
#
# Coloca este script en la raíz del proyecto
# (junto a Dockerfile, compose.yml y .env)
# y ejecútalo desde cualquier lugar.
# =============================================

set -e  # Salir si hay error

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Directorio donde está este script = raíz del proyecto
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${GREEN}=== ApiSensoresDAW - Deploy Script ===${NC}"
echo -e "Directorio del proyecto: ${YELLOW}$APP_DIR${NC}"

# -----------------------------------------------
# 1. Comprobar dependencias
# -----------------------------------------------
echo -e "\n${YELLOW}[1/4] Comprobando dependencias...${NC}"

if ! command -v podman &> /dev/null; then
    echo -e "${RED}Podman no encontrado. Instalando...${NC}"
    sudo apt-get update && sudo apt-get install -y podman
fi

if ! command -v podman-compose &> /dev/null; then
    echo -e "${RED}podman-compose no encontrado. Instalando...${NC}"
    sudo apt-get install -y podman-compose || pip3 install podman-compose
fi

echo -e "${GREEN}✓ Dependencias OK${NC}"

# -----------------------------------------------
# 2. Comprobar archivos necesarios
# -----------------------------------------------
echo -e "\n${YELLOW}[2/4] Verificando archivos del proyecto...${NC}"

cd "$APP_DIR"

for f in Dockerfile compose.yml .env; do
    if [ ! -f "$APP_DIR/$f" ]; then
        echo -e "${RED}✗ Falta el archivo: $f${NC}"
        echo "  Asegúrate de que $f está en $APP_DIR"
        exit 1
    fi
done

echo -e "${YELLOW}⚠ Recuerda que .env debe tener tus contraseñas reales.${NC}"
read -p "  ¿Confirmas que el .env está configurado? (s/n): " confirm
if [[ "$confirm" != "s" && "$confirm" != "S" ]]; then
    echo "Edita el archivo: nano $APP_DIR/.env"
    exit 1
fi

echo -e "${GREEN}✓ Archivos OK${NC}"

# -----------------------------------------------
# 3. Construir y levantar contenedores
# -----------------------------------------------
echo -e "\n${YELLOW}[3/4] Construyendo imagen y levantando contenedores...${NC}"

podman-compose down 2>/dev/null || true
podman-compose up -d --build

echo -e "${GREEN}✓ Contenedores levantados${NC}"

# -----------------------------------------------
# 4. Verificar estado
# -----------------------------------------------
echo -e "\n${YELLOW}[4/4] Verificando estado...${NC}"

sleep 5

podman ps --filter "name=sensores"

echo ""
echo -e "${GREEN}=== ¡Despliegue completado! ===${NC}"
echo -e "Aplicación disponible en: ${GREEN}http://localhost:8080${NC}"
echo ""
echo "Comandos útiles:"
echo "  Ver logs app:    podman logs -f sensores-app"
echo "  Ver logs BD:     podman logs -f sensores-db"
echo "  Detener todo:    cd $APP_DIR && podman-compose down"
echo "  Reiniciar app:   podman-compose restart app"
echo "  Consola MySQL:   podman exec -it sensores-db mysql -u sensores -p"
