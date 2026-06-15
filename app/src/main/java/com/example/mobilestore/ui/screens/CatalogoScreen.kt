package com.nexus.mobilestore.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexus.mobilestore.ui.components.ProductCard
import com.nexus.mobilestore.ui.viewmodels.CatalogoUiState
import com.nexus.mobilestore.ui.viewmodels.CatalogoViewModel
import com.nexus.mobilestore.ui.viewmodels.OrdenPrecio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit = {},
    viewModel: CatalogoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val marcaSeleccionada by viewModel.marcaSeleccionada.collectAsState()
    val ordenPrecio by viewModel.ordenPrecio.collectAsState()
    val marcasDisponibles by viewModel.marcasDisponibles.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nexus Store",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A3A5C)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(onCartClick = onCartClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Buscar teléfonos...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF1A3A5C)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00A8E8),
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Chips: marcas + orden de precio
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip "Todas"
                FilterChip(
                    selected = marcaSeleccionada == null,
                    onClick = { viewModel.onMarcaSelected(null) },
                    label = { Text("Todas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00A8E8),
                        selectedLabelColor = Color.White
                    )
                )

                // Chips por marca (dinámicos desde Firestore)
                marcasDisponibles.forEach { marca ->
                    FilterChip(
                        selected = marcaSeleccionada == marca,
                        onClick = {
                            viewModel.onMarcaSelected(
                                if (marcaSeleccionada == marca) null else marca
                            )
                        },
                        label = { Text(marca) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00A8E8),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Chip orden precio ascendente
                FilterChip(
                    selected = ordenPrecio == OrdenPrecio.MENOR_A_MAYOR,
                    onClick = {
                        viewModel.onOrdenPrecioChange(
                            if (ordenPrecio == OrdenPrecio.MENOR_A_MAYOR) OrdenPrecio.NINGUNO
                            else OrdenPrecio.MENOR_A_MAYOR
                        )
                    },
                    label = { Text("Precio ↑") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1A3A5C),
                        selectedLabelColor = Color.White
                    )
                )

                // Chip orden precio descendente
                FilterChip(
                    selected = ordenPrecio == OrdenPrecio.MAYOR_A_MENOR,
                    onClick = {
                        viewModel.onOrdenPrecioChange(
                            if (ordenPrecio == OrdenPrecio.MAYOR_A_MENOR) OrdenPrecio.NINGUNO
                            else OrdenPrecio.MAYOR_A_MENOR
                        )
                    },
                    label = { Text("Precio ↓") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1A3A5C),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (val state = uiState) {
                is CatalogoUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00A8E8))
                    }
                }

                is CatalogoUiState.Success -> {
                    if (state.productos.isEmpty()) {
                        // Estado vacío: sin resultados
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 56.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sin resultados",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A3A5C)
                                )
                                Text(
                                    text = "Intenta con otro término o filtro",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.productos, key = { it.id }) { producto ->
                                ProductCard(
                                    producto = producto,
                                    onClick = { onProductClick(producto.id) }
                                )
                            }
                        }
                    }
                }

                is CatalogoUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(onCartClick: () -> Unit = {}) {
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Inicio", Icons.Outlined.Home, Icons.Filled.Home),
            BottomNavItem("Catálogo", Icons.Outlined.Storefront, Icons.Filled.Storefront),
            BottomNavItem("Carrito", Icons.Outlined.ShoppingCart, Icons.Filled.ShoppingCart),
            BottomNavItem("Perfil", Icons.Outlined.Person, Icons.Filled.Person)
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    if (index == 2) onCartClick()
                },
                label = { Text(item.title, fontSize = 10.sp) },
                icon = {
                    Icon(
                        imageVector = if (selectedItem == index) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00A8E8),
                    selectedTextColor = Color(0xFF00A8E8),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
