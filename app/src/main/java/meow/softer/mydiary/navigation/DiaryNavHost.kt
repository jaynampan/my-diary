package meow.softer.mydiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import meow.softer.mydiary.ui.dialog.AddTopicDialog
import meow.softer.mydiary.ui.dialog.AddTopicDialogWrapper
import meow.softer.mydiary.ui.dialog.BottomSettingSheet
import meow.softer.mydiary.ui.dialog.ColorPickerDialog
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
                            navController.popBackStack()
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
        dialog(route = ColorPickerDialog.route) {
            ColorPickerDialog(
                onConfirm = { colorArray ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            "color_key",
                            Color.hsv(
                                hue = colorArray[0],
                                saturation = colorArray[1],
                                value = colorArray[2]
                            ).toArgb()
                        )
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() },
            )
        }
        dialog(route = AddTopicDialog.route) {
            AddTopicDialogWrapper(
                homeViewModel = homeViewModel,
                navController = navController
            )
        }
    }
}

/**
 * Navigates to a route with singleTop behavior.
 * Also pops up to the start destination to prevent backstack buildup.
 */
fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) {
        // Avoid multiple copies of the same destination when re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
        // Optional: Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
//        popUpTo(this@navigateSingleTop.graph.findStartDestination().id) {
//            saveState = true
//        }
    }
}