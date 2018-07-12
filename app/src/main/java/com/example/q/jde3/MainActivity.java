package com.example.q.jde3;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //static ThreeFragment thirdfrag;


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 20;
    public static ArrayList<String> ImageList = new ArrayList<>();
    public static ArrayList<Map<String, String>> ContactList = new ArrayList<>();
    private static ContentResolver contentResolver;

    public static String email = null;
/*
    public static void showController()
    {
        thirdfrag.controller.show(0);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver = getContentResolver();


        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            fetchAllImages();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            takeContacts();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        updateEmail(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                takeContacts();
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED || grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                setupViewPager(viewPager);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Frag1(), "Contacts");
        adapter.addFragment(new Frag2(), "Images");
        adapter.addFragment(new KaraokeFrag(), "Karaoke Partner");
        //thirdfrag = new ThreeFragment();
        //thirdfrag.setRetainInstance(true);
        //adapter.addFragment(thirdfrag, "Music");
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
    }
    public void PressThis(View view){
        Log.i("Toolbar", "pressed");
    }
/*
    public void songPicked(View view){
        if (thirdfrag.musicSrv == null){
            Log.i("songpicked","noService");
            return;
        }
        thirdfrag.musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        thirdfrag.musicSrv.playSong();
        if(thirdfrag.playbackPaused){
            Log.i("songPicked", "and setController is called");
            thirdfrag.setController();
            thirdfrag.playbackPaused=false;
        }
        else
            thirdfrag.controller.show(0);
    }
*/
    @Override
    protected void onDestroy() {

        Log.i("MainActivity", "onDestroy");
        super.onDestroy();
    }
/*
    @Override
    protected void onPause(){


        Log.i("MainActivity", "onPause is called");
        if (thirdfrag.controller != null) {
            if (thirdfrag.controller.nowshowing) {//we will hide it
                thirdfrag.controller.reallyhide = true;
                thirdfrag.controller.hide();
                thirdfrag.controller.nowshowing = !(thirdfrag.controller.nowshowing);
            }
        }

        super.onPause();
    }
    public static void setStart(){
        thirdfrag.MusicStarted = true;
    }

    protected void togglePichu(View view){

        Log.i("togglePichu", "outside" + thirdfrag.MusicStarted );
        if(thirdfrag.controller != null && thirdfrag.MusicStarted)
        {
            Log.i("togglePichu", "inside");
            //if it is showing right now,
            if (thirdfrag.controller.nowshowing)
            {//we will hide it
                thirdfrag.controller.reallyhide = true;
                thirdfrag.controller.hide();
            }
            //if it is hiding right now
            else
            {//we will show it
                thirdfrag.controller.show();
            }
            thirdfrag.controller.nowshowing = !(thirdfrag.controller.nowshowing);

        }


        else
            Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_LONG).show();

    }
*/

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        }

    private static void fetchAllImages() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor imageCursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        ImageList = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);

        if (imageCursor == null) {
        } else if (imageCursor.moveToFirst()) {
            do {
                ImageList.add(imageCursor.getString(dataColumnIndex));
            } while (imageCursor.moveToNext());
        } else {
        }

        imageCursor.close();
        return;
    }

    public void takeContacts () {
        ContactList.clear();

        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (c.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            // 연락처 id 값
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            // 연락처 대표 이름
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            map.put("name", name);

            // ID로 전화 정보 조회
            Cursor phoneCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null, null);

            // 데이터가 있는 경우
            if (phoneCursor.moveToFirst()) {
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                map.put("phone", number);
            }
            phoneCursor.close();
            ContactList.add(map);
        }// end while
        c.close();
    }

    public static boolean request_email(Context ctx) {
        if (email== null)
            MainActivity.updateEmail(ctx);
        if(email == null) {
            reqFB(ctx);
            return false;
        }

        return true;
    }

    public static void reqFB(Context ctx){
        Intent myIntent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(myIntent);
    }

    public static void updateEmail(final Context ctx){
        if(AccessToken.getCurrentAccessToken() !=null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.v("LoginActivity Response ", response.toString());

                            try {
                                Toast.makeText(ctx, "Email is updated to " + object.getString("email"), Toast.LENGTH_SHORT).show();
                                //txtEmail.setText("You are logged in as: " + FEmail);
                                email = object.getString("email");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();


        }
    }
}


