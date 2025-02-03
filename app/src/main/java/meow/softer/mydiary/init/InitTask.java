package meow.softer.mydiary.init;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import meow.softer.mydiary.R;
import meow.softer.mydiary.db.DBManager;
import meow.softer.mydiary.entries.diary.DiaryInfoHelper;
import meow.softer.mydiary.entries.diary.item.IDiaryRow;
import meow.softer.mydiary.main.topic.ITopic;
import meow.softer.mydiary.shared.SPFManager;

public class InitTask extends AsyncTask<Long, Void, Boolean> {
    public interface InitCallBack {
        void onInitCompiled(boolean showReleaseNote);
    }

    private InitCallBack callBack;
    private Context mContext;
    boolean showReleaseNote;


    public InitTask(Context context, InitCallBack callBack) {
        this.mContext = context;
        this.callBack = callBack;
        this.showReleaseNote = SPFManager.getReleaseNoteClose(mContext);
    }

    @Override
    protected Boolean doInBackground(Long... longs) {
        try {
            DBManager dbManager = new DBManager(mContext);
            dbManager.openDB();
            if (SPFManager.getFirstRun(mContext)){
                loadSampleData(dbManager);
            }

            updateData(dbManager);
            dbManager.closeDB();
            saveCurrentVersionCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showReleaseNote;
    }


    @Override
    protected void onPostExecute(Boolean showReleaseNote) {
        super.onPostExecute(showReleaseNote);
        callBack.onInitCompiled(this.showReleaseNote);
    }

    private void loadSampleData(DBManager dbManager) throws Exception {

        /*
         *insert sample memo
         */
        //Insert sample topic
        long mitsuhaMemoId = dbManager.insertTopic("Sample Memo", ITopic.TYPE_MEMO, Color.BLACK);


        dbManager.insertTopicOrder(mitsuhaMemoId, 0);

        //Insert sample memo
        if (mitsuhaMemoId != -1) {
            dbManager.insertMemoOrder(mitsuhaMemoId,
                    dbManager.insertMemo("don't forget to water the flower", false, mitsuhaMemoId)
                    , 0);
            dbManager.insertMemoOrder(mitsuhaMemoId,
                    dbManager.insertMemo("check emails", false, mitsuhaMemoId)
                    , 1);
            dbManager.insertMemoOrder(mitsuhaMemoId,
                    dbManager.insertMemo("remember to buy coffee", true, mitsuhaMemoId)
                    , 2);
            dbManager.insertMemoOrder(mitsuhaMemoId,
                    dbManager.insertMemo("don't skip classes!!!", false, mitsuhaMemoId)
                    , 3);
            dbManager.insertMemoOrder(mitsuhaMemoId,
                    dbManager.insertMemo("get up early tomorrow!", true, mitsuhaMemoId)
                    , 4);
        }

        /*
         *insert sample diary
         */
        //Insert sample topic
        long topicOnDiarySampleId = dbManager.insertTopic("Sample Diary", ITopic.TYPE_DIARY, Color.BLACK);
        dbManager.insertTopicOrder(topicOnDiarySampleId, 2);
        if (topicOnDiarySampleId != -1) {
            //Insert sample diary
            long diarySampleId = dbManager.insertDiaryInfo(1475665800000L, "Cozy life❤",
                    DiaryInfoHelper.MOOD_HAPPY, DiaryInfoHelper.WEATHER_RAINY, true, topicOnDiarySampleId, "Tokyo");
            dbManager.insertDiaryContent(IDiaryRow.TYPE_TEXT, 0, "I had a good time with friends today!", diarySampleId);
        }

        /*
         *insert sample contact
         */
        //Insert sample contacts
        long sampleContactsId = dbManager.insertTopic("Emergency contact", ITopic.TYPE_CONTACTS, Color.BLACK);
        dbManager.insertTopicOrder(sampleContactsId, 3);
        //Insert sample contacts
        if (sampleContactsId != -1) {
            dbManager.insertContacts(mContext.getString(R.string.emergency_contact), "123456", "", sampleContactsId);
        }


    }

    private void updateData(DBManager dbManager) throws Exception {
        //Photo path modify in version 17
        if (SPFManager.getVersionCode(mContext) < 17) {
//            OldVersionHelper.Version17MoveTheDiaryIntoNewDir(mContext);
        }

    }

    private void saveCurrentVersionCode() {
        //Save currentVersion
        Log.e("Mytest", "SPF version " + SPFManager.getVersionCode(mContext) + "buildconfig version" + Build.VERSION.SDK_INT);
        if (SPFManager.getVersionCode(mContext) < Build.VERSION.SDK_INT) {
            SPFManager.setReleaseNoteClose(mContext, false);
            showReleaseNote = true;
            SPFManager.setVersionCode(mContext);
        }
    }
}
