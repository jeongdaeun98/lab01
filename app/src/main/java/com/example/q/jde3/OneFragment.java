package com.example.q.jde3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class OneFragment extends Fragment{

    public OneFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_one, container, false);
        View view = inflater.inflate(R.layout.fragment_one, container, false);


        ListView listView = (ListView) view.findViewById(R.id.listView);


        listentries Kane = new listentries ("Harry Kane", "010-0000-9999");
        listentries Son = new listentries ("Heung-min Son", "010-0000-7777");
        listentries Eriksen = new listentries ("Christian Eriksen", "010-0000-2323");

        List<listentries> users = new ArrayList<listentries>();
        users.add(Kane);
        users.add(Son);
        users.add(Eriksen);

        listView.setAdapter(new CustomListAdapter(getActivity().getApplication(),users));
        //listView.setAdapter(new ArrayAdapter<listentries>(getActivity().getApplication(), android.R.layout.simple_list_item_1, users));

        return view;
    }

}
