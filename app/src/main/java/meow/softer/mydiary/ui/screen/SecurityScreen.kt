package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SecurityScreen() {
    SecurityScreenContent()
}

@Composable
fun SecurityScreenContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.statusBarsPadding()) {
        Text("Security to be implemented")
    }
}