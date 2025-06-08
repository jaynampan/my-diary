package meow.softer.mydiary.ui.home

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import meow.softer.mydiary.contacts.ContactsEntity
import meow.softer.mydiary.main.topic.Contacts
import meow.softer.mydiary.main.topic.ITopic
import kotlin.collections.mutableListOf

class MainViewModel : ViewModel() {
    val userName = MutableStateFlow("User")
    val userPainter = MutableStateFlow<Painter?>(null)
    val headerBgPainter = MutableStateFlow<Painter?>(null)
    val topicData = MutableStateFlow<List<ITopic>>(listOf())
    val contactTitle = MutableStateFlow<String>("")
    val importPath = MutableStateFlow<String>("")
    val exportPath = MutableStateFlow<String>("")
    val contacts = MutableStateFlow<List<ContactGroup>>(emptyList())


    fun updateUserName(value: String) {
        userName.value = value
    }

    fun updateUserPic(value: Painter) {
        userPainter.value = value
    }

    fun updateHeaderBgPic(value: Painter) {
        headerBgPainter.value = value
    }

    fun updateTopicData(value: List<ITopic>) {
        topicData.value = value
    }

    fun updateBackUpSrc(path: String) {
        exportPath.value = path
    }

    fun updateImportPath(path: String) {
        importPath.value = path
    }

    fun updateContactTitle(title: String) {
        contactTitle.value = title

    }

    fun updateContactData(contactList: MutableList<ContactsEntity>?) {
        val data = mutableListOf<ContactGroup>()
        contactList
            ?.map { it ->
                ContactInfo(
                    id = it.contactsId,
                    name = it.name ?: "",
                    number = it.phoneNumber ?: ""
                )
            }
            ?.groupBy { it.name.first() }
            ?.forEach { key, value ->
                data.add(ContactGroup(title = key.toString(), value))
            }
        contacts.value = data

    }
}