package meow.softer.mydiary.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import meow.softer.mydiary.data.local.db.entity.DiaryEntry
import meow.softer.mydiary.data.local.db.entity.DiaryItem
import meow.softer.mydiary.ui.models.DiaryInfoHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DiaryScreen(
    topicId: Int,
    topicTitle: String,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(topicId) {
        viewModel.initTopic(topicId, topicTitle)
    }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addImageItem(uri)
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            DiaryTopBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) },
                topicTitle = uiState.topicTitle
            )
        },
        bottomBar = {
            DiaryBottomBar(
                selectedTab = uiState.selectedTab,
                onAddClick = { viewModel.createNewDiary() },
                onPhotoClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onSaveClick = { viewModel.saveDiary() },
                onDiscardClick = { viewModel.discardDiary() },
                entryCount = uiState.diaries.size
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background color for Entries and Calendar (light blue gradient-like)
            if (uiState.selectedTab != 2) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE3F2FD))
                )
            }

            when (uiState.selectedTab) {
                0 -> EntriesPage(uiState.diaries) { viewModel.editDiary(it) }
                1 -> CalendarPage(uiState.diaries)
                2 -> DiaryPage(
                    diary = uiState.currentDiary,
                    items = uiState.currentDiaryItems,
                    onTitleChange = { viewModel.updateDiaryTitle(it) },
                    onItemChange = { index, content -> viewModel.updateDiaryItem(index, content) }
                )
            }
        }
    }
}

@Composable
fun DiaryTopBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    topicTitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TabItem(
                text = "Entries",
                isSelected = selectedTab == 0,
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
            ) { onTabSelected(0) }
            TabItem(
                text = "Calendar",
                isSelected = selectedTab == 1,
                shape = RectangleShape
            ) { onTabSelected(1) }
            TabItem(
                text = "Diary",
                isSelected = selectedTab == 2,
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
            ) { onTabSelected(2) }
        }
        Text(
            text = topicTitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = Color(0xFF607D8B)
        )
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
    }
}

@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    shape: Shape = RectangleShape,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF5C9EB2) else Color.White
    val textColor = if (isSelected) Color.White else Color(0xFF5C9EB2)

    Box(
        modifier = modifier
            .width(100.dp)
            .height(32.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, Color(0xFF5C9EB2), shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 14.sp)
    }
}

@Composable
fun EntriesPage(diaries: List<DiaryEntry>, onDiaryClick: (DiaryEntry) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(diaries) { _, diary ->
            DiaryEntryCard(diary, onClick = { onDiaryClick(diary) })
        }
    }
}

@Composable
fun DiaryEntryCard(diary: DiaryEntry, onClick: () -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = diary.time.toLong() * 1000 }
    val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = day, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = dayOfWeek, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = time, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painterResource(
                            id = DiaryInfoHelper.getWeatherResourceId(
                                diary.weather ?: 0
                            )
                        ), null, modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        painterResource(id = DiaryInfoHelper.getMoodResourceId(diary.mood ?: 0)),
                        null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(text = diary.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CalendarPage(diaries: List<DiaryEntry>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White.copy(alpha = 0.8f))
    ) {
        val calendar = Calendar.getInstance()
        val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) { Icon(Icons.AutoMirrored.Filled.ArrowLeft, null) }
            Text(text = monthName, fontWeight = FontWeight.Bold)
            IconButton(onClick = { }) { Icon(Icons.AutoMirrored.Filled.ArrowRight, null) }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Calendar Grid placeholder", color = Color.Gray)
        }
    }
}

@Composable
fun DiaryPage(
    diary: DiaryEntry?,
    items: List<DiaryItem>,
    onTitleChange: (String) -> Unit,
    onItemChange: (Int, String) -> Unit
) {
    if (diary == null) return

    val calendar = Calendar.getInstance().apply { timeInMillis = diary.time.toLong() * 1000 }
    val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF64B5F6))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = month, color = Color.White, fontSize = 14.sp)
            Text(text = day, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            Text(text = "$dayOfWeek $timeStr", color = Color.White, fontSize = 14.sp)
            Text(
                text = "📍 ${diary.location ?: "No Location"}",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = diary.title,
                onValueChange = onTitleChange,
                placeholder = { Text("Diary title") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            IconButton(onClick = { }) {
                Icon(
                    painterResource(id = DiaryInfoHelper.getWeatherResourceId(diary.weather ?: 0)),
                    null
                )
            }
            IconButton(onClick = { }) {
                Icon(painterResource(id = DiaryInfoHelper.getMoodResourceId(diary.mood ?: 0)), null)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(items) { index, item ->
                if (item.type == 0) {
                    TextField(
                        value = item.content,
                        onValueChange = { onItemChange(index, it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { if (index == 0) Text("Write your diary here") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                } else if (item.type == 1) {
                    AsyncImage(
                        model = item.content,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }
}

@Composable
fun DiaryBottomBar(
    selectedTab: Int,
    onAddClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    entryCount: Int
) {
    BottomAppBar(
        containerColor = Color(0xFF64B5F6),
        contentColor = Color.White,
        modifier = Modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { }) { Icon(Icons.Default.Menu, null) }

            if (selectedTab != 2) {
                IconButton(onClick = onAddClick) { Icon(Icons.Default.Edit, null) }
                IconButton(onClick = onPhotoClick) { Icon(Icons.Default.Image, null) }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$entryCount Entries", modifier = Modifier.padding(end = 16.dp))
            } else {
                IconButton(onClick = { }) { Icon(Icons.Default.MoreHoriz, null) }
                IconButton(onClick = { }) { Icon(Icons.Default.KeyboardHide, null) }
                IconButton(onClick = onPhotoClick) { Icon(Icons.Default.CameraAlt, null) }
                IconButton(onClick = onDiscardClick) { Icon(Icons.Default.Close, null) }
                IconButton(onClick = onSaveClick) { Icon(Icons.Default.Save, null) }
            }
        }
    }
}
