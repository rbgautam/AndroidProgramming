package us.forgeinnovations.deltaman.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.List;

import us.forgeinnovations.deltaman.notes.*;
import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract;
import us.forgeinnovations.deltaman.repository.ShopkeeperOpenHelper;
import us.forgeinnovations.deltaman.repository.ShoppingListDataManager;

import static us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.*;


public class ShoppingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ITEM_INFO = "us.forgeinnovations.deltaman.ui.ITEM_INFO";
    public static final String ITEM_POSITION = "us.forgeinnovations.deltaman.ui.ITEM_POSITION";
    public static final int POSITION_NOT_SET = -1;

    public static final String ORIGINAL_NOTE_COURSE_ID = "us.forgeinnovations.deltaman.ui.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_ITEM_TITLE = "us.forgeinnovations.deltaman.ui.ORIGINAL_ITEM_TITLE";
    public static final String ORIGINAL_ITEM_TEXT = "us.forgeinnovations.deltaman.ui.ORIGINAL_ITEM_TEXT";
    public static final int LOADER_COURSES = 0;
    public static final int LOADER_NOTES = 1;


    private NoteInfo mItem;
    private boolean mIsNewNote;
    private int mItemPosition;
    private Spinner mSpinnerShoppingType;
    private EditText mTextItemName;
    private EditText mTextItemDesc;
    private boolean mIsCanceling;
    private ArrayAdapter<CourseInfo> mAdapterCourses;
    private String mOriginalNoteCourseId;
    private String mOriginalItemDesc;
    private String mOriginalItemTitle;
    private SimpleCursorAdapter mAdapterCourse;
    private SimpleCursorAdapter mSimpleAdapterCourse;
    private ShopkeeperOpenHelper mDbOpenHelper;
    private Cursor mSpinnerCursor;

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
        mDbOpenHelper = new ShopkeeperOpenHelper(this);
        mSpinnerShoppingType = (Spinner) findViewById(R.id.spinner_shoppingtype);
//        List<CourseInfo> courses = DataManager.getInstance().getCourses();

//        mAdapterCourses =
// new ArrayAdapter<CourseInfo>(this,android.R.layout.simple_spinner_item,courses);
        mSimpleAdapterCourse = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,null,new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1},0);
        mSimpleAdapterCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        mSpinnerShoppingType.setAdapter(mSimpleAdapterCourse);

        //loadCourseData();

        getLoaderManager().initLoader(LOADER_COURSES,null,this);


        readDisplayStateValues();
        if(savedInstanceState == null)
            saveOriginalNotesValues();
        else
            restoreOriginalStateValues(savedInstanceState);
        mTextItemName = (EditText) findViewById(R.id.editText_itemname);
        mTextItemDesc = (EditText) findViewById(R.id.editText_itemdesc);

        if(!mIsNewNote){

            displayNote(mSpinnerShoppingType, mTextItemName, mTextItemDesc);


        }else{
            createNewNote();
        }

    }

    private void loadCourseData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        final String[] columns = {"_ID _id",CourseInfoEntry.COLUMN_COURSE_TITLE, CourseInfoEntry.COLUMN_COURSE_ID};
        mSpinnerCursor = db.query(CourseInfoEntry.TABLE_NAME, columns,null,null,null,null, CourseInfoEntry.COLUMN_COURSE_TITLE);
        mSimpleAdapterCourse.changeCursor(mSpinnerCursor);
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
        mItemPosition = dm.createNewNote();
        mItem = dm.getNotes().get(mItemPosition);
    }

    private void displayNote(Spinner shoppingType, EditText textItemName, EditText textItemDesc) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        int itemIndex = 0;

        //itemIndex = courses.indexOf(mItem.getCourse());
        if(mSpinnerCursor != null) {


            boolean more = mSpinnerCursor.moveToFirst();
            while (more) {
                int courseIdPos = mSpinnerCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
                String cursorCid = mSpinnerCursor.getString(courseIdPos);
                String mItemCid = mItem.getCourse().getCourseId();
                if (cursorCid.equals(mItemCid))
                    break;

                itemIndex++;
                more = mSpinnerCursor.moveToNext();
            }


            shoppingType.setSelection(itemIndex);


            textItemName.setText(mItem.getTitle());
            textItemDesc.setText(mItem.getText());
        }
    }

    private void readDisplayStateValues() {

        Intent intent = getIntent();

        //mItem = intent.getParcelableExtra(ITEM_INFO);

        mItemPosition  =  intent.getIntExtra(ITEM_POSITION, POSITION_NOT_SET);
        mIsNewNote =  ((mItemPosition == POSITION_NOT_SET) ? true:false);

        if(mItemPosition != -1)
        {

            //mItem = DataManager.getInstance().getNotes().get(mItemPosition);
            getLoaderManager().initLoader(LOADER_NOTES,null,this);

        }



    }

    private void LoadNotesFromDb(NoteInfo mItem) {


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

        if(id == R.id.action_next) {
            moveNext();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleAdapterCourse.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCanceling){
            if(mIsNewNote){
                DataManager.getInstance().removeNote(mItemPosition);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mItemPosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        ++mItemPosition;
        mItem = DataManager.getInstance().getNotes().get(mItemPosition);

        saveOriginalNotesValues();
        displayNote(mSpinnerShoppingType, mTextItemName, mTextItemDesc);
        invalidateOptionsMenu();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader loader = null;
        if(id == LOADER_COURSES)
            loader = createloaderCourses();
        if(id == LOADER_NOTES)
            loader = createLoaderNotes();
        return loader;
    }

    private CursorLoader createLoaderNotes() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                Cursor cursor =  db.query(NoteInfoEntry.TABLE_NAME,new String[]{},"Noteid = ?",new String[]{},null,null,null);
                return cursor;
            }
        };
    }

    private CursorLoader createloaderCourses() {
        final String[] columns = {"_ID _id",CourseInfoEntry.COLUMN_COURSE_TITLE, CourseInfoEntry.COLUMN_COURSE_ID};
        Uri uri = Uri.parse("content://us.forgeinnovations.deltaman.shoppinglist.provider");

        return new CursorLoader(this,uri, columns,null,null,CourseInfoEntry.COLUMN_COURSE_TITLE );
        //Replacing call with contentprovider
//        return new CursorLoader(this){
//            @Override
//            public Cursor loadInBackground() {
//                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//
//                final String[] columns = {"_ID _id",CourseInfoEntry.COLUMN_COURSE_TITLE, CourseInfoEntry.COLUMN_COURSE_ID};
//                return  db.query(CourseInfoEntry.TABLE_NAME, columns,null,null,null,null, CourseInfoEntry.COLUMN_COURSE_TITLE);
//
//            }
//        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(loader.getId() == LOADER_COURSES){
            mSpinnerCursor = cursor;
            mSimpleAdapterCourse.changeCursor(mSpinnerCursor);
            displayNote(mSpinnerShoppingType,mTextItemName,mTextItemDesc);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if(loader.getId()== LOADER_COURSES){
            if(mSpinnerCursor != null)
                mSpinnerCursor.close();
        }

    }
}
