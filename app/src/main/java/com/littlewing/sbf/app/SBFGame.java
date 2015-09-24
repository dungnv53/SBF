package com.littlewing.sbf.app;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.content.Context;

import java.util.Random;

/**
 * Created by dungnv on 12/10/14.
 */
public class SBFGame {
    private Random rnd = new Random();

    public SBFGame() {
        super();
    }

    public int heroMove(int deltaX, int x, int y, int scr_width, int scr_height, Sprite donald, int m_snow_fire) {
        if(y < scr_height*3/4) { donald.setPosX(donald.getPosX() + deltaX); }
        if(x < scr_width && (x > scr_width*3/4)) {
            if(y < scr_height && (y > scr_height*3/4)) { m_snow_fire = 10; }
        }
        if (donald.getIdx() < 2) {
            donald.setIdx(donald.getIdx()+1);
            if (donald.getIdx() == 2)  donald.setIdx(0);
            else if (donald.getIdx() == 0)  donald.setIdx(1);
        } else {
            donald.setIdx(1);
            donald.setIdx(donald.getIdx() +1);
            if (donald.getIdx() == 2)  donald.setIdx(0);
            else if (donald.getIdx() == 0)  donald.setIdx(1);
        }

        return m_snow_fire; // wtf TODO
    }


    public Paint newPaint(int clr, Paint.Style stl, int txtSize) {
        Paint paint = new Paint();
        if(clr != 0) { paint.setColor(clr); }
        if(stl != null) { paint.setStyle(stl); }
        if(txtSize > 0) { paint.setTextSize(txtSize); }
        return paint;
    }
    public int get_random(int paramInt) {
        int i = this.rnd.nextInt() % paramInt;
        return (i < 0) ? -i : i;
    }
    public int get_random1(int paramInt) {
        int i = this.rnd.nextInt() % paramInt;
        return (i<0) ? -5 : i;
    }

    // Load hero sprite images. TODO move to a method handle all
    public Bitmap[] loadHero(Bitmap mBmp[], Context mContext) {
        Resources res = mContext.getResources();

        mBmp[0] = BitmapFactory.decodeResource(res, R.drawable.hero3_0);
        mBmp[1] = BitmapFactory.decodeResource(res, R.drawable.hero3_1);
        mBmp[2] = BitmapFactory.decodeResource(res, R.drawable.hero3_2);
        mBmp[3] = BitmapFactory.decodeResource(res, R.drawable.hero3_3);
        mBmp[4] = BitmapFactory.decodeResource(res, R.drawable.hero3_4);
        mBmp[5] = BitmapFactory.decodeResource(res, R.drawable.hero_vic3);
        mBmp[6] = BitmapFactory.decodeResource(res, R.drawable.hero_lose3);

        return mBmp;
    }

    public Bitmap[] loadEnemy(Bitmap mBmp[], Context mContext) {
        Resources res = mContext.getResources();

        mBmp[0] = BitmapFactory.decodeResource(res, R.drawable.enemy00_3);
        mBmp[1] = BitmapFactory.decodeResource(res, R.drawable.enemy01_3);
        mBmp[2] = BitmapFactory.decodeResource(res, R.drawable.enemy02_3);
        mBmp[3] = BitmapFactory.decodeResource(res, R.drawable.enemy03_3);
        mBmp[4] = BitmapFactory.decodeResource(res, R.drawable.enemy00_3);

        return mBmp;
    }

    public Bitmap[] loadBoss(Bitmap mBmp[], Context mContext) {
        Resources res = mContext.getResources();

        mBmp[0] = BitmapFactory.decodeResource(res, R.drawable.boss20_3);
        mBmp[1] = BitmapFactory.decodeResource(res, R.drawable.boss21_3);
        mBmp[2] = BitmapFactory.decodeResource(res, R.drawable.boss22_3);
        mBmp[3] = BitmapFactory.decodeResource(res, R.drawable.boss23_3);
        mBmp[4] = BitmapFactory.decodeResource(res, R.drawable.boss20_3);

        return mBmp;
    }

    public Bitmap[] loadItem(Bitmap mBmp[], Context mContext) {
        Resources res = mContext.getResources();

        mBmp[0] = BitmapFactory.decodeResource(res, R.drawable.item3_0);
        mBmp[1] = BitmapFactory.decodeResource(res, R.drawable.item1_3);
        mBmp[2] = BitmapFactory.decodeResource(res, R.drawable.item2_3);
        mBmp[3] = BitmapFactory.decodeResource(res, R.drawable.item3_3);
        mBmp[4] = BitmapFactory.decodeResource(res, R.drawable.item4_3);

        return mBmp;
    }


}
