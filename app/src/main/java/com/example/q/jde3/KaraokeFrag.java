package com.example.q.jde3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class KaraokeFrag extends Fragment {
    String FavoritesFile = "FavoritesFile";
    ArrayList<KaraokeSong> Favorites;
    View viewforuse;
    KaraokeListAdapter Adapter;

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        if (data!=null) {
            String ID = data.getStringExtra("NAMA_JEFF");
            Log.i("NAMA_JEFF", "was " + ID);
            AddWithID(ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewforuse= inflater.inflate(R.layout.karaoke_layout, container, false);
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED )
            return viewforuse;


        LoadFile();

        initList();

        final FloatingActionButton fab1 = (FloatingActionButton) viewforuse.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(KaraokeFrag.this.getActivity());
                View mView = getLayoutInflater().inflate(R.layout.karaokesearch_layout, null);
                View SearchButton = mView.findViewById(R.id.SearchButton);
                final EditText QueryText = mView.findViewById(R.id.QueryText);
                final boolean[] true_if_song = {true};

                Switch mySwitch = mView.findViewById(R.id.SearchSwitch);

                mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            // the search mode is on artist mode
                            true_if_song[0] = false;
                            QueryText.setHint("가수명으로...");
                        }
                        else {
                            // the search mode is on song mode
                            true_if_song[0] = true;
                            QueryText.setHint("노래 제목으로...");
                        }
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog mEditCategoryDialog = mBuilder.create();
                mEditCategoryDialog.show();

                SearchButton.setOnClickListener(
                        new Button.OnClickListener(){
                            public void onClick(View vv){
                                String thing;
                                if (QueryText.getText().toString().length()< 2){
                                    Toast.makeText(getContext(), "Search query should be at least 2 chars", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    if (true_if_song[0])
                                        thing = "song";
                                    else
                                        thing = "artist";
                                    //Toast.makeText(getContext(), "Should then search " + QueryText.getText().toString() + " as a " + thing, Toast.LENGTH_SHORT).show();
                                    ShowSearchActivity(QueryText.getText().toString(), true_if_song[0]);
                                    mEditCategoryDialog.dismiss();
                                }

                            }
                        });

            }
        });

        return viewforuse;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveFile();
    }

    public class KaraokeSong{
        public String Title, Artist, ID, TimesSung;

        public String getTitle(){
            return this.Title;
        }
        public String getArtist(){
            return this.Artist;
        }
        public String getID(){
            return this.ID;
        }
        public String getTimesSung(){
            return this.TimesSung;
        }

    }

    ArrayList<KaraokeSong> ParseDataToSongList (String datastring)
    {
        ArrayList<KaraokeSong> ListToReturn = new ArrayList<>();
        if (datastring == "")
            return ListToReturn;
        for (String tok: datastring.split(";")){
            String songID, songtimes;
            songID = tok.split(",")[0];
            songtimes = tok.split(",")[1];
            KaraokeSong SongThing = new KaraokeSong();
            SongThing.ID = songID;
            SongThing.TimesSung = songtimes;
            UpdateWithID(SongThing);
            ListToReturn.add(SongThing);
            }
        return ListToReturn;

    }


    public void UpdateWithID(final KaraokeSong updateme){
        final String URL_String = "https://api.manana.kr/karaoke/no/" + updateme.ID + "/tj.json";
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Log.i("favorites list", "I'm goign to check data for song at " + URL_String);
                    int temp =0;
                    JSONArray JasonA = readJsonFromUrl(URL_String);
                    JSONObject Jason = JasonA.getJSONObject(temp);
                    while (Integer.parseInt(Jason.getString("no")) != Integer.parseInt(updateme.ID)){
                        Log.i("Hey yall","" +Jason.getString("no") +" and "+ updateme.ID);
                        temp += 1;
                        Jason = readJsonFromUrl(URL_String).getJSONObject(temp);
                    }

                    updateme.Title = Jason.getString("title");
                    Log.i("favorites list", "I just updated the thing so that title is now " + updateme.Title);
                    updateme.Artist = Jason.getString("singer");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            ProgressDialog mdialog = new ProgressDialog(getContext());
            mdialog.setMessage("Retreiving data...");
            mdialog.show();
            thread.join();
            mdialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray thearray = new JSONArray(jsonText);
            return thearray;
        } finally {
            is.close();
        }
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public void initList(){
        if (Favorites != null) {
            ListView KaraokeListView = (ListView) viewforuse.findViewById(R.id.KaraokeListView);
            Adapter = new KaraokeListAdapter(getActivity().getApplication(), Favorites);
            KaraokeListView.setAdapter(Adapter);
            KaraokeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               final int pos, long id) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("Delete song");
                    alertDialogBuilder.setMessage(((TextView) arg1.findViewById(R.id.song_artist)).getText() + "의 " + ((TextView) arg1.findViewById(R.id.song_title)).getText() + "을(를) 지우겠습니까?");

                    alertDialogBuilder.setPositiveButton("Delete",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getContext(), "" + Favorites.get(pos).getTitle() + " by " + Favorites.get(pos).getArtist() + " was deleted", Toast.LENGTH_SHORT).show();
                                    Favorites.remove(pos);
                                    Adapter.notifyDataSetChanged();
                                    SaveFile();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    return true;
                }
            });

            KaraokeListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int itwo, long l) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                    alertDialogBuilder.setMessage("" + ((TextView) view.findViewById(R.id.song_artist)).getText() + "의 " + (((TextView) view.findViewById(R.id.song_title)).getText()) + " 을(를) 부르셨나요?");

                    alertDialogBuilder.setPositiveButton("ㅇㅇ 게다가 잘 불렀음",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int temp;
                                    temp = Integer.parseInt(Favorites.get(itwo).TimesSung);
                                    Favorites.get(itwo).TimesSung = Integer.toString(temp + 1);
                                    Adapter.notifyDataSetChanged();
                                    String funstring = "그래 이 노래도 도전해 보는거야!! ༽ʕ•ᴥ•ʔ/";
                                    if (temp > 1)
                                        funstring = "점점 실력이 느는걸 ? (ﾉ◕ヮ◕)ﾉ*:・ﾟ";
                                    if (temp > 6)
                                        funstring = "올~ 이제 좀 하는군 (•̀ᴗ•́)و";
                                    if (temp > 9)
                                        funstring = "잘 부르는데... 다른 노래도 연습해 보는거는 어때? ლ(ಠ_ಠლ)";
                                    if (temp > 13)
                                        funstring = "이 노래 그만 좀;;";
                                    Toast.makeText(getContext(), "" + funstring, Toast.LENGTH_SHORT).show();
                                    SaveFile();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }

            });


        }
    }

    private void LoadFile(){
        try{
            FileInputStream fin = getContext().openFileInput(FavoritesFile);
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            Favorites = ParseDataToSongList(temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SaveFile(){
        File file = new File(getContext().getFilesDir(), FavoritesFile);
        String fileContents = "";
        if (Favorites !=null) {
            for (KaraokeSong NowSong : Favorites) {
                fileContents += NowSong.getID() + "," + NowSong.getTimesSung() + ";";
            }
            try {
                FileOutputStream fout = getContext().openFileOutput(FavoritesFile, getContext().MODE_PRIVATE);
                fout.write(fileContents.getBytes());
                fout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void ShowSearchActivity(String query, boolean trueif){
        int REQ_CODE = 103;
        Intent myIntent = new Intent(getContext(), SearchResultActivity.class);
        myIntent.putExtra("query", query); //Optional parameters
        myIntent.putExtra("bool", Boolean.toString(trueif)); //Optional parameters
        startActivityForResult(myIntent, REQ_CODE);
    }

    //The onClick function when a song is added using ID
    public void AddWithID(String ID_String){
        boolean shdup = false;
        KaraokeSong newSong = new KaraokeSong();
        newSong.ID = ID_String;
        newSong.TimesSung = "0";
        UpdateWithID(newSong);

        if (Favorites == null)
        {
            shdup = true;
            Favorites = new ArrayList<>();
        }

        Favorites.add(newSong);
        if (shdup) {
        //    Log.i("favorites list1", "" + Favorites.get(0).getTitle());
            initList();
          //  Log.i("favorites list2", "" + Favorites.get(0).getTitle());
        }
        else {
            Adapter.notifyDataSetChanged();
            //Log.i("favorites list3", "" + Favorites.get(0).getTitle() + Favorites.get(1).getTitle());
        }

    }
}
