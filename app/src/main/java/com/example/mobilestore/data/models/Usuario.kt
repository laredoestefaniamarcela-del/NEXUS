package com.example.mobilestore.data.models

import com.google.firebase.Timestamp

data class Usuario(
    val nombre: String = "",
    val correo: String = "",
    val fechaRegistro: Timestamp? = null
)
