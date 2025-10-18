// Funcionalidad específica para la página de productos (REST) - VERSIÓN CORREGIDA

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
        <div class="product-card" onclick="selectProduct(${product.id})" style="cursor: pointer;">
            <div class="card-header">
                <h3 class="card-title">${escapeHtml(product.nombre || product.name)}</h3>
                <span class="card-id">#${product.id}</span>
            </div>
            <div class="card-content">
                <div class="card-meta">
                    <p><strong>Organización:</strong> ${escapeHtml(product.organizacion || product.organization?.name || 'N/A')}</p>
                    <p><strong>Categoría:</strong> ${escapeHtml(product.categoria || product.category?.name || 'N/A')}</p>
                    <p><strong>Precio:</strong> $${product.precio || 'N/A'}</p>
                    <p><strong>Stock:</strong> ${product.stock || 'N/A'}</p>
                    ${product.descripcion ? `<p><strong>Descripción:</strong> ${escapeHtml(product.descripcion)}</p>` : ''}
                </div>
                <div class="card-actions">
                    <button class="btn btn-primary btn-sm" onclick="event.stopPropagation(); selectProduct(${product.id})">
                        <i class="fas fa-shopping-cart"></i> Comprar
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    console.log('=== renderProducts completado ===');
}

// Poblar select de organizaciones (no se usa en el formulario actual)
function populateOrganizationSelect() {
    console.log('=== populateOrganizationSelect ejecutado ===');
    // No se necesita para el formulario actual
    return;
}

// Poblar select de categorías
function populateCategorySelect() {
    console.log('=== populateCategorySelect iniciado ===');
    const select = document.getElementById('productCategory');
    console.log('Elemento productCategory encontrado:', select ? 'SÍ' : 'NO');
    
    if (!select) {
        console.log('Elemento productCategory no encontrado, saltando...');
        return;
    }

    select.innerHTML = '<option value="">Seleccionar categoría...</option>' +
        allCategories.map(cat => 
            `<option value="${cat.id}">${escapeHtml(cat.name)}</option>`
        ).join('');
    console.log('=== populateCategorySelect completado ===');
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

// Mostrar modal de creación de producto
function showCreateProductModal() {
    Modal.show('createProductModal');
}

// Cerrar modal de creación de producto
function closeCreateProductModal() {
    Modal.hide('createProductModal');
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

// Variables para el flujo de compra
let selectedProduct = null;

// Seleccionar producto para compra
function selectProduct(productId) {
    console.log('=== selectProduct iniciado ===');
    console.log('Producto seleccionado ID:', productId);
    
    selectedProduct = allProducts.find(p => p.id === productId);
    if (!selectedProduct) {
        console.error('Producto no encontrado con ID:', productId);
        Notification.show('Producto no encontrado', 'error');
        return;
    }
    
    console.log('Producto encontrado:', selectedProduct);
    showPurchaseModal();
}

// Mostrar modal de compra
function showPurchaseModal() {
    console.log('=== showPurchaseModal iniciado ===');
    
    if (!selectedProduct) {
        console.error('No hay producto seleccionado');
        return;
    }
    
    // Actualizar la información del producto en el modal
    const productInfo = document.getElementById('selectedProductInfo');
    if (productInfo) {
        productInfo.innerHTML = `
            <div class="selected-product">
                <h4>${escapeHtml(selectedProduct.nombre || selectedProduct.name)}</h4>
                <p><strong>SKU:</strong> ${selectedProduct.sku}</p>
                <p><strong>Precio:</strong> $${selectedProduct.precio}</p>
                <p><strong>Stock disponible:</strong> ${selectedProduct.stock}</p>
                <p><strong>Categoría:</strong> ${selectedProduct.categoria}</p>
            </div>
        `;
    }
    
    // Limpiar el formulario
    document.getElementById('customerName').value = '';
    document.getElementById('customerEmail').value = '';
    document.getElementById('quantity').value = '1';
    document.getElementById('quantity').max = selectedProduct.stock;
    
    Modal.show('purchaseModal');
}

// Cerrar modal de compra
function closePurchaseModal() {
    console.log('=== closePurchaseModal iniciado ===');
    Modal.hide('purchaseModal');
    selectedProduct = null;
}

// Procesar compra
async function processPurchase() {
    console.log('=== processPurchase iniciado ===');
    
    if (!selectedProduct) {
        console.error('No hay producto seleccionado');
        Notification.show('No hay producto seleccionado', 'error');
        return;
    }
    
    const customerEmail = document.getElementById('customerEmail').value;
    const customerName = document.getElementById('customerName').value;
    const quantity = parseInt(document.getElementById('quantity').value) || 1;
    
    if (!customerEmail || !customerName) {
        Notification.show('Por favor completa todos los campos', 'error');
        return;
    }
    
    if (quantity > selectedProduct.stock) {
        Notification.show('No hay suficiente stock disponible', 'error');
        return;
    }
    
    try {
        console.log('Procesando compra...');
        console.log('Producto:', selectedProduct);
        console.log('Cliente:', customerName, customerEmail);
        console.log('Cantidad:', quantity);
        
        // Crear la orden de compra
        const orderData = {
            productId: selectedProduct.id,
            customerName: customerName,
            customerEmail: customerEmail,
            quantity: quantity,
            totalPrice: selectedProduct.precio * quantity
        };
        
        console.log('Datos de la orden:', orderData);
        
        // Usar fetch directamente para evitar problemas con RestService
        console.log('Enviando datos al backend:', orderData);
        
        const response = await fetch('http://localhost:8080/api/checkout/simple', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderData)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('Respuesta del checkout:', result);
        
        Notification.show('¡Compra procesada exitosamente! Revisa tu email.', 'success');
        closePurchaseModal();
        
        // Recargar productos para actualizar stock
        loadProducts();
        
    } catch (error) {
        console.error('Error procesando compra:', error);
        Notification.show('Error procesando la compra: ' + error.message, 'error');
    }
}
