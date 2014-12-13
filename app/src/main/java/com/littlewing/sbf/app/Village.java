package com.littlewing.sbf.app;

import com.littlewing.sbf.app.Village_View.VillageThread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;

public class Village extends Activity {

    private static final int MENU_PAUSE = 4;

    private static final int MENU_RESUME = 5;

    private static final int MENU_START = 6;

    private static final int MENU_STOP = 7;

    /** A handle to the thread that's actually running the animation. */
    private VillageThread mVillageThread;

    /** A handle to the View in which the game is running. */
    private Village_View mVillage_View;

    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     *
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
        //    menu.add(0, MENU_EASY, 0, R.string.menu_easy);
        //    menu.add(0, MENU_MEDIUM, 0, R.string.menu_medium);
        //    menu.add(0, MENU_HARD, 0, R.string.menu_hard);
        return true;
    }

    /**
     * Invoked when the user selects an item from the Menu.
     *
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                mVillageThread.doStart();
                return true;
            case MENU_STOP:
                mVillageThread.setState(VillageThread.STATE_LOSE, getText(R.string.message_stopped));
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            case MENU_PAUSE:
                mVillageThread.pause();
                return true;
            case MENU_RESUME:
                mVillageThread.unpause();
                return true;
        }
        return false;
    }

    /**
     * Invoked when the Activity is created.
     *
     * @param savedInstanceState a Bundle containing state saved from a previous
     *        execution, or null if this is a new execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.village);

        // get handles to the Village_View from XML, and its VillageThread
        mVillage_View = (Village_View) findViewById(R.id.vil);
        mVillageThread = mVillage_View.getThread();

        // give the Village_View a handle to the TextView used for messages
        mVillage_View.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mVillageThread.setState(VillageThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mVillageThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
    }

    public void startSBFActivity(View view) {
        Intent i = new Intent(Village.this, SBF.class);
        startActivity(i);
//        Intent intent = new Intent(SBF.this, SBF.class);
//        startActivity(intent);
//        finish();
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mVillage_View.getThread().pause(); // pause game when Activity pauses
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        mVillageThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
}
