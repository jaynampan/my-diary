package meow.softer.mydiary.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meow.softer.mydiary.R
import meow.softer.mydiary.main.DiaryDialogFragment
import meow.softer.mydiary.main.MainSettingDialogFragment
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.ui.components.HomeBottomBar
import meow.softer.mydiary.ui.components.HomeHeader
import meow.softer.mydiary.ui.components.TopicList
import meow.softer.mydiary.ui.theme.DiaryTheme

@Composable
fun HomeWrapper(
    mainViewModel: MainViewModel,
    onProfileClick: () -> Unit,
    onSettingClick: () -> Unit,
    onTopicClick: (ITopic) -> Unit,
) {
    val userName = mainViewModel.userName.collectAsStateWithLifecycle().value
    val profilePic = mainViewModel.userPainter.collectAsStateWithLifecycle().value
        ?: painterResource(R.drawable.ic_person_picture_default)
    val bgPainter = mainViewModel.headerBgPainter.collectAsStateWithLifecycle().value
        ?: painterResource(R.drawable.profile_theme_bg_taki)
    val topicListData = mainViewModel.topicData.collectAsStateWithLifecycle().value
    DiaryTheme {
        HomeScreen(
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
}

@Composable
fun HomeScreen(
    profilePic: Painter,
    bgPainter: Painter,
    userName: String,
    topics: List<ITopic>,
    onProfileClick: () -> Unit,
    onSettingClick: () -> Unit,
    onTopicClick: (ITopic) -> Unit
) {
    Column {
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