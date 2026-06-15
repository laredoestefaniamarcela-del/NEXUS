package com.nexus.mobilestore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexus.mobilestore.ui.screens.CartScreen
import com.nexus.mobilestore.ui.screens.CatalogoScreen
import com.nexus.mobilestore.ui.screens.DetalleProductoScreen
import com.nexus.mobilestore.ui.screens.LoginScreen
import com.nexus.mobilestore.ui.screens.RegisterScreen
import com.nexus.mobilestore.ui.screens.SplashScreen
import com.nexus.mobilestore.ui.viewmodels.CartViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Catalogo : Screen("catalogo")
    object Detalle : Screen("detalle/{productoId}") {
        fun createRoute(productoId: String) = "detalle/$productoId"
    }
    object Cart : Screen("cart")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // CartViewModel compartido entre DetalleProductoScreen y CartScreen
    val cartViewModel: CartViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToCatalogo = {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                onProductClick = { productoId ->
                    navController.navigate(Screen.Detalle.createRoute(productoId))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(
            route = Screen.Detalle.route,
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            DetalleProductoScreen(
                productoId = productoId,
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onBack = { navController.popBackStack() },
                viewModel = cartViewModel
            )
        }
    }
}
