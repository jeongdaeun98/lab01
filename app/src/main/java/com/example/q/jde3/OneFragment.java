package com.example.q.jde3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import com.example.q.jde3.R;
import android.widget.ArrayAdapter;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;


import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.Toast;

public class OneFragment extends Fragment{

    public OneFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    //Log.i("Hello", "ASKJDFHASKJDFNASJKDFAKSJDFHA");

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
                    String ID = PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    listentries NewEntry = new listentries (name, phoneNumber, ID);
                    users.add(NewEntry);
                }

            }

            Collections.sort(users, new Comparator<listentries>(){
                public int compare(listentries a, listentries b){
                    return a.getName().compareTo(b.getName());
                }
            });

            listView.setAdapter(new CustomListAdapter(getActivity().getApplication(), users));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?>adapter, final View v, int position, long something){
                    //Toast.makeText(getContext(), ((TextView)v.findViewById(R.id.NameEntry)).getText(), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage(((TextView)v.findViewById(R.id.NameEntry)).getText()+" was selected");

                    alertDialogBuilder.setNeutralButton("edit",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String phone = ((TextView)v.findViewById(R.id.NumEntry)).getText().toString();
                                    String contactid = null;
                                    ContentResolver contentResolver = getActivity().getContentResolver();

                                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));

                                    Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null,null,null);

                                    if(cursor!=null){
                                        while(cursor.moveToNext()){
                                            //String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                                            contactid= cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                                        }
                                        cursor.close();
                                    }
                                    if (contactid != null) {
                                        Intent intent_contacts = new Intent(Intent.ACTION_EDIT, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactid)));
                                        startActivity(intent_contacts);
                                        Log.i("OneFragment", "contactid is " + String.valueOf(contactid));
                                    }
                                    else Log.i("OneFragment", "contactid is null");

                                }
                            });

                    alertDialogBuilder.setPositiveButton("call",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    String phone = ((TextView)v.findViewById(R.id.NumEntry)).getText().toString();

                                    Intent intent_contacts = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                                    startActivity(intent_contacts);

                                }
                            });

                    alertDialogBuilder.setNegativeButton("message",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String phone = ((TextView)v.findViewById(R.id.NumEntry)).getText().toString();

                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
                                    startActivity(sendIntent);

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            return view;
        }
    }

}
