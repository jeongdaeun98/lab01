package com.example.q.jde3.musicplayer;

import android.content.Context;
import android.widget.MediaController;

public class MusicController extends MediaController {
    public boolean reallyhide;
    public boolean nowshowing = true;

    public MusicController(Context c){
        super(c);
    }

    public void hide(){
        if (reallyhide) {
            reallyhide = false;
            super.hide();
        }
    }

}

