package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import meow.softer.mydiary.data.local.db.entity.MemoEntry

@Composable
fun MemoScreen(
    memoViewModel: MemoViewModel,
    topicId: Int,
    topicName: String
) {
    val memoList by memoViewModel.memoData.collectAsState()

    LaunchedEffect(topicId) {
        memoViewModel.loadMemos(topicId)
    }

    MemoScreenContent(
        topicName = topicName,
        memoList = memoList,
        onToggleChecked = { memoViewModel.toggleChecked(it) },
        onAddMemo = { memoViewModel.addMemo(it) }
    )
}

@Composable
fun MemoScreenContent(
    topicName: String,
    memoList: List<MemoEntry>,
    onToggleChecked: (MemoEntry) -> Unit,
    onAddMemo: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        MemoHeader(title = topicName, onAddClick = { showAddDialog = true })
        MemoBody(memoList = memoList, onToggleChecked = onToggleChecked)
    }

    if (showAddDialog) {
        AddMemoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = {
                onAddMemo(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MemoHeader(title: String, onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF81D4FA)) // Light blue from design
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        IconButton(
            onClick = onAddClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Add Memo",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MemoBody(memoList: List<MemoEntry>, onToggleChecked: (MemoEntry) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(memoList) { memo ->
            MemoItem(memoEntry = memo, onClick = { onToggleChecked(memo) })
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun MemoItem(memoEntry: MemoEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bullet point
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color.Black, shape = CircleShape)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = memoEntry.content,
            fontSize = 16.sp,
            color = if (memoEntry.checked) Color.LightGray else Color(0xFF607D8B),
            textDecoration = if (memoEntry.checked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

@Composable
fun AddMemoDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Memo") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Enter memo content") }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onConfirm(text) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
