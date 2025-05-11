package meow.softer.mydiary.init

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.entries.diary.DiaryInfoHelper
import meow.softer.mydiary.entries.diary.item.IDiaryRow
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.shared.SPFManager

class InitTask(private val mContext: Context, private val callBack: InitCallBack) :
    AsyncTask<Long?, Void?, Boolean?>() {
    interface InitCallBack {
        fun onInitCompiled(showReleaseNote: Boolean)
    }

    var showReleaseNote: Boolean


    init {
        this.showReleaseNote = SPFManager.getReleaseNoteClose(mContext)
    }

    override fun doInBackground(vararg longs: Long?): Boolean {
        try {
            val dbManager = DBManager(mContext)
            dbManager.openDB()
            if (SPFManager.getFirstRun(mContext)) {
                loadSampleData(dbManager)
            }

            updateData(dbManager)
            dbManager.closeDB()
            saveCurrentVersionCode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return showReleaseNote
    }


    override fun onPostExecute(showReleaseNote: Boolean?) {
        super.onPostExecute(showReleaseNote)
        callBack.onInitCompiled(this.showReleaseNote)
    }

    @Throws(Exception::class)
    private fun loadSampleData(dbManager: DBManager) {

        val mitsuhaMemoId = dbManager.insertTopic("Sample Memo", ITopic.TYPE_MEMO, Color.BLACK)

        dbManager.insertTopicOrder(mitsuhaMemoId, 0)
        //Insert sample memo
        if (mitsuhaMemoId != -1L) {
            dbManager.insertMemoOrder(
                mitsuhaMemoId,
                dbManager.insertMemo("don't forget to water the flower", false, mitsuhaMemoId),
                0
            )
            dbManager.insertMemoOrder(
                mitsuhaMemoId,
                dbManager.insertMemo("check emails", false, mitsuhaMemoId),
                1
            )
            dbManager.insertMemoOrder(
                mitsuhaMemoId,
                dbManager.insertMemo("remember to buy coffee", true, mitsuhaMemoId),
                2
            )
            dbManager.insertMemoOrder(
                mitsuhaMemoId,
                dbManager.insertMemo("don't skip classes!!!", false, mitsuhaMemoId),
                3
            )
            dbManager.insertMemoOrder(
                mitsuhaMemoId,
                dbManager.insertMemo("get up early tomorrow!", true, mitsuhaMemoId),
                4
            )
        }

        /*
         *insert sample diary
         */
        //Insert sample topic
        val topicOnDiarySampleId =
            dbManager.insertTopic("Sample Diary", ITopic.TYPE_DIARY, Color.BLACK)
        dbManager.insertTopicOrder(topicOnDiarySampleId, 2)
        if (topicOnDiarySampleId != -1L) {
            //Insert sample diary
            val diarySampleId = dbManager.insertDiaryInfo(
                1475665800000L,
                "Cozy life❤",
                DiaryInfoHelper.MOOD_HAPPY,
                DiaryInfoHelper.WEATHER_RAINY,
                true,
                topicOnDiarySampleId,
                "Tokyo"
            )
            dbManager.insertDiaryContent(
                IDiaryRow.TYPE_TEXT,
                0,
                "I had a good time with friends today!",
                diarySampleId
            )
        }

        /*
         *insert sample contact
         */
        //Insert sample contacts
        val sampleContactsId =
            dbManager.insertTopic("Emergency contact", ITopic.TYPE_CONTACTS, Color.BLACK)
        dbManager.insertTopicOrder(sampleContactsId, 3)
        //Insert sample contacts
        if (sampleContactsId != -1L) {
            dbManager.insertContacts(
                mContext.getString(R.string.emergency_contact),
                "123456",
                "",
                sampleContactsId
            )
        }
    }

    @Throws(Exception::class)
    private fun updateData(dbManager: DBManager?) {
        //Photo path modify in version 17
        if (SPFManager.getVersionCode(mContext) < 17) {
//            OldVersionHelper.Version17MoveTheDiaryIntoNewDir(mContext);
        }
    }

    private fun saveCurrentVersionCode() {
        //Save currentVersion
        Log.e(
            "Mytest",
            "SPF version " + SPFManager.getVersionCode(mContext) + "buildconfig version" + Build.VERSION.SDK_INT
        )
        if (SPFManager.getVersionCode(mContext) < Build.VERSION.SDK_INT) {
            SPFManager.setReleaseNoteClose(mContext, false)
            showReleaseNote = true
            SPFManager.setVersionCode(mContext)
        }
    }
}
