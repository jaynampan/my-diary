package meow.softer.mydiary.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import meow.softer.mydiary.ui.component.CommonDialog

/**
 * Dialog for adding a new contact
 */
@Composable
fun AddContactDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAddContact: (String, String) -> Unit,
    navController: NavHostController,
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    CommonDialog(
        modifier = modifier,
        onConfirm = {
            if (name.isEmpty()) {
                Toast.makeText(navController.context, "Contact name is empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (phoneNumber.isEmpty()) {
                Toast.makeText(navController.context, "Phone number is empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                onAddContact(name, phoneNumber)
                onDismiss()
            }
        },
        onCancel = {
            onDismiss()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add New Contact", color = Color.White, style = TextStyle(fontSize = 18.sp))
            
            Spacer(Modifier.height(16.dp))
            
            // Name input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Name", color = Color.White, modifier = Modifier.width(80.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Left,
                        color = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Phone number input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Phone", color = Color.White, modifier = Modifier.width(80.dp))
                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Left,
                        color = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
