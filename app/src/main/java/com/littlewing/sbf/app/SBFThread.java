package com.littlewing.sbf.app;

/**
 * Created by dungnv on 12/10/14.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.Random;
import android.media.SoundPool;
import android.media.AudioManager;

/**
 * View that draws, takes keystrokes, etc. for a simple LL game.
 *
 * Has a mode which RUNNING, PAUSED, etc. Has a x, y, dx, dy, ... capturing the
 * current ship physics. All x/y etc. are measured with (0,0) at the lower left.
 * updatePhysics() advances the physics based on realtime.
 *  draw() renders the
 * ship, and does an invalidate() to prompt another draw() as soon as possible
 * by the system.
 */
class SBFThread extends Thread {
    private Bitmap[] mHeroMoving = new Bitmap[7];
    private Bitmap[] mEnemy = new Bitmap[5];

    private Bitmap[] mBoss = new Bitmap[5];
    private Bitmap[] item = new Bitmap[5];

    private Bitmap snow_h;
    private Bitmap snow_shadow;

    private Bitmap[] img_Special = new Bitmap[4];
    private Bitmap img_sp1;
    private Bitmap img_sp2;
    private Bitmap img_sp3;
    /**
     * dung luu 3 kieu spec tuy vao mana
     * Dung ra fai dung RMS nhung o day tam test cai int nay da.
     *
     */
    private int use_special = 0;	// integer for store 3 type of special follow the mana degree
    Resources mRes;

    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;

    private static final String KEY_X = "h_x";
    private static final String KEY_Y = "h_y";

        /*
         * Member (state) fields
         */
    /** The drawable to use as the background of the animation canvas */
    private Bitmap mBackgroundImage;

    /**
     * Current height of the surface/canvas.
     */
    private int mCanvasHeight = 1;

    /**
     * Current width of the surface/canvas.
     */
    private int mCanvasWidth = 1;

    /** Message handler used by thread to interact with TextView */
    private Handler mHandler;

    /** Pixel height of lander image. */
    private  int mLanderHeight;

    /** What to draw for the title game in its normal state */
    private Bitmap mTitleImage;

    /** Pixel width of lander image. */
    private int mLanderWidth;

    /** Used to figure out elapsed time between frames */
    private  long mLastTime;

    /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
    private  int mMode;

    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = true;

    /** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;

    private int scr_width = 720; //getWidth();         // width of game screen
    private int scr_height = 1280; //getHeight();	    // height of game screen
    private int x_bound = scr_width/12;        // biên ngang cho màn hình game
    private int y_bound = scr_height/8;        // biên trên dưới cho màn hình game.

    private  int h_x = (scr_width/2) - x_bound; // vị trí ngang của hero.
    private  int h_y = 600; //(scr_height/2-y_bound);   // vị trí dưới của hero (player).

    private int mHeroIndex = 0;
    private int m_snow_fire = 0;
    private int e_snow_fired = 0;

    int snow_h_y = (scr_height-y_bound);
    int h_hp = 84; // hp cua hero
    private Random rnd = new Random();

    public static final int GAME_THREAD_DELAY = 600;
    private Donald donald;
    /**
     * Mang donald luie
     */
    private Donald[] luie = new Donald[3];
    Bomb bomb;
    // Vi tri bat dau nem cua boss.
    private int test_snow_h_y = (scr_height-y_bound);

    private Bitmap allclear;

    private int[] test_snow_e_y = new int[] {70, 70, 70};

    private int win_sound_flag = 0;
    private int lose_flag_sound = 0;

    private Bitmap v;

    private Context myContext; // Them vao nhu mContext ben SBF class. fix me
    private MediaPlayer mpx; // music

    private SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

    public SBFThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        // get handles to some important objects
        mSurfaceHolder = surfaceHolder;
        mHandler = handler;
        myContext = context;

        Resources res = context.getResources();

        mHeroMoving[0] = BitmapFactory.decodeResource(res, R.drawable.hero3_0);
        mHeroMoving[1] = BitmapFactory.decodeResource(res, R.drawable.hero3_1);
        mHeroMoving[2] = BitmapFactory.decodeResource(res, R.drawable.hero3_2);
        mHeroMoving[3] = BitmapFactory.decodeResource(res, R.drawable.hero3_3);
        mHeroMoving[4] = BitmapFactory.decodeResource(res, R.drawable.hero3_4);
        mHeroMoving[5] = BitmapFactory.decodeResource(res, R.drawable.hero_vic3);
        mHeroMoving[6] = BitmapFactory.decodeResource(res, R.drawable.hero_lose3);

        mEnemy[0] = BitmapFactory.decodeResource(res, R.drawable.enemy00_3);
        mEnemy[1] = BitmapFactory.decodeResource(res, R.drawable.enemy01_3);
        mEnemy[2] = BitmapFactory.decodeResource(res, R.drawable.enemy02_3);
        mEnemy[3] = BitmapFactory.decodeResource(res, R.drawable.enemy03_3);
        mEnemy[4] = BitmapFactory.decodeResource(res, R.drawable.enemy00_3);

        mBoss[0] = BitmapFactory.decodeResource(res, R.drawable.boss20_3);
        mBoss[1] = BitmapFactory.decodeResource(res, R.drawable.boss21_3);
        mBoss[2] = BitmapFactory.decodeResource(res, R.drawable.boss22_3);
        mBoss[3] = BitmapFactory.decodeResource(res, R.drawable.boss23_3);

        mBoss[4] = BitmapFactory.decodeResource(res, R.drawable.boss20_3);

        snow_h = BitmapFactory.decodeResource(res, R.drawable.item3_0);
        snow_shadow = BitmapFactory.decodeResource(res, R.drawable.shadow0_3);

        // Mana hero cung voi ban tay = 2 PNG
        img_Special[0] = BitmapFactory.decodeResource(res, R.drawable.special0_3);
        img_Special[1] = BitmapFactory.decodeResource(res, R.drawable.special1_3);
        img_Special[2] = BitmapFactory.decodeResource(res, R.drawable.special2_3);

        // sp la 3 special tuy vao MANA, co the cho vo mang;
        img_sp1 = BitmapFactory.decodeResource(res, R.drawable.sp1_3);
        img_sp2 = BitmapFactory.decodeResource(res, R.drawable.sp2_3);
        img_sp3 = BitmapFactory.decodeResource(res, R.drawable.sp3_3);

        item[0] = BitmapFactory.decodeResource(res, R.drawable.item3_0);
        item[1] = BitmapFactory.decodeResource(res, R.drawable.item1_3);
        item[2] = BitmapFactory.decodeResource(res, R.drawable.item2_3);
        item[3] = BitmapFactory.decodeResource(res, R.drawable.item3_3);
        item[4] = BitmapFactory.decodeResource(res, R.drawable.item4_3);

        // cache handles to our key sprites & other drawables
        mTitleImage = BitmapFactory.decodeResource(res, R.drawable.title_bg_hori);
        mTitleImage = Bitmap.createScaledBitmap(mTitleImage, scr_width, (int)(scr_height/2), true);

        // load background image as a Bitmap instead of a Drawable b/c
        // we don't need to transform it and it's faster to draw this way
        mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bck01);

        allclear = BitmapFactory.decodeResource(res, R.drawable.allclear);
        allclear = Bitmap.createScaledBitmap(allclear, scr_width, (int)(scr_height/2), true);

        v = BitmapFactory.decodeResource(res, R.drawable.v);

        // Use the regular lander image as the model size for all sprites
        mLanderWidth = mTitleImage.getWidth();
        mLanderHeight = mTitleImage.getHeight();

        donald = new Donald(50, 50, mBoss);
        for(int kk = 0; kk < 3; kk ++) {
            luie[kk] = new Donald (60*kk, kk*25, mEnemy);
        }
        bomb = new Bomb (50, 30, context);
        bomb.setImage(item);
    }

    /**
     * Starts the game, setting parameters for the current difficulty.
     */
    public  void doStart() {
        synchronized (mSurfaceHolder) {
            mLastTime = System.currentTimeMillis() + 100;
            setState(STATE_RUNNING);
        }
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
    /**
     * Pauses the physics update & animation.
     */
    public  void pause() {
        synchronized (mSurfaceHolder) {
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    /**
     * Restores game state from the indicated Bundle. Typically called when
     * the Activity is being restored after having been previously
     * destroyed.
     *
     * @param savedState Bundle containing the game state
     */
    public synchronized void restoreState(Bundle savedState) {
        synchronized (mSurfaceHolder) {
            setState(STATE_PAUSE);
        }
    }

    @Override
    public void run() {
        while (mRun) {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    if (mMode == STATE_RUNNING) {
                        updatePhysics();
                        doDraw(c);
                        // boss_attack(c);
                    } else {
                        // TODO
                    }
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }  // hay bi thread end
        }
    }

    /**
     * Dump game state to the provided Bundle. Typically called when the
     * Activity is being suspended.
     *
     * @return Bundle with this view's state
     */
    public Bundle saveState(Bundle map) {
        synchronized (mSurfaceHolder) {
            if (map != null) {
                map.putDouble(KEY_X, Double.valueOf(h_x));
                map.putDouble(KEY_Y, Double.valueOf(h_y));
            }
        }
        return map;
    }
    public void setFiring(boolean firing) {
        synchronized (mSurfaceHolder) {
        }
    }
    public void setRunning(boolean b) {
        mRun = b;
    }

    public  void setState(int mode) {
        synchronized (mSurfaceHolder) {
            setState(mode, null);
        }
    }

    /**
     * Sets the game mode. That is, whether we are running, paused, in the
     * failure state, in the victory state, etc.
     * ham nay can fai sua de hop voi SBF thay vi LL
     *
     * @param mode one of the STATE_* constants
     * @param message string to add to screen or null
     */
    public void setState(int mode, CharSequence message) {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             *
             */
        synchronized (mSurfaceHolder) {
            mMode = mode;

            if (mMode == STATE_RUNNING) {
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", "");
                b.putInt("viz", View.INVISIBLE);
                msg.setData(b);
                mHandler.sendMessage(msg);
            } else {
                Resources res = myContext.getResources();
                CharSequence str = "";
                if (mMode == STATE_READY)
                    str = res.getText(R.string.mode_ready);
                else if (mMode == STATE_PAUSE)
                    str = res.getText(R.string.mode_pause);
                else if (mMode == STATE_LOSE)
                    str = res.getText(R.string.mode_lose);
                else if (mMode == STATE_WIN)
                    str = res.getString(R.string.mode_win_prefix)
                            + "Acquired: "
                            + res.getString(R.string.mode_win_suffix);

                if (message != null) {
                    str = message + "\n" + str;
                }

                if (mMode == STATE_LOSE)
                {
                    str = "\n\n" + "You lose!" + "\n Good job!";
                }

                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", str.toString());
                b.putInt("viz", View.VISIBLE);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    }

    /** Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;

            // don't forget to resize the background image
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
        }
    }

    /**
     * Resumes from a pause.
     */
    public void unpause() {
        // Move the real time clock up to now
        synchronized (mSurfaceHolder) {
            mLastTime = System.currentTimeMillis() + 100;
        }
        setState(STATE_RUNNING);
    }

    /* Handles a key-up event. */
    boolean doKeyUp(int keyCode, KeyEvent msg) {
        boolean handled = false;

        synchronized (mSurfaceHolder) {
            if (mMode == STATE_RUNNING) {
                // TODO move to touch event
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    handled = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    handled = true;
                }
                else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_P) {
                    handled = true;
                }
            }
        }
        return handled;
    }

    /*
     * Check hero fire hit enemy, enemy lose hp. fire_step is snow in grid to check hit target.
     * hp_lose is value of hp lose if hero fire hit.
     * mTop: position reset for enemy.
     * TODO: move to class Donlad instead of lie here.
     */
    public void hitTarget(Donald dn, int fire_step, int hp_lose) {
        int mTop = (scr_height-y_bound);      // Vị trí phía trên màn hình game đối với player (hero).
        int mLeft = h_x + 20; // vi snow lech ra 1 chut
        if ((dn.getDonaldX()+20 >= mLeft) && (dn.getDonaldX() - 10) <= mLeft) {
            if( (dn.getDonaldY()+10 >= (180 - 12*fire_step)) && (dn.getDonaldY()-20) <= (180 - 12*fire_step)) {
                dn.setHp(dn.getHp() - hp_lose);
                mTop = 180; // TODO y ?
                m_snow_fire = 0;
            }
        }
    }

    /* Draw rectangle | show hp (percent) like in other game: AOE (horizontal).
     * TODO nhieu so hardcode
    * */
    public void drawEnemy(Canvas cv, Donald huey, int snow_e_y_idx, int rand_donald_y, int h_y_step) {
        test_snow_e_y [snow_e_y_idx] += 6;
        if(test_snow_e_y[snow_e_y_idx] >= 205)
            test_snow_e_y[snow_e_y_idx] = 70;
        cv.drawBitmap(snow_h, huey.getDonaldX(), test_snow_e_y[snow_e_y_idx]-22 + rand_donald_y, null); // 22 la distance giua snow va shadow
        cv.drawBitmap(snow_shadow, huey.getDonaldX(), test_snow_e_y[snow_e_y_idx], null);

        if ((h_x - 8) <= huey.getDonaldX() && ((h_x + 8) >= huey.getDonaldX())
                && (h_y - h_y_step <= test_snow_e_y[snow_e_y_idx]) && (h_y + h_y_step >= test_snow_e_y[snow_e_y_idx])) {
            h_hp -= 2;
        }
        if ( (h_x <= (huey.getDonaldX() + 8)) && (h_x >= (huey.getDonaldX() - 22))) {
            if ( (snow_h_y <= (huey.getDonaldY() + 30)) && (snow_h_y >= (huey.getDonaldY() - 10))) {
                huey.setHp(huey.getHp() - 2);
                snow_h_y = 200; // fai set lai snow_y ko thi hp mat lien tuc
            }
        }
    }
    /**
     * Draws the hero, enemy, and background to the provided
     * Mau cua enemy van chua chuan: Hp giam lien tuc nhu kieu loop neu trung dan nhieu
     * GameState reload -> hp cua enemy cung full trog khi hp dang < full.
     * De test -> truoc tien giam nhanh hp de test win state
     */
    public void doDraw(Canvas canvas) {
        if (mMode == STATE_RUNNING) {
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            boss_attack(canvas);
//            draw_enemy(canvas);
            donald.act(1, scr_width, scr_height);
            donald.move();
            Paint paint = newPaint(Color.RED, Paint.Style.FILL_AND_STROKE, 0);
            // In Running mode

            canvas.drawBitmap(mHeroMoving[mHeroIndex], h_x, h_y, null);

            if (donald.getHp() > 0) {
                canvas.drawBitmap(donald.getBossImage(), donald.getDonaldX(), 85, null);
                canvas.drawRect((float) donald.getDonaldX()-5, 126 - (donald.getHp()*9/11), (float) donald.getDonaldX(), 126, paint);
            }
            for(int ii = 0; ii < 3; ii ++) {
                luie[ii].act(1, scr_width, scr_height);
                luie[ii].move();

                luie[ii].setBomb(bomb);
                luie[ii].item.dropBomb(luie[ii].getDonaldY(), 3/4*scr_height);

                if (luie[ii].getHp() > 0) {
                    canvas.drawBitmap(luie[ii].getBossImage(), luie[ii].getDonaldX(), luie[ii].getDonaldY(), null);
                    canvas.drawRect((float) luie[ii].getDonaldX()-5, luie[ii].getDonaldY() - (luie[ii].getHp()*4/7) + 30, (float) luie[ii].getDonaldX(), 30 + luie[ii].getDonaldY(), paint);
                }
            }
            // Draw the speed gauge, with a two-tone effect
            canvas.save();

            int snow_h_x = h_x + 12;   // snow_h_x va y fai ko dc change du xLeft top change
            if (m_snow_fire == 10) {
//            	make_attack(canvas);
                int mTop = (scr_height-y_bound);      // Vị trí phía trên màn hình game đối với player (hero).
                snow_h_y -= 12;
                if (snow_h_y < 25) snow_h_y = (scr_height-y_bound);
//        		canvas.drawBitmap(snow_h, snow_h_x, snow_h_y-22, null);
                int rand_img = get_random(4);
                canvas.drawBitmap(bomb.getImage(2), h_x + 22, snow_h_y-22, null); // 22 la distance giua snow va shadow
                canvas.drawBitmap(snow_shadow, h_x + 22, snow_h_y, null);

                for (int ii = 0; ii <= (mTop/12); ii ++) {
                    hitTarget(donald, ii, 1);
                    hitTarget(luie[0], ii, 1);
                    hitTarget(luie[1], ii, 1);
                    hitTarget(luie[2], ii, 1);

                    m_snow_fire = 0;
                } // end for loop
            }
            if (donald.getHp() <= 0) {  // WINNING
                if (luie[0].getHp() <= 0 && (luie[1].getHp() <= 0) && (luie[2].getHp() <= 0)) {
                    mMode = STATE_WIN;
                }
            }
            // Boss attack
            if (donald.getHp() >= 0) {
                test_snow_h_y += 6;
                if (test_snow_h_y >= (scr_height-y_bound))    // biên cho item bay tới.
                    test_snow_h_y = 80;

                canvas.drawBitmap(snow_h, donald.getDonaldX() + get_random1(10), test_snow_h_y-22, null); // 22 la distance giua snow va shadow
                canvas.drawBitmap(snow_shadow, donald.getDonaldX(), test_snow_h_y, null);
                if ((h_x + 12) >= donald.getDonaldX() && ((h_x - 12) <= donald.getDonaldX())
                        && (h_y + 12 >= test_snow_h_y) && (h_y - 12 <= test_snow_h_y)) {
                    h_hp -= 4;
                }
            }
            // check snow hit hero
            // Van chua su dung dc tinh nang isDestroyed cua Bomb Class de tranh viec hp giam lien tuc do snow ko huy.

            // hero die ?
            if (h_hp <= 0) {
                mMode = STATE_LOSE;
            }
            // enemy attack
            // cause can not call e_attack_ai() propertly
            if(luie[0].getHp() > 0) {
                drawEnemy(canvas, luie[0], 0, get_random1(8), 18);
            }
            if(luie[1].getHp() > 0) {
                drawEnemy(canvas, luie[1], 1, get_random1(8), 10);
            }
            if(luie[2].getHp() > 0) {
                drawEnemy(canvas, luie[2], 2, 0, 8);
            }
//            	draw special
            if (use_special == 1) {
                use_special(canvas);
//            			for(int ii = 0; ii <=3 ; ii ++) {
//            				luie[ii].setHp(luie[ii].getHp() - 8);
//            			}
//            			donald.setHp(donald.getHp() - 8);

            }
//            	canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.save();

        } // end running draw

        else if(mMode == STATE_LOSE) {
            lose_flag_sound ++;
            mpx = MediaPlayer.create(myContext, R.raw.s_lose);
            if (lose_flag_sound  == 1) {
                mpx.start();
            } else {
                if(mpx != null) mpx.stop();
            }
            canvas.drawBitmap(mHeroMoving[6], h_x, h_y, null);
            String text = "You lose ...";
            Paint p = new Paint();
            p.setColor(Color.RED);
            canvas.drawText(text, h_x - 5, h_y - 40, p);
//            	canvas.restore();
        } else  if(mMode == STATE_WIN) {
            win_sound_flag++; if(win_sound_flag ==1) { mpx.start(); } else { stopSound(); };

            String text = "Victory !... \n";
            Paint p = newPaint(Color.RED, null, 35);
            String text2 = "Acquired 32 golds.";

            try {
                if(canvas != null) {
                    canvas.save();
                    canvas.drawBitmap(allclear, 0, 30, null);
                    canvas.drawBitmap(mHeroMoving[5], (scr_width/2-x_bound), (scr_height/2-y_bound), null);
                    canvas.drawBitmap(v, (scr_width/2-x_bound+18), (scr_height/2-y_bound-22), null);
                    canvas.drawText(text, (scr_width/2-x_bound), (scr_height/2 - 20), p);
                    canvas.drawText(text2, (scr_width/2-x_bound), (scr_height/2), p);
                }
            } catch(NullPointerException e) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myContext.startActivity(intent);
            }

//            	canvas.restore();
        } else if (mMode == STATE_PAUSE) {
            canvas.save();
        } else {
            canvas.drawBitmap(mTitleImage, 0, scr_height/4, null);
        }
        canvas.save();
        canvas.restore();
    }      // end doDraw()

    /** ve special bay ngang man hinh roi quay lai 1 lan roi bien mat; */
    public void use_special(Canvas canvas) {
//        	playHitTargetSound();
        int sp_Left = 200;
        int sp_Top = 100;
        for (int i = 0; i < 200; i++) {
            sp_Left --;
            canvas.drawBitmap(img_sp3, i, sp_Top, null); // if (sp_Left >= 200) use_special = 0;
        }
        if (sp_Left <= 1) {
            for (int j = 200; j > 0; j--) {
                canvas.drawBitmap(img_sp2, j, sp_Top, null);
            }
            use_special = 0;
        }
    }

    /** draw snow ball from hero x, y to the target
     1st we make target length is random n then use the POWER later
     */
    public void make_attack(Canvas canvas) {
//        	playFiringSound();
        int yTop = mCanvasHeight - ((int) h_y + mLanderHeight);
        int xLeft = ((int) h_x - mLanderWidth / 2) + 20;
        for (int ii = 0; ii <= (yTop/20); ii ++)
        {
            canvas.drawBitmap(snow_h, xLeft, 180 - (20*ii), null);
            canvas.drawBitmap(snow_shadow, xLeft, 180 -(20*ii) + 22, null);
        }
        // if (yTop < 25) ...
        m_snow_fire = 0;
    }

    public void e_attack(Canvas canvas, int targetX, int targetY) {
        // dat co flag de chi cho enemy nem tuyet khi snow da ve vi tri dich
        // co se dc bat lai khi snow trung dich hay roi xuong
        for (int ii = 0; ii <= (targetY/20); ii ++)
        {
            canvas.drawBitmap(snow_h, targetX,  (20*ii), null);
            canvas.drawBitmap(snow_shadow, targetX, (20*ii) + 22, null);
        }
        // Test draw snow gap
        canvas.drawBitmap(snow_h, targetX += 3, targetX += 3, null);
    }
    public void e_attack_ai(Canvas canvas, int enemy_x, int enemy_y, int hero_x, int hero_y) {
        int delta_x = (int)(Math.abs(enemy_x - hero_x));
        int delta_y = (int)(Math.abs(enemy_y - hero_y)); // tinh duong cheo tao boi hero va enemy
        int c = (int) (Math.sqrt(delta_x*delta_x + delta_y*delta_y));
        // Tinh Delta X, Delta Y de ve duong dan cho enemy nem vao hero
        int d_x = 12 * delta_x / c; // d_x de cho snow bay lech theo phuong ngang
        int d_y = 12 * delta_y / c;
        int snow_e_xx = enemy_x;  // de ve trog canvas
        int snow_e_yy = enemy_y;
        if(e_snow_fired == 10) {
            snow_e_xx += d_x;
            snow_e_yy += d_y;

            canvas.drawBitmap(snow_h, snow_e_xx, snow_e_yy-22, null); // 22 la distance giua snow va shadow
            canvas.drawBitmap(snow_shadow, snow_e_xx, snow_e_yy, null);
        }
        if ( snow_e_yy > 205 ){
            e_snow_fired = 0;
        }
        e_snow_fired = 10;
    }
    /**
     * Them ham boss_attack() voi random target, sound play
     * test hp ... va vi tri nem
     * ko run co le do chi dc goi 1 lan
     * Neu dat vao ham run() chay chung voi doDraw() thi lam game speed nhanh gap co 2x
     * -> tam thoi cho chay trong ham doDraw()
     */
    public void boss_attack(Canvas canvas) {
        int width = scr_width;     // game screen width
        int height = scr_height;   // game screen height
        int h_bound = height/6;     // bound head n bottom of the screen
        int step = height/15;       // step of snowball over screen

        if (donald.getHp() >= 0) {
            test_snow_h_y += step;
            if (test_snow_h_y >= (height - h_bound))     // enemy snow pass hero position
                test_snow_h_y = h_bound/2;
//				playFiringSound();
//				playHitTargetSound();
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.drawBitmap(snow_h, donald.getDonaldX() + step, test_snow_h_y-(step/2), null); // 22 la distance giua snow va shadow
            canvas.drawBitmap(snow_shadow, donald.getDonaldX() + step, test_snow_h_y, null);
//				SBF_View.this.invalidate();
        }
    }
    /**
     * Figures the lander state (x, y, fuel, ...) based on the passage of
     * realtime. Does not invalidate(). Called at the start of draw().
     * Detects the end-of-game and sets the UI to the next state. TODO nghien cuu ham nay co the co ich cho time handler
     */
    private void updatePhysics() {
        long now = System.currentTimeMillis();
        if (mLastTime > now) return;
        mLastTime = now;                    return;
    }

    public boolean onTouch(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (h_x > 13 && x < h_x) { // tap on left side
                    heroMove(-12, x, y);
                } else if (h_x < (scr_width-x_bound) && x >= h_x) { // tap on right side
                    heroMove(12, x, y);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return true;
    }
    public void heroMove(int deltaX, int x, int y) {
        if(y < scr_height*3/4) { h_x += deltaX; }
        if(x < scr_width && (x > scr_width*3/4)) {
            if(y < scr_height && (y > scr_height*3/4)) { m_snow_fire = 10; }
        }
        if (mHeroIndex < 2) {
            mHeroIndex ++;
            if (mHeroIndex == 2)  mHeroIndex = 0;
            else if (mHeroIndex == 0)  mHeroIndex = 1;
        } else {
            mHeroIndex = 1;
            mHeroIndex ++;
            if (mHeroIndex == 2)  mHeroIndex = 0;
            else if (mHeroIndex == 0)  mHeroIndex = 1;
        }
    }

    public  void stopSound() { // TODO move to soundPool
        if(mpx != null) {
            mpx.stop();
            mpx.release();
            mpx = null;
        }
    }

    // Create sound effect for game
    private void loadSoundPool(Context mContext) {
        int soundIds[] = new int[10]; // 10 sound
        soundIds[0] = sp.load(mContext, R.raw.night, 1); // remember smaller 1MB
        soundIds[1] = sp.load(mContext, R.raw.one, 2);
        soundIds[2] = sp.load(mContext, R.raw.night, 3);
        soundIds[3] = sp.load(mContext, R.raw.three, 4);
        soundIds[4] = sp.load(mContext, R.raw.four, 5);
        soundIds[5] = sp.load(mContext, R.raw.five, 6);
        soundIds[6] = sp.load(mContext, R.raw.six, 7);
        soundIds[7] = sp.load(mContext, R.raw.s_fire, 8);
        soundIds[8] = sp.load(mContext, R.raw.s_hit, 9);
        soundIds[9] = sp.load(mContext, R.raw.night, 1);

        // use
        //sp.play(soundIds[0], 1, 1, 1, 0, 1.0);
        /*
            soundID a soundID returned by the load() function

            leftVolume left volume value (range = 0.0 to 1.0)

            rightVolume right volume value (range = 0.0 to 1.0)

            priority stream priority (0 = lowest priority)

            loop loop mode (0 = no loop, -1 = loop forever)

            rate playback rate (1.0 = normal playback, range 0.5 to 2.0)
         */
    }

}
// end class SBF_Thread