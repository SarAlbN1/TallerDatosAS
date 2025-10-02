# Script de prueba rápida para validar transacciones distribuidas
# Autor: Sistema JTA con Atomikos

Write-Host "===========================================" -ForegroundColor Green
Write-Host "🔄 PRUEBAS DE TRANSACCIONES DISTRIBUIDAS" -ForegroundColor Green  
Write-Host "===========================================" -ForegroundColor Green

$BASE_URL = "http://localhost:8080"

Write-Host ""
Write-Host "1️⃣  Verificando que el servicio esté disponible..." -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/transactions/health" -Method Get -TimeoutSec 5
    Write-Host "✅ Servicio disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ Servicio no disponible - verificar que la aplicación esté corriendo" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "2️⃣  Probando transacción distribuida exitosa..." -ForegroundColor Yellow

try {
    $url = "$BASE_URL/api/transactions/venta-completa?sku=LAPTOP001&cantidad=2&clienteId=1&metodoPagoId=TARJETA"
    
    $response = Invoke-RestMethod -Uri $url -Method Post
    
    if ($response.status -eq "success") {
        Write-Host "✅ Transacción distribuida exitosa" -ForegroundColor Green
    } else {
        Write-Host "❌ Error en transacción distribuida" -ForegroundColor Red
        Write-Host "Respuesta: $($response | ConvertTo-Json)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error en transacción distribuida: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "3️⃣  Probando rollback de transacción..." -ForegroundColor Yellow

try {
    $url = "$BASE_URL/api/transactions/simular-fallo?sku=LAPTOP002&cantidad=1&clienteId=2&metodoPagoId=EFECTIVO"
    
    $response = Invoke-RestMethod -Uri $url -Method Post
    
    if ($response.status -eq "rollback_success") {
        Write-Host "✅ Rollback funcionando correctamente" -ForegroundColor Green
    } else {
        Write-Host "❌ Error en rollback" -ForegroundColor Red
        Write-Host "Respuesta: $($response | ConvertTo-Json)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error en rollback: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "4️⃣  Probando transacción individual (solo inventario)..." -ForegroundColor Yellow

try {
    $url = "$BASE_URL/api/transactions/actualizar-stock?sku=MOUSE001&cantidad=5"
    
    $response = Invoke-RestMethod -Uri $url -Method Post
    
    if ($response.status -eq "success") {
        Write-Host "✅ Transacción individual funcionando" -ForegroundColor Green
    } else {
        Write-Host "❌ Error en transacción individual" -ForegroundColor Red
        Write-Host "Respuesta: $($response | ConvertTo-Json)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error en transacción individual: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "5️⃣  Probando transacción de facturación..." -ForegroundColor Yellow

try {
    $url = "$BASE_URL/api/transactions/crear-factura?clienteId=1&sku=MOUSE001&cantidad=5&total=250.00"
    
    $response = Invoke-RestMethod -Uri $url -Method Post
    
    if ($response.status -eq "success") {
        Write-Host "✅ Transacción de facturación funcionando" -ForegroundColor Green
    } else {
        Write-Host "❌ Error en transacción de facturación" -ForegroundColor Red
        Write-Host "Respuesta: $($response | ConvertTo-Json)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error en transacción de facturación: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "6️⃣  Probando transacción de pagos..." -ForegroundColor Yellow

try {
    $url = "$BASE_URL/api/transactions/procesar-pago?metodoPagoId=TARJETA&monto=250.00"
    
    $response = Invoke-RestMethod -Uri $url -Method Post
    
    if ($response.status -eq "success") {
        Write-Host "✅ Transacción de pagos funcionando" -ForegroundColor Green
    } else {
        Write-Host "❌ Error en transacción de pagos" -ForegroundColor Red
        Write-Host "Respuesta: $($response | ConvertTo-Json)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error en transacción de pagos: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "===========================================" -ForegroundColor Green
Write-Host "✅ PRUEBAS COMPLETADAS" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Para verificar los datos en las bases:" -ForegroundColor Cyan
Write-Host "  Inventario (puerto 3306): docker exec -it inventario-db mysql -u root -p inventario" -ForegroundColor White
Write-Host "  Facturacion (puerto 3307): docker exec -it facturacion-db mysql -u root -p facturacion" -ForegroundColor White
Write-Host "  Pagos (puerto 3308): docker exec -it pagos-db mysql -u root -p pagos" -ForegroundColor White
Write-Host ""
Write-Host "Para ver logs de transacciones:" -ForegroundColor Cyan
Write-Host "  Revisar logs en la consola donde se ejecuto 'mvn spring-boot:run'" -ForegroundColor White
Write-Host ""