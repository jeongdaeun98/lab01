package com.example.q.jde3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.database.Cursor;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Toast;



public class TwoFragment extends Fragment {
    Fragment mefrag = this;
    static View viewforuse;
    private String pkgName;
    static int firstnum = 0;
    static int allimagenum = 0;
    static int buttonclick = 0;
    static int dataCol;
    static int total;
    static String[] proj = {MediaStore.Images.Media.DATA};
    Cursor imageCursor;
    static ImageView iv;
    static String fileName;
    public TwoFragment() {
        // Required empty public constructor
    }

    public void setMenuVisibility(final boolean visible) {
        if (visible) {
            Toast.makeText(getActivity().getApplicationContext(), "Images: " + Integer.toString(buttonclick) + "/" + Integer.toString(total), Toast.LENGTH_LONG).show();
        }
        super.setMenuVisibility(visible);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pkgName = getActivity().getPackageName();
        viewforuse = inflater.inflate(R.layout.fragment_two, container, false);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("onCreateView", "we have no permission");
            return viewforuse;
        } else {
            Log.i("onCreateView", "we have permission");
            imageCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
            Button button1 = (Button) viewforuse.findViewById(R.id.button1);
            button1.setOnClickListener(new OnClickListener(){
                public void onClick(View v) {
                    buttonclick++;
                    int i = 0;
                    if (allimagenum - firstnum > 0 && total != 1) { // 현재로써는 3번 돌아야 함
                        Toast.makeText(getActivity().getApplicationContext(), "Images: " + Integer.toString(buttonclick) + "/" + Integer.toString(total), Toast.LENGTH_LONG).show();
                        String name = fileName;
                        int resID;
                        do {
                            resID = getResources().getIdentifier("my_image" + i, "id", pkgName);
                            iv = (ImageView) viewforuse.findViewById(resID);
                            fileName = imageCursor.getString(dataCol);
                            if (fileName != null && iv != null)
                                GlideApp.with(mefrag).load(fileName).into(iv);
                            i++;
                        } while (imageCursor.moveToNext() && i != 20);// 다음엔 20부터 해야함
                        if (i % 20 == 0) firstnum = firstnum + 20;
                        else firstnum = allimagenum;// firstnum = 23, allimagenum = 23

                        while (i != 20) {
                            resID = getResources().getIdentifier("my_image" + i, "id", pkgName);
                            iv = (ImageView) viewforuse.findViewById(resID);
                            GlideApp.with(mefrag).load(R.mipmap.ic_launcher).into(iv);
                            i++;
                        }
                    } else if(allimagenum != 0) {
                        buttonclick = 1;
                        Toast.makeText(getActivity().getApplicationContext(), "Images: " + Integer.toString(buttonclick) + "/" + Integer.toString(total), Toast.LENGTH_LONG).show();
                        imageCursor.moveToFirst();
                        String fileName;
                        int resID;
                        int dataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        do {
                            resID = getResources().getIdentifier("my_image" + i, "id", pkgName);
                            iv = (ImageView) viewforuse.findViewById(resID);
                            fileName = imageCursor.getString(dataCol);
                            if (fileName != null && iv != null)
                                GlideApp.with(mefrag).load(fileName).into(iv);
                            i++;
                        } while (imageCursor.moveToNext() && i != 20);// 다음엔 20부터 해야함
                        firstnum = 0;

                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Images: " + Integer.toString(buttonclick) + "/" + Integer.toString(total), Toast.LENGTH_LONG).show();
                    }


                }
            });
            if ((imageCursor != null) && imageCursor.moveToFirst()) {
                int resID, i = 0;
                imageCursor.moveToFirst();
                buttonclick = 1;
                allimagenum = imageCursor.getCount();
                total = (allimagenum / 20) + 1;
                //Toast.makeText(getActivity().getApplicationContext(), "Images: " + Integer.toString(buttonclick) + "/" + Integer.toString(total), Toast.LENGTH_LONG).show();
                int dataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                do {
                    resID = getResources().getIdentifier("my_image" + i, "id", pkgName);
                    iv = (ImageView) viewforuse.findViewById(resID);
                    fileName = imageCursor.getString(dataCol);
                    if (fileName != null && iv != null) {
                        GlideApp.with(mefrag).load(fileName).into(iv);
                        Log.i("onCreateView", "glide");
                        i++;
                    }
                } while (imageCursor.moveToNext() && i != 20); // 20개 돌고 나옴
                if (i % 20 == 0) firstnum = 20; //20부터 시작해야 함
            }
        }
        return viewforuse;
    }


}