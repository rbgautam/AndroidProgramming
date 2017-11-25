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
    private NoteInfo mItem;
    private boolean mIsNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner shoppingType = (Spinner) findViewById(R.id.spinner_shoppingtype);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        ArrayAdapter<CourseInfo> adapterCourses =  new ArrayAdapter<CourseInfo>(this,android.R.layout.simple_spinner_item,courses);

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        shoppingType.setAdapter(adapterCourses);
        
        readDisplayStateValues();


        if(!mIsNewNote){
            EditText textItemName =  (EditText) findViewById(R.id.editText_itemname);
            EditText textItemDesc =  (EditText) findViewById(R.id.editText_itemdesc);
            displayNote(shoppingType,textItemName,textItemDesc);
        }

    }

    private void displayNote(Spinner shoppingType, EditText textItemName, EditText textItemDesc) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int itemIndex = courses.indexOf(mItem.getCourse());

        shoppingType.setSelection(itemIndex);


        textItemName.setText(mItem.getTitle());
        textItemDesc.setText(mItem.getText());
    }

    private void readDisplayStateValues() {

        Intent intent = getIntent();

        mItem = intent.getParcelableExtra(ITEM_INFO);

        mIsNewNote =  (mItem == null ? true:false);


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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
