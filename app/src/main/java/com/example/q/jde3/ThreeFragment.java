package com.example.q.jde3;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;

import com.example.q.jde3.musicplayer.MusicController;
import com.example.q.jde3.musicplayer.MusicService;
import com.example.q.jde3.musicplayer.MusicService.MusicBinder;
import com.example.q.jde3.musicplayer.Song;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;


import com.example.q.jde3.R;


public class ThreeFragment extends Fragment implements MediaPlayerControl{

    public MusicController controller;
    private boolean paused=false;
    public boolean MusicStarted = false;
    protected boolean playbackPaused=false;

    public MusicService musicSrv;
    protected Intent playIntent;
    public boolean musicBound=false;
    private View viewforuse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        //    init();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("ThirdFrag", "Oncreateview");
        // Inflate the layout for this fragment
        viewforuse = inflater.inflate(R.layout.fragment_three, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return viewforuse;
        init();

        getSongList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        Log.i("ThirdFrag", "ThreeFragment onCreateView is called");
        SongAdapter songAdt = new SongAdapter(getContext(), songList);
        songView.setAdapter(songAdt);
        if (controller == null) {
            Log.i("onCreateView", "and setController is called");
            setController();

        }
        return viewforuse;
    }

    void init(){
        Log.i("init", "starting init");
        songList = new ArrayList<Song>();
        if (viewforuse==null)
            Log.i("init", "viewforuse is no longer there");

        songView = (ListView) viewforuse.findViewById(R.id.song_list);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Sets the respective song in the Service, and then plays it.

                musicSrv.setSong(position);
                musicSrv.playSong();

                // Sets the flag to false for the controller's duration and position purposes.
                if (playbackPaused) playbackPaused = false;
                else Log.i("init", "playback was not paused");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onStop() {
        controller.hide();
        super.onStop();
    }

    protected void setController(){
        Log.i("setController", "and setController is called");
        //set the controller up
        if (controller == null)
            controller = new MusicController(getContext());


        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(viewforuse.findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            //setController();
            playbackPaused=false;
        }
        //controller.show(0);
    }

    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            //setController();
            playbackPaused=false;
        }
        //controller.show(0);
    }


   public ThreeFragment() {
        // Required empty public constructor
    }

    private ArrayList<Song> songList;
    private ListView songView;


    //connect to the service

    ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ThreeFragment", "onServiceConnected");
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ThreeFragment", "onServiceDisconnected");
            musicBound = false;
        }
    };




    public void getSongList() {
        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);


        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    public void onDestroy() {

        musicSrv=null;
        if (musicBound) {
            Log.i("onDestroy", "musicBound, so we unbind");
            getContext().unbindService(musicConnection); // blogger missed this
        }
        getContext().stopService(playIntent);
        super.onDestroy();
    }



    @Override
    public void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(paused){
            //Log.i("onResume", "and setController is called");
            //setController();
            paused=false;
        }
    }



    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        Log.i("pause","playbackPaused is true");
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        try {
            if (musicSrv != null && musicBound && musicSrv.isPng())
                return musicSrv.getDur();
            else return 0;
        }
            catch (Exception e){}
        return 0;
    }

    @Override
    public int getCurrentPosition() {

        if(musicSrv!=null)
            try {
                if (musicBound && musicSrv.isPng())
                    return musicSrv.getPosn();
            }
            catch (Exception e){
                Log.i("ThreeFragment", "getCurrentPosition exception");
            }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            try {
                return musicSrv.isPng();
            }
            catch (Exception e){}
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
