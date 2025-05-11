package meow.softer.mydiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import meow.softer.mydiary.main.ProfileDialog
import meow.softer.mydiary.main.ProfileDialogWrapper
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.ui.home.AboutScreen
import meow.softer.mydiary.ui.home.HomeWrapper
import meow.softer.mydiary.ui.home.MainViewModel


@Composable
fun DiaryNav(
    mainViewModel: MainViewModel,
    onTopicClick: (ITopic) -> Unit,
    onSettingClick: () -> Unit,
    onProfileClick: () -> Unit

) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable(route = "Home") {
            HomeWrapper(
                mainViewModel = mainViewModel,
                onProfileClick = { onProfileClick() },
                onSettingClick = {
                    onSettingClick()
                },
                onTopicClick = { onTopicClick(it) }
            )
        }
        composable(route = "About") {
            AboutScreen()
        }
        dialog(route = "ProfileDialog") {
            ProfileDialogWrapper(
                mainViewModel = mainViewModel,
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
    }
}