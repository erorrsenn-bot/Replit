package com.nova.client.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nova.client.ui.screens.*
import com.nova.client.viewmodel.MainViewModel

object Routes {
    const val HOME           = "home"
    const val HUD_EDITOR     = "hud_editor"
    const val THEME          = "theme"
    const val CROSSHAIR      = "crosshair"
    const val WAYPOINTS      = "waypoints"
    const val MACROS         = "macros"
    const val FPS_BOOSTER    = "fps_booster"
    const val RESOURCE_PACKS = "resource_packs"
    const val CHAT           = "chat"
    const val SETTINGS       = "settings"
    const val SERVER_PING    = "server_ping"
}

@Composable
fun NavGraph(
    mainViewModel: MainViewModel,
    onRequestOverlayPermission: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController   = navController,
        startDestination = Routes.HOME,
        enterTransition  = { slideInHorizontally(tween(280)) { it / 4 } + fadeIn(tween(280)) },
        exitTransition   = { slideOutHorizontally(tween(280)) { -it / 4 } + fadeOut(tween(280)) },
        popEnterTransition = { slideInHorizontally(tween(280)) { -it / 4 } + fadeIn(tween(280)) },
        popExitTransition  = { slideOutHorizontally(tween(280)) { it / 4 } + fadeOut(tween(280)) }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = mainViewModel,
                onNavigate = { navController.navigate(it) },
                onRequestOverlayPermission = onRequestOverlayPermission
            )
        }
        composable(Routes.HUD_EDITOR) {
            HudEditorScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.THEME) {
            ThemeScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.CROSSHAIR) {
            CrosshairScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.WAYPOINTS) {
            WaypointsScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.MACROS) {
            MacrosScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.FPS_BOOSTER) {
            FpsBoosterScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.RESOURCE_PACKS) {
            ResourcePacksScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CHAT) {
            ChatScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SERVER_PING) {
            ServerPingScreen(viewModel = mainViewModel, onBack = { navController.popBackStack() })
        }
    }
}
