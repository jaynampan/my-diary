package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meow.softer.mydiary.R
import meow.softer.mydiary.main.topic.Diary
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.ui.component.HomeBottomBar
import meow.softer.mydiary.ui.component.HomeHeader
import meow.softer.mydiary.ui.component.TopicList
import meow.softer.mydiary.ui.theme.DiaryTheme
import meow.softer.mydiary.util.debug

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onTopicClick: (ITopic) -> Unit = {},
) {
    val userName = homeViewModel.userName.collectAsStateWithLifecycle().value
    val profilePic = homeViewModel.userPainter.collectAsStateWithLifecycle().value
        ?: painterResource(R.drawable.ic_person_picture_default)
    val headerBg = homeViewModel.headerBgPainter.collectAsStateWithLifecycle().value
    debug("HomeScreen", "headerBg: $headerBg")
    val bgPainter = headerBg
        ?: painterResource(R.drawable.profile_theme_bg_taki)
    debug("HomeScreen", "bgPainter: $bgPainter, ")
    val topicListData = homeViewModel.topicData.collectAsStateWithLifecycle().value

    HomeScreenContent(
        profilePic = profilePic,
        bgPainter = bgPainter,
        userName = userName,
        topics = topicListData,
        onProfileClick = {
            onProfileClick()
        },
        onSettingClick = {
            onSettingClick()
        },
        onTopicClick = {
            onTopicClick(it)
        }
    )

}

@Composable
fun HomeScreenContent(
    profilePic: Painter,
    bgPainter: Painter,
    userName: String,
    topics: List<ITopic>,
    onProfileClick: () -> Unit,
    onSettingClick: () -> Unit,
    onTopicClick: (ITopic) -> Unit
) {
    Column(modifier = Modifier.statusBarsPadding()) {
        HomeHeader(
            profilePic = profilePic,
            bgPainter = bgPainter,
            userName = userName,
            onClick = onProfileClick
        )
        TopicList(
            modifier = Modifier.weight(1f),
            topicList = topics,
            onClick = { onTopicClick(it) },
        )
        HomeBottomBar(
            onSettingClick = { onSettingClick() }
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,

    )
@Composable
private fun HomeScreenContentPreview() {
    DiaryTheme {
        HomeScreenContent(
            profilePic = painterResource(R.drawable.ic_person_picture_default),
            bgPainter = painterResource(R.drawable.profile_theme_bg_taki),
            userName = "Hello User",
            topics = listOf(
                Diary(
                    id = 1,
                    title = "Sample Diary",
                    color = Color.Red.toArgb()
                ).apply { count = 8L }
            ),
            onProfileClick = {},
            onSettingClick = {},
            onTopicClick = {}
        )
    }
}