package meow.softer.mydiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import meow.softer.mydiary.ui.dialog.NewTopicDialogWrapper
import meow.softer.mydiary.ui.dialog.BottomSettingSheet
import meow.softer.mydiary.ui.dialog.ColorPickerDialog
import meow.softer.mydiary.ui.dialog.ContactDetailDialog
import meow.softer.mydiary.ui.dialog.ProfileDialogWrapper
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.ui.screen.AboutScreen
import meow.softer.mydiary.ui.screen.BackupScreen
import meow.softer.mydiary.ui.screen.ContactScreen
import meow.softer.mydiary.ui.screen.DiaryScreen
import meow.softer.mydiary.ui.screen.HomeScreen
import meow.softer.mydiary.ui.screen.HomeViewModel
import meow.softer.mydiary.ui.screen.MemoScreen
import meow.softer.mydiary.ui.screen.MemoViewModel
import meow.softer.mydiary.ui.screen.SecurityScreen
import meow.softer.mydiary.ui.screen.SecurityViewModel
import meow.softer.mydiary.ui.screen.SettingScreen


@Composable
fun DiaryNav() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val memoViewModel: MemoViewModel = hiltViewModel()
    val securityViewModel: SecurityViewModel = hiltViewModel()
    val settings by securityViewModel.settings.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HomeScreen.route
    ) {
        composable(route = HomeScreen.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                navController = navController,
                onProfileClick = { navController.navigate(ProfileDialog.route) },
                onSettingClick = {
                    navController.navigate(BottomSettingDialog.route)
                },
                onTopicClick = { topic ->
                    when (topic.type) {
                        ITopic.TYPE_MEMO -> {
                            navController.navigate("${MemoScreen.route}/${topic.id}/${topic.title}")
                        }

                        ITopic.TYPE_DIARY -> {
                            navController.navigate("${DiaryScreen.route}/${topic.id}/${topic.title}")
                        }

                        ITopic.TYPE_CONTACTS -> {
                            navController.navigate(ContactScreen.route)
                        }
                    }
                }
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
            SecurityScreen(securityViewModel)
        }
        composable(
            route = MemoScreen.routeWithArgs,
            arguments = listOf(
                navArgument(MemoScreen.topicIdArg) { type = NavType.IntType },
                navArgument(MemoScreen.topicNameArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt(MemoScreen.topicIdArg) ?: -1
            val topicName = backStackEntry.arguments?.getString(MemoScreen.topicNameArg) ?: ""

            MemoScreen(
                memoViewModel = memoViewModel,
                topicId = topicId,
                topicName = topicName
            )
        }
        composable(
            route = DiaryScreen.routeWithArgs,
            arguments = listOf(
                navArgument(DiaryScreen.topicIdArg) { type = NavType.IntType },
                navArgument(DiaryScreen.topicNameArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt(DiaryScreen.topicIdArg) ?: -1
            val topicName = backStackEntry.arguments?.getString(DiaryScreen.topicNameArg) ?: ""

            DiaryScreen(
                topicId = topicId,
                topicTitle = topicName
            )
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
                onClick = {
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
                hasPassword = settings.isSecurityEnabled,
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
        dialog(route = NewTopicDialog.route) {
            NewTopicDialogWrapper(
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
    }
}