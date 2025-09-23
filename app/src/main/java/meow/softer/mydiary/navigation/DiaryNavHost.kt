package meow.softer.mydiary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import meow.softer.mydiary.main.BottomSettingDialog
import meow.softer.mydiary.main.ProfileDialogWrapper
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.ui.home.AboutScreen
import meow.softer.mydiary.ui.home.BackupScreen
import meow.softer.mydiary.ui.home.HomeScreen
import meow.softer.mydiary.ui.home.HomeViewModel
import meow.softer.mydiary.ui.home.SecurityScreen
import meow.softer.mydiary.ui.home.SettingScreen


@Composable
fun DiaryNav(
    homeViewModel: HomeViewModel,
    onTopicClick: (ITopic) -> Unit,
    onProfileClick: () -> Unit

) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onProfileClick = { onProfileClick() },
                onSettingClick = {
                    navController.navigate(BottomSettingDialog.route)
                },
                onTopicClick = { onTopicClick(it) }
            )
        }
        composable(route = AboutScreen.route) {
            AboutScreen()
        }
        composable(route = SettingScreen.route) {
            SettingScreen()
        }
        composable(route = BackupScreen.route) {
            BackupScreen()
        }
        composable(route = SecurityScreen.route) {
            SecurityScreen()
        }
        dialog(route = ProfileDialog.route) {
            ProfileDialogWrapper(
                homeViewModel = homeViewModel,
                onClick = { it ->
                    when (it) {
                        "Dismiss" -> {

                        }

                        "Confirm" -> {

                        }

                        "Photo" -> {

                        }

                        "Reset" -> {

                        }
                    }
                }
            )
        }
        dialog(route = BottomSettingDialog.route) {
            BottomSettingDialog {
                navController.navigate(it)
            }
        }
    }
}