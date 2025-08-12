package meow.softer.mydiary.data.db

import android.provider.BaseColumns

class DBStructure {
    object DiaryEntry : BaseColumns {
        const val TABLE_NAME: String = "diary_entry"
        const val COLUMN_TIME: String = "diary_time"
        const val COLUMN_TITLE: String = "diary_count"
        const val COLUMN_CONTENT: String = "diary_content"
        const val COLUMN_MOOD: String = "diary_mood"
        const val COLUMN_WEATHER: String = "diary_weather"
        const val COLUMN_ATTACHMENT: String = "diary_attachment"
        const val COLUMN_REF_TOPIC__ID: String = "diary_ref_topic_id"
        const val COLUMN_LOCATION: String = "diary_location"
    }

    object DiaryEntry_V2 : BaseColumns {
        const val TABLE_NAME: String = "diary_entry_v2"
        const val COLUMN_TIME: String = "diary_time"
        const val COLUMN_TITLE: String = "diary_title"
        const val COLUMN_MOOD: String = "diary_mood"
        const val COLUMN_WEATHER: String = "diary_weather"
        const val COLUMN_ATTACHMENT: String = "diary_attachment"
        const val COLUMN_REF_TOPIC__ID: String = "diary_ref_topic_id"
        const val COLUMN_LOCATION: String = "diary_location"
    }

    /**
     * Type see @IDairyRow
     */
    object DiaryItemEntry_V2 : BaseColumns {
        const val TABLE_NAME: String = "diary_item_entry_v2"
        const val COLUMN_TYPE: String = "diary_item_type"
        const val COLUMN_POSITION: String = "diary_item_position"
        const val COLUMN_CONTENT: String = "diary_item_content"
        const val COLUMN_REF_DIARY__ID: String = "item_ref_diary_id"
    }


    object TopicEntry : BaseColumns {
        const val TABLE_NAME: String = "topic_entry"
        const val COLUMN_ORDER: String = "topic_order"
        const val COLUMN_NAME: String = "topic_name"
        const val COLUMN_TYPE: String = "topic_type"
        const val COLUMN_SUBTITLE: String = "topic_subtitle"
        const val COLUMN_COLOR: String = "topic_color"
    }


    object TopicOrderEntry : BaseColumns {
        const val TABLE_NAME: String = "topic_order"
        const val COLUMN_ORDER: String = "topic_order_order_in_parent"
        const val COLUMN_REF_TOPIC__ID: String = "topic_order_ref_topic_id"
    }

  companion  object MemoEntry : BaseColumns {
        const val TABLE_NAME: String = "memo_entry"
        const val COLUMN_ORDER: String = "memo_order"
        const val COLUMN_CONTENT: String = "memo_content"
        const val COLUMN_CHECKED: String = "memo_checked"
        const val COLUMN_REF_TOPIC__ID: String = "memo_ref_topic_id"
    }

    object MemoOrderEntry : BaseColumns {
        const val TABLE_NAME: String = "memo_order"
        const val COLUMN_ORDER: String = "memo_order_order_in_parent"
        const val COLUMN_REF_TOPIC__ID: String = "memo_order_ref_topic_id"
        const val COLUMN_REF_MEMO__ID: String = "memo_order_ref_memo_id"
    }


    object ContactsEntry : BaseColumns {
        const val TABLE_NAME: String = "contacts_entry"
        const val COLUMN_NAME: String = "contacts_name"
        const val COLUMN_PHONENUMBER: String = "contacts_phone_number"
        const val COLUMN_PHOTO: String = "contacts_photo"
        const val COLUMN_REF_TOPIC__ID: String = "contacts_ref_topic_id"
    }
}
