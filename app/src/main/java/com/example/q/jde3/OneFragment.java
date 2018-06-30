package com.example.q.jde3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import java.util.List;
import java.util.ArrayList;
import com.example.q.jde3.R;
import android.widget.ArrayAdapter;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;


import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class OneFragment extends Fragment{

    public OneFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    Log.i("Hello", "ASKJDFHASKJDFNASJKDFAKSJDFHA");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_one, container, false);
        View view = inflater.inflate(R.layout.fragment_one, container, false);


        ListView listView = (ListView) view.findViewById(R.id.listView);

        List<listentries> users = new ArrayList<listentries>();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            return view;
        else{
            ContentResolver resolver = getContext().getContentResolver();

            Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,null,null,null);

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                Cursor PhoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);



                while (PhoneCursor.moveToNext()) {
                    String phoneNumber = PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    listentries NewEntry = new listentries (name, phoneNumber);
                    users.add(NewEntry);
                }

            }

    /*
            listentries Kane = new listentries ("Harry Kane", "010-0000-9999");
            listentries Son = new listentries ("Heung-min Son", "010-0000-7777");
            listentries Eriksen = new listentries ("Christian Eriksen", "010-0000-2323");


            users.add(Kane);
            users.add(Son);
            users.add(Eriksen);

            listView.setAdapter(new ArrayAdapter<listentries>(getActivity().getApplication(), android.R.layout.simple_list_item_1, users));
    */
            listView.setAdapter(new CustomListAdapter(getActivity().getApplication(), users));

            return view;
        }
    }

}
