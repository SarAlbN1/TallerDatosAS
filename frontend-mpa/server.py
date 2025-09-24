#!/usr/bin/env python3
"""
Servidor HTTP simple para servir la aplicación MPA
Uso: python server.py [puerto]
"""

import http.server
import socketserver
import sys
import os
from pathlib import Path

# Puerto por defecto
PORT = 3001

# Cambiar al directorio del script
os.chdir(Path(__file__).parent)

class CustomHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        # Agregar headers CORS para desarrollo
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def do_OPTIONS(self):
        # Manejar preflight requests
        self.send_response(200)
        self.end_headers()

if __name__ == "__main__":
    # Verificar si se proporciona un puerto
    if len(sys.argv) > 1:
        try:
            PORT = int(sys.argv[1])
        except ValueError:
            print("Error: El puerto debe ser un número")
            sys.exit(1)

    # Crear el servidor
    with socketserver.TCPServer(("", PORT), CustomHTTPRequestHandler) as httpd:
        print(f"🚀 Servidor MPA ejecutándose en http://localhost:{PORT}")
        print(f"📁 Directorio: {os.getcwd()}")
        print(f"🌐 Abre http://localhost:{PORT} en tu navegador")
        print("Presiona Ctrl+C para detener el servidor")
        
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\n👋 Servidor detenido")
            httpd.shutdown()
