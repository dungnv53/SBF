package com.littlewing.sbf.app;

/**
 * Created by dungnv on 12/10/14.
 */
public class SBFGame {

    public SBFGame() {
        super();
    }

    public int heroMove(int deltaX, int x, int y, int scr_width, int scr_height, Donald donald, int m_snow_fire) {
        if(y < scr_height*3/4) { donald.setDonaldX(donald.getDonaldX() + deltaX); }
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


}
