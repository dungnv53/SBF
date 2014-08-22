package com.littlewing.sbf.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.Canvas;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.village);
        addListenerOnButton();

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

}
