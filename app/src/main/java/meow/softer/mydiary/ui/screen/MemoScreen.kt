package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
        onAddMemo = { memoViewModel.addMemo(it) },
        onUpdateMemo = { memo, content -> memoViewModel.updateMemoContent(memo, content) },
        onDeleteMemo = { memoViewModel.deleteMemo(it) },
        onMoveUp = { memoViewModel.moveUp(it) },
        onMoveDown = { memoViewModel.moveDown(it) }
    )
}

@Composable
fun MemoScreenContent(
    topicName: String,
    memoList: List<MemoEntry>,
    onToggleChecked: (MemoEntry) -> Unit,
    onAddMemo: (String) -> Unit,
    onUpdateMemo: (MemoEntry, String) -> Unit,
    onDeleteMemo: (MemoEntry) -> Unit,
    onMoveUp: (MemoEntry) -> Unit,
    onMoveDown: (MemoEntry) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var memoToEdit by remember { mutableStateOf<MemoEntry?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        MemoHeader(
            title = topicName,
            isEditMode = isEditMode,
            onEditToggle = { isEditMode = !isEditMode }
        )
        
        if (isEditMode) {
            Text(
                text = "Add",
                color = Color(0xFF81D4FA),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { showAddDialog = true },
                fontSize = 18.sp
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }

        MemoBody(
            memoList = memoList,
            isEditMode = isEditMode,
            onToggleChecked = onToggleChecked,
            onDeleteMemo = onDeleteMemo,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onEditClick = { memoToEdit = it }
        )
    }

    if (showAddDialog) {
        MemoEditDialog(
            initialText = "",
            onDismiss = { showAddDialog = false },
            onConfirm = {
                onAddMemo(it)
                showAddDialog = false
            }
        )
    }

    memoToEdit?.let { memo ->
        MemoEditDialog(
            initialText = memo.content,
            onDismiss = { memoToEdit = null },
            onConfirm = {
                onUpdateMemo(memo, it)
                memoToEdit = null
            }
        )
    }
}

@Composable
fun MemoHeader(
    title: String,
    isEditMode: Boolean,
    onEditToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF81D4FA))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )
        IconButton(
            onClick = onEditToggle,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = if (isEditMode) Icons.Default.EditOff else Icons.Default.Edit,
                contentDescription = "Toggle Edit Mode",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MemoBody(
    memoList: List<MemoEntry>,
    isEditMode: Boolean,
    onToggleChecked: (MemoEntry) -> Unit,
    onDeleteMemo: (MemoEntry) -> Unit,
    onMoveUp: (MemoEntry) -> Unit,
    onMoveDown: (MemoEntry) -> Unit,
    onEditClick: (MemoEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(memoList) { memo ->
            MemoItem(
                memoEntry = memo,
                isEditMode = isEditMode,
                onClick = {
                    if (isEditMode) onEditClick(memo)
                    else onToggleChecked(memo)
                },
                onDelete = { onDeleteMemo(memo) },
                onMoveUp = { onMoveUp(memo) },
                onMoveDown = { onMoveDown(memo) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun MemoItem(
    memoEntry: MemoEntry,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditMode) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Move Up",
                    modifier = Modifier.size(20.dp).clickable { onMoveUp() }
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Move Down",
                    modifier = Modifier.size(20.dp).clickable { onMoveDown() }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.Black, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        
        Text(
            text = memoEntry.content,
            fontSize = 18.sp,
            color = if (memoEntry.checked) Color.LightGray else Color(0xFF607D8B),
            textDecoration = if (memoEntry.checked) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )

        if (isEditMode) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun MemoEditDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF81D4FA),
                        unfocusedIndicatorColor = Color(0xFF81D4FA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                    OutlinedButton(
                        onClick = { if (text.isNotBlank()) onConfirm(text) },
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("OK", color = Color.Gray)
                    }
                }
            }
        }
    }
}
