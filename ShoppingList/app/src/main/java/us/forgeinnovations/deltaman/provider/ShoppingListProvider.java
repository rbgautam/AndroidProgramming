package us.forgeinnovations.deltaman.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract;
import us.forgeinnovations.deltaman.repository.ShopkeeperOpenHelper;

import static us.forgeinnovations.deltaman.provider.ShoppingListContentProviderContract.*;
import static us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.*;

public class ShoppingListProvider extends ContentProvider {
    private ShopkeeperOpenHelper mDbOpenhelper;

    private static UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;
    public static final int NOTES = 1;

    static{
        sUrimatcher.addURI(AUTHORITY, Courses.PATH, COURSES);
        sUrimatcher.addURI(AUTHORITY,Notes.PATH, NOTES);

    }


    public ShoppingListProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDbOpenhelper = new ShopkeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder)
    {
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenhelper.getReadableDatabase();
        int uriMatch = sUrimatcher.match(uri);

        switch (uriMatch){
            case COURSES:
                cursor = db.query(CourseInfoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case NOTES:
                cursor = db.query(NoteInfoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
