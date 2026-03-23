package meow.softer.mydiary.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import meow.softer.mydiary.ui.component.CommonDialog
import meow.softer.mydiary.ui.models.ITopic

@Composable
fun EditTopicDialog(
    topic: ITopic,
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit,
    onColorPickRequest: () -> Unit,
    selectedColor: Color? = null
) {
    var title by remember { mutableStateOf(topic.title) }
    val currentColor = selectedColor ?: Color(topic.color)

    Dialog(onDismissRequest = onDismiss) {
        CommonDialog(
            onConfirm = { onConfirm(title, currentColor) },
            onCancel = onDismiss
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Edit Topic", color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Title", color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.White,
                            focusedIndicatorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            color = Color.White
                        )
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Text Color", color = Color.White)
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp)
                            .background(currentColor)
                            .clickable { onColorPickRequest() }
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    topicTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CommonDialog(
            onConfirm = onConfirm,
            onCancel = onDismiss
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Do you want to delete\n$topicTitle?",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }
        }
    }
}
