package com.littlewing.sbf.app;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {
//	private  SoundPool mSoundPool;
//	private  HashMap<Integer, Integer> mSoundPoolMap;
//	private  AudioManager  mAudioManager;
	private  Context mContext;

    private MediaPlayer mpx; // music
    private SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

	public SoundManager(Context theContext) {
	    mContext = theContext;
//	    mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//	    mSoundPoolMap = new HashMap<Integer, Integer>();
//	    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

//        this.loadSoundPool(theContext);
	}

	public SoundManager() {

	}
	public void addSound(int index, int SoundID) {
//	    mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
	}

	public void playSound(int index) {
//	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
//	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//
//	    mSoundPool.play((Integer) mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
	}

	public void playLoopedSound(int index) {
//	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//
//	    mSoundPool.play((Integer) mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
	}

    public  void stopSound(MediaPlayer mp) { // TODO move to soundPool
        if(mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    // Create sound effect for game
    public void loadSoundPool(Context mContext) {
        int soundIds[] = new int[12]; // 10 sound
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
        soundIds[10] = sp.load(mContext, R.raw.s_lose, 1);
        soundIds[11] = sp.load(mContext, R.raw.s_hit, 1);

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

    public SoundPool getmSoundPool() {
        return this.sp;
    }
}