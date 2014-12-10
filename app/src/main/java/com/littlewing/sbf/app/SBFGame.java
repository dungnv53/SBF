package com.littlewing.sbf.app;

/**
 * Created by dungnv on 12/10/14.
 */
public class SBFGame {

    public SBFGame() {
        super();
    }

    public void heroMove(int deltaX, int x, int y, int scr_width, int scr_height, int hero_x, int m_snow_fire, int mHeroIndex) {
        if(y < scr_height*3/4) { hero_x += deltaX; }
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


}
