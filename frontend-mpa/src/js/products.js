// Funcionalidad específica para la página de productos (REST)

let allProducts = [];
let allOrganizations = [];
let allCategories = [];

// Inicialización de la página
document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
    loadOrganizations();
    loadCategories();
    initSearch();
    initCreateProductForm();
});

// Cargar productos desde la API REST
async function loadProducts() {
    try {
        Utils.showLoading('loading');
        Utils.hideError('error');
        
        allProducts = await RestService.getProducts();
        renderProducts(allProducts);
        
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
        allOrganizations = await RestService.getOrganizations();
        populateOrganizationSelect();
    } catch (error) {
        console.error('Error loading organizations:', error);
    }
}

// Cargar categorías para el formulario
async function loadCategories() {
    try {
        allCategories = await RestService.getCategories();
        populateCategorySelect();
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// Renderizar productos en la grilla
function renderProducts(products) {
    const grid = document.getElementById('productsGrid');
    if (!grid) return;

    if (products.length === 0) {
        grid.innerHTML = `
            <div class="no-data">
                <i class="fas fa-box-open"></i>
                <h3>No hay productos</h3>
                <p>No se encontraron productos que coincidan con los criterios de búsqueda.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = products.map(product => `
        <div class="product-card">
            <div class="card-header">
                <h3 class="card-title">${escapeHtml(product.name)}</h3>
                <span class="card-id">#${product.id}</span>
            </div>
            <div class="card-content">
                <div class="card-meta">
                    <p><strong>Organización:</strong> ${escapeHtml(product.organization?.name || 'N/A')}</p>
                    <p><strong>Categoría:</strong> ${escapeHtml(product.category?.name || 'N/A')}</p>
                    ${product.category?.description ? `<p><strong>Descripción:</strong> ${escapeHtml(product.category.description)}</p>` : ''}
                </div>
            </div>
        </div>
    `).join('');
}

// Poblar select de organizaciones
function populateOrganizationSelect() {
    const select = document.getElementById('productOrganization');
    if (!select) return;

    select.innerHTML = '<option value="">Seleccionar organización...</option>' +
        allOrganizations.map(org => 
            `<option value="${org.id}">${escapeHtml(org.name)}</option>`
        ).join('');
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
    Search.init('searchInput', (searchTerm) => {
        const filteredProducts = Search.filterItems(allProducts, searchTerm, [
            'name',
            'organization.name',
            'category.name',
            'category.description'
        ]);
        renderProducts(filteredProducts);
    });
}

// Inicializar formulario de creación
function initCreateProductForm() {
    const form = document.getElementById('createProductForm');
    if (!form) return;

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        
        const formData = new FormData(form);
        const product = {
            name: formData.get('name'),
            organization: {
                id: parseInt(formData.get('organizationId'))
            },
            category: {
                id: parseInt(formData.get('categoryId'))
            }
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
