import requests
import json
from concurrent.futures import ThreadPoolExecutor

# --- CONFIGURACIÓN ---
URL_DESTINO = "http://monogon.ojos-mark.ts.net:8080/sensors" # Cambia esto por tu URL
ARCHIVO_DATOS = "datosSensores.txt"                       # Nombre de tu archivo .txt
MAX_CONEXIONES = 10                               # Ajusta según la velocidad de tu servidor

def enviar_linea(linea):
    """Procesa y envía una sola línea del archivo"""
    linea = linea.strip()
    if not linea:
        return # Salta líneas vacías

    try:
        # Convertimos el texto a diccionario para enviarlo correctamente como JSON
        dato_json = json.loads(linea)
        
        response = requests.post(
            URL_DESTINO, 
            json=dato_json, 
            headers={"Content-Type": "application/json"}
        )
        
        if response.status_code in [200, 201]:
            print(f"✅ Enviado: Sensor {dato_json.get('sensorId')} - {dato_json.get('valor')}")
        else:
            print(f"❌ Error {response.status_code} en línea: {linea[:50]}... -> {response.text}")
            
    except json.JSONDecodeError:
        print(f"⚠️ Error de formato en línea (no es JSON válido): {linea[:50]}...")
    except Exception as e:
        print(f"⚠️ Fallo de conexión: {e}")

def procesar_archivo():
    print(f"🚀 Iniciando lectura de {ARCHIVO_DATOS}...")
    
    try:
        with open(ARCHIVO_DATOS, 'r', encoding='utf-8') as f:
            lineas = f.readlines()
            
        print(f"📦 Se encontraron {len(lineas)} líneas. Enviando...")
        
        # Ejecución en paralelo para terminar en segundos
        with ThreadPoolExecutor(max_workers=MAX_CONEXIONES) as executor:
            executor.map(enviar_linea, lineas)
            
    except FileNotFoundError:
        print(f"❌ Error: No se encontró el archivo '{ARCHIVO_DATOS}'")

if __name__ == "__main__":
    procesar_archivo()
    print("\n🏁 Proceso completado.")
