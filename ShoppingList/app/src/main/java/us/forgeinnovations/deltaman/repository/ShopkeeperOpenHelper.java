package us.forgeinnovations.deltaman.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RGautam on 12/8/2017.
 */

public class ShopkeeperOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notekeeper.db";
    //Command to download db on local system: adb exec-out run-as us.forgeinnovations.deltaman.shoppinglist cat databases/Notekeeper.db


    public static final int DATABASE_VERSION  = 1;

    public ShopkeeperOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ShopkeeperDatabaseContract.CourseInfoEntry.CREATE_TABLE_SQL);
        db.execSQL(ShopkeeperDatabaseContract.NoteInfoEntry.CREATE_TABLE_NOTE_SQL);

        DatabaseDataWorker dbw = new DatabaseDataWorker(db);
        dbw.insertSampleNotes();
        dbw.insertCourses();

        
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
