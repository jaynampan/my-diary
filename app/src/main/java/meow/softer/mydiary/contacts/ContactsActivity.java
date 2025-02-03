package meow.softer.mydiary.contacts;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import meow.softer.mydiary.R;
import meow.softer.mydiary.db.DBManager;
import meow.softer.mydiary.shared.MyDiaryApplication;
import meow.softer.mydiary.shared.SPFManager;
import meow.softer.mydiary.shared.ThemeManager;
import meow.softer.mydiary.shared.gui.LetterComparator;
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactsActivity extends FragmentActivity implements View.OnClickListener,
        ContactsDetailDialogFragment.ContactsDetailCallback, LetterSortLayout.OnTouchingLetterChangedListener {

    /**
     * getId
     */
    private long topicId;

    /**
     * Sort
     */

    private String EN;
    private String ZH;
    private String BN;

    /**
     * UI
     */
    private ThemeManager themeManager;
    private RelativeLayout RL_contacts_content;
    private TextView IV_contacts_title;
    private EditText EDT_main_contacts_search;
    private LetterSortLayout STL_contacts;
    private ImageView IV_contacts_add;
    private TextView TV_contact_short_sort;

    /**
     * DB
     */
    private DBManager dbManager;
    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_contacts;
    private ContactsAdapter contactsAdapter;
    private LinearLayoutManager layoutManager;

    //Contacts list from DB
    private List<ContactsEntity> contactsNamesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true);
        setStatusBarBgColor();

        themeManager = ThemeManager.getInstance();
        initLanguageStr();

        topicId = getIntent().getLongExtra("topicId", -1);
        if (topicId == -1) {
            finish();
        }
        /**
         * init UI
         */
        RL_contacts_content = findViewById(R.id.RL_contacts_content);
        RL_contacts_content.setBackground(themeManager.getContactsBgDrawable(this, topicId));

        TV_contact_short_sort = findViewById(R.id.TV_contact_short_sort);
        TV_contact_short_sort.setBackgroundColor(themeManager.getThemeDarkColor(ContactsActivity.this));

        STL_contacts = findViewById(R.id.STL_contacts);
        STL_contacts.setSortTextView(TV_contact_short_sort);
        STL_contacts.setOnTouchingLetterChangedListener(this);

        EDT_main_contacts_search = findViewById(R.id.EDT_main_contacts_search);
        IV_contacts_add = findViewById(R.id.IV_contacts_add);
        IV_contacts_add.setOnClickListener(this);

        IV_contacts_title = findViewById(R.id.IV_contacts_title);
        String diaryTitle = getIntent().getStringExtra("diaryTitle");
        if (diaryTitle == null) {
            diaryTitle = "Contacts";
        }
        IV_contacts_title.setText(diaryTitle);


        /**
         * init RecyclerVie
         */
        STL_contacts = findViewById(R.id.STL_contacts);
        RecyclerView_contacts = findViewById(R.id.RecyclerView_contacts);
        contactsNamesList = new ArrayList<>();
        dbManager = new DBManager(ContactsActivity.this);

        initTopbar();
        loadContacts();
        initTopicAdapter();
    }

    private void initLanguageStr() {
        EN = Locale.ENGLISH.getLanguage();
        ZH = Locale.CHINA.getLanguage();
        BN = new Locale("bn", "").getLanguage();


    }

    private void initTopbar() {
        EDT_main_contacts_search.getBackground().setColorFilter(themeManager.getThemeMainColor(this),
                PorterDuff.Mode.SRC_ATOP);
        IV_contacts_title.setTextColor(themeManager.getThemeMainColor(this));
        IV_contacts_add.setColorFilter(themeManager.getThemeMainColor(this));
    }

    private void loadContacts() {
        contactsNamesList.clear();
        dbManager.openDB();
        Cursor contactsCursor = dbManager.selectContacts(topicId);
        for (int i = 0; i < contactsCursor.getCount(); i++) {
            contactsNamesList.add(
                    new ContactsEntity(contactsCursor.getLong(0), contactsCursor.getString(1),
                            contactsCursor.getString(2), contactsCursor.getString(3)));
            contactsCursor.moveToNext();
        }
        contactsCursor.close();
        dbManager.closeDB();
        sortContacts();
    }

    private void sortContacts() {
        for (ContactsEntity contactsEntity : contactsNamesList) {
            String sortString = contactsEntity.getName().substring(0, 1).toUpperCase();
            if (checkLanguage().equals(ZH)) {
                sortContactsCN(contactsEntity, sortString);
            } else {
                sortContactsEN(contactsEntity, sortString);
            }
        }
        contactsNamesList.sort(new LetterComparator());
    }

    private String sortContactsCN(ContactsEntity contactsEntity, String sortString) {
        if (sortString.matches("[\\u4E00-\\u9FA5]")) {
            //TODO:String[] arr = PinyinHelper.toHanyuPinyinStringArray(sortString.trim().charAt(0));
            //sortString = arr[0].substring(0, 1).toUpperCase();
        }
        if (sortString.matches("[A-Z]")) {
            contactsEntity.setSortLetters(sortString.toUpperCase());
        } else {
            contactsEntity.setSortLetters("#");
        }
        return sortString;
    }

    private void sortContactsEN(ContactsEntity contactsEntity, String sortString) {
        if (sortString.matches("[A-Z]")) {
            contactsEntity.setSortLetters(sortString.toUpperCase());
        } else {
            contactsEntity.setSortLetters("#");
        }
    }

    private void initTopicAdapter() {
        //Init topic adapter
        layoutManager = new LinearLayoutManager(this);
        RecyclerView_contacts.setLayoutManager(layoutManager);
        RecyclerView_contacts.setHasFixedSize(true);
        contactsAdapter = new ContactsAdapter(ContactsActivity.this, contactsNamesList, topicId, this);
        RecyclerView_contacts.setAdapter(contactsAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_contacts_add:
                ContactsDetailDialogFragment contactsDetailDialogFragment =
                        ContactsDetailDialogFragment.newInstance(ContactsDetailDialogFragment.ADD_NEW_CONTACTS,
                                "", "", topicId);
                contactsDetailDialogFragment.show(getSupportFragmentManager(), "contactsDetailDialogFragment");
                break;
        }
    }

    @Override
    public void addContacts() {
        loadContacts();
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateContacts() {
        loadContacts();
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteContacts() {
        loadContacts();
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = contactsAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            RecyclerView_contacts.getLayoutManager().scrollToPosition(position);
        }
    }

    /**
     * This code is from array
     * System = 0
     * English = 1
     * 中文 = 2
     * Bangla = 3
     */

    private String checkLanguage() {
        String language;
        switch (SPFManager.getLocalLanguageCode(this)) {
            //default = 0 = system
            default:
                language = getResources().getConfiguration().locale.getLanguage() +
                        "-" + getResources().getConfiguration().locale.getCountry();
                break;
            case 1:
                language = EN;
                break;
            case 2:
                // CHINESE;
                language = ZH;
                break;
            case 3:
                //BANGLA
                language = BN;
                break;
        }
        return language;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));

    }

    private Context updateBaseContextLocale(Context context) {
        Locale locale = MyDiaryApplication.mLocale;
        Log.e("Mytest", "contacts mLocale:" + locale);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }
    private void setStatusBarBgColor(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);
    }
}
