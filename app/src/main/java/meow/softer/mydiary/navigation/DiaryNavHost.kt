package meow.softer.mydiary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import meow.softer.mydiary.ui.dialog.BottomSettingSheet
import meow.softer.mydiary.ui.dialog.ProfileDialogWrapper
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.ui.dialog.ContactDetailDialog
import meow.softer.mydiary.ui.screen.AboutScreen
import meow.softer.mydiary.ui.screen.BackupScreen
import meow.softer.mydiary.ui.screen.ContactScreen
import meow.softer.mydiary.ui.screen.DiaryScreen
import meow.softer.mydiary.ui.screen.HomeScreen
import meow.softer.mydiary.ui.screen.HomeViewModel
import meow.softer.mydiary.ui.screen.MemoScreen
import meow.softer.mydiary.ui.screen.SecurityScreen
import meow.softer.mydiary.ui.screen.SettingScreen


@Composable
fun DiaryNav(
    homeViewModel: HomeViewModel,
    onTopicClick: (ITopic) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onProfileClick = { navController.navigate(ProfileDialog.route) },
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
        composable(route = MemoScreen.route) {
            MemoScreen()
        }
        composable(route = DiaryScreen.route) {
            DiaryScreen()
        }
        composable(route = ContactScreen.route) {
            //todo:update
            ContactScreen(
                headerName = "",
                data = listOf(),
                onAddContact = {},
                onClickContact = {

                }

            ) {}
        }
        dialog(route = ProfileDialog.route) {
            ProfileDialogWrapper(
                homeViewModel = homeViewModel,
                onClick = { it ->
                    when (it) {
                        "Dismiss" -> {
                            navController.popBackStack()
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
            BottomSettingSheet(
                onDismiss = {
                    navController.popBackStack()
                }
            ) {
                navController.navigate(it)
            }
        }
        dialog(route = ContactDetailDialog.route) {
            ContactDetailDialog()
        }
    }
}