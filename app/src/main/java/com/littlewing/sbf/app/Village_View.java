package com.littlewing.sbf.app;

import java.util.Random;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;


class Village_View extends SurfaceView implements SurfaceHolder.Callback {
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
    class VillageThread extends Thread {
        private Bitmap[] mHeroMoving = new Bitmap[7];
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

        public static final int LEFT = 6;
        public static final int RIGHT = 7;
        public static final int UP = 8;
        public static final int DOWN = 9;

        public static final int DRUG_STORE = 11;
        public static final int ITEM_SHOP = 12;
        public static final int EASTERN_BOY = 13;
        public static final int SOUTHERN_BOY = 14;
        public static final int WESTERN_BOY = 15;
        public static final int NORTHERN_BOY = 16;

        private static final String KEY_X = "h_x";
        private static final String KEY_Y = "h_y";

        /*
         * Member (state) fields
         */
        /** The drawable to use as the background of the animation canvas */
        private Bitmap mBackgroundImage;

        /** Message handler used by thread to interact with TextView */
        private  Handler mHandler;

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
        private  SurfaceHolder mSurfaceHolder;

        private int scr_width = 720; //getWidth();         // width of game screen
        private int scr_height = 1280; //getHeight();	    // height of game screen
        private int x_bound = scr_width/12;        // biên ngang cho màn hình game
        private int y_bound = scr_height/8;        // biên trên dưới cho màn hình game.

        private  int h_x = (scr_width/2) - x_bound; // vị trí ngang của hero.
        private  int h_y = 600; //(scr_height/2-y_bound);   // vị trí dưới của hero (player).

        private int mHeroIndex = 0;

        private Random rnd = new Random();

        public static final int GAME_THREAD_DELAY = 600;

        private Bitmap allclear;


        private Bitmap v;

        public VillageThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            Resources res = context.getResources();

            mHeroMoving[0] = BitmapFactory.decodeResource(res, R.drawable.hero3_0);
            mHeroMoving[1] = BitmapFactory.decodeResource(res, R.drawable.hero3_1);
            mHeroMoving[2] = BitmapFactory.decodeResource(res, R.drawable.hero3_2);
            mHeroMoving[3] = BitmapFactory.decodeResource(res, R.drawable.hero3_3);
            mHeroMoving[4] = BitmapFactory.decodeResource(res, R.drawable.hero3_4);
            mHeroMoving[5] = BitmapFactory.decodeResource(res, R.drawable.hero_vic3);
            mHeroMoving[6] = BitmapFactory.decodeResource(res, R.drawable.hero_lose3);

            // cache handles to our key sprites & other drawables
            mTitleImage = BitmapFactory.decodeResource(res, R.drawable.title_bg_hori);
            mTitleImage = Bitmap.createScaledBitmap(mTitleImage, scr_width, (int)(scr_height/2), true);

            // load background image as a Bitmap instead of a Drawable b/c
            // we don't need to transform it and it's faster to draw this way
            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.village2);
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, scr_width, (int)(scr_height/2), true);

            allclear = BitmapFactory.decodeResource(res, R.drawable.allclear);
            allclear = Bitmap.createScaledBitmap(allclear, scr_width, (int)(scr_height/2), true);

            v = BitmapFactory.decodeResource(res, R.drawable.v);

            // Use the regular lander image as the model size for all sprites
            mLanderWidth = mTitleImage.getWidth();
            mLanderHeight = mTitleImage.getHeight();
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

        public Paint newPaint(int clr, Style stl, int txtSize) {
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
                        if (mMode == STATE_RUNNING)  updatePhysics(); doDraw(c);
//                        boss_attack(c);
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
                    Resources res = mContext.getResources();
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

        /**
         * Draws the hero, enemy, and background to the provided
         * Mau cua enemy van chua chuan: Hp giam lien tuc nhu kieu loop neu trung dan nhieu
         * GameState reload -> hp cua enemy cung full trog khi hp dang < full.
         * De test -> truoc tien giam nhanh hp de test win state
         */
        public void doDraw(Canvas canvas) {
            if (mMode == STATE_RUNNING) {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);
                Paint paint = newPaint(Color.RED, Style.FILL_AND_STROKE, 0);
                // In Running mode

                canvas.drawBitmap(mHeroMoving[mHeroIndex], h_x, h_y, null);

                int mTop = (scr_height-y_bound);      // Vị trí phía trên màn hình game đối với player (hero).
                canvas.save();

            } // end running draw

            else if(mMode == STATE_LOSE) {
                canvas.drawBitmap(mHeroMoving[6], h_x, h_y, null);
                String text = "You lose ...";
                Paint p = new Paint();
                p.setColor(Color.RED);
                canvas.drawText(text, h_x - 5, h_y - 40, p);
//            	canvas.restore();
            } else  if(mMode == STATE_WIN) {
                String text = "Victory !... \n";
                Paint p = newPaint(Color.RED, null, 35);
                canvas.drawBitmap(allclear, 0, 30, null);
                canvas.drawBitmap(mHeroMoving[5], (scr_width/2-x_bound), (scr_height/2-y_bound), null);
                canvas.drawBitmap(v, (scr_width/2-x_bound+18), (scr_height/2-y_bound-22), null);
                canvas.drawText(text, (scr_width/2-x_bound), (scr_height/2 - 20), p);
                String text2 = "Southern Boys challenged you!!";
                canvas.drawText(text2, (scr_width/2-x_bound), (scr_height/2), p);
                canvas.save();
//            	canvas.restore();
            } else if (mMode == STATE_PAUSE) {
                canvas.save();
            } else {
                canvas.drawBitmap(mTitleImage, 0, getHeight()/4, null);
            }
            canvas.save();
            canvas.restore();
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
                    if (inRange(x, 200, 150) && inRange(y, 1000, 200)) { // tap on left side
                        heroMove(12, LEFT);
                    } else if (inRange(x, 0, 150) && inRange(y, 1000, 200)) { // tap on right side
                        heroMove(12, RIGHT);
                    } else if (inRange(x, 100, 180) && inRange(y, 800, 100)) { // up
                        heroMove(12, UP);
                    } else if (inRange(x, 100, 180) && inRange(y, 960, 150)) { // down
                        heroMove(12, DOWN);
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
        // Move hero by direction and shift_delta
        public void heroMove(int delta, int dir) {
            switch(dir) {
                case LEFT:
                    if (inRange(h_x, 150, 420) && inRange(h_y, 360, 250)) { // tap on left side
                        h_x -= delta;
                    } else {
                        h_x = 560;
                    }
                    break;
                case RIGHT:
                    if (inRange(h_x, 150, 420) && inRange(h_y, 360, 250)) { // tap on left side
                        h_x += delta;
                    } else {
                        h_x = 150;
                    }
                    break;
                case UP:
                    if (inRange(h_x, 150, 420) && inRange(h_y, 360, 250)) { // tap on left side
                        h_y -= delta;
                    } else {
                        h_y = 590;
                    }
                    break;
                case DOWN:
                    if (inRange(h_x, 150, 420) && inRange(h_y, 360, 250)) { // tap on left side
                        h_y += delta;
                    } else {
                        h_y = 360;
                    }
                    break;
                default:
                    break;
            }

        }

        // Check x is in range from to from+range
        public boolean inRange(int x, int from, int range) {
            if((x >= from) && (x <= (from+range))) {
                return true;
            } else {
                return false;
            }
        }
        // Simulate hero motion by sprite image
        public void heroMotion() {
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

    }
    // end class Village_Thread

    private  Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private VillageThread thread;


    private MediaPlayer mpx;

    public Village_View(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new VillageThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setText(m.getData().getString("text"));
            }
        });
        try {
            VillageThread.sleep(600);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setFocusable(true); // make sure we get key events

        mpx = MediaPlayer.create(context, R.raw.night);
        mpx.start();
    }

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Village_View.this.update();
            Village_View.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    public void update() {
        mRedrawHandler.sleep(600);
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public VillageThread getThread() {
        return thread;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return thread.doKeyUp(keyCode, msg);
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    /**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean onTouchEvent(MotionEvent mtEvent) {
        return thread.onTouch(mtEvent);
    }
}

