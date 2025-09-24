// Funcionalidad específica para la página de organizaciones

let allOrganizations = [];

// Inicialización de la página
document.addEventListener('DOMContentLoaded', () => {
    loadOrganizations();
    initSearch();
    initCreateOrganizationForm();
});

// Cargar organizaciones desde la API REST
async function loadOrganizations() {
    try {
        Utils.showLoading('loading');
        Utils.hideError('error');
        
        allOrganizations = await RestService.getOrganizations();
        renderOrganizations(allOrganizations);
        
    } catch (error) {
        console.error('Error loading organizations:', error);
        Utils.showError('error', `Error al cargar organizaciones: ${error.message}`);
        Notification.show('Error al cargar organizaciones', 'error');
    } finally {
        Utils.hideLoading('loading');
    }
}

// Renderizar organizaciones en la grilla
function renderOrganizations(organizations) {
    const grid = document.getElementById('organizationsGrid');
    if (!grid) return;

    if (organizations.length === 0) {
        grid.innerHTML = `
            <div class="no-data">
                <i class="fas fa-building"></i>
                <h3>No hay organizaciones</h3>
                <p>No se encontraron organizaciones que coincidan con los criterios de búsqueda.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = organizations.map(organization => `
        <div class="organization-card">
            <div class="card-header">
                <h3 class="card-title">${escapeHtml(organization.name)}</h3>
                <span class="card-id">#${organization.id}</span>
            </div>
            <div class="card-content">
                <div class="card-meta">
                    <p><strong>ID:</strong> ${organization.id}</p>
                    <p><strong>Nombre:</strong> ${escapeHtml(organization.name)}</p>
                </div>
            </div>
        </div>
    `).join('');
}

// Inicializar búsqueda
function initSearch() {
    Search.init('searchInput', (searchTerm) => {
        const filteredOrganizations = Search.filterItems(allOrganizations, searchTerm, [
            'name',
            'id'
        ]);
        renderOrganizations(filteredOrganizations);
    });
}

// Inicializar formulario de creación
function initCreateOrganizationForm() {
    const form = document.getElementById('createOrganizationForm');
    if (!form) return;

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        
        const formData = new FormData(form);
        const organization = {
            name: formData.get('name')
        };

        try {
            await RestService.createOrganization(organization);
            Notification.show('Organización creada exitosamente', 'success');
            closeCreateOrganizationModal();
            form.reset();
            loadOrganizations(); // Recargar la lista
        } catch (error) {
            console.error('Error creating organization:', error);
            Notification.show('Error al crear organización', 'error');
        }
    });
}

// Mostrar modal de creación
function showCreateOrganizationModal() {
    Modal.show('createOrganizationModal');
}

// Cerrar modal de creación
function closeCreateOrganizationModal() {
    Modal.hide('createOrganizationModal');
    const form = document.getElementById('createOrganizationForm');
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
