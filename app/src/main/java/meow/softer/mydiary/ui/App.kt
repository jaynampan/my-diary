package meow.softer.mydiary.ui

import androidx.compose.runtime.Composable
import meow.softer.mydiary.ui.home.HomeViewModel
import meow.softer.mydiary.ui.navigation.DiaryNav
import meow.softer.mydiary.ui.theme.DiaryTheme

@Composable
fun App(
    homeViewModel: HomeViewModel,
    onTopicClick: () -> Unit,
    onSettingClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    DiaryTheme {
        DiaryNav(
            homeViewModel = homeViewModel,
            onTopicClick = { onTopicClick() },
            onSettingClick = { onSettingClick() },
            onProfileClick = { onProfileClick() }
        )
    }
}