package com.example.q.jde3.musicplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

import com.example.q.jde3.MainActivity;
import com.example.q.jde3.R;
import com.example.q.jde3.ThreeFragment;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener
{

    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();

    private String songTitle="";
    private static final int NOTIFY_ID=1;

    public boolean actuallydie;

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        if(player!=null && !player.isPlaying()){
            //player = MediaPlayer.create(getApplicationContext(), R.raw.subhanallah);
            playSong();
        }

        else {
            player = new MediaPlayer();

            initMusicPlayer();}

    }


    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }


    public void playSong(){
        //play a song
        player.reset();
        MainActivity.setStart();
        //get song
        Song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        songTitle=playSong.getTitle();
        player.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public boolean onUnbind(Intent intent){

        return false;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
    if(player.getCurrentPosition()>0){
        mp.reset();
        playNext();
    }
}

    @Override
    public void onDestroy() {
        Log.i("MusicService", "onDestroy is called");
        player.stop();
        player.release();
        stopForeground(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, 0);

        Notification.Builder builder = new Notification.Builder(this);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Music is playing";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);


        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("Playing").setContentText(songTitle);
        Notification not = builder.build();



        mChannel.setSound(null, null);
        mChannel.enableVibration(false);

        startForeground(NOTIFY_ID, not);
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        notificationManager.createNotificationChannel(mChannel);

        notificationManager.notify(NOTIFY_ID, not);
        */
        mp.start();
        MainActivity.showController();


    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();

    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){

        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0)
            songPosn=songs.size()-1;
        playSong();
    }

    public void playNext(){
        songPosn++;
        if(songPosn>=songs.size())
            songPosn=0;
        playSong();
    }
}
