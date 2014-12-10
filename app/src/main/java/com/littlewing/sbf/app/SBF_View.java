package com.littlewing.sbf.app;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;


class SBF_View extends SurfaceView implements SurfaceHolder.Callback {

    private  Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private SBFThread thread;


	private MediaPlayer mpx;
    private int mViewWidth = getWidth(); // TODO move to Surface like class
    private int mViewHeight = getHeight();

    public SBF_View(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new SBFThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setText(m.getData().getString("text"));
            }
        });
        try {
			SBFThread.sleep(600);
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
            SBF_View.this.update();
            SBF_View.this.invalidate();
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
    public SBFThread getThread() {
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

    public  void playTitleSound(){
    	mpx = MediaPlayer.create(mContext, R.raw.sbf_win);
	    	mpx.start();
    		mpx.setLooping(false);
    }
    public  void playFiringSound(){
    	mpx = MediaPlayer.create(mContext, R.raw.s_fire);
    	mpx.start();
    }
    public  void playHitTargetSound(){
    	mpx = MediaPlayer.create(mContext, R.raw.s_hit);
    	mpx.start();
    }
    public  void stopSound(){
    	if(mpx != null){
    		mpx.stop();
    		mpx.release();
    		mpx = null;
    	}
    }

    public boolean onTouchEvent(MotionEvent mtEvent) {
        return thread.onTouch(mtEvent);
    }
}

