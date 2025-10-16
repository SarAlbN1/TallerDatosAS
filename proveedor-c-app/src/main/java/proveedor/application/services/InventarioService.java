package proveedor.application.services;

import proveedor.application.events.VentaCompletadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InventarioService {

    public void actualizarStock(List<VentaCompletadaEvent.ProductoVenta> productos) {
        log.info("📦 Actualizando stock para {} productos", productos.size());
        
        for (VentaCompletadaEvent.ProductoVenta producto : productos) {
            log.info("  - SKU: {}, Cantidad vendida: {}, Precio: {}", 
                    producto.getSku(), producto.getCantidad(), producto.getPrecio());
            
            // Simular actualización de stock
            // En un sistema real, aquí actualizarías la base de datos del proveedor
            try {
                Thread.sleep(100); // Simular procesamiento
                log.info("  ✅ Stock actualizado para SKU: {}", producto.getSku());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean necesitaReposicion() {
        // Simular lógica de reposición
        // En un sistema real, verificarías niveles mínimos de stock
        boolean necesitaReposicion = Math.random() > 0.7; // 30% probabilidad
        log.info("🔍 Verificando reposición necesaria: {}", necesitaReposicion);
        return necesitaReposicion;
    }

    public void generarOrdenReposicion(VentaCompletadaEvent event) {
        log.info("📋 Generando orden de reposición para factura: {}", event.getFacturaId());
        
        // Simular generación de orden
        String ordenId = "ORD-" + System.currentTimeMillis();
        log.info("  📄 Orden generada: {} para productos de factura: {}", ordenId, event.getFacturaId());
        
        // En un sistema real, aquí crearías la orden en la base de datos
        // y enviarías notificación al departamento de compras
    }
}
