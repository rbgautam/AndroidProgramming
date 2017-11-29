package us.forgeinnovations.deltaman.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import us.forgeinnovations.deltaman.notes.CourseInfo;
import us.forgeinnovations.deltaman.notes.DataManager;

import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.pressBack;
import static org.hamcrest.Matchers.*;
/**
 * Created by RGautam on 11/28/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ShoppingListActivityTest {
    static DataManager sDataManager;
    @BeforeClass
    public static void clasSetUp() throws Exception{
        sDataManager =  DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<ShoppingListActivity> mShoppingListActivityRule = new ActivityTestRule<ShoppingListActivity>(ShoppingListActivity.class);

    @Test
    public void CreateNewNote()
    {
        final CourseInfo course = sDataManager.getCourse("java_lang");
        final String itemTitle = "Note text title";
        final String itemDesc = "This the description for the product";
        //Getting reference to the Fab button
        ViewInteraction fabNewNote = onView(withId(R.id.fab));
        fabNewNote.perform(click()); //Takes us to the ShoppingActivity

        onView(withId(R.id.spinner_shoppingtype)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class),equalTo(course))).perform(click());

        onView(withId(R.id.editText_itemname)).perform(typeText(itemTitle));
        onView(withId(R.id.editText_itemdesc)).perform(typeText(itemDesc),closeSoftKeyboard());

        pressBack();

    }
}