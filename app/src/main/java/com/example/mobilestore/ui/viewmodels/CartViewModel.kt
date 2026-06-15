package com.nexus.mobilestore.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.mobilestore.data.CarritoRepository
import com.nexus.mobilestore.data.models.CarritoItem
import com.nexus.mobilestore.data.models.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CarritoRepository = CarritoRepository()
) : ViewModel() {

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCarrito()
                .catch { /* ignorar errores de red */ }
                .collect { _items.value = it }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        val lista = _items.value.toMutableList()
        val idx = lista.indexOfFirst { it.productoId == producto.id }
        if (idx >= 0) {
            lista[idx] = lista[idx].copy(cantidad = lista[idx].cantidad + 1)
        } else {
            lista.add(
                CarritoItem(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    marca = producto.marca,
                    precio = producto.precio,
                    imagen = producto.imagen,
                    cantidad = 1
                )
            )
        }
        _items.value = lista
        sync()
    }

    fun incrementar(productoId: String) {
        _items.value = _items.value.map {
            if (it.productoId == productoId) it.copy(cantidad = it.cantidad + 1) else it
        }
        sync()
    }

    fun decrementar(productoId: String) {
        _items.value = _items.value
            .map { if (it.productoId == productoId) it.copy(cantidad = it.cantidad - 1) else it }
            .filter { it.cantidad > 0 }
        sync()
    }

    fun eliminar(productoId: String) {
        _items.value = _items.value.filter { it.productoId != productoId }
        sync()
    }

    fun getTotal(): Double = _items.value.sumOf { it.precio * it.cantidad }

    fun getCantidadTotal(): Int = _items.value.sumOf { it.cantidad }

    private fun sync() {
        repository.guardarCarrito(_items.value)
    }
}
