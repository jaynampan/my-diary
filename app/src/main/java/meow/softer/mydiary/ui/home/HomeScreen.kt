package meow.softer.mydiary.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.ui.components.HomeBottomBar
import meow.softer.mydiary.ui.components.HomeHeader
import meow.softer.mydiary.ui.components.TopicList

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