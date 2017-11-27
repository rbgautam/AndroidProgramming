package us.forgeinnovations.deltaman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import us.forgeinnovations.deltaman.notes.*;


public class ShoppingActivity extends AppCompatActivity {

    public static final String ITEM_INFO = "us.forgeinnovations.deltaman.ui.ITEM_INFO";
    public static final String ITEM_POSITION = "us.forgeinnovations.deltaman.ui.ITEM_POSITION";
    public static final int POSITION_NOT_SET = -1;

    public static final String ORIGINAL_NOTE_COURSE_ID = "us.forgeinnovations.deltaman.ui.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_ITEM_TITLE = "us.forgeinnovations.deltaman.ui.ORIGINAL_ITEM_TITLE";
    public static final String ORIGINAL_ITEM_TEXT = "us.forgeinnovations.deltaman.ui.ORIGINAL_ITEM_TEXT";


    private NoteInfo mItem;
    private boolean mIsNewNote;
    private int mItemPosition;
    private Spinner mSpinnerShoppingType;
    private EditText mTextItemName;
    private EditText mTextItemDesc;
    private int mNotePosition;
    private boolean mIsCanceling;
    private ArrayAdapter<CourseInfo> mAdapterCourses;
    private String mOriginalNoteCourseId;
    private String mOriginalItemDesc;
    private String mOriginalItemTitle;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID,mOriginalNoteCourseId);
        outState.putString(ORIGINAL_ITEM_TITLE, mOriginalItemTitle);
        outState.putString(ORIGINAL_ITEM_TEXT,mOriginalItemDesc);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerShoppingType = (Spinner) findViewById(R.id.spinner_shoppingtype);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        mAdapterCourses = new ArrayAdapter<CourseInfo>(this,android.R.layout.simple_spinner_item,courses);

        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerShoppingType.setAdapter(mAdapterCourses);
        
        readDisplayStateValues();
        if(savedInstanceState == null)
            saveOriginalNotesValues();
        else
            restoreOriginalStateValues(savedInstanceState);

        if(!mIsNewNote){
            mTextItemName = (EditText) findViewById(R.id.editText_itemname);
            mTextItemDesc = (EditText) findViewById(R.id.editText_itemdesc);
            displayNote(mSpinnerShoppingType, mTextItemName, mTextItemDesc);


        }else{
            createNewNote();
        }

    }

    private void restoreOriginalStateValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalItemTitle = savedInstanceState.getString(ORIGINAL_ITEM_TITLE);
        mOriginalItemDesc =  savedInstanceState.getString(ORIGINAL_ITEM_TEXT);
    }

    private void saveOriginalNotesValues() {
        if(mIsNewNote)
            return;
        mOriginalNoteCourseId = mItem.getCourse().getCourseId();
        mOriginalItemDesc = mItem.getText();
        mOriginalItemTitle = mItem.getTitle();

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mItem = dm.getNotes().get(mNotePosition);
    }

    private void displayNote(Spinner shoppingType, EditText textItemName, EditText textItemDesc) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        int itemIndex = POSITION_NOT_SET;

        itemIndex = courses.indexOf(mItem.getCourse());


        shoppingType.setSelection(itemIndex);


        textItemName.setText(mItem.getTitle());
        textItemDesc.setText(mItem.getText());
    }

    private void readDisplayStateValues() {

        Intent intent = getIntent();

        //mItem = intent.getParcelableExtra(ITEM_INFO);

        mItemPosition  =  intent.getIntExtra(ITEM_POSITION, POSITION_NOT_SET);
        mIsNewNote =  ((mItemPosition == POSITION_NOT_SET) ? true:false);

        if(mItemPosition != -1)
            mItem = DataManager.getInstance().getNotes().get(mItemPosition);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }

        if(id == R.id.action_cancel){
            mIsCanceling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapterCourses.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCanceling){
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mNotePosition);
            }else{
                restoreOriginalValues();
            }
        }
        else
            saveNote();
    }

    private void restoreOriginalValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mItem.setCourse(course);
        mItem.setTitle(mOriginalItemTitle);
        mItem.setText(mOriginalItemDesc);
    }

    private void saveNote() {
        mItem.setCourse((CourseInfo) mSpinnerShoppingType.getSelectedItem());
        mItem.setTitle(mTextItemName.getText().toString());
        mItem.setText(mTextItemDesc.getText().toString());
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerShoppingType.getSelectedItem();
        String subject = mTextItemName.getText().toString();
        String emailBody = "Check out what I learned\""+ course.getTitle() + "\"\n" + mTextItemDesc.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");

        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        startActivity(intent);

    }
}
