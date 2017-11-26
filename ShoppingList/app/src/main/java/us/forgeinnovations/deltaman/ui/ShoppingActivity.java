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

import us.forgeinnovations.deltaman.models.*;


public class ShoppingActivity extends AppCompatActivity {

    public static final String ITEM_INFO = "us.forgeinnovations.deltaman.ui.ITEM_INFO";
    public static final String ITEM_POSITION = "us.forgeinnovations.deltaman.ui.ITEM_POSITION";
    public static final int POSITION_NOT_SET = -1;

    private NoteInfo mItem;
    private boolean mIsNewNote;
    private int mItemPosition;
    private Spinner mSpinnerShoppingType;
    private EditText mTextItemName;
    private EditText mTextItemDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerShoppingType = (Spinner) findViewById(R.id.spinner_shoppingtype);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        ArrayAdapter<CourseInfo> adapterCourses =  new ArrayAdapter<CourseInfo>(this,android.R.layout.simple_spinner_item,courses);

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerShoppingType.setAdapter(adapterCourses);
        
        readDisplayStateValues();


        if(!mIsNewNote){
            mTextItemName = (EditText) findViewById(R.id.editText_itemname);
            mTextItemDesc = (EditText) findViewById(R.id.editText_itemdesc);
            displayNote(mSpinnerShoppingType, mTextItemName, mTextItemDesc);


        }

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
        mItem = DataManager.getInstance().getNotes().get(mItemPosition);
        mIsNewNote =  ((mItemPosition == POSITION_NOT_SET) ? true:false);


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

        return super.onOptionsItemSelected(item);
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
