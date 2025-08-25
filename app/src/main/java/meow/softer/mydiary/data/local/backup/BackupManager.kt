package meow.softer.mydiary.data.local.backup

import android.os.Build
import meow.softer.mydiary.data.local.backup.obj.BUContactsEntries
import meow.softer.mydiary.data.local.backup.obj.BUDiaryEntries
import meow.softer.mydiary.data.local.backup.obj.BUMemoEntries

class BackupManager {
    var create_time: Long = 0
        private set
    var version_code: Int = 0
        private set
    private var backup_topic_list: MutableList<BackupTopicListBean?>? = null

    fun initBackupManagerExportInfo() {
        backup_topic_list = ArrayList<BackupTopicListBean?>()
        version_code = Build.VERSION.SDK_INT // TODO: note: changed this line, might have bug
        create_time = System.currentTimeMillis()
    }

    fun addTopic(topic: BackupTopicListBean?) {
        backup_topic_list!!.add(topic)
    }

    val header: String
        get() = Companion.header


    fun getBackup_topic_list(): MutableList<BackupTopicListBean?> {
        return backup_topic_list!!
    }

    fun setBackup_topic_list(backup_topic_list: MutableList<BackupTopicListBean?>) {
        this.backup_topic_list = backup_topic_list
    }

    class BackupTopicListBean(
        @JvmField var topic_id: Long,
        /**
         * contacts_topic_entries_list : []
         * topic_title : 緊急時以外かけちゃダメ！
         * topic_type : 1
         * topic_id : 4
         * topic_color : -16777216
         * topic_order : 3
         * diary_topic_entries_list : [{"diary_item_list":[{"diary_item_content":"85dfc217-d0aa-47fd-8528-2c85eec21686","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":6,"diary_entries_time":1487691454693,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"98819be5-dd6d-477c-9487-31daa878d5f9","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":5,"diary_entries_time":1487688752707,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"58fb4af4-a78a-4ef1-9a24-27cb6d126cc1","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0},{"diary_item_content":"b5c2eeaa-7519-40d9-8c5c-4e6aed7796c2","diary_item_position":2,"diary_item_type":1},{"diary_item_content":"","diary_item_position":3,"diary_item_type":0},{"diary_item_content":"17f31dc7-274b-4a8b-9611-6a4b71e05cac","diary_item_position":4,"diary_item_type":1},{"diary_item_content":"","diary_item_position":5,"diary_item_type":0},{"diary_item_content":"8b8c1c61-db90-4e1f-b9a1-6c183bbc3c29","diary_item_position":6,"diary_item_type":1},{"diary_item_content":"","diary_item_position":7,"diary_item_type":0},{"diary_item_content":"6b15b9e6-bc1d-463f-8f7c-24fe649b57a1","diary_item_position":8,"diary_item_type":1},{"diary_item_content":"","diary_item_position":9,"diary_item_type":0},{"diary_item_content":"18ea77ea-5abb-48c3-97a8-dd6ed2f0d61b","diary_item_position":10,"diary_item_type":1},{"diary_item_content":"","diary_item_position":11,"diary_item_type":0},{"diary_item_content":"005bf34c-eaf9-4307-8514-0ee9960ae128","diary_item_position":12,"diary_item_type":1},{"diary_item_content":"","diary_item_position":13,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":4,"diary_entries_time":1487687347039,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"fd38fc7e-a310-498c-85d8-5b2e83ac9fcd","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":3,"diary_entries_time":1487687062783,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"635a8c1e-25e5-4b66-b8a8-1c2a7278128f","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":2,"diary_entries_time":1487683670471,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"There are many coffee shop in Tokyo!","diary_item_position":0,"diary_item_type":0}],"diary_entries_location":"Tokyo","diary_entries_id":1,"diary_entries_time":1475665800000,"diary_entries_mood":0,"diary_entries_weather":3,"diary_entries_attachment":true}]
         * memo_topic_entries_list : [{"memo_entries_content":"無駄つかい禁止！","checked":true,"memo_entries_order":5},{"memo_entries_content":"訛り禁止！","checked":false,"memo_entries_order":4},{"memo_entries_content":"遅刻するな！","checked":true,"memo_entries_order":3},{"memo_entries_content":"女言葉NG！","checked":false,"memo_entries_order":2},{"memo_entries_content":"奧寺先輩と馴れ馴れしくするな.....","checked":true,"memo_entries_order":1},{"memo_entries_content":"司とベタベタするな.....","checked":true,"memo_entries_order":0}]
         */
        @JvmField var topic_title: String?, var topic_order: Int,
        @JvmField var topic_color: Int
    ) {
        @JvmField
        var topic_type: Int = 0
        @JvmField
        var contacts_topic_entries_list: MutableList<BUContactsEntries?>? = null
        @JvmField
        var diary_topic_entries_list: MutableList<BUDiaryEntries?>? = null
        @JvmField
        var memo_topic_entries_list: MutableList<BUMemoEntries?>? = null

        fun setTopic_id(topic_id: Int) {
            this.topic_id = topic_id.toLong()
        }
    }

    companion object {
        /**
         * backup_topic_list : []
         * header : 79997e7ee0902e2010690e4f1951f81d
         * create_time : 1487944303719
         * version_code : 29
         */
        const val BACKUP_JSON_FILE_NAME: String = "backup.json"
        const val BACKUP_ZIP_FILE_HEADER: String = "MyDiaryBackup_"
        const val BACKUP_ZIP_FILE_SUB_FILE_NAME: String = ".zip"

        const val header: String = "79997e7ee0902e2010690e4f1951f81d"
    }
}
