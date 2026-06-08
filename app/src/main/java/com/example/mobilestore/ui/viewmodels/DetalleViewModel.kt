package com.nexus.mobilestore.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.mobilestore.data.ProductoRepository
import com.nexus.mobilestore.data.models.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DetalleUiState {
    object Loading : DetalleUiState()
    data class Success(val producto: Producto) : DetalleUiState()
    data class Error(val message: String) : DetalleUiState()
}

class DetalleViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Loading)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun fetchProducto(id: String) {
        viewModelScope.launch {
            repository.getProductoById(id).collect { producto ->
                if (producto != null) {
                    _uiState.value = DetalleUiState.Success(producto)
                } else {
                    _uiState.value = DetalleUiState.Error("Producto no encontrado")
                }
            }
        }
    }
}
