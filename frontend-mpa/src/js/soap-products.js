// Funcionalidad específica para la página de productos SOAP

let allProducts = [];
let allOrganizations = [];
let allCategories = [];
let lastSoapResponse = null;

// Inicialización de la página
document.addEventListener('DOMContentLoaded', () => {
    loadOrganizations();
    loadCategories();
    initCreateProductSOAPForm();
});

// Cargar productos via SOAP
async function loadProductsSOAP() {
    try {
        console.log('Iniciando carga de productos via SOAP...');
        Utils.showLoading('loading');
        Utils.hideError('error');
        
        const soapResponse = await SoapService.getProducts();
        lastSoapResponse = soapResponse;
        
        console.log('Respuesta SOAP recibida:', soapResponse);
        
        // Mostrar respuesta SOAP raw
        displaySoapResponse(soapResponse);
        
        // Parsear productos de la respuesta SOAP
        allProducts = parseSoapProducts(soapResponse);
        console.log('Productos parseados:', allProducts);
        console.log('Número de productos encontrados:', allProducts.length);
        
        renderProducts(allProducts);
        
        if (allProducts.length > 0) {
            Notification.show(`${allProducts.length} productos cargados via SOAP exitosamente`, 'success');
        } else {
            Notification.show('No se encontraron productos en la respuesta SOAP', 'warning');
        }
        
    } catch (error) {
        console.error('Error loading products via SOAP:', error);
        Utils.showError('error', `Error al cargar productos via SOAP: ${error.message}`);
        Notification.show('Error al cargar productos via SOAP', 'error');
    } finally {
        Utils.hideLoading('loading');
    }
}

// Parsear productos de la respuesta SOAP
function parseSoapProducts(soapResponse) {
    const products = [];
    
    try {
        // Buscar productos en la respuesta SOAP con diferentes namespaces
        const productElements = soapResponse.querySelectorAll('product, Product, ns3\\:product, [local-name()="product"]');
        
        productElements.forEach(productElement => {
            const product = {
                id: getSoapElementValue(productElement, 'id, Id, ns3\\:id'),
                name: getSoapElementValue(productElement, 'name, Name, ns3\\:name'),
                organization: {
                    id: getSoapElementValue(productElement, 'organization id, organization Id, Organization Id, ns3\\:organization id'),
                    name: getSoapElementValue(productElement, 'organization name, organization Name, Organization Name, ns3\\:organization name')
                },
                category: {
                    id: getSoapElementValue(productElement, 'category id, category Id, Category Id, ns3\\:category id'),
                    name: getSoapElementValue(productElement, 'category name, category Name, Category Name, ns3\\:category name'),
                    description: getSoapElementValue(productElement, 'category description, category Description, Category Description, ns3\\:category description')
                }
            };
            
            // También intentar obtener datos con selectores directos
            if (!product.id) {
                product.id = productElement.querySelector('id, [local-name()="id"]')?.textContent?.trim();
            }
            if (!product.name) {
                product.name = productElement.querySelector('name, [local-name()="name"]')?.textContent?.trim();
            }
            
            // Organización
            const orgElement = productElement.querySelector('organization, [local-name()="organization"]');
            if (orgElement && !product.organization.id) {
                product.organization.id = orgElement.querySelector('id, [local-name()="id"]')?.textContent?.trim();
                product.organization.name = orgElement.querySelector('name, [local-name()="name"]')?.textContent?.trim();
            }
            
            // Categoría
            const catElement = productElement.querySelector('category, [local-name()="category"]');
            if (catElement && !product.category.id) {
                product.category.id = catElement.querySelector('id, [local-name()="id"]')?.textContent?.trim();
                product.category.name = catElement.querySelector('name, [local-name()="name"]')?.textContent?.trim();
                product.category.description = catElement.querySelector('description, [local-name()="description"]')?.textContent?.trim();
            }
            
            if (product.name && product.id) {
                products.push(product);
            }
        });
        
    } catch (error) {
        console.error('Error parsing SOAP products:', error);
    }
    
    return products;
}

// Obtener valor de elemento SOAP
function getSoapElementValue(parentElement, selectors) {
    const selectorList = selectors.split(', ');
    
    for (const selector of selectorList) {
        try {
            const element = parentElement.querySelector(selector.trim());
            if (element && element.textContent) {
                return element.textContent.trim();
            }
        } catch (e) {
            // Continuar si el selector falla (por namespace issues)
            console.debug('Selector failed:', selector, e);
        }
    }
    
    return null;
}

// Mostrar respuesta SOAP
function displaySoapResponse(soapResponse) {
    const responseDiv = document.getElementById('soapResponse');
    const contentDiv = document.getElementById('soapResponseContent');
    
    if (responseDiv && contentDiv) {
        responseDiv.style.display = 'block';
        
        // Formatear el XML para mejor legibilidad
        const xmlString = new XMLSerializer().serializeToString(soapResponse);
        const formattedXml = formatXml(xmlString);
        contentDiv.textContent = formattedXml;
        
        console.log('SOAP Response XML:', xmlString);
        console.log('Formatted XML:', formattedXml);
    }
}

// Formatear XML para mejor visualización
function formatXml(xml) {
    let formatted = '';
    const reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    let pad = 0;
    
    xml.split('\r\n').forEach(function(line) {
        let indent = 0;
        if (line.match(/.+<\/\w[^>]*>$/)) {
            indent = 0;
        } else if (line.match(/^<\/\w/)) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (line.match(/^<\w[^>]*[^\/]>.*$/)) {
            indent = 1;
        } else {
            indent = 0;
        }
        
        const padding = new Array(pad + 1).join('  ');
        formatted += padding + line + '\r\n';
        pad += indent;
    });
    
    return formatted;
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
                <p>No se encontraron productos en la respuesta SOAP.</p>
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

// Poblar select de organizaciones
function populateOrganizationSelect() {
    const select = document.getElementById('soapProductOrganization');
    if (!select) return;

    select.innerHTML = '<option value="">Seleccionar organización...</option>' +
        allOrganizations.map(org => 
            `<option value="${org.id}">${escapeHtml(org.name)}</option>`
        ).join('');
}

// Poblar select de categorías
function populateCategorySelect() {
    const select = document.getElementById('soapProductCategory');
    if (!select) return;

    select.innerHTML = '<option value="">Seleccionar categoría...</option>' +
        allCategories.map(cat => 
            `<option value="${cat.id}">${escapeHtml(cat.name)}</option>`
        ).join('');
}

// Inicializar formulario de creación SOAP
function initCreateProductSOAPForm() {
    const form = document.getElementById('createProductSOAPForm');
    if (!form) return;

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        
        const formData = new FormData(form);
        const product = {
            name: formData.get('name'),
            organizationId: parseInt(formData.get('organizationId')),
            categoryId: parseInt(formData.get('categoryId'))
        };

        try {
            const soapResponse = await SoapService.createProduct(product);
            lastSoapResponse = soapResponse;
            
            // Mostrar respuesta SOAP
            displaySoapResponse(soapResponse);
            
            Notification.show('Producto creado via SOAP exitosamente', 'success');
            closeCreateProductSOAPModal();
            form.reset();
            
            // Recargar productos
            loadProductsSOAP();
            
        } catch (error) {
            console.error('Error creating product via SOAP:', error);
            Notification.show('Error al crear producto via SOAP', 'error');
        }
    });
}

// Mostrar modal de creación SOAP
function showCreateProductSOAPModal() {
    Modal.show('createProductSOAPModal');
}

// Cerrar modal de creación SOAP
function closeCreateProductSOAPModal() {
    Modal.hide('createProductSOAPModal');
    const form = document.getElementById('createProductSOAPForm');
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

// Función para formatear fecha
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}
