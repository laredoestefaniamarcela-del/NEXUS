package com.nexus.mobilestore.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.mobilestore.data.ProductoRepository
import com.nexus.mobilestore.data.models.Producto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class OrdenPrecio { NINGUNO, MENOR_A_MAYOR, MAYOR_A_MENOR }

sealed class CatalogoUiState {
    object Loading : CatalogoUiState()
    data class Success(val productos: List<Producto>) : CatalogoUiState()
    data class Error(val message: String) : CatalogoUiState()
}

class CatalogoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _todosLosProductos = MutableStateFlow<List<Producto>>(emptyList())
    private val _isLoading = MutableStateFlow(true)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _marcaSeleccionada = MutableStateFlow<String?>(null)
    val marcaSeleccionada: StateFlow<String?> = _marcaSeleccionada.asStateFlow()

    private val _ordenPrecio = MutableStateFlow(OrdenPrecio.NINGUNO)
    val ordenPrecio: StateFlow<OrdenPrecio> = _ordenPrecio.asStateFlow()

    // Lista dinámica de marcas obtenida de los productos cargados
    val marcasDisponibles: StateFlow<List<String>> = _todosLosProductos
        .map { productos ->
            productos.map { it.marca }.distinct().filter { it.isNotBlank() }.sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado principal: combina todos los filtros en tiempo real
    val uiState: StateFlow<CatalogoUiState> = combine(
        _todosLosProductos,
        _searchQuery,
        _marcaSeleccionada,
        _ordenPrecio,
        _isLoading
    ) { productos, query, marca, orden, loading ->
        if (loading) return@combine CatalogoUiState.Loading

        var filtrados = productos

        // Filtrar por nombre en tiempo real
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtrados = filtrados.filter { it.nombre.lowercase().contains(q) }
        }

        // Filtrar por marca
        if (marca != null) {
            filtrados = filtrados.filter { it.marca.equals(marca, ignoreCase = true) }
        }

        // Ordenar por precio
        filtrados = when (orden) {
            OrdenPrecio.MENOR_A_MAYOR -> filtrados.sortedBy { it.precio }
            OrdenPrecio.MAYOR_A_MENOR -> filtrados.sortedByDescending { it.precio }
            OrdenPrecio.NINGUNO -> filtrados
        }

        CatalogoUiState.Success(filtrados)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogoUiState.Loading
    )

    init {
        fetchProductos()
    }

    private fun fetchProductos() {
        viewModelScope.launch {
            repository.getProductos()
                .catch { _isLoading.value = false }
                .collect { productos ->
                    _todosLosProductos.value = productos
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onMarcaSelected(marca: String?) { _marcaSeleccionada.value = marca }
    fun onOrdenPrecioChange(orden: OrdenPrecio) { _ordenPrecio.value = orden }
}
