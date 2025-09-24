import { useState, useEffect, useMemo } from 'react'
import axios from 'axios'
import { motion, AnimatePresence } from 'framer-motion'
import { 
  Search, 
  Filter, 
  Plus, 
  Eye, 
  Building2, 
  Package, 
  Tag, 
  X,
  ChevronDown,
  ChevronUp,
  RefreshCw,
  AlertCircle,
  CheckCircle,
  Users,
  FolderPlus
} from 'lucide-react'
import toast, { Toaster } from 'react-hot-toast'
import './App.css'

function App() {
  const [products, setProducts] = useState([])
  const [organizations, setOrganizations] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  // Filtros y búsqueda
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedOrg, setSelectedOrg] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [sortBy, setSortBy] = useState('name')
  const [sortOrder, setSortOrder] = useState('asc')
  
  // UI states
  const [showFilters, setShowFilters] = useState(false)
  const [selectedProduct, setSelectedProduct] = useState(null)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [isCreating, setIsCreating] = useState(false)
  const [showCreateDropdown, setShowCreateDropdown] = useState(false)
  const [createType, setCreateType] = useState('product')

  const API_BASE_URL = '/api'

  useEffect(() => {
    fetchData()
  }, [])

  // Cerrar dropdown al hacer click fuera
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showCreateDropdown && !event.target.closest('.create-dropdown-container')) {
        setShowCreateDropdown(false)
      }
      if (showFilters && !event.target.closest('.filters-container')) {
        setShowFilters(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [showCreateDropdown, showFilters])

  const fetchData = async () => {
    try {
      setLoading(true)
      const [productsRes, organizationsRes, categoriesRes] = await Promise.all([
        axios.get(`${API_BASE_URL}/products`),
        axios.get(`${API_BASE_URL}/organizations`),
        axios.get(`${API_BASE_URL}/categories`)
      ])
      
      setProducts(productsRes.data)
      setOrganizations(organizationsRes.data)
      setCategories(categoriesRes.data)
      toast.success('Datos cargados exitosamente')
    } catch (err) {
      setError('Error al cargar los datos: ' + err.message)
      toast.error('Error al cargar los datos')
      console.error('Error fetching data:', err)
    } finally {
      setLoading(false)
    }
  }

  // Filtrado y ordenamiento
  const filteredAndSortedProducts = useMemo(() => {
    let filtered = products.filter(product => {
      const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           product.organization?.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           product.category?.name.toLowerCase().includes(searchTerm.toLowerCase())
      
      const matchesOrg = !selectedOrg || product.organization?.id.toString() === selectedOrg
      const matchesCategory = !selectedCategory || product.category?.id.toString() === selectedCategory
      
      return matchesSearch && matchesOrg && matchesCategory
    })

    // Ordenamiento
    filtered.sort((a, b) => {
      let aValue, bValue
      
      switch (sortBy) {
        case 'name':
          aValue = a.name.toLowerCase()
          bValue = b.name.toLowerCase()
          break
        case 'organization':
          aValue = a.organization?.name?.toLowerCase() || ''
          bValue = b.organization?.name?.toLowerCase() || ''
          break
        case 'category':
          aValue = a.category?.name?.toLowerCase() || ''
          bValue = b.category?.name?.toLowerCase() || ''
          break
        default:
          aValue = a.name.toLowerCase()
          bValue = b.name.toLowerCase()
      }
      
      if (sortOrder === 'asc') {
        return aValue.localeCompare(bValue)
      } else {
        return bValue.localeCompare(aValue)
      }
    })

    return filtered
  }, [products, searchTerm, selectedOrg, selectedCategory, sortBy, sortOrder])

  const handleCreateProduct = async (productData) => {
    try {
      setIsCreating(true)
      await axios.post(`${API_BASE_URL}/products`, productData)
      // Recargar todos los productos para obtener las relaciones completas
      await fetchData()
      toast.success('Producto creado exitosamente')
      setShowCreateForm(false)
    } catch (err) {
      toast.error('Error al crear el producto')
      console.error('Error creating product:', err)
    } finally {
      setIsCreating(false)
    }
  }

  const handleCreateOrganization = async (orgData) => {
    try {
      setIsCreating(true)
      const response = await axios.post(`${API_BASE_URL}/organizations`, orgData)
      setOrganizations(prev => [...prev, response.data])
      toast.success('Organización creada exitosamente')
      setShowCreateForm(false)
    } catch (err) {
      toast.error('Error al crear la organización')
      console.error('Error creating organization:', err)
    } finally {
      setIsCreating(false)
    }
  }

  const handleCreateCategory = async (categoryData) => {
    try {
      setIsCreating(true)
      const response = await axios.post(`${API_BASE_URL}/categories`, categoryData)
      setCategories(prev => [...prev, response.data])
      toast.success('Categoría creada exitosamente')
      setShowCreateForm(false)
    } catch (err) {
      toast.error('Error al crear la categoría')
      console.error('Error creating category:', err)
    } finally {
      setIsCreating(false)
    }
  }

  const resetFilters = () => {
    setSearchTerm('')
    setSelectedOrg('')
    setSelectedCategory('')
    setSortBy('name')
    setSortOrder('asc')
  }

  if (loading) {
    return (
      <div className="loading-container">
        <motion.div 
          className="loading-spinner"
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
        >
          <RefreshCw size={48} />
        </motion.div>
        <motion.h2
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          Cargando datos...
        </motion.h2>
      </div>
    )
  }

  if (error) {
    return (
      <div className="error-container">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="error-content"
        >
          <AlertCircle size={64} className="error-icon" />
          <h2>Error</h2>
          <p>{error}</p>
          <motion.button 
            onClick={fetchData}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="retry-button"
          >
            <RefreshCw size={20} />
            Reintentar
          </motion.button>
        </motion.div>
      </div>
    )
  }

  return (
    <div className="app">
      <Toaster position="top-right" />
      
      <motion.header 
        className="header"
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
      >
        <div className="header-content">
          <div className="header-left">
            <div className="header-text">
              <h1>
                <Building2 size={28} />
                Gestión de Productos
              </h1>
              <p>Sistema de consulta de productos usando JPA y arquitectura de dos niveles</p>
            </div>
          </div>
          
          <div className="header-center">
            <div className="search-bar">
              <Search size={18} />
              <input
                type="text"
                placeholder="Buscar productos..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          
          <div className="header-right">
            <div className="stats-mini">
              <div className="stat-mini">
                <Package size={16} />
                <span>{products.length}</span>
              </div>
              <div className="stat-mini">
                <Building2 size={16} />
                <span>{organizations.length}</span>
              </div>
              <div className="stat-mini">
                <Tag size={16} />
                <span>{categories.length}</span>
              </div>
            </div>
            
            <div className="create-dropdown-container">
              <motion.button
                onClick={() => setShowCreateDropdown(!showCreateDropdown)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="create-button"
              >
                <Plus size={18} />
                Crear Nuevo
                <ChevronDown size={14} />
              </motion.button>
              
              <AnimatePresence>
                {showCreateDropdown && (
                  <motion.div
                    className="create-dropdown"
                    initial={{ opacity: 0, y: -10, scale: 0.95 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: -10, scale: 0.95 }}
                    transition={{ duration: 0.2 }}
                  >
                    <motion.button
                      onClick={() => {
                        setCreateType('product')
                        setShowCreateForm(true)
                        setShowCreateDropdown(false)
                      }}
                      whileHover={{ backgroundColor: '#f8f9fa' }}
                      className="dropdown-item"
                    >
                      <Package size={16} />
                      Producto
                    </motion.button>
                    <motion.button
                      onClick={() => {
                        setCreateType('organization')
                        setShowCreateForm(true)
                        setShowCreateDropdown(false)
                      }}
                      whileHover={{ backgroundColor: '#f8f9fa' }}
                      className="dropdown-item"
                    >
                      <Building2 size={16} />
                      Organización
                    </motion.button>
                    <motion.button
                      onClick={() => {
                        setCreateType('category')
                        setShowCreateForm(true)
                        setShowCreateDropdown(false)
                      }}
                      whileHover={{ backgroundColor: '#f8f9fa' }}
                      className="dropdown-item"
                    >
                      <Tag size={16} />
                      Categoría
                    </motion.button>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </div>
      </motion.header>

      <main className="main">

        {/* Products Section Header */}
        <motion.section 
          className="products-header-section"
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.6, delay: 0.3 }}
        >
          <div className="products-header">
            <div className="section-header">
              <h2>
                <Package size={24} />
                Productos ({filteredAndSortedProducts.length})
              </h2>
            </div>
            
            <div className="filters-container">
              <motion.button
                onClick={() => setShowFilters(!showFilters)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="filter-toggle"
              >
                <Filter size={16} />
                {showFilters ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                {showFilters ? 'Ocultar' : 'Mostrar'} Filtros
              </motion.button>

              <AnimatePresence>
                {showFilters && (
                  <motion.div
                    className="filters-content"
                    initial={{ 
                      opacity: 0,
                      scale: 0.95
                    }}
                    animate={{ 
                      opacity: 1,
                      scale: 1
                    }}
                    exit={{ 
                      opacity: 0,
                      scale: 0.95
                    }}
                    transition={{ 
                      duration: 0.2,
                      ease: "easeOut"
                    }}
                  >
                    <div className="filter-controls">
                      <div className="filter-group">
                        <label>Organización</label>
                        <select
                          value={selectedOrg}
                          onChange={(e) => setSelectedOrg(e.target.value)}
                        >
                          <option value="">Todas</option>
                          {organizations.map(org => (
                            <option key={org.id} value={org.id}>{org.name}</option>
                          ))}
                        </select>
                      </div>

                      <div className="filter-group">
                        <label>Categoría</label>
                        <select
                          value={selectedCategory}
                          onChange={(e) => setSelectedCategory(e.target.value)}
                        >
                          <option value="">Todas</option>
                          {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                          ))}
                        </select>
                      </div>

                      <div className="filter-group">
                        <label>Ordenar</label>
                        <select
                          value={sortBy}
                          onChange={(e) => setSortBy(e.target.value)}
                        >
                          <option value="name">Nombre</option>
                          <option value="organization">Organización</option>
                          <option value="category">Categoría</option>
                        </select>
                      </div>

                      <div className="filter-group">
                        <label>Dirección</label>
                        <select
                          value={sortOrder}
                          onChange={(e) => setSortOrder(e.target.value)}
                        >
                          <option value="asc">A-Z</option>
                          <option value="desc">Z-A</option>
                        </select>
                      </div>

                      <motion.button
                        onClick={resetFilters}
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        className="reset-filters"
                      >
                        <RefreshCw size={16} />
                        Limpiar
                      </motion.button>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </motion.section>

        {/* Products Section */}
        <motion.section 
          className="products-section"
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.6, delay: 0.4 }}
        >

          {filteredAndSortedProducts.length === 0 ? (
            <motion.div 
              className="empty-state"
              initial={{ scale: 0.9, opacity: 0, y: 20 }}
              animate={{ scale: 1, opacity: 1, y: 0 }}
              exit={{ scale: 0.9, opacity: 0, y: -20 }}
              transition={{ duration: 0.4, ease: "easeOut" }}
            >
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ delay: 0.2, duration: 0.5, ease: "easeOut" }}
              >
                <Package size={80} className="empty-icon" />
              </motion.div>
              <h3>
                {searchTerm || selectedOrg || selectedCategory 
                  ? 'No se encontraron productos'
                  : 'No hay productos disponibles'
                }
              </h3>
              <p>
                {searchTerm || selectedOrg || selectedCategory 
                  ? 'Intenta ajustar tus filtros o buscar un término diferente para encontrar lo que buscas.'
                  : 'Aún no se han agregado productos al sistema. ¡Crea el primero!'
                }
              </p>
              {(searchTerm || selectedOrg || selectedCategory) && (
                <motion.button
                  onClick={resetFilters}
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  className="clear-filters-button"
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.4 }}
                >
                  <RefreshCw size={18} />
                  Limpiar Filtros
                </motion.button>
              )}
            </motion.div>
          ) : (
            <div className="products-grid">
              {filteredAndSortedProducts.map((product, index) => (
                  <motion.div
                    key={product.id}
                    className="product-card"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.2 }}
                    whileHover={{ scale: 1.02 }}
                    onClick={() => setSelectedProduct(product)}
                  >
                    <div className="product-header">
                      <h3>{product.name}</h3>
                      <div className="view-details">
                        <Eye size={18} />
                      </div>
                    </div>
                    <div className="product-details">
                      <div className="detail-row">
                        <div className="detail-item">
                          <Building2 size={14} />
                          <span className="detail-text">{product.organization?.name || 'N/A'}</span>
                        </div>
                        <div className="detail-item">
                          <Tag size={14} />
                          <span className="detail-text">{product.category?.name || 'N/A'}</span>
                        </div>
                      </div>
                      {product.category?.description && (
                        <div className="detail-item description">
                          <span>{product.category.description}</span>
                        </div>
                      )}
                    </div>
                  </motion.div>
                ))}
            </div>
          )}
        </motion.section>
      </main>

      <motion.footer 
        className="footer"
        initial={{ y: 100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.6, delay: 0.5 }}
      >
        <p>Taller de Datos - Arquitectura de Software</p>
        <p>Backend: Spring Boot + JPA + MySQL | Frontend: React + Vite</p>
      </motion.footer>

      {/* Product Details Modal */}
      <AnimatePresence>
        {selectedProduct && (
          <motion.div
            className="modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setSelectedProduct(null)}
          >
            <motion.div
              className="modal-content"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="modal-header">
                <h2>Detalles del Producto</h2>
                <motion.button
                  onClick={() => setSelectedProduct(null)}
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                  className="close-button"
                >
                  <X size={24} />
                </motion.button>
              </div>
              <div className="modal-body">
                <div className="detail-section">
                  <h3>{selectedProduct.name}</h3>
                  <div className="detail-grid">
                    <div className="detail-item">
                      <Building2 size={20} />
                      <div className="detail-content">
                        <span className="detail-label">Organización</span>
                        <span className="detail-value">{selectedProduct.organization?.name || 'N/A'}</span>
                      </div>
                    </div>
                    <div className="detail-item">
                      <Tag size={20} />
                      <div className="detail-content">
                        <span className="detail-label">Categoría</span>
                        <span className="detail-value">{selectedProduct.category?.name || 'N/A'}</span>
                      </div>
                    </div>
                    {selectedProduct.category?.description && (
                      <div className="detail-item full-width">
                        <strong>Descripción de la categoría:</strong>
                        <span>{selectedProduct.category.description}</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Create Product Modal */}
      <AnimatePresence>
        {showCreateForm && (
          <motion.div
            className="modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setShowCreateForm(false)}
          >
            <motion.div
              className="modal-content create-modal"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="modal-header">
                <h2>
                  {createType === 'product' && 'Crear Nuevo Producto'}
                  {createType === 'organization' && 'Crear Nueva Organización'}
                  {createType === 'category' && 'Crear Nueva Categoría'}
                </h2>
                <motion.button
                  onClick={() => setShowCreateForm(false)}
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                  className="close-button"
                >
                  <X size={24} />
                </motion.button>
              </div>
              <div className="modal-body">
                {createType === 'product' && (
                  <CreateProductForm
                    organizations={organizations}
                    categories={categories}
                    onSubmit={handleCreateProduct}
                    isSubmitting={isCreating}
                    onCancel={() => setShowCreateForm(false)}
                  />
                )}
                {createType === 'organization' && (
                  <CreateOrganizationForm
                    onSubmit={handleCreateOrganization}
                    isSubmitting={isCreating}
                    onCancel={() => setShowCreateForm(false)}
                  />
                )}
                {createType === 'category' && (
                  <CreateCategoryForm
                    onSubmit={handleCreateCategory}
                    isSubmitting={isCreating}
                    onCancel={() => setShowCreateForm(false)}
                  />
                )}
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}

// Componente para el formulario de creación de productos
function CreateProductForm({ organizations, categories, onSubmit, isSubmitting, onCancel }) {
  const [formData, setFormData] = useState({
    name: '',
    organizationId: '',
    categoryId: ''
  })

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!formData.name || !formData.organizationId || !formData.categoryId) {
      toast.error('Por favor completa todos los campos')
      return
    }
    
    const productData = {
      name: formData.name,
      organization: { id: parseInt(formData.organizationId) },
      category: { id: parseInt(formData.categoryId) }
    }
    
    onSubmit(productData)
  }

  return (
    <form onSubmit={handleSubmit} className="create-form">
      <div className="form-group">
        <label htmlFor="name">Nombre del Producto *</label>
        <input
          type="text"
          id="name"
          value={formData.name}
          onChange={(e) => setFormData({...formData, name: e.target.value})}
          placeholder="Ingresa el nombre del producto"
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="organizationId">Organización *</label>
        <select
          id="organizationId"
          value={formData.organizationId}
          onChange={(e) => setFormData({...formData, organizationId: e.target.value})}
          required
        >
          <option value="">Selecciona una organización</option>
          {organizations.map(org => (
            <option key={org.id} value={org.id}>{org.name}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="categoryId">Categoría *</label>
        <select
          id="categoryId"
          value={formData.categoryId}
          onChange={(e) => setFormData({...formData, categoryId: e.target.value})}
          required
        >
          <option value="">Selecciona una categoría</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      <div className="form-actions">
        <motion.button
          type="button"
          onClick={onCancel}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="cancel-button"
        >
          Cancelar
        </motion.button>
        <motion.button
          type="submit"
          disabled={isSubmitting}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="submit-button"
        >
          {isSubmitting ? (
            <>
              <RefreshCw size={16} className="spinning" />
              Creando...
            </>
          ) : (
            <>
              <CheckCircle size={16} />
              Crear Producto
            </>
          )}
        </motion.button>
      </div>
    </form>
  )
}

// Componente para el formulario de creación de organizaciones
function CreateOrganizationForm({ onSubmit, isSubmitting, onCancel }) {
  const [formData, setFormData] = useState({
    name: ''
  })

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!formData.name) {
      toast.error('Por favor ingresa el nombre de la organización')
      return
    }
    
    onSubmit(formData)
  }

  return (
    <form onSubmit={handleSubmit} className="create-form">
      <div className="form-group">
        <label htmlFor="name">Nombre de la Organización *</label>
        <input
          type="text"
          id="name"
          value={formData.name}
          onChange={(e) => setFormData({...formData, name: e.target.value})}
          placeholder="Ingresa el nombre de la organización"
          required
        />
      </div>

      <div className="form-actions">
        <motion.button
          type="button"
          onClick={onCancel}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="cancel-button"
        >
          Cancelar
        </motion.button>
        <motion.button
          type="submit"
          disabled={isSubmitting}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="submit-button"
        >
          {isSubmitting ? (
            <>
              <RefreshCw size={16} className="spinning" />
              Creando...
            </>
          ) : (
            <>
              <CheckCircle size={16} />
              Crear Organización
            </>
          )}
        </motion.button>
      </div>
    </form>
  )
}

// Componente para el formulario de creación de categorías
function CreateCategoryForm({ onSubmit, isSubmitting, onCancel }) {
  const [formData, setFormData] = useState({
    name: '',
    description: ''
  })

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!formData.name) {
      toast.error('Por favor ingresa el nombre de la categoría')
      return
    }
    
    onSubmit(formData)
  }

  return (
    <form onSubmit={handleSubmit} className="create-form">
      <div className="form-group">
        <label htmlFor="name">Nombre de la Categoría *</label>
        <input
          type="text"
          id="name"
          value={formData.name}
          onChange={(e) => setFormData({...formData, name: e.target.value})}
          placeholder="Ingresa el nombre de la categoría"
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="description">Descripción</label>
        <textarea
          id="description"
          value={formData.description}
          onChange={(e) => setFormData({...formData, description: e.target.value})}
          placeholder="Ingresa una descripción de la categoría (opcional)"
          rows={3}
        />
      </div>

      <div className="form-actions">
        <motion.button
          type="button"
          onClick={onCancel}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="cancel-button"
        >
          Cancelar
        </motion.button>
        <motion.button
          type="submit"
          disabled={isSubmitting}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          className="submit-button"
        >
          {isSubmitting ? (
            <>
              <RefreshCw size={16} className="spinning" />
              Creando...
            </>
          ) : (
            <>
              <CheckCircle size={16} />
              Crear Categoría
            </>
          )}
        </motion.button>
      </div>
    </form>
  )
}

export default App