package com.example.mobilestore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobilestore.ui.screens.CatalogoScreen
import com.example.mobilestore.ui.screens.DetalleProductoScreen
import com.example.mobilestore.ui.screens.LoginScreen
import com.example.mobilestore.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Catalogo : Screen("catalogo")
    object Detalle : Screen("detalle/{productoId}") {
        fun createRoute(productoId: String) = "detalle/$productoId"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

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
                }
            )
        }

        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                onProductClick = { productoId ->
                    navController.navigate(Screen.Detalle.createRoute(productoId))
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
                onBack = { navController.popBackStack() }
            )
        }
    }
}
