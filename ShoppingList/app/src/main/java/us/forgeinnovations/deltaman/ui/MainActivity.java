package us.forgeinnovations.deltaman.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import us.forgeinnovations.deltaman.notes.CourseInfo;
import us.forgeinnovations.deltaman.notes.DataManager;
import us.forgeinnovations.deltaman.notes.NoteInfo;
import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract;
import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.NoteInfoEntry;
import us.forgeinnovations.deltaman.repository.ShopkeeperOpenHelper;
import us.forgeinnovations.deltaman.repository.ShoppingListDataManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProductRecyclerAdapter mProductRecyclerAdapter;
    private ShoplistRecyclerAdapter mShoplistRecyclerAdapter;
    private RecyclerView mRecyclerViewItems;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private ShopkeeperOpenHelper mDatabaseOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseOpenHelper = new ShopkeeperOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this,ShoppingActivity.class);
                startActivity(intent);
            }
        });

        PreferenceManager.setDefaultValues(this,R.xml.pref_general,false);
        PreferenceManager.setDefaultValues(this,R.xml.pref_data_sync,false);
        PreferenceManager.setDefaultValues(this,R.xml.pref_notification,false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initializeDisplayContent();
    }

    @Override
    protected void onDestroy() {
        mDatabaseOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProductRecyclerAdapter.notifyDataSetChanged();
        updateNavheader();
    }

    private void updateNavheader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textuserName = (TextView) headerView.findViewById(R.id.text_edit_displayname);
        TextView textUserEmail = (TextView) headerView.findViewById(R.id.text_edit_user_email);
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);

        textuserName.setText(pref.getString("user_display_name",""));
        textUserEmail.setText(pref.getString("user_email_address",""));
    }


    private void initializeDisplayContent() {

        ShoppingListDataManager.loadFromDatabase(mDatabaseOpenHelper);

        mRecyclerViewItems = (RecyclerView) findViewById(R.id.list_menu_items);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mGridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.col_span));


        //List<NoteInfo> notes = DataManager.getInstance().getNotes();
        Cursor notes = getNotesFromDb();
        mProductRecyclerAdapter = new ProductRecyclerAdapter(this,notes);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mShoplistRecyclerAdapter = new ShoplistRecyclerAdapter(this, courses);

        displayNotes();

    }

    private Cursor getNotesFromDb( ) {

        SQLiteDatabase db =  mDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor =  db.query(NoteInfoEntry.TABLE_NAME,new String[]{NoteInfoEntry.COLUMN_NOTE_TITLE,NoteInfoEntry.COLUMN_COURSE_ID},null,null,null,null,null);
        return cursor;
    }

    private void displayNotes() {
        mRecyclerViewItems.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewItems.setAdapter(mProductRecyclerAdapter);
        //SQLiteDatabase sqldb =  mDatabaseOpenHelper.getReadableDatabase();

        selectNavigationMenuItem(R.id.nav_notes);

    }

    private void selectNavigationMenuItem(int nav_item) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        menu.findItem(nav_item).setChecked(true);
    }


    private void displayCourses() {
        mRecyclerViewItems.setLayoutManager(mGridLayoutManager);
        mRecyclerViewItems.setAdapter(mShoplistRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_course);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNotes();
        } else if (id == R.id.nav_course) {
            displayCourses();
        } else if (id == R.id.nav_share) {
            handleShare();
        }else if (id == R.id.nav_send) {
            handleSelection("Send data by email");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_menu_items);
        String message = "Share to : "+PreferenceManager.getDefaultSharedPreferences(this).getString("user_pref_social_network","");
        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();

    }


    private void handleSelection(String message) {
        View view = findViewById(R.id.list_menu_items);

        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
    }
}
