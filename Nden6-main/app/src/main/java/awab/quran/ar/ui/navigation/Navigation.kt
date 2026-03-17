package awab.quran.ar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import awab.quran.ar.ui.screens.auth.LoginScreen
import awab.quran.ar.ui.screens.auth.RegisterScreen
import awab.quran.ar.ui.screens.auth.ForgotPasswordScreen
import awab.quran.ar.ui.screens.home.HomeScreen
import awab.quran.ar.ui.screens.splash.SplashScreen
import awab.quran.ar.ui.screens.recitation.RecitationScreen
import awab.quran.ar.ui.screens.profile.ProfileScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Recitation : Screen("recitation/{surahName}/{totalVerses}") {
        fun createRoute(surahName: String, totalVerses: Int) = "recitation/$surahName/$totalVerses"
    }
    object Profile : Screen("profile")
}

@Composable
fun NadeemNavigation() {
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
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRecitation = { surahName, totalVerses ->
                    navController.navigate(Screen.Recitation.createRoute(surahName, totalVerses))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(
            route = Screen.Recitation.route,
            arguments = listOf(
                navArgument("surahName") { type = NavType.StringType },
                navArgument("totalVerses") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val surahName = backStackEntry.arguments?.getString("surahName") ?: "الفاتحة"
            val totalVerses = backStackEntry.arguments?.getInt("totalVerses") ?: 7
            RecitationScreen(
                surahName = surahName,
                totalVerses = totalVerses,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
