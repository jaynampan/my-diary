package meow.softer.mydiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.R
import meow.softer.mydiary.main.topic.ITopic

@Composable
fun TopicList(
    modifier: Modifier = Modifier,
    topicList: List<ITopic>,
    onClick: (ITopic) -> Unit
) {
    LazyColumn(modifier.fillMaxWidth()) {
        items(topicList) { it ->
            TopicItem(topic = it) {  onClick(it)}
        }
    }
}

@Composable
fun TopicItem(
    modifier: Modifier = Modifier,
    topic: ITopic,
    onClick: (ITopic) -> Unit
) {
    Column(
        Modifier
            .padding(start= 30.dp)
            .clickable{onClick(topic)}
    ) {
        Row(
            modifier = modifier
                .height(60.dp)
                , verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(topic.icon),
                contentDescription = null
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = topic.title!!
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = topic.count.toString()
            )
            Icon(
                painter = painterResource(R.drawable.ic_keyboard_arrow_right_black_24dp),
                contentDescription = null
            )
            Spacer(Modifier.width(10.dp))

        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFCACACA))
        )
    }

}