package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neverwasradio.neverwasplayer.Model.NWProgram;
import com.neverwasradio.neverwasplayer.Model.NWProgramManager;
import com.neverwasradio.neverwasplayer.R;

public class ProgramDetail extends Activity {

    NWProgram program;
    NWProgramManager programManager;

    ImageView icon, fbLogo;
    TextView title, desc, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);

        Intent intent = getIntent();
        programManager = NWProgramManager.getInstance();
        program=programManager.getProgramById(intent.getStringExtra("id"));

        icon = (ImageView) findViewById(R.id.pDetailImage);
        fbLogo = (ImageView) findViewById(R.id.pDetailFbLogo);
        title = (TextView) findViewById(R.id.pDetailTitle);
        desc = (TextView) findViewById(R.id.pDetailDesc);
        date = (TextView) findViewById(R.id.pDetailDate);

        icon.setImageBitmap(program.getIcon());
        title.setText(program.getName());
        desc.setText(program.getDesc());
        String day = "";
        if(program.getDay()==0) {day="Lunedì";}
        else if(program.getDay()==1) {day="Martedì";}
        else if(program.getDay()==2) {day="Mercoledì";}
        date.setText("Tutti i " + day+ " alle " + program.getHour());

        initListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.program_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListeners()
    {
        fbLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent;

                if(program.getFbId().compareTo("")!=0) {
                    try {
                        getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                        browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/"+program.getFbId()));
                    } catch (Exception e) {
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(program.getSiteUrl()));
                    }
                }
                else { browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(program.getSiteUrl())); }

                startActivity(browserIntent);
            }
        });
    }
}
