package us.forgeinnovations.deltaman.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;


/**
 * Created by RGautam on 11/28/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ShoppingListActivityTest {
    @Rule
    public ActivityTestRule<ShoppingListActivity> mShoppingListActivityRule = new ActivityTestRule<ShoppingListActivity>(ShoppingListActivity.class);

    @Test
    public void CreateNewNote()
    {
        //Getting reference to the Fab button
        ViewInteraction fabNewNote = onView(withId(R.id.fab));
        fabNewNote.perform(click()); //Takes us to the ShoppingActivity

        onView(withId(R.id.editText_itemname)).perform(typeText("Note text title"));
        onView(withId(R.id.editText_itemdesc)).perform(typeText("This the description for the product"),closeSoftKeyboard());

    }
}