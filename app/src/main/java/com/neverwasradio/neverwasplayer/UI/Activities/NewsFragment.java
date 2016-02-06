package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.TextView;

import com.neverwasradio.neverwasplayer.Core.ConnectionHandler;
import com.neverwasradio.neverwasplayer.Model.NWPost;
import com.neverwasradio.neverwasplayer.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Giovanni on 21/01/16.
 */
public class NewsFragment extends Fragment {
    NewsFragment thisFragment;

    public ArrayList<NWPost> postList;

    private ListView listView;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }
    public NewsFragment() {
        // Required empty public constructor
        thisFragment=this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetJSONArrayTask getJSONArrayTask = new GetJSONArrayTask();
        getJSONArrayTask.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView);

        if(postList!=null) { updateView(); }

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





    class PostListAdapter extends ArrayAdapter<NWPost> {

        private ArrayList<NWPost> posts=null;
        private Context context=null;
        private SimpleDateFormat simple=new SimpleDateFormat("dd/MM", Locale.ITALIAN);

        public PostListAdapter(Context context,ArrayList<NWPost> posts)
        {
            super(context, R.layout.post_element_layout, posts);
            this.posts=posts;
            this.context=context;
        }

        @Override
        public int getCount()
        {
            return posts.size();
        }

        @Override
        public NWPost getItem(int position)
        {
            return posts.get(position);
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
                v= LayoutInflater.from(context).inflate(R.layout.post_element_layout, null);
            }
            NWPost post=(NWPost) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.postTitle);
            txt.setText(post.getTitle());
            txt=(TextView) v.findViewById(R.id.postDate);
            txt.setText(post.getDate());


            return v;
        }


    }


    private class GetJSONArrayTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params)  {

            //if(params[0]==null) {return "Failed";}


            String url = "http://www.associazionesmart.it/wp-json/wp/v2/posts";

            JSONArray json=null;
            try {
                json = ConnectionHandler.getJSONArrayAtUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            postList = new ArrayList<NWPost>();

            for(int i = 0; i<json.length(); i++) {
                try {
                    postList.add(new NWPost(json.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //text = text.replace("\\n", "");
            //text = text.replace("\\", "");


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            postList = cleanPostList(postList);

            // aggiorna View con results
            updateView();


        }

        @Override
        protected void onPreExecute() {

            // Aggiorna WebView su loading

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private ArrayList<NWPost> cleanPostList(ArrayList<NWPost> list) {

        ArrayList<NWPost> result = new ArrayList<NWPost>();

        for (int i=0; i<list.size(); i++) {
            if(list.get(i).getTitle().compareTo("")!=0 && list.get(i).getContent().compareTo("")!=0) {
                result.add(list.get(i));
            }
        }

        return result;
    }


    private void updateView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NWPost post = postList.get(position);

                Intent intent = new Intent(getActivity(), PostActivity.class);
                Bundle b = new Bundle();
                b.putString("title", post.getTitle());
                b.putString("date", post.getDate());
                b.putString("content", post.getContent());
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);

            }
        });
        listView.setAdapter(new PostListAdapter(getActivity(), postList));
    }


}