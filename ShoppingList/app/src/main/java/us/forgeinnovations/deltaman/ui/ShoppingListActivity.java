package us.forgeinnovations.deltaman.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import us.forgeinnovations.deltaman.notes.DataManager;
import us.forgeinnovations.deltaman.notes.NoteInfo;
import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract;
import us.forgeinnovations.deltaman.repository.ShopkeeperOpenHelper;
import us.forgeinnovations.deltaman.repository.ShoppingListDataManager;

import static us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.*;

public class ShoppingListActivity extends AppCompatActivity {

    private ProductRecyclerAdapter mProductRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(ShoppingListActivity.this,ShoppingActivity.class);
                startActivity(intent);
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProductRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {

//        final ListView listItems = (ListView) findViewById(R.id.list_items);
//
//        List<NoteInfo> items = DataManager.getInstance().getNotes();
//
////        ArrayAdapter<NoteInfo> itemAdapter  = new ArrayAdapter<NoteInfo>(this,android.R.layout.simple_list_item_1, items);
////        listItems.setAdapter(itemAdapter);
//
//        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent intent =  new Intent(ShoppingListActivity.this,ShoppingActivity.class);
//
//                NoteInfo item = (NoteInfo) listItems.getItemAtPosition(position);
//
//                //intent.putExtra(ShoppingActivity.ITEM_INFO,item);
//
//                intent.putExtra(ShoppingActivity.ITEM_POSITION, position);
//
//                startActivity(intent);
//            }
//        });

        final RecyclerView recyclerViewItems = (RecyclerView) findViewById(R.id.list_items);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewItems.setLayoutManager(linearLayoutManager);

        ShopkeeperOpenHelper dbHelper = new ShopkeeperOpenHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor notes = db.query(NoteInfoEntry.TABLE_NAME,new String[]{NoteInfoEntry.COLUMN_NOTE_TITLE,NoteInfoEntry.COLUMN_COURSE_ID},null,null,null,null,null);

        mProductRecyclerAdapter = new ProductRecyclerAdapter(this,notes);

        recyclerViewItems.setAdapter(mProductRecyclerAdapter);

    }


}
