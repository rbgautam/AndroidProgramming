package us.forgeinnovations.deltaman.repository;

import android.provider.BaseColumns;

/**
 * Created by RGautam on 12/8/2017.
 */

public final class ShopkeeperDatabaseContract {
    private ShopkeeperDatabaseContract(){}

    public static final class CourseInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "course_info";

        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";
        public static final String _ID = "_ID";
        public static final String CREATE_TABLE_SQL = "CREATE TABLE "+ TABLE_NAME + "("+_ID+" INTEGER PRIMARY KEY , "+COLUMN_COURSE_ID+" TEXT NOT NULL ,"+COLUMN_COURSE_TITLE+" TEXT NOT NULL)";

    }


    public static final class NoteInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "note_info";

        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String CREATE_TABLE_NOTE_SQL = "CREATE TABLE "+TABLE_NAME + " ("+_ID+" INTEGER PRIMARY KEY, "+COLUMN_NOTE_TITLE+" TEXT NOT NULL,"+COLUMN_NOTE_TEXT+" TEXT ,"+COLUMN_COURSE_ID+" TEXT NOT NULL)";


    }
}
