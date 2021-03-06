package com.littlewing.sbf.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import android.media.SoundPool;
import android.media.AudioManager;
import android.view.WindowManager;

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
    private int use_special = 0;	// integer for store 3 type of special follow the mana degree
    Resources mRes;

    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;

    private static final String KEY_X = "h_x";
    private static final String KEY_Y = "h_y";

    /** The drawable to use as the background of the animation canvas */
    private Bitmap mBackgroundImage;

    // ui
    private Bitmap mUi;
    private Bitmap mBg2;
    private Bitmap mFire;
    private Bitmap mMove;

    /** Message handler used by thread to interact with TextView */
    private Handler mHandler;

    /** What to draw for the title game in its normal state */
    private Bitmap mTitleImage;

    /** Used to figure out elapsed time between frames */
    private  long mLastTime;

    /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
    private  int mMode;

    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = true;

    /** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;

    private int scr_width  = 720; //getWidth();         // width of game screen
    private int scr_height = 1280; //getHeight();	    // height of game screen
    private int x_bound = 32;        // biên ngang cho màn hình game
    private int y_bound = 80;        // biên trên dưới cho màn hình game.

    // game scene or game screen instead

    // vị trí ngang của hero.
    //(scr_height/2-y_bound);   // vị trí dưới của hero (player).
    private Sprite Hero;

    private int m_snow_fire = 0;

    private Sprite donald;
    private Sprite[] luie = new Sprite[3];
    Bomb bomb;
    // Vi tri bat dau nem cua boss.
    private Bitmap allclear;

    private int[] test_snow_e_y = new int[] {70, 70, 70};

    private int win_sound_flag = 0;
    private int lose_flag_sound = 0;

    private Bitmap v;

    private Context myContext; // Them vao nhu mContext ben SBF class. fix me

    private SoundManager sound = new SoundManager(myContext);
//    private  int soundIds[] = new int[12];
//    private  SoundPool mpx = sound.getmSoundPool();

    // start, play, running, lose are the states we use
    public int mState;

    private SBFGame sbf = new SBFGame();

    public SBFThread() {
        super();
    }

    public SBFThread(Context context) {
        super();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.scr_width = size.x;
        this.scr_height = size.y;

        this.Hero = new Sprite((scr_width/2) - x_bound, scr_height*2/3+120); // Nen de align 150 theo bottom ui
    }

    public SBFThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        // get handles to some important objects
        mSurfaceHolder = surfaceHolder;
        mHandler = handler;
        myContext = context;

        // set lai screen size
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.scr_width = size.x;
        this.scr_height = size.y;

        this.Hero = new Sprite((scr_width/2) - x_bound, scr_height*2/3);

        Resources res = context.getResources();
        mHeroMoving = sbf.loadHero(mHeroMoving, myContext); // TODO create a method handle all load sprite with number of img

        mEnemy = sbf.loadEnemy(mEnemy, myContext);
        mBoss = sbf.loadBoss(mBoss, myContext);

        double zoom_lvl = 1.5;
        snow_h = BitmapFactory.decodeResource(res, R.drawable.item3_0);
        snow_h =  Bitmap.createScaledBitmap(snow_h, (int) (snow_h.getWidth() * zoom_lvl), (int) (snow_h.getHeight() * zoom_lvl), true);
        snow_shadow = BitmapFactory.decodeResource(res, R.drawable.shadow0_3);
        snow_shadow = Bitmap.createScaledBitmap(snow_shadow, (int) (snow_shadow.getWidth() * zoom_lvl), (int) (snow_shadow.getHeight() * zoom_lvl), true);

        // Mana hero cung voi ban tay = 2 PNG
        img_Special[0] = BitmapFactory.decodeResource(res, R.drawable.special0_3);
        img_Special[1] = BitmapFactory.decodeResource(res, R.drawable.special1_3);
        img_Special[2] = BitmapFactory.decodeResource(res, R.drawable.special2_3);

        // sp la 3 special tuy vao MANA, co the cho vo mang;
        img_sp1 = BitmapFactory.decodeResource(res, R.drawable.sp1_3);
        img_sp2 = BitmapFactory.decodeResource(res, R.drawable.sp2_3);
        img_sp3 = BitmapFactory.decodeResource(res, R.drawable.sp3_3);

        item = sbf.loadItem(item, myContext);

        // cache handles to our key sprites & other drawables
        mTitleImage = BitmapFactory.decodeResource(res, R.drawable.title_bg_hori);
        mTitleImage = Bitmap.createScaledBitmap(mTitleImage, scr_width, (int) (scr_height / 2), true);

        // load background image as a Bitmap instead of a Drawable b/c
        // we don't need to transform it and it's faster to draw this way
        mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg);
        mUi = BitmapFactory.decodeResource(res, R.drawable.ui);
        mUi = Bitmap.createScaledBitmap(mUi, scr_width, 110 * scr_height / 1280, true); // Ti le man hinh
        mBg2 = BitmapFactory.decodeResource(res, R.drawable.bg_2);
        mBg2 = Bitmap.createScaledBitmap(mBg2, scr_width, 148 * scr_height / 1280, true);

        mFire = BitmapFactory.decodeResource(res, R.drawable.snow);
        mMove = BitmapFactory.decodeResource(res, R.drawable.move);

        allclear = BitmapFactory.decodeResource(res, R.drawable.allclear);
        allclear = Bitmap.createScaledBitmap(allclear, scr_width, (int)(scr_height/2), true);

        v = BitmapFactory.decodeResource(res, R.drawable.v);

        Bomb donald_bomd = new Bomb(350, 350); // hard code
        donald = new Sprite(350, 350, mBoss);
        donald.setBomb(donald_bomd);
        for(int kk = 0; kk < 3; kk ++) {
            luie[kk] = new Sprite (60*kk, kk*25 + 200, mEnemy); // hard code
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
        // Store the current time values.
        long time1 = System.currentTimeMillis();
        long time2;

        while (mRun) {
            try {
                // This is your target delta. 25ms = 40fps
                Thread.sleep(25);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            time2 = System.currentTimeMillis(); // Get current time
            int delta = (int) (time2 - time1); // Calculate how long it's been since last update

            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);

                synchronized (mSurfaceHolder) {
                }
                try{
                    updatePhysics();
                    doDraw(c, delta);
//                    if (mMode == STATE_RUNNING) {
//                        doDrawRunning(c);
//                    } else if(mMode == STATE_WIN) {
//                        doDrawWinning(c);
//                    }
//                    else {
//                        // TODO
//                    }
                    Thread.sleep(23); // Stune 1 it
                }catch(InterruptedException e){
                    System.out.println("got interrupted!");
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }  // hay bi thread end

            time1 = time2; // Update our time variables.
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
                map.putDouble(KEY_X, Double.valueOf(Hero.getPosX()));
                map.putDouble(KEY_Y, Double.valueOf(Hero.getPosY()));
            }
        }
        return map;
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

    /**
     * returns the current int value of game state as defined by state
     * tracking constants
     *
     * @return
     */
    public int getGameState() {
        synchronized (mSurfaceHolder) {
            return mState;
        }
    }

    /** Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
            // don't forget to resize the background image
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
            // ui TODO
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
    public void hitTarget(Sprite dn, int fire_step, int hp_lose) {
        int mLeft = Hero.getPosX() + 20; // vi snow lech ra 1 chut
        if ((dn.getBomb().getX()+20 >= mLeft) && (dn.getBomb().getX() - 10) <= mLeft) {
            if( (dn.getBomb().getY()+10 >= (180 - 12*fire_step)) && (dn.getBomb().getY()-20) <= (180 - 12*fire_step)) {
                Log.e(this.getClass().getName(), " Good Hit Lose hp: " +dn.getHp());
                dn.setHp(dn.getHp() - hp_lose);  // use loseHp
                m_snow_fire = 0;
            }
        }
    }

    /* Draw rectangle | show hp (percent) like in other game: AOE (horizontal).
     * TODO nhieu so hardcode, cho qua SBF game cho nhe
     * effects and item character using.
    * */
    public void drawEnemy(Canvas cv, Sprite huey, int snow_e_y_idx, int rand_donald_y, int h_y_step) {
//        Log.e(this.getClass().getName(), " Fuck u: ");
        test_snow_e_y [snow_e_y_idx] += 6;
        if(test_snow_e_y[snow_e_y_idx] >= scr_height*2/3) {
            // luie stop acceleration
            test_snow_e_y[snow_e_y_idx] = 300; // hard code
            huey.getBomb().setPower(0);
        }

        // TODO cho shadow va snow tach xa dan theo time
        // them gia toc chut it cho snow bay xa nhanh dan
        huey.getBomb().fireTarget(new Point(huey.getBomb().getX(), huey.getBomb().getY()), new Point(Hero.getPosX(), Hero.getPosY()));
        huey.getBomb().fireTarget(new Point(huey.getBomb().getX(), test_snow_e_y[snow_e_y_idx]-22 + rand_donald_y), new Point(Hero.getPosX(), Hero.getPosY()));
        cv.drawBitmap(snow_h, huey.getBomb().getX(), test_snow_e_y[snow_e_y_idx]-22 + rand_donald_y, null); // 22 la distance giua snow va shadow
        cv.drawBitmap(snow_shadow, huey.getBomb().getX(), test_snow_e_y[snow_e_y_idx], null);

        if ((Hero.getPosX() - 8) <= huey.getPosX() && ((Hero.getPosX() + 8) >= huey.getPosX())
                && (Hero.getPosY() - h_y_step <= test_snow_e_y[snow_e_y_idx]) && (Hero.getPosY() + h_y_step >= test_snow_e_y[snow_e_y_idx])) {
//            Hero.setHp(Hero.getHp()-2); // hero lose HP ?
        }
        if ( (Hero.getBomb().getX() <= (huey.getPosX() + 8)) && (Hero.getBomb().getX() >= (huey.getPosX() - 22))) {
            if ( (Hero.getBomb().getX() <= (huey.getPosY() + 30)) && (Hero.getBomb().getY() >= (huey.getPosY() - 10))) {
                Log.e(this.getClass().getName(), " Enemy Lose hp: " +huey.getHp());
                huey.loseHp(12); // fix me
                Hero.getBomb().setY(scr_height*2/3); // fai set lai snow_y ko thi hp mat lien tuc
            }
        }
    }
    /**
     * Draws the hero, enemy, and background to the provided
     * Mau cua enemy van chua chuan: Hp giam lien tuc nhu kieu loop neu trung dan nhieu
     * GameState reload -> hp cua enemy cung full trog khi hp dang < full.
     * De test -> truoc tien giam nhanh hp de test win state
     * TODO tách ra các hàm con như bên Snake, BoxJump: doDrawRunning, doDrawReady ...
     * Trong doDrawRunning cũng chia nhỏ handle cho bên SBFGame như BJ
     */
    public void doDraw(Canvas canvas, int delta) {
        if (mMode == STATE_RUNNING) { //state
//            doDrawRunning(canvas);
            try {
                doDrawRunning(canvas);
            } catch(NullPointerException null_p_e) {
                System.exit(1);
            } finally {
                doDrawRunning(canvas);
            }

        } // end running draw

        else if(mMode == STATE_LOSE) {
            doDrawLose(canvas);
//            	canvas.restore();
        } else  if(mMode == STATE_WIN) {
            Log.e(this.getClass().getName(), " Teemo on duty: Victory!");
            doDrawWinning(canvas);
//            	canvas.restore();
        } else if (mMode == STATE_PAUSE) {
            canvas.save();
        } else {
            canvas.drawBitmap(mTitleImage, 0, scr_height/4, null);
        }
    }      // end doDraw()

    public void doDrawRunning(Canvas canvas) {
        // TODO doDrawRunning()
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
        canvas.drawBitmap(mUi, 0, scr_height-mUi.getHeight(), null);
        canvas.drawBitmap(mBg2, 0, 0, null);
        canvas.drawBitmap(mFire, scr_width - 142, scr_height - (110 * scr_height / 1280) - 70*2, null); // hard code , snow size is 68x68
        // fire button abow ui about 80px (~fire size)

        canvas.drawBitmap(mMove, 72, scr_height - (110 * scr_height / 1280) - 70*2, null); // hard code , snow size is 66x72

//            boss_attack(canvas);
//            draw_enemy(canvas);
        donald.act(1, scr_width, scr_height);
        donald.move();
        Paint paint = sbf.newPaint(Color.RED, Paint.Style.FILL_AND_STROKE, 0);

        canvas.drawBitmap(mHeroMoving[Hero.getIdx()], Hero.getPosX(), Hero.getPosY(), null);

        if (donald.getHp() > 0) {
            drawBossHp(donald, canvas, paint);
        }
        for(int ii = 0; ii < 3; ii ++) {
            luie[ii].act(1, scr_width, scr_height);
            luie[ii].move();
            luie[ii].setBomb(bomb);
            luie[ii].getBomb().dropBomb(luie[ii].getPosY(), 3/4*scr_height);

            if (luie[ii].getHp() > 0) {
                drawHpEnemy(luie[ii], canvas, paint); // TODO how to push this method to Sprite class
            }
        }
        // Draw the speed gauge, with a two-tone effect
//        canvas.save();

        if (m_snow_fire == 10) {
            int mTop = (scr_height-y_bound);      // Vị trí phía trên màn hình game đối với player (hero).
            Hero.getBomb().throwDownY(12);
            Hero.setIdx(2);  // hero throwing
            if (Hero.getBomb().getY() < 25) Hero.getBomb().setY((scr_height-y_bound));
            canvas.drawBitmap(bomb.getImage(2), Hero.getPosX() + 22, Hero.getBomb().getY()-22, null); // 22 la distance giua snow va shadow
            canvas.drawBitmap(snow_shadow, Hero.getPosX() + 22, Hero.getBomb().getY(), null);

            for (int ii = 0; ii <= (mTop/12); ii ++) {
                hitTarget(donald, ii, 100);
                hitTarget(luie[0], ii, 100);
                hitTarget(luie[1], ii, 100);
                hitTarget(luie[2], ii, 100); // fix me

                m_snow_fire = 0;
            } // end for loop

            if ( (Hero.getBomb().getX() <= (donald.getPosX() + 80)) && (Hero.getBomb().getX() >= (donald.getPosX() - 80))) { // fix me 8 and 22
                Log.e(this.getClass().getName(), " Ring the bell: " +donald.getPosX() + " y " + donald.getPosY());
                if ( (Hero.getBomb().getX() <= (donald.getPosY() + 30)) && (Hero.getBomb().getY() >= (donald.getPosY() - 10))) {
                    Log.e(this.getClass().getName(), " Donald Lose hp: " +donald.getHp());
                    donald.loseHp(12); // fix me
                    Hero.getBomb().setY(scr_height*2/3); // fai set lai snow_y ko thi hp mat lien tuc
                }
            }
        }

        if (donald.getHp() <= 0) {  // WINNING
            if (luie[0].getHp() <= 0 && (luie[1].getHp() <= 0) && (luie[2].getHp() <= 0)) {
    //                    canvas.drawColor(Color.BLACK);
    //                    mMode = STATE_WIN;
                Log.e(this.getClass().getName(), " Teemo on duty: Victory! ARAM 1");
                doDrawWinning(canvas);
            }
        }
        // Boss attack
        if (donald.getHp() >= 0) {
            enemy_attack(canvas);
        }

        if (Hero.getHp() <= 0) {   // hero die
            mMode = STATE_LOSE;
        }

        // cause can not call e_attack_ai() propertly
        if(luie[0].getHp() > 0) {
            drawEnemy(canvas, luie[0], 0, sbf.get_random1(8), 18);
        }
        if(luie[1].getHp() > 0) {
            drawEnemy(canvas, luie[1], 1, sbf.get_random1(8), 10);
        }
        if(luie[2].getHp() > 0) {
            drawEnemy(canvas, luie[2], 2, 0, 8);
        }
//            	draw special
        if (use_special == 1) {
            use_special(canvas);
        }
//        canvas.save();
    }
    public void doDrawPlay(Canvas canvas) {

    }
    public void doDrawReady(Canvas canvas) {

    }
    public void doDrawWinning(Canvas canvas) {
    //    win_sound_flag++; if(win_sound_flag ==1) {
    //        mpx.play(soundIds[9], 1, 1, 1, 0, 1);
    //    } else {
    //        mpx.stop(soundIds[9]);
    //    }
        canvas.drawColor(Color.WHITE);
        String text = "Victory !... \n";
        Paint p = sbf.newPaint(Color.RED, null, 35);
        String text2 = "Acquired 32 golds.";

    //        try {
    //            if(canvas != null) {
    //            }
    //        } catch(NullPointerException e) {
    //            Intent intent = new Intent(Intent.ACTION_MAIN);
    //            intent.addCategory(Intent.CATEGORY_HOME);
    //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //            myContext.startActivity(intent);
    //        }
        try {
            canvas.save();
            canvas.drawBitmap(allclear, 0, 150, null);
            canvas.drawBitmap(mHeroMoving[5], (scr_width/2-x_bound), (scr_height/2-y_bound), null);
            canvas.drawBitmap(v, (scr_width/2-x_bound+18), (scr_height/2-y_bound-22), null);
            canvas.drawText(text, (scr_width/2-x_bound), (scr_height/2 - 20), p);
            canvas.drawText(text2, (scr_width / 2 - x_bound), (scr_height / 2), p);
        } catch (NullPointerException e) {
            System.exit(1);
        }
    }

    public void doDrawLose(Canvas canvas) {
        lose_flag_sound ++;

        if (lose_flag_sound  == 1) {
//                soundIds[10] = mpx.play(soundIds[10], 1, 1, 1, 0, 1);
        } else {
//                if(mpx != null) mpx.stop(soundIds[10]);
        }
        canvas.drawBitmap(mHeroMoving[6], Hero.getPosX(), Hero.getPosY(), null);
        String text = "You lose ...";
        Paint p = new Paint();
        p.setColor(Color.RED);
        canvas.drawText(text, Hero.getPosX() - 5, Hero.getPosY() - 40, p);
    }

    private void setInitialGameState() {

    }
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

    public void enemy_attack(Canvas canvas) {
        donald.getBomb().throwDownY(-6); // throw down minus like throw up
        if (donald.getBomb().getY() >= (scr_height-y_bound)) {    // biên cho item bay tới.
            donald.getBomb().setY(380); // reset bomb position
            donald.getBomb().setPower(0);
        }

//         when donald x,y change, so fireTarget become wrong on start, end ?
//         how to fix only one start, end ?
//         power of fire
        donald.getBomb().setPower(2);
        donald.getBomb().fireTarget(new Point(donald.getBomb().getX(), donald.getBomb().getY()), new Point(Hero.getPosX(), Hero.getPosY()));
        canvas.drawBitmap(snow_h, donald.getBomb().getX() + sbf.get_random1(10), donald.getBomb().getY()-22, null); // 22 la distance giua snow va shadow
        canvas.drawBitmap(snow_shadow, donald.getBomb().getX(), donald.getBomb().getY(), null);
        if ((Hero.getPosX() + 12) >= donald.getPosX() && ((Hero.getPosX() - 12) <= donald.getPosX())
                && (Hero.getPosY() + 12 >= donald.getBomb().getY()) && (Hero.getPosY() - 12 <= donald.getBomb().getY())) {
//            Hero.loseHp(4);
        }
    }

    private void drawHpEnemy(Sprite dn, Canvas cv, Paint paint) {
        cv.drawBitmap(dn.getBossImage(), dn.getPosX(), dn.getPosY(), null);
        cv.drawRect((float) dn.getPosX()-5, dn.getPosY() - (dn.getHp()*4/7) + 30, (float) dn.getPosX(), 30 + dn.getPosY(), paint);
    }

    private void drawBossHp(Sprite donald, Canvas canvas, Paint paint) {
        canvas.drawBitmap(donald.getBossImage(), donald.getPosX(), 310, null); // hard code 310+40 = 350 HP Rect
        canvas.drawRect((float) donald.getPosX()-5, 360 - (donald.getHp()*9/11), (float) donald.getPosX(), 360, paint);
    }

    /**
     * Figures the lander state (x, y, fuel, ...) based on the passage of
     * realtime. Does not invalidate(). Called at the start of draw().
     * Detects the end-of-game and sets the UI to the next state. TODO nghien cuu ham nay co the co ich cho time handler
     */
    private void updatePhysics() {
        long now = System.currentTimeMillis();
        if (mLastTime > now) return;
        mLastTime = now; return;
    }

    public boolean onTouch(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (Hero.getPosX() > 13 && x < Hero.getPosX()) { // tap on left side
                    // fix me
                    // do ham ben kia ko change dc value h_x ... nen can cho ham nay vao Sprite
                    // neu Sprite co roi thi lam cach nao tao 1 Object thay vi h_x hard code.
                    m_snow_fire = sbf.heroMove(-12, x, y, scr_width, scr_height, Hero, m_snow_fire);
                } else if (Hero.getPosX() < (scr_width-x_bound) && x >= Hero.getPosX()) { // tap on right side
                    m_snow_fire = sbf.heroMove(12, x, y, scr_width, scr_height, Hero, m_snow_fire);
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

}
// end class SBF_Thread