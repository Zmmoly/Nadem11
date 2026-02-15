package awab.quran.ar.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import awab.quran.ar.ui.screens.home.HomeScreen
import awab.quran.ar.ui.screens.home.Surah
import awab.quran.ar.ui.screens.surah.SurahScreen
import awab.quran.ar.ui.screens.profile.ProfileScreen
import awab.quran.ar.ui.screens.recitation.RecitationScreen

@Composable
fun Navigation(navController: NavHostController) {
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToRecitation = {
                    navController.navigate("recitation")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onSurahClick = { surah ->
                    selectedSurah = surah
                    navController.navigate("surah")
                }
            )
        }
        
        composable("surah") {
            selectedSurah?.let { surah ->
                SurahScreen(
                    surah = surah,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable("recitation") {
            RecitationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
