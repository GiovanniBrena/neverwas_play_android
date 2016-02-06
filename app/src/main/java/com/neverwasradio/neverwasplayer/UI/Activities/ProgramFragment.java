package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.neverwasradio.neverwasplayer.Core.XMLProgramParser;
import com.neverwasradio.neverwasplayer.Model.NWProgram;
import com.neverwasradio.neverwasplayer.Model.NWProgramManager;
import com.neverwasradio.neverwasplayer.R;

import java.util.ArrayList;

/**
 * Created by chiara on 21/11/15.
 */
public class ProgramFragment extends Fragment {

    NWProgramManager programManager;
    ProgramFragment thisFragment;

    private ListView listView;
    private ProgramListAdapter programListAdapter;

    private ToggleButton buttLune, buttMarte, buttMerco, selectedButton;
    private RelativeLayout progressLayout, noConnectionLayout;

    public static ProgramFragment newInstance() {
        ProgramFragment fragment = new ProgramFragment();
        return fragment;
    }
    public ProgramFragment() {
        thisFragment=this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_programs, container, false);

        buttLune = (ToggleButton) rootView.findViewById(R.id.button0);
        buttMarte = (ToggleButton) rootView.findViewById(R.id.button1);
        buttMerco = (ToggleButton) rootView.findViewById(R.id.button2);

        progressLayout = (RelativeLayout) rootView.findViewById(R.id.programProgressLayout);
        noConnectionLayout = (RelativeLayout) rootView.findViewById(R.id.programNoConnection);

        listView= (ListView) rootView.findViewById(R.id.listView);

        if(programManager==null && !MainActivity.isNetworkConnected(getActivity())) {
            listView.setVisibility(View.GONE);
            noConnectionLayout.setVisibility(View.VISIBLE);
            buttLune.setEnabled(false);
            buttMarte.setEnabled(false);
            buttMerco.setEnabled(false);
        }

        else if(programManager==null) {
            selectedButton=buttLune;
            new getProgramsTask().execute();
            buttLune.setEnabled(true);
            buttMarte.setEnabled(true);
            buttMerco.setEnabled(true);
        }
        else {listView.setAdapter(programListAdapter);}

        if(selectedButton!=null) {selectedButton.setChecked(true);}

        initListeners();
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initListeners(){
        buttLune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton.setChecked(false);
                buttLune.setChecked(true);
                selectedButton=buttLune;

                programListAdapter = new ProgramListAdapter(getActivity(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.MONDAY));
                listView.setAdapter(programListAdapter);
            }
        });

        buttMarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton.setChecked(false);
                buttMarte.setChecked(true);
                selectedButton=buttMarte;

                programListAdapter = new ProgramListAdapter(getActivity(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.TUESDAY));
                listView.setAdapter(programListAdapter);
            }
        });

        buttMerco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton.setChecked(false);
                buttMerco.setChecked(true);
                selectedButton=buttMerco;

                programListAdapter = new ProgramListAdapter(getActivity(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.WEDNESDAY));
                listView.setAdapter(programListAdapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NWProgram p = programListAdapter.getItem(position);

                Intent myIntent = new Intent(getActivity(), ProgramDetail.class);
                myIntent.putExtra("id", p.getId()); //Optional parameters
                startActivity(myIntent);
            }
        });
    }

    private class ProgramListAdapter extends ArrayAdapter<NWProgram> {

        ArrayList<NWProgram> programs;

        public ProgramListAdapter(Context context, int resource, ArrayList<NWProgram> objects) {
            super(context, resource, objects);
            programs=objects;
        }

        @Override
        public int getCount()
        {
            return programs.size();
        }

        @Override
        public NWProgram getItem(int position)
        {
            return programs.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            if (v==null)
            {
                v=LayoutInflater.from(getContext()).inflate(R.layout.item_programs_list, null);
            }

            NWProgram p=(NWProgram) getItem(position);

            TextView label=(TextView) v.findViewById(R.id.programTitle);
            label.setText(p.getName());
            label=(TextView) v.findViewById(R.id.programData);
            label.setText(p.getHour());

            ImageView icon = (ImageView) v.findViewById(R.id.programIcon);
            icon.setImageBitmap(p.getIcon());
            return v;
        }
    }

    private class getProgramsTask extends AsyncTask<Object,Object,Object>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            NWProgramManager.create(XMLProgramParser.getPrograms());
            programManager = NWProgramManager.getInstance();

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            programListAdapter = new ProgramListAdapter(getActivity(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.MONDAY));
            listView.setAdapter(programListAdapter);

            progressLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

        }
    }
}
