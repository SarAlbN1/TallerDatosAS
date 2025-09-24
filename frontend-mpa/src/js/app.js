// Configuración de la aplicación MPA (se carga desde config.js)
const API_BASE_URL = CONFIG.API_BASE_URL;
const SOAP_BASE_URL = CONFIG.SOAP_BASE_URL;

// Utilidades generales
class Utils {
    static showLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'block';
        }
    }

    static hideLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'none';
        }
    }

    static showError(elementId, message) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'block';
            const messageElement = element.querySelector('p') || element;
            messageElement.textContent = message;
        }
    }

    static hideError(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'none';
        }
    }

    static async fetchData(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error fetching data:', error);
            throw error;
        }
    }

    static formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
}

// Clase para manejar servicios REST
class RestService {
    static async getProducts() {
        return await Utils.fetchData(`${API_BASE_URL}/products`);
    }

    static async createProduct(product) {
        return await Utils.fetchData(`${API_BASE_URL}/products`, {
            method: 'POST',
            body: JSON.stringify(product)
        });
    }

    static async getOrganizations() {
        return await Utils.fetchData(`${API_BASE_URL}/organizations`);
    }

    static async createOrganization(organization) {
        return await Utils.fetchData(`${API_BASE_URL}/organizations`, {
            method: 'POST',
            body: JSON.stringify(organization)
        });
    }

    static async getCategories() {
        return await Utils.fetchData(`${API_BASE_URL}/categories`);
    }

    static async createCategory(category) {
        return await Utils.fetchData(`${API_BASE_URL}/categories`, {
            method: 'POST',
            body: JSON.stringify(category)
        });
    }
}

// Clase para manejar servicios SOAP
class SoapService {
    static async getProducts() {
        const soapEnvelope = `
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:prod="http://example.com/products">
                <soapenv:Header/>
                <soapenv:Body>
                    <prod:GetProductsRequest/>
                </soapenv:Body>
            </soapenv:Envelope>
        `;

        try {
            const response = await fetch(`${SOAP_BASE_URL}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'text/xml; charset=utf-8',
                    'SOAPAction': 'http://example.com/products/GetProductsRequest'
                },
                body: soapEnvelope
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const xmlText = await response.text();
            return this.parseSoapResponse(xmlText);
        } catch (error) {
            console.error('Error calling SOAP service:', error);
            throw error;
        }
    }

    static async createProduct(product) {
        const soapEnvelope = `
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:prod="http://example.com/products">
                <soapenv:Header/>
                <soapenv:Body>
                    <prod:CreateProductRequest>
                        <prod:product>
                            <prod:name>${product.name}</prod:name>
                            <prod:organization>
                                <prod:id>${product.organizationId}</prod:id>
                            </prod:organization>
                            <prod:category>
                                <prod:id>${product.categoryId}</prod:id>
                            </prod:category>
                        </prod:product>
                    </prod:CreateProductRequest>
                </soapenv:Body>
            </soapenv:Envelope>
        `;

        try {
            const response = await fetch(`${SOAP_BASE_URL}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'text/xml; charset=utf-8',
                    'SOAPAction': 'http://example.com/products/CreateProductRequest'
                },
                body: soapEnvelope
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const xmlText = await response.text();
            return this.parseSoapResponse(xmlText);
        } catch (error) {
            console.error('Error calling SOAP service:', error);
            throw error;
        }
    }

    static parseSoapResponse(xmlText) {
        try {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlText, 'text/xml');
            
            // Verificar si hay errores SOAP
            const fault = xmlDoc.querySelector('soap\\:Fault, Fault');
            if (fault) {
                const faultString = fault.querySelector('faultstring, faultString')?.textContent || 'SOAP Fault';
                throw new Error(`SOAP Fault: ${faultString}`);
            }

            return xmlDoc;
        } catch (error) {
            console.error('Error parsing SOAP response:', error);
            throw error;
        }
    }
}

// Clase para manejar la navegación
class Navigation {
    static init() {
        // Manejar el menú hamburguesa en móviles
        const hamburger = document.querySelector('.hamburger');
        const navMenu = document.querySelector('.nav-menu');

        if (hamburger && navMenu) {
            hamburger.addEventListener('click', () => {
                hamburger.classList.toggle('active');
                navMenu.classList.toggle('active');
            });

            // Cerrar menú al hacer clic en un enlace
            document.querySelectorAll('.nav-link').forEach(link => {
                link.addEventListener('click', () => {
                    hamburger.classList.remove('active');
                    navMenu.classList.remove('active');
                });
            });
        }

        // Marcar enlace activo basado en la página actual
        this.setActiveLink();
    }

    static setActiveLink() {
        const currentPage = window.location.pathname.split('/').pop() || 'index.html';
        const navLinks = document.querySelectorAll('.nav-link');

        navLinks.forEach(link => {
            link.classList.remove('active');
            const href = link.getAttribute('href');
            if (href === currentPage || (currentPage === '' && href === 'index.html')) {
                link.classList.add('active');
            }
        });
    }
}

// Clase para manejar modales
class Modal {
    static show(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        }
    }

    static hide(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    }

    static init() {
        // Cerrar modal al hacer clic fuera de él
        document.addEventListener('click', (event) => {
            if (event.target.classList.contains('modal')) {
                event.target.style.display = 'none';
                document.body.style.overflow = 'auto';
            }
        });

        // Cerrar modal con tecla Escape
        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') {
                const openModal = document.querySelector('.modal[style*="block"]');
                if (openModal) {
                    openModal.style.display = 'none';
                    document.body.style.overflow = 'auto';
                }
            }
        });
    }
}

// Clase para manejar búsquedas
class Search {
    static init(inputId, searchFunction) {
        const searchInput = document.getElementById(inputId);
        if (searchInput) {
            const debouncedSearch = Utils.debounce(searchFunction, 300);
            searchInput.addEventListener('input', (event) => {
                debouncedSearch(event.target.value);
            });
        }
    }

    static filterItems(items, searchTerm, searchFields) {
        if (!searchTerm) return items;
        
        const term = searchTerm.toLowerCase();
        return items.filter(item => {
            return searchFields.some(field => {
                const value = this.getNestedValue(item, field);
                return value && value.toString().toLowerCase().includes(term);
            });
        });
    }

    static getNestedValue(obj, path) {
        return path.split('.').reduce((current, key) => {
            return current && current[key] !== undefined ? current[key] : null;
        }, obj);
    }
}

// Clase para manejar notificaciones
class Notification {
    static show(message, type = 'info', duration = 3000) {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas fa-${this.getIcon(type)}"></i>
                <span>${message}</span>
            </div>
        `;

        // Estilos para la notificación
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${this.getBackgroundColor(type)};
            color: white;
            padding: 15px 20px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            z-index: 3000;
            animation: slideInRight 0.3s ease;
            max-width: 400px;
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, duration);
    }

    static getIcon(type) {
        const icons = {
            success: 'check-circle',
            error: 'exclamation-circle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    }

    static getBackgroundColor(type) {
        const colors = {
            success: '#28a745',
            error: '#dc3545',
            warning: '#ffc107',
            info: '#17a2b8'
        };
        return colors[type] || '#17a2b8';
    }
}

// Inicialización de la aplicación
document.addEventListener('DOMContentLoaded', () => {
    Navigation.init();
    Modal.init();
    
    // Cargar datos del dashboard si estamos en la página principal
    if (window.location.pathname.endsWith('index.html') || window.location.pathname === '/') {
        loadDashboardData();
    }
});

// Función para cargar datos del dashboard
async function loadDashboardData() {
    try {
        const [products, organizations, categories] = await Promise.all([
            RestService.getProducts(),
            RestService.getOrganizations(),
            RestService.getCategories()
        ]);

        // Actualizar contadores
        const productsCount = document.getElementById('products-count');
        const organizationsCount = document.getElementById('organizations-count');
        const categoriesCount = document.getElementById('categories-count');

        if (productsCount) productsCount.textContent = `${products.length} productos`;
        if (organizationsCount) organizationsCount.textContent = `${organizations.length} organizaciones`;
        if (categoriesCount) categoriesCount.textContent = `${categories.length} categorías`;

    } catch (error) {
        console.error('Error loading dashboard data:', error);
        Notification.show('Error al cargar los datos del dashboard', 'error');
    }
}

// Agregar estilos CSS para animaciones de notificaciones
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }

    .notification-content {
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .notification-content i {
        font-size: 1.2rem;
    }
`;
document.head.appendChild(style);
