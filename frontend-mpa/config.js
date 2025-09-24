// Configuración de la aplicación MPA
const CONFIG = {
    // URLs de los servicios
    API_BASE_URL: 'http://localhost:8080/api',
    SOAP_BASE_URL: 'http://localhost:8080/ws',
    
    // Configuración de la aplicación
    APP_NAME: 'TallerDatosAS MPA',
    APP_VERSION: '1.0.0',
    
    // Configuración de notificaciones
    NOTIFICATION_DURATION: 3000,
    
    // Configuración de búsqueda
    SEARCH_DEBOUNCE_DELAY: 300,
    
    // Configuración de paginación
    ITEMS_PER_PAGE: 10,
    
    // Configuración de la UI
    THEME: {
        PRIMARY_COLOR: '#667eea',
        SECONDARY_COLOR: '#764ba2',
        SUCCESS_COLOR: '#28a745',
        ERROR_COLOR: '#dc3545',
        WARNING_COLOR: '#ffc107',
        INFO_COLOR: '#17a2b8'
    }
};

// Exportar configuración para uso en otros archivos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CONFIG;
} else {
    window.CONFIG = CONFIG;
}
