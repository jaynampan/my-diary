package meow.softer.mydiary.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
    // Use key (id) for stable tracking during reordering to avoid "shaking" caused by index shifts
    var draggedItemKey by remember { mutableStateOf<Any?>(null) }
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
                                draggedItemKey = it.key
                                draggingOffset = 0f
                            }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        draggingOffset += dragAmount.y

                        draggedItemKey?.let { key ->
                            val layoutInfo = listState.layoutInfo
                            val currentItemInfo = layoutInfo.visibleItemsInfo
                                .find { it.key == key } ?: return@let
                            val currentIndex = currentItemInfo.index
                            
                            // Current center point of the dragged item in the list's viewport
                            val draggedItemCenter = currentItemInfo.offset + currentItemInfo.size / 2 + draggingOffset
                            
                            // Detect which item to swap with, using a small hysteresis (buffer) for stability
                            val targetItem = layoutInfo.visibleItemsInfo
                                .find { item ->
                                    val itemCenter = item.offset + item.size / 2
                                    val hysteresis = item.size / 8f
                                    when {
                                        item.index > currentIndex -> draggedItemCenter > itemCenter + hysteresis
                                        item.index < currentIndex -> draggedItemCenter < itemCenter - hysteresis
                                        else -> false
                                    }
                                }

                            if (targetItem != null) {
                                val targetIndex = targetItem.index
                                val distance = targetItem.offset - currentItemInfo.offset
                                
                                currentOnMove(currentIndex, targetIndex)
                                // Adjust offset so the item stays visually under the user's finger after the list updates
                                draggingOffset -= distance
                            }
                        }
                    },
                    onDragEnd = {
                        draggedItemKey = null
                        draggingOffset = 0f
                        currentOnDragEnd()
                    },
                    onDragCancel = {
                        draggedItemKey = null
                        draggingOffset = 0f
                        currentOnDragEnd()
                    }
                )
            },
        state = listState
    ) {
        itemsIndexed(topicList, key = { _, item -> item.id }) { _, it ->
            val isDragging = it.id == draggedItemKey
            
            // Smoothly animate elevation and scale changes for a "natural" pickup feel
            val elevation by animateDpAsState(
                targetValue = if (isDragging) 8.dp else 0.dp,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "elevation"
            )
            val scale by animateFloatAsState(
                targetValue = if (isDragging) 1.04f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "scale"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    // zIndex ensures the dragged item stays on top of others and their shadows
                    .zIndex(if (isDragging) 10f else 1f)
                    .graphicsLayer {
                        translationY = if (isDragging) draggingOffset else 0f
                        scaleX = scale
                        scaleY = scale
                    }
                    .animateItem(
                        // Crucial: disable placement animation for the dragged item to prevent it from 
                        // fighting with manual translationY. Use a smooth spring for others.
                        placementSpec = if (isDragging) null else spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                tonalElevation = elevation,
                shadowElevation = elevation,
                color = Color.Transparent
            ) {
                TopicItem(
                    topic = it,
                    onClick = { if (draggedItemKey == null) onClick(it) }
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
