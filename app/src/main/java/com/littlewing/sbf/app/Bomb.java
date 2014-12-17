package com.littlewing.sbf.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Bomb {
    private int bom_x;
    private int bom_y;
    private final String bomb = "spacepix/bomb1.png";
    private boolean destroyed;
    Bitmap bom_pic[] = new Bitmap[5];
    private int b_idx;			// index for bomb image

    private int damage;		// the hp that enemy lose
    private int speed; // speed of fire
    private int power; // power of fire
    private int gap; // snow_gap

    private int acceleration;

    public Bomb(int x, int y, Context context) {
        setDestroyed(true);
        this.bom_x = x;
        this.bom_y = y;

        Resources res = context.getResources();
        bom_pic[0] = BitmapFactory.decodeResource(res, R.drawable.item3_0);
        bom_pic[1] = BitmapFactory.decodeResource(res, R.drawable.item1_3);
        bom_pic[2] = BitmapFactory.decodeResource(res, R.drawable.item2_3);
        bom_pic[3] = BitmapFactory.decodeResource(res, R.drawable.item3_3);
        bom_pic[4] = BitmapFactory.decodeResource(res, R.drawable.item4_3);
    }
    public Bomb(int paramInt1, int paramInt2) {
        this.bom_x = paramInt1;
        this.bom_y = paramInt2;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
    public void setX(int paramInt) {
        this.bom_x = paramInt;
    }
    public void setY(int paramInt) {
        this.bom_y = paramInt;
    }
    public int getX() {
        return this.bom_x;
    }
    public int getY() {
        return this.bom_y;
    }
    public int getItemIdx() {
        return this.b_idx;
    }
    public void setItemIdx(int paramInt) {
        this.b_idx = paramInt;
    }

    public void setPower(int power) {
        this.power = power;
    }
    public int getPower() {
        return this.power;
    }

    public void dropBomb(int paramInt1, int paramInt2) {
        while (this.bom_y < paramInt2)
        {
                this.bom_x = paramInt1;
                this.bom_y += 12;
                // need set_y ?
                if (this.bom_y >= paramInt2)
                    this.destroyed = true;
        } // ; cug dc ma ko cung dc, y?
    }

    public Bitmap getImage(int i) { // chua cho id
        if(i < 5 && i >= 0)
            return this.bom_pic[i];
        else
            return this.bom_pic[0];
    }

    public void setImage(Bitmap[] paramBitmap) {
        this.bom_pic = paramBitmap;
    }

    // item fly toward
    // TODO need Point target, from, speed, destroy item when out
    public void throwing() {

    }

    // thowing item down
    public void throwDownY(int step) {
        this.bom_y -= step;
    }

    public void throwDownX(int step) {
        this.bom_x -= step;
    }

    // fire at enemy
    // maybe boolean return
    public void fireTarget(Point start, Point dest) {
        int step = 12; // 12 px step
        this.acceleration += power;
        // do somethg
        float angle = getRadAngle(start, dest);
        // acceleration bomb
        this.bom_x += (step+this.acceleration)*Math.tan(angle);
        this.bom_y += (step+this.acceleration)/Math.tan(angle);
    }


    // Helper method
    public float getAngle(Point start, Point target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - start.y, target.x - start.x));

        if(angle < 0){
            angle += 360;
        }

        return angle; // Return angle or radian
    }

    public float getRadAngle(Point start, Point target) {
        float angle = (float) Math.atan2(target.y - start.y, target.x - start.x);

        if(angle < 0){
            angle += 2*Math.PI;
        }

        return angle; // Return angle or radian
    }
}