package us.forgeinnovations.deltaman.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RGautam on 12/21/2017.
 */

public final class ShoppingListContentProviderContract {
    private ShoppingListContentProviderContract(){}
    public static final String AUTHORITY = "us.forgeinnovations.deltaman.shoppinglist.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://"+AUTHORITY);

    protected interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CoursesColumns{
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NotesColumns{

        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";

    }

    public static final class Courses implements BaseColumns,CoursesIdColumns, CoursesColumns{
        public static final String PATH = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);

    }

    public static final class Notes implements BaseColumns,CoursesIdColumns, NotesColumns{
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);
    }

}
