package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import meow.softer.mydiary.R
import meow.softer.mydiary.navigation.ColorPickerDialog
import meow.softer.mydiary.ui.component.HomeBottomBar
import meow.softer.mydiary.ui.component.HomeHeader
import meow.softer.mydiary.ui.component.TopicList
import meow.softer.mydiary.ui.dialog.DeleteConfirmDialog
import meow.softer.mydiary.ui.dialog.EditTopicDialog
import meow.softer.mydiary.ui.models.Diary
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.ui.theme.DiaryTheme
import meow.softer.mydiary.util.debug

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
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
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()

    var editingTopic by remember { mutableStateOf<ITopic?>(null) }
    var deletingTopic by remember { mutableStateOf<ITopic?>(null) }

    // Color picker handling
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val returnedColorInt = navBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Int>("color_key")
        ?.observeAsState()

    HomeScreenContent(
        profilePic = profilePic,
        bgPainter = bgPainter,
        userName = userName,
        topics = topicListData,
        searchQuery = searchQuery,
        onSearchQueryChange = { homeViewModel.onSearchQueryChange(it) },
        onProfileClick = {
            onProfileClick()
        },
        onSettingClick = {
            onSettingClick()
        },
        onTopicClick = {
            onTopicClick(it)
        },
        onEditClick = {
            editingTopic = it
        },
        onDeleteClick = {
            deletingTopic = it
        },
        onMove = { from, to ->
            homeViewModel.moveTopic(from, to)
        },
        onDragEnd = {
            homeViewModel.saveTopicOrder()
        }
    )

    editingTopic?.let { topic ->
        EditTopicDialog(
            topic = topic,
            onDismiss = { editingTopic = null },
            onConfirm = { newTitle, newColor ->
                topic.title = newTitle
                topic.color = newColor.toArgb()
                homeViewModel.updateTopic(topic)
                editingTopic = null
                // Clear the saved color so it doesn't affect subsequent edits
                navBackStackEntry?.savedStateHandle?.remove<Int>("color_key")
            },
            onColorPickRequest = {
                navController.navigate(ColorPickerDialog.route)
            },
            selectedColor = returnedColorInt?.value?.let { Color(it) }
        )
    }

    deletingTopic?.let { topic ->
        DeleteConfirmDialog(
            topicTitle = topic.title,
            onDismiss = { deletingTopic = null },
            onConfirm = {
                homeViewModel.deleteTopic(topic)
                deletingTopic = null
            }
        )
    }

}

@Composable
fun HomeScreenContent(
    profilePic: Painter,
    bgPainter: Painter,
    userName: String,
    topics: List<ITopic>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSettingClick: () -> Unit,
    onTopicClick: (ITopic) -> Unit,
    onEditClick: (ITopic) -> Unit = {},
    onDeleteClick: (ITopic) -> Unit = {},
    onMove: (Int, Int) -> Unit = { _, _ -> },
    onDragEnd: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .fillMaxSize()
    ) {
        if (searchQuery.isEmpty()) {
            HomeHeader(
                profilePic = profilePic,
                bgPainter = bgPainter,
                userName = userName,
                onClick = onProfileClick
            )
        }
        TopicList(
            modifier = Modifier.weight(1f),
            topicList = topics,
            searchQuery = searchQuery,
            onClick = { onTopicClick(it) },
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            onMove = onMove,
            onDragEnd = onDragEnd
        )
        HomeBottomBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
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
                ).apply { count = 8 }
            ),
            searchQuery = "",
            onSearchQueryChange = {},
            onProfileClick = {},
            onSettingClick = {},
            onTopicClick = {}
        )
    }
}
