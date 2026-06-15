package com.nexus.mobilestore.data.models

data class CarritoItem(
    val productoId: String = "",
    val nombre: String = "",
    val marca: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val cantidad: Int = 1
)
