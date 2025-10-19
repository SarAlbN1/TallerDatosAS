// Funcionalidad específica para la página de productos (REST)

let allProducts = [];
let allOrganizations = [];
let allCategories = [];

// Inicialización de la página
document.addEventListener('DOMContentLoaded', () => {
    console.log('=== Inicializando página de productos ===');
    try {
        loadProducts();
        loadOrganizations();
        loadCategories();
        initSearch();
        initCreateProductForm();
        console.log('=== Inicialización completada ===');
    } catch (error) {
        console.error('Error en inicialización:', error);
    }
});

// Cargar productos desde la API REST
async function loadProducts() {
    try {
        console.log('=== loadProducts iniciado ===');
        Utils.showLoading('loading');
        Utils.hideError('error');
        
        console.log('Llamando a RestService.getProducts()...');
        allProducts = await RestService.getProducts();
        console.log('Productos recibidos:', allProducts);
        
        console.log('Llamando a renderProducts...');
        renderProducts(allProducts);
        console.log('=== loadProducts completado ===');
        
    } catch (error) {
        console.error('Error loading products:', error);
        Utils.showError('error', `Error al cargar productos: ${error.message}`);
        Notification.show('Error al cargar productos', 'error');
    } finally {
        Utils.hideLoading('loading');
    }
}

// Cargar organizaciones para el formulario
async function loadOrganizations() {
    try {
        console.log('=== loadOrganizations iniciado ===');
        allOrganizations = await RestService.getOrganizations();
        console.log('Organizaciones recibidas:', allOrganizations);
        populateOrganizationSelect();
        console.log('=== loadOrganizations completado ===');
    } catch (error) {
        console.error('Error loading organizations:', error);
    }
}

// Cargar categorías para el formulario
async function loadCategories() {
    try {
        console.log('=== loadCategories iniciado ===');
        allCategories = await RestService.getCategories();
        console.log('Categorías recibidas:', allCategories);
        populateCategorySelect();
        console.log('=== loadCategories completado ===');
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// Renderizar productos en la grilla
function renderProducts(products) {
    console.log('=== renderProducts iniciado ===');
    console.log('Productos a renderizar:', products);
    
    const grid = document.getElementById('productsGrid');
    console.log('Elemento productsGrid encontrado:', grid ? 'SÍ' : 'NO');
    
    if (!grid) {
        console.error('Elemento productsGrid no encontrado');
        return;
    }

    if (products.length === 0) {
        console.log('No hay productos para mostrar');
        grid.innerHTML = `
            <div class="no-data">
                <i class="fas fa-box-open"></i>
                <h3>No hay productos</h3>
                <p>No se encontraron productos que coincidan con los criterios de búsqueda.</p>
            </div>
        `;
        return;
    }

    console.log('Renderizando', products.length, 'productos...');
    grid.innerHTML = products.map(product => `
        <div class="product-card">
            <div class="card-header">
                <h3 class="card-title">${escapeHtml(product.nombre || product.name)}</h3>
                <span class="card-id">#${product.id}</span>
            </div>
            <div class="card-content">
                <div class="card-meta">
                    <p><strong>Organización:</strong> ${escapeHtml(product.organizacion || product.organization?.name || 'N/A')}</p>
                    <p><strong>Categoría:</strong> ${escapeHtml(product.categoria || product.category?.name || 'N/A')}</p>
                    ${product.descripcion ? `<p><strong>Descripción:</strong> ${escapeHtml(product.descripcion)}</p>` : ''}
                </div>
            </div>
        </div>
    `).join('');
    
    console.log('=== renderProducts completado ===');
}

// Poblar select de organizaciones (no se usa en el formulario actual)
function populateOrganizationSelect() {
    // No se necesita para el formulario actual
    return;
}

// Poblar select de categorías
function populateCategorySelect() {
    const select = document.getElementById('productCategory');
    if (!select) return;

    select.innerHTML = '<option value="">Seleccionar categoría...</option>' +
        allCategories.map(cat => 
            `<option value="${cat.id}">${escapeHtml(cat.name)}</option>`
        ).join('');
}

// Inicializar búsqueda
function initSearch() {
    try {
        console.log('=== initSearch iniciado ===');
        const searchInput = document.getElementById('searchInput');
        console.log('Elemento searchInput encontrado:', searchInput ? 'SÍ' : 'NO');
        
        if (!searchInput) {
            console.log('Elemento searchInput no encontrado, saltando initSearch');
            return;
        }
        
        Search.init('searchInput', (searchTerm) => {
            const filteredProducts = Search.filterItems(allProducts, searchTerm, [
                'nombre',
                'name',
                'organizacion',
                'organization.name',
                'categoria',
                'category.name',
                'descripcion',
                'category.description'
            ]);
            renderProducts(filteredProducts);
        });
        console.log('=== initSearch completado ===');
    } catch (error) {
        console.error('Error en initSearch:', error);
    }
}

// Inicializar formulario de creación
function initCreateProductForm() {
    try {
        console.log('=== initCreateProductForm iniciado ===');
        const form = document.getElementById('createProductForm');
        console.log('Elemento createProductForm encontrado:', form ? 'SÍ' : 'NO');
        
        if (!form) {
            console.log('Elemento createProductForm no encontrado, saltando initCreateProductForm');
            return;
        }

        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            
            const formData = new FormData(form);
            const product = {
                nombre: formData.get('name'),
                sku: formData.get('sku') || `SKU-${Date.now()}`,
                stock: parseInt(formData.get('stock')) || 0,
                categoriaId: parseInt(formData.get('categoryId'))
            };

            try {
                await RestService.createProduct(product);
                Notification.show('Producto creado exitosamente', 'success');
                closeCreateProductModal();
                form.reset();
                loadProducts(); // Recargar la lista
            } catch (error) {
                console.error('Error creating product:', error);
                Notification.show('Error al crear producto', 'error');
            }
        });
        console.log('=== initCreateProductForm completado ===');
    } catch (error) {
        console.error('Error en initCreateProductForm:', error);
    }
}

// Mostrar modal de creación
function showCreateProductModal() {
    Modal.show('createProductModal');
}

// Cerrar modal de creación
function closeCreateProductModal() {
    Modal.hide('createProductModal');
    const form = document.getElementById('createProductForm');
    if (form) {
        form.reset();
    }
}

// Función para escapar HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Función para formatear fecha
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}
