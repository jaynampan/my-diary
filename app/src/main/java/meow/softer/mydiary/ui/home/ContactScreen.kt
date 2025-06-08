package meow.softer.mydiary.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.components.DiarySearchBar

@Composable
fun ContactScreen(
    modifier: Modifier = Modifier, headerName: String, data: List<ContactGroup>,
    onAddContact: () -> Unit,
    onClickContact: (ContactInfo) -> Unit,
    onLongPressContact: (ContactInfo) -> Unit,
) {
    Column(modifier) {
        ContactHeader(
            Modifier.padding(top = 8.dp),
            headerName = headerName
        ) {
            onAddContact()
        }
        Row(Modifier.fillMaxSize()) {
            ContactIndex(Modifier.fillMaxHeight()) { }
            ContactList(
                Modifier
                    .fillMaxSize(),
                groups = data,
                onClickContact = { onClickContact(it) },
                onLongPressContact = { onLongPressContact(it) },
            )
        }
    }
}

@Composable
fun ContactHeader(
    modifier: Modifier = Modifier,
    headerName: String,
    onAddContact: () -> Unit
) {
    Column(
        modifier = modifier.padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                headerName, fontSize = 20.sp, overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_add_white_24dp),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable { onAddContact() },
                colorFilter = ColorFilter.tint(Color.Blue) //todo
            )
        }
        Spacer(Modifier.height(4.dp))
        DiarySearchBar(
            Modifier
                .height(40.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ContactIndex(
    modifier: Modifier = Modifier, onClick: (Char) -> Unit
) {

    val data = listOf('#') + ('A'..'Z').toList()

    Column(
        modifier = modifier.padding(top = 4.dp, bottom = 36.dp, start = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        data.forEach { elem ->
            Text(
                elem.toString(), fontSize = 16.sp, modifier = Modifier.clickable {
                    onClick(elem)
                })
        }
    }
}

@Composable
fun ContactCard(
    modifier: Modifier = Modifier,
    profile: Painter? = null,
    name: String,
    number: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ), shadowElevation = 4.dp
    ) {
        Row(
            Modifier
                .padding(4.dp)
                .padding(start = 8.dp)
                .height(80.dp)
                .width(260.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = profile ?: painterResource(R.drawable.ic_person_picture_default),
                contentDescription = null,
                Modifier.size(45.dp)
            )
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name, fontSize = 33.sp, maxLines = 1, overflow = TextOverflow.Ellipsis

                )
                Spacer(Modifier.width(10.dp))

                Text(
                    text = number,
                    color = colorResource(R.color.contacts_photo_tint),
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )
            }
            Spacer(Modifier.weight(2f))

        }
    }
}

@Composable
fun ContactList(
    modifier: Modifier = Modifier, groups: List<ContactGroup>,
    onClickContact: (ContactInfo) -> Unit,
    onLongPressContact: (ContactInfo) -> Unit
) {
    LazyColumn(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(groups) { elem ->
            ContactGroup(
                title = elem.title, items = elem.data,
                onClickContact = {
                    onClickContact(it)
                },
                onLongPressContact = {
                    onLongPressContact(it)
                })

        }
    }
}

@Composable
fun ContactGroup(
    modifier: Modifier = Modifier, title: String, items: List<ContactInfo>,
    onClickContact: (ContactInfo) -> Unit,
    onLongPressContact: (ContactInfo) -> Unit
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title, fontSize = 30.sp
        )

        items.forEach { elem ->
            ContactCard(
                name = elem.name, number = elem.number, profile = elem.profile,
                onClick = { onClickContact(elem) },
                onLongClick = { onLongPressContact(elem) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun ContactHeaderPreview() {
    ContactHeader(
        headerName = "Emergency Contacts"
    ) {}
}

@Preview(showBackground = true)
@Composable
private fun IndexPreview() {
    ContactIndex(Modifier.width(50.dp)) {
        Log.d("Mytest", it.toString())
    }
}

@Preview
@Composable
private fun ContactScreenPreview() {
    ContactScreen(
        headerName = "Emergency Contacts",
        data = listOf(
            ContactGroup(
                title = "L", listOf(
                    ContactInfo(name = "Lucy", number = "18564"),
                    ContactInfo(name = "Linda", number = "123456659"),
                    ContactInfo(name = "Lily", number = "98415645"),
                )
            ), ContactGroup(
                title = "M", listOf(
                    ContactInfo(name = "Mike", number = "18564"),
                    ContactInfo(name = "Mily", number = "9841287456"),
                    ContactInfo(name = "Mimily", number = "9841565"),
                    ContactInfo(name = "Mike", number = "18564"),
                    ContactInfo(name = "Mily", number = "9841287456"),
                    ContactInfo(name = "Mimily", number = "9841565"),
                )
            ),
            ContactGroup(
                title = "L", listOf(
                    ContactInfo(name = "Lucy", number = "18564"),
                    ContactInfo(name = "Linda", number = "123456659"),
                    ContactInfo(name = "Lily", number = "98415645"),
                )
            ),
            ContactGroup(
                title = "L", listOf(
                    ContactInfo(name = "Lucy", number = "18564"),
                    ContactInfo(name = "Linda", number = "123456659"),
                    ContactInfo(name = "Lily", number = "98415645"),
                )
            )
        ),
        onAddContact = {},
        onClickContact = {},
        onLongPressContact = {}
    )
}

data class ContactInfo(
    val id: Long = -1, //todo:error-prone
    val profile: Painter? = null, val name: String, val number: String
)

data class ContactGroup(
    val title: String, val data: List<ContactInfo>
)