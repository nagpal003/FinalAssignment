package com.example.hp.finalassignment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.hp.finalassignment.R.id.textView;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HttpExample";

    private EditText ed;
    private TextView tx;
    private Button btn;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ed = (EditText)findViewById(R.id.editText);
        tx = (TextView)findViewById(textView);
        btn = (Button)findViewById(R.id.btn_fetch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringurl = ed.getText().toString();
                ConnectivityManager connectivityManager =
                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo =
                        connectivityManager.getActiveNetworkInfo();

                if(networkInfo != null && networkInfo.isConnected())
                {
                    new downloadpage().execute(stringurl);
                }
                else
                { tx.setText("No network connection available");}
            }
        });

    }
    @Override
   public void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       outState.putString("savText", tx.getText().toString());
//save the state
//1st parameter is the key
//2nd parameter is the value
   }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tx.setText(savedInstanceState.getString("savText"));
//restore it using the key
    }

    private class downloadpage extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            try
            {
                return downloadUrl(params[0]);
            }catch(IOException e)
            {return "Unable to retrieve web page.";}
        }

        @Override
        protected void onPostExecute(String result)
        {tx.setText(result);}

        private String downloadUrl(String input) throws IOException
        {
            InputStream in = null;
            int cnt = 100;
            try
            {
                URL url = new URL(input);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                //starts the query
                connection.connect();
                int response = connection.getResponseCode();
                Log.d(DEBUG_TAG,"The response is:"+ response);
                in = connection.getInputStream();
                //Convert the inputStream into a String
                String connectAsString = readinput(in,cnt);

//                if(in != null)
//                {
//                    Log.i(OUTPUT_TAG,in.toString());
//                }
                BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                String line = null;
                StringBuilder bull = new StringBuilder();
                while((line = buffer.readLine()) != null)
                   {
                       bull.append(line).append('\n');
                   }
            Log.d("ABC",bull.toString());
                return connectAsString;

            }finally {if(in != null) in.close();}
        }
        public String readinput(InputStream stream, int len)
                throws IOException,UnsupportedEncodingException
        {
            Reader r = null;
            r = new InputStreamReader(stream,"UTF-8");
//            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
//
//            StringBuilder bull = new StringBuilder();
//            while((line = buffer.readLine()) != null)
//            {
//                bull.append(line).append('\n');
//            }
//            Log.d(OUTPUT_TAG,bull.toString());
            char[] buf = new char[len];
            r.read(buf);
            return new String(buf);
        }

    }

}
