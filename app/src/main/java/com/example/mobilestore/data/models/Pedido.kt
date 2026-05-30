package com.example.mobilestore.data.models

import com.google.firebase.Timestamp

data class Pedido(
    val usuario: String = "",
    val productos: List<String> = emptyList(),
    val fecha: Timestamp? = null,
    val estado: String = "pendiente"
)
