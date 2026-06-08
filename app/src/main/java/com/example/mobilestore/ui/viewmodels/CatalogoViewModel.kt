package com.example.mobilestore.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilestore.data.ProductoRepository
import com.example.mobilestore.data.models.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class CatalogoUiState {
    object Loading : CatalogoUiState()
    data class Success(val productos: List<Producto>) : CatalogoUiState()
    data class Error(val message: String) : CatalogoUiState()
}

class CatalogoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogoUiState>(CatalogoUiState.Loading)
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init {
        fetchProductos()
    }

    private fun fetchProductos() {
        viewModelScope.launch {
            repository.getProductos()
                .catch { e ->
                    _uiState.value = CatalogoUiState.Error(e.message ?: "Error desconocido")
                }
                .collect { productos ->
                    _uiState.value = CatalogoUiState.Success(productos)
                }
        }
    }
}
