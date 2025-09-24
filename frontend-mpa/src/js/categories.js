// Funcionalidad específica para la página de categorías

let allCategories = [];

// Inicialización de la página
document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    initSearch();
    initCreateCategoryForm();
});

// Cargar categorías desde la API REST
async function loadCategories() {
    try {
        Utils.showLoading('loading');
        Utils.hideError('error');
        
        allCategories = await RestService.getCategories();
        renderCategories(allCategories);
        
    } catch (error) {
        console.error('Error loading categories:', error);
        Utils.showError('error', `Error al cargar categorías: ${error.message}`);
        Notification.show('Error al cargar categorías', 'error');
    } finally {
        Utils.hideLoading('loading');
    }
}

// Renderizar categorías en la grilla
function renderCategories(categories) {
    const grid = document.getElementById('categoriesGrid');
    if (!grid) return;

    if (categories.length === 0) {
        grid.innerHTML = `
            <div class="no-data">
                <i class="fas fa-tags"></i>
                <h3>No hay categorías</h3>
                <p>No se encontraron categorías que coincidan con los criterios de búsqueda.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = categories.map(category => `
        <div class="category-card">
            <div class="card-header">
                <h3 class="card-title">${escapeHtml(category.name)}</h3>
                <span class="card-id">#${category.id}</span>
            </div>
            <div class="card-content">
                <div class="card-meta">
                    <p><strong>ID:</strong> ${category.id}</p>
                    <p><strong>Nombre:</strong> ${escapeHtml(category.name)}</p>
                    ${category.description ? `<p><strong>Descripción:</strong> ${escapeHtml(category.description)}</p>` : ''}
                </div>
            </div>
        </div>
    `).join('');
}

// Inicializar búsqueda
function initSearch() {
    Search.init('searchInput', (searchTerm) => {
        const filteredCategories = Search.filterItems(allCategories, searchTerm, [
            'name',
            'description',
            'id'
        ]);
        renderCategories(filteredCategories);
    });
}

// Inicializar formulario de creación
function initCreateCategoryForm() {
    const form = document.getElementById('createCategoryForm');
    if (!form) return;

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        
        const formData = new FormData(form);
        const category = {
            name: formData.get('name'),
            description: formData.get('description')
        };

        try {
            await RestService.createCategory(category);
            Notification.show('Categoría creada exitosamente', 'success');
            closeCreateCategoryModal();
            form.reset();
            loadCategories(); // Recargar la lista
        } catch (error) {
            console.error('Error creating category:', error);
            Notification.show('Error al crear categoría', 'error');
        }
    });
}

// Mostrar modal de creación
function showCreateCategoryModal() {
    Modal.show('createCategoryModal');
}

// Cerrar modal de creación
function closeCreateCategoryModal() {
    Modal.hide('createCategoryModal');
    const form = document.getElementById('createCategoryForm');
    if (form) {
        form.reset();
    }
}

// Función para escapar HTML
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
