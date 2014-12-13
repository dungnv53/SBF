package com.littlewing.sbf.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class SBF extends Activity implements View.OnClickListener {
	
	private static final int MENU_PAUSE = 4;
	
	private static final int MENU_RESUME = 5;
	
	private static final int MENU_START = 6;
	
	private static final int MENU_STOP = 7;

    /** A handle to the thread that's actually running the animation. */
    private SBFThread mSBFThread;

    /** A handle to the View in which the game is running. */
    private SBF_View mSBF_View;

    public void SBF() {
        mSBFThread = new SBFThread();
    }

    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     *
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        menu.add(0, MENU_START, 0, R.string.menu_start);
//        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
//        menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
//        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
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
                mSBFThread.doStart();
                return true;
            case MENU_STOP:
                mSBFThread.setState(SBFThread.STATE_LOSE, getText(R.string.message_stopped));
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            case MENU_PAUSE:
                mSBFThread.pause();
                return true;
            case MENU_RESUME:
                mSBFThread.unpause();
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
        setContentView(R.layout.activity_sbf);

        Button start = (Button) findViewById(R.id.gogo);
        start.setOnClickListener(this);

        // get handles to the SBF_View from XML, and its SBFThread
        mSBF_View = (SBF_View) findViewById(R.id.sbf);
        mSBFThread = mSBF_View.getThread();

        // give the SBF_View a handle to the TextView used for messages
        mSBF_View.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mSBFThread.setState(SBFThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mSBFThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
    }

    public void startSBFActivity() {
        mSBFThread.doStart();
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSBF_View.getThread().pause(); // pause game when Activity pauses
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
        mSBFThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }


    /**
     * Handles component interaction
     *
     * @param v The object which has been clicked
     */
    public void onClick(View v) {
        // this is the first screen
        // TODO change button state follow gamestate like AA
            mSBFThread.setState(mSBFThread.STATE_RUNNING);
    }
}
