package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.theme.primaryLight

@Composable
fun LockScreen(
    onAuthenticate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_enhanced_encryption_white_36dp),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(primaryLight)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "App Locked",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your diary is protected. Please authenticate to continue.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onAuthenticate,
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            modifier = Modifier.height(50.dp)
        ) {
            Text("Unlock App", color = Color.White)
        }
    }
}