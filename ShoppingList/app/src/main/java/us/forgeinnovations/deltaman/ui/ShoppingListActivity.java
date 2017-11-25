package us.forgeinnovations.deltaman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import us.forgeinnovations.deltaman.models.DataManager;
import us.forgeinnovations.deltaman.models.NoteInfo;
import us.forgeinnovations.deltaman.ui.R;

public class ShoppingListActivity extends AppCompatActivity {

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

    private void initializeDisplayContent() {

        final ListView listItems = (ListView) findViewById(R.id.list_items);

        List<NoteInfo> items = DataManager.getInstance().getNotes();

        ArrayAdapter<NoteInfo> itemAdapter  = new ArrayAdapter<NoteInfo>(this,android.R.layout.simple_list_item_1, items);
        listItems.setAdapter(itemAdapter);

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent =  new Intent(ShoppingListActivity.this,ShoppingActivity.class);

                NoteInfo item = (NoteInfo) listItems.getItemAtPosition(position);

                intent.putExtra(ShoppingActivity.ITEM_INFO,item);

                startActivity(intent);
            }
        });
    }


}
