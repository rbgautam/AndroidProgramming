package us.forgeinnovations.deltaman.ui;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.*;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import us.forgeinnovations.deltaman.notes.DataManager;
import us.forgeinnovations.deltaman.notes.NoteInfo;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.pressBack;
import static org.hamcrest.Matchers.*;

/**
 * Created by deltamanpro on 12/5/17.
 */
public class NextThroughNotesTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void nextThroughNotes() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.list_menu_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        for (int index = 0; index < notes.size(); index++) {

            NoteInfo note = notes.get(index);


            onView(withId(R.id.spinner_shoppingtype)).check(
                    matches(withSpinnerText(note.getCourse().getTitle()))
            );

            onView(withId(R.id.editText_itemname)).check(
                    matches(withText(note.getTitle()))
            );

            onView(withId(R.id.editText_itemdesc)).check(
                    matches(withText(note.getText()))
            );

            if(index < notes.size()-1)
                onView(allOf(withId(R.id.action_next),isEnabled())).perform(click());
        }
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();
    }

}