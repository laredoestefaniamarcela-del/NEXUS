package com.nexus.mobilestore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable

@Composable
fun CatalogoScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Catálogo de Teléfonos",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = "Próximamente...(^_^;) sorry")
    }
}