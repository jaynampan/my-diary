package meow.softer.mydiary.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import meow.softer.mydiary.navigation.ColorPickerDialog
import meow.softer.mydiary.navigation.HomeScreen
import meow.softer.mydiary.navigation.navigateSingleTop
import meow.softer.mydiary.ui.component.CommonDialog
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.ui.screen.HomeViewModel

@Composable
fun NewTopicDialogWrapper(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
) {
    NewTopicDialog(
        onDismiss = { navController.navigateSingleTop(HomeScreen.route) },
        addTopic = { name, type, color ->
            homeViewModel.addITopic(name, type, color)
            navController.navigateSingleTop(HomeScreen.route)
        },
        navController = navController,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTopicDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    addTopic: (String, Int, Color) -> Unit,
    navController: NavHostController,
) {
    var topicName by remember { mutableStateOf("") }
    //Text color
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Observe the result
    val returnedColorInt = navBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Int>("color_key")
        ?.observeAsState()

    // Use the color
    val topicColor = returnedColorInt?.value?.let { Color(it) } ?: Color.Gray
    // --- Dropdown for Topic Type ---
    val topicTypes = remember {
        listOf(
            "Contacts" to ITopic.TYPE_CONTACTS,
            "Diary" to ITopic.TYPE_DIARY,
            "Memo" to ITopic.TYPE_MEMO
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(topicTypes[1]) } // Default to Diary
    CommonDialog(
        modifier = modifier,
        onConfirm = {
            addTopic(topicName, selectedType.second, topicColor)
        },
        onCancel = {
            onDismiss()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Topic Name", color = Color.White)
            TextField(
                value = topicName,
                onValueChange = { topicName = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White
                ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
        }
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Text color", color = Color.White)
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(40.dp)
                    .background(topicColor)
                    .clickable {
                        navController.navigate(ColorPickerDialog.route)
                    }
            )
        }
        Spacer(Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedType.first,
                onValueChange = {},
                readOnly = true,
                label = { Text("Topic Type", color = Color.White) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable,true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                topicTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.first) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}