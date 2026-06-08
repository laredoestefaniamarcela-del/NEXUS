package com.example.mobilestore.data.models

import com.google.firebase.firestore.DocumentId

data class Producto(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val marca: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val especificaciones: String = ""
)
