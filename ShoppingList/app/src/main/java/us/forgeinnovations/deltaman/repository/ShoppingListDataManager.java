package us.forgeinnovations.deltaman.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import us.forgeinnovations.deltaman.models.shop.*;
import us.forgeinnovations.deltaman.notes.CourseInfo;
import us.forgeinnovations.deltaman.notes.NoteInfo;
import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.CourseInfoEntry;

import static us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.*;

/**
 * Created by Deltaman.
 */

public class ShoppingListDataManager {
    private static ShoppingListDataManager ourInstance = null;

    public List<ShoplistInfo> mCourses = new ArrayList<>();
    private List<ItemInfo> mNotes = new ArrayList<>();
    public static Cursor mCourseCursor;
    public static Cursor mNoteCursor;

    public static ShoppingListDataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new ShoppingListDataManager();
//            ourInstance.initializeCourses();
//            ourInstance.initializeExampleNotes();
        }
        return ourInstance;
    }

    public static void loadFromDatabase(ShopkeeperOpenHelper dbHelper){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String[] courseColumns = {CourseInfoEntry.COLUMN_COURSE_ID, CourseInfoEntry.COLUMN_COURSE_TITLE};
        mCourseCursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns, null, null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE + " DESC");

        loadCoursesFromDatabase(mCourseCursor);
        final String[] noteColumns = {NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry.COLUMN_NOTE_TEXT, NoteInfoEntry.COLUMN_NOTE_TITLE};
        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns, null, null, null, null,NoteInfoEntry.COLUMN_COURSE_ID+","+ NoteInfoEntry.COLUMN_NOTE_TITLE);
        loadNotesFromDatabase(mNoteCursor);
    }

    private static void loadNotesFromDatabase(Cursor mNoteCursor) {
        int noteTitlepos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        int noteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        int courseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);

        ShoppingListDataManager dm = getInstance();
        dm.mNotes.clear();

        while (mNoteCursor.moveToNext()){
            String noteTitle = mNoteCursor.getString(noteTitlepos);
            String noteText = mNoteCursor.getString(noteTextPos);
            String courseId = mNoteCursor.getString(courseIdPos);
            ShoplistInfo noteCourse = dm.getCourse(courseId);

            ShoplistInfo slist = new ShoplistInfo(courseId,noteTitle,null);
            dm.mCourses.add(slist);
        }

        mNoteCursor.close();

    }

    private static void loadCoursesFromDatabase(Cursor cursor) {
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseTitlePos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
        ShoppingListDataManager dm = getInstance();
        dm.mCourses.clear();

        while(cursor.moveToNext()){
            String courseId = cursor.getString(courseIdPos);
            String courseTitle = cursor.getString(courseTitlePos);

            ItemInfo prod = new ItemInfo(null,courseId,courseTitle);
            dm.mNotes.add(prod);
        }
        cursor.close();

    }

    public String getCurrentUserName() {
        return "Jim Wilson";
    }

    public String getCurrentUserEmail() {
        return "jimw@jwhh.com";
    }

    public List<ItemInfo> getNotes() {
        return mNotes;
    }

    public int createNewNote() {
        ItemInfo note = new ItemInfo(null, null, null);
        mNotes.add(note);
        return mNotes.size() - 1;
    }

    public int findNote(ItemInfo note) {
        for(int index = 0; index < mNotes.size(); index++) {
            if(note.equals(mNotes.get(index)))
                return index;
        }

        return -1;
    }

    public void removeNote(int index) {
        mNotes.remove(index);
    }

    public List<ShoplistInfo> getCourses() {
        return mCourses;
    }

    public ShoplistInfo getCourse(String id) {
        for (ShoplistInfo course : mCourses) {
            if (id.equals(course.getCourseId()))
                return course;
        }
        return null;
    }

    public List<ItemInfo> getNotes(ShoplistInfo course) {
        ArrayList<ItemInfo> notes = new ArrayList<>();
        for(ItemInfo note:mNotes) {
            if(course.equals(note.getCourse()))
                notes.add(note);
        }
        return notes;
    }

    public int getNoteCount(ShoplistInfo course) {
        int count = 0;
        for(ItemInfo note:mNotes) {
            if(course.equals(note.getCourse()))
                count++;
        }
        return count;
    }

    private ShoppingListDataManager() {
    }

    //region Initialization code

    private void initializeCourses() {
        mCourses.add(initializeCourse1());
        mCourses.add(initializeCourse2());
        mCourses.add(initializeCourse3());
        mCourses.add(initializeCourse4());
    }

    public void initializeExampleNotes() {
        final ShoppingListDataManager dm = getInstance();

        ShoplistInfo course = dm.getCourse("android_intents");
        course.getModule("android_intents_m01").setComplete(true);
        course.getModule("android_intents_m02").setComplete(true);
        course.getModule("android_intents_m03").setComplete(true);
//        mNotes.add(new ItemInfo(course, "Dynamic intent resolution",
//                "Wow, intents allow components to be resolved at runtime"));
//        mNotes.add(new ItemInfo(course, "Delegating intents",
//                "PendingIntents are powerful; they delegate much more than just a component invocation"));

        course = dm.getCourse("android_async");
        course.getModule("android_async_m01").setComplete(true);
        course.getModule("android_async_m02").setComplete(true);
//        mNotes.add(new ItemInfo(course, "Service default threads",
//                "Did you know that by default an Android Service will tie up the UI thread?"));
//        mNotes.add(new ItemInfo(course, "Long running operations",
//                "Foreground Services can be tied to a notification icon"));

        course = dm.getCourse("java_lang");
        course.getModule("java_lang_m01").setComplete(true);
        course.getModule("java_lang_m02").setComplete(true);
        course.getModule("java_lang_m03").setComplete(true);
        course.getModule("java_lang_m04").setComplete(true);
        course.getModule("java_lang_m05").setComplete(true);
        course.getModule("java_lang_m06").setComplete(true);
        course.getModule("java_lang_m07").setComplete(true);
//        mNotes.add(new ItemInfo(course, "Parameters",
//                "Leverage variable-length parameter lists"));
//        mNotes.add(new ItemInfo(course, "Anonymous classes",
//                "Anonymous classes simplify implementing one-use types"));

        course = dm.getCourse("java_core");
        course.getModule("java_core_m01").setComplete(true);
        course.getModule("java_core_m02").setComplete(true);
        course.getModule("java_core_m03").setComplete(true);
//        mNotes.add(new ItemInfo(course, "Compiler options",
//                "The -jar option isn't compatible with with the -cp option"));
//        mNotes.add(new ItemInfo(course, "Serialization",
//                "Remember to include SerialVersionUID to assure version compatibility"));
    }

    private ShoplistInfo initializeCourse1() {
        List<ProductInfo> modules = new ArrayList<>();
        modules.add(new ProductInfo("android_intents_m01", "Android Late Binding and Intents"));
        modules.add(new ProductInfo("android_intents_m02", "Component activation with intents"));
        modules.add(new ProductInfo("android_intents_m03", "Delegation and Callbacks through PendingIntents"));
        modules.add(new ProductInfo("android_intents_m04", "IntentFilter data tests"));
        modules.add(new ProductInfo("android_intents_m05", "Working with Platform Features Through Intents"));

       // return new ShoplistInfo("android_intents", "Android Programming with Intents", modules);
        
        return new ShoplistInfo("android_intents", "Android Programming with Intents", modules);
    }

    private ShoplistInfo initializeCourse2() {
        List<ProductInfo> modules = new ArrayList<>();
        modules.add(new ProductInfo("android_async_m01", "Challenges to a responsive user experience"));
        modules.add(new ProductInfo("android_async_m02", "Implementing long-running operations as a service"));
        modules.add(new ProductInfo("android_async_m03", "Service lifecycle management"));
        modules.add(new ProductInfo("android_async_m04", "Interacting with services"));

        return new ShoplistInfo("android_async", "Android Async Programming and Services", modules);
    }

    private ShoplistInfo initializeCourse3() {
        List<ProductInfo> modules = new ArrayList<>();
        modules.add(new ProductInfo("java_lang_m01", "Introduction and Setting up Your Environment"));
        modules.add(new ProductInfo("java_lang_m02", "Creating a Simple App"));
        modules.add(new ProductInfo("java_lang_m03", "Variables, Data Types, and Math Operators"));
        modules.add(new ProductInfo("java_lang_m04", "Conditional Logic, Looping, and Arrays"));
        modules.add(new ProductInfo("java_lang_m05", "Representing Complex Types with Classes"));
        modules.add(new ProductInfo("java_lang_m06", "Class Initializers and Constructors"));
        modules.add(new ProductInfo("java_lang_m07", "A Closer Look at Parameters"));
        modules.add(new ProductInfo("java_lang_m08", "Class Inheritance"));
        modules.add(new ProductInfo("java_lang_m09", "More About Data Types"));
        modules.add(new ProductInfo("java_lang_m10", "Exceptions and Error Handling"));
        modules.add(new ProductInfo("java_lang_m11", "Working with Packages"));
        modules.add(new ProductInfo("java_lang_m12", "Creating Abstract Relationships with Interfaces"));
        modules.add(new ProductInfo("java_lang_m13", "Static Members, Nested Types, and Anonymous Classes"));

        return new ShoplistInfo("java_lang", "Java Fundamentals: The Java Language", modules);
    }

    private ShoplistInfo initializeCourse4() {
        List<ProductInfo> modules = new ArrayList<>();
        modules.add(new ProductInfo("java_core_m01", "Introduction"));
        modules.add(new ProductInfo("java_core_m02", "Input and Output with Streams and Files"));
        modules.add(new ProductInfo("java_core_m03", "String Formatting and Regular Expressions"));
        modules.add(new ProductInfo("java_core_m04", "Working with Collections"));
        modules.add(new ProductInfo("java_core_m05", "Controlling App Execution and Environment"));
        modules.add(new ProductInfo("java_core_m06", "Capturing Application Activity with the Java Log System"));
        modules.add(new ProductInfo("java_core_m07", "Multithreading and Concurrency"));
        modules.add(new ProductInfo("java_core_m08", "Runtime Type Information and Reflection"));
        modules.add(new ProductInfo("java_core_m09", "Adding Type Metadata with Annotations"));
        modules.add(new ProductInfo("java_core_m10", "Persisting Objects with Serialization"));

        return new ShoplistInfo("java_core", "Java Fundamentals: The Core Platform", modules);
    }
    //endregion

}
