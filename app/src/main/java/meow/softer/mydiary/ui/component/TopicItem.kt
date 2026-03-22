package meow.softer.mydiary.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.util.debug

@Composable
fun TopicList(
    modifier: Modifier = Modifier,
    topicList: List<ITopic>,
    onClick: (ITopic) -> Unit,
    onMove: (Int, Int) -> Unit = { _, _ -> },
    onDragEnd: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffset by remember { mutableFloatStateOf(0f) }

    val currentOnMove by rememberUpdatedState(onMove)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        listState.layoutInfo.visibleItemsInfo
                            .firstOrNull { item ->
                                offset.y.toInt() in item.offset..(item.offset + item.size)
                            }
                            ?.also {
                                draggedItemIndex = it.index
                            }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        draggingOffset += dragAmount.y

                        draggedItemIndex?.let { currentIndex ->
                            val layoutInfo = listState.layoutInfo
                            val currentItemInfo = layoutInfo.visibleItemsInfo
                                .find { it.index == currentIndex } ?: return@let
                            
                            val draggedItemCenter = currentItemInfo.offset + currentItemInfo.size / 2 + draggingOffset
                            
                            val targetItem = layoutInfo.visibleItemsInfo
                                .find { item ->
                                    draggedItemCenter.toInt() in item.offset..(item.offset + item.size) &&
                                            item.index != currentIndex
                                }

                            if (targetItem != null) {
                                val targetIndex = targetItem.index
                                val distance = targetItem.offset - currentItemInfo.offset
                                
                                currentOnMove(currentIndex, targetIndex)
                                draggedItemIndex = targetIndex
                                draggingOffset -= distance
                            }
                        }
                    },
                    onDragEnd = {
                        draggedItemIndex = null
                        draggingOffset = 0f
                        currentOnDragEnd()
                    },
                    onDragCancel = {
                        draggedItemIndex = null
                        draggingOffset = 0f
                        currentOnDragEnd()
                    }
                )
            },
        state = listState
    ) {
        itemsIndexed(topicList, key = { _, item -> item.id }) { index, it ->
            val isDragging = index == draggedItemIndex
            val zIndex = if (isDragging) 1f else 0f
            val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elevation")

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(zIndex)
                    .graphicsLayer {
                        translationY = if (isDragging) draggingOffset else 0f
                    }
                    .animateItem(),
                tonalElevation = elevation,
                shadowElevation = elevation
            ) {
                TopicItem(
                    topic = it,
                    onClick = { if (draggedItemIndex == null) onClick(it) }
                )
            }
        }
    }
}

@Composable
fun TopicItem(
    modifier: Modifier = Modifier,
    topic: ITopic,
    onClick: (ITopic) -> Unit
) {
    debug("TopicItem color","title: ${topic.title} color: ${topic.color}")
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
                contentDescription = null,
                tint = Color(topic.color)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = topic.title,
                color = Color(topic.color)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = topic.count.toString(),
                color = Color(topic.color)
            )
            Icon(
                painter = painterResource(R.drawable.ic_keyboard_arrow_right_black_24dp),
                contentDescription = null,
                tint = Color(topic.color)
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