#!/bin/bash

# Script de prueba rápida para validar transacciones distribuidas
# Autor: Sistema JTA con Atomikos

echo "=========================================="
echo "🔄 PRUEBAS DE TRANSACCIONES DISTRIBUIDAS"
echo "=========================================="

BASE_URL="http://localhost:8080"

echo ""
echo "1️⃣  Verificando que el servicio esté disponible..."
curl -s "${BASE_URL}/api/transactions/health" > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Servicio disponible"
else
    echo "❌ Servicio no disponible - verificar que la aplicación esté corriendo"
    exit 1
fi

echo ""
echo "2️⃣  Probando transacción distribuida exitosa..."
response=$(curl -s -X POST "${BASE_URL}/api/transactions/venta-completa" \
  -d "sku=LAPTOP001" \
  -d "cantidad=2" \
  -d "clienteId=1" \
  -d "metodoPagoId=TARJETA")

if echo "$response" | grep -q "success"; then
    echo "✅ Transacción distribuida exitosa"
else
    echo "❌ Error en transacción distribuida"
    echo "Respuesta: $response"
fi

echo ""
echo "3️⃣  Probando rollback de transacción..."
response=$(curl -s -X POST "${BASE_URL}/api/transactions/simular-fallo" \
  -d "sku=LAPTOP002" \
  -d "cantidad=1" \
  -d "clienteId=2" \
  -d "metodoPagoId=EFECTIVO")

if echo "$response" | grep -q "rollback_success"; then
    echo "✅ Rollback funcionando correctamente"
else
    echo "❌ Error en rollback"
    echo "Respuesta: $response"
fi

echo ""
echo "4️⃣  Probando transacción individual (solo inventario)..."
response=$(curl -s -X POST "${BASE_URL}/api/transactions/actualizar-stock" \
  -d "sku=MOUSE001" \
  -d "cantidad=5")

if echo "$response" | grep -q "success"; then
    echo "✅ Transacción individual funcionando"
else
    echo "❌ Error en transacción individual"
    echo "Respuesta: $response"
fi

echo ""
echo "5️⃣  Probando transacción de facturación..."
response=$(curl -s -X POST "${BASE_URL}/api/transactions/crear-factura" \
  -d "clienteId=1" \
  -d "sku=MOUSE001" \
  -d "cantidad=5" \
  -d "total=250.00")

if echo "$response" | grep -q "success"; then
    echo "✅ Transacción de facturación funcionando"
else
    echo "❌ Error en transacción de facturación"
    echo "Respuesta: $response"
fi

echo ""
echo "6️⃣  Probando transacción de pagos..."
response=$(curl -s -X POST "${BASE_URL}/api/transactions/procesar-pago" \
  -d "metodoPagoId=TARJETA" \
  -d "monto=250.00")

if echo "$response" | grep -q "success"; then
    echo "✅ Transacción de pagos funcionando"
else
    echo "❌ Error en transacción de pagos"
    echo "Respuesta: $response"
fi

echo ""
echo "=========================================="
echo "✅ PRUEBAS COMPLETADAS"
echo "=========================================="
echo ""
echo "Para verificar los datos en las bases:"
echo "🗃️  Inventario (puerto 3306): docker exec -it inventario-db mysql -u root -p inventario"
echo "🧾 Facturación (puerto 3307): docker exec -it facturacion-db mysql -u root -p facturacion"  
echo "💳 Pagos (puerto 3308): docker exec -it pagos-db mysql -u root -p pagos"
echo ""
echo "Para ver logs de transacciones:"
echo "📋 docker logs client-java-container"
echo ""