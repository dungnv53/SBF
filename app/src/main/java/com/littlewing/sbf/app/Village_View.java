package com.littlewing.sbf.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

/**
 * Created by nickfarrow on 8/21/14.
 */
class Village_View extends SurfaceView implements SurfaceHolder.Callback {
    public static final int EASTERN_BOY = 1;
    public static final int SOUTHERN_BOY = 2;
    public static final int WESTERN_BOY = 3;
    public static final int NORTHEN_BOY = 4;
    public static final int ITEM_SHOP = 5;
    public static final int DRUG_STORE = 6;

    Paint p;

    class VilThread extends Thread {
        private Bitmap[] mHeroMoving = new Bitmap[7];
        private Bitmap mBackgroundImage;

        private Handler mHandler;

        /** What to draw for the title game in its normal state */
        private Bitmap mTitleImage;

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

        public static final int GAME_THREAD_DELAY = 600;

        private Bitmap allclear;
        private Bitmap heroImg;

        public VilThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            Resources res = context.getResources();

            mHeroMoving[0] = BitmapFactory.decodeResource(res, R.drawable.hero3_0);
            mHeroMoving[6] = BitmapFactory.decodeResource(res, R.drawable.hero_lose3);

            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bck01);

            allclear = BitmapFactory.decodeResource(res, R.drawable.allclear);
            allclear = Bitmap.createScaledBitmap(allclear, scr_width, (int)(scr_height/2), true);

            heroImg = BitmapFactory.decodeResource(res, R.drawable.hero_icon3);

        }

        public Paint newPaint(int clr, Paint.Style stl, int txtSize) {
            Paint paint = new Paint();
            if(clr != 0) { paint.setColor(clr); }
            if(stl != null) { paint.setStyle(stl); }
            if(txtSize > 0) { paint.setTextSize(txtSize); }
            return paint;
        }

        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
            }
        }

        @Override
        public void run() {
//            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
//                            if (mMode == )  updatePhysics(); doDraw(c);
                        doDraw(c);
//                        boss_attack(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                //}  // hay bi thread end
            }
        }

        public  void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        public void setState(int mode, CharSequence message) {
            synchronized (mSurfaceHolder) {
                mMode = mode;

                if (mMode == EASTERN_BOY) {
                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("text", "");
                    b.putInt("viz", View.INVISIBLE);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                } else {
                }
            }
        }

        public void doDraw(Canvas canvas) {
            canvas.drawBitmap(heroImg, 200, 200, null);
            Log.d("in ", " draw");

//            if(mMode == WESTERN_BOY) {
                canvas.drawBitmap(mHeroMoving[6], h_x, h_y, null);
                String text = "You lose ...";
                Paint p = new Paint();
                p.setColor(Color.RED);
                canvas.drawText(text, h_x - 5, h_y - 40, p);
                canvas.save();
                canvas.restore();
//            } else
                if(mMode == SOUTHERN_BOY) {

            } else if (mMode == EASTERN_BOY) {
                canvas.save();
            } else {
            }
            canvas.save();
            canvas.restore();
        }      // end doDraw()

        public boolean onTouch(MotionEvent event) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            Log.d("x= ", x + " touch event");
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

        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                if (map != null) {
                    Log.d("in", " save state");
                }
            }
            return map;
        }

        public  void doStart() {
            Log.d("in do ", " start");
            synchronized (mSurfaceHolder) {
                Log.d("in do ", " start");
            }
        }

    }
    // end class SBF_Thread

    private  Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private VilThread thread;


    public Village_View(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new VilThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setText(m.getData().getString("text"));
            }
        });
        try {
            VilThread.sleep(600);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setFocusable(true); // make sure we get key events
    }

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
//                VilThread.this.update();
//                VilThread.this.invalidate();
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
    public VilThread getThread() {
        return thread;
    }

    @Override
    public boolean onTouchEvent(MotionEvent mtEvent) {
        return thread.onTouch(mtEvent);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
    }
    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p=new Paint();
        Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.hero_icon3);
        p.setColor(Color.RED);
        canvas.drawBitmap(b, 200, 200, p);
    }
}