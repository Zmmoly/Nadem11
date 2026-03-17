package awab.quran.ar.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import awab.quran.ar.ui.screens.auth.LoginScreen
import awab.quran.ar.ui.screens.auth.RegisterScreen
import awab.quran.ar.ui.screens.auth.ForgotPasswordScreen
import awab.quran.ar.ui.screens.home.HomeScreen
import awab.quran.ar.ui.screens.home.Surah
import awab.quran.ar.ui.screens.splash.SplashScreen
import awab.quran.ar.ui.screens.recitation.RecitationScreen
import awab.quran.ar.ui.screens.profile.ProfileScreen
import awab.quran.ar.ui.screens.surah.SurahScreen
import awab.quran.ar.ui.screens.terms.TermsScreen
import awab.quran.ar.ui.screens.terms.isTermsAccepted

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Terms : Screen("terms")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Recitation : Screen("recitation")
    object Profile : Screen("profile")
    object Surah : Screen("surah")
}

@Composable
fun NadeemNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    if (!isTermsAccepted(context)) {
                        navController.navigate(Screen.Terms.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToHome = {
                    if (!isTermsAccepted(context)) {
                        navController.navigate(Screen.Terms.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Terms.route) {
            TermsScreen(
                onAccepted = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Terms.route) { inclusive = true }
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
                onNavigateToRecitation = {
                    navController.navigate(Screen.Recitation.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onSurahClick = { surah ->
                    selectedSurah = surah
                    navController.navigate(Screen.Surah.route)
                }
            )
        }

        composable(Screen.Recitation.route) {
            RecitationScreen(
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

        composable(Screen.Surah.route) {
            selectedSurah?.let { surah ->
                SurahScreen(
                    surah = surah,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
