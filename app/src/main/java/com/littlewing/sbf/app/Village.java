package com.littlewing.sbf.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.Canvas;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by dungnv on 8/18/14.
 */
public class Village  extends Activity{
    Button button;
    private Bitmap ville;

    private Village_View vileView;
    private Village_View.VilThread vileThread;

    private Village_View SBF_View;
    private SBF_View.SBFThread villeThread;

    private static final int MENU_PAUSE = 4;
    private static final int MENU_RESUME = 5;
    private static final int MENU_START = 6;
    private static final int MENU_STOP = 7;

    LinearLayout mLinearLayout;

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
                vileThread.doStart();
                return true;
            case MENU_STOP:
                Log.d("in", "stop");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case MENU_PAUSE:
                return true;
            case MENU_RESUME:
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinearLayout = new LinearLayout(this);

        setContentView(R.layout.village);
        addListenerOnButton();

//        mSBFThread = mSBF_View.getThread();

        vileView = (Village_View) findViewById(R.id.villageView);
        vileThread = vileView.getThread();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        ville = Bitmap.createBitmap(720, 720, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ville);
        vileThread.doDraw(canvas);

//        villeThread.doStart();
    }

    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, SBF.class);
                startActivity(intent);

            }

        });

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        vileThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }

}
