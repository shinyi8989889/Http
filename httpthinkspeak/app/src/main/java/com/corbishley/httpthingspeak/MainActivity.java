package com.corbishley.httpthingspeak;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String TAG = "main";
    private Context context;
    private EditText editField1,editField2;
    private TextView textViewResult;
    private Button buttonGet,buttonPost,buttonGetData;
    private StringBuilder thingSpeakURL;
    private String webAddress = "https://api.thingspeak.com/";
    private String channelLast = "channels/637316/fields/1/last.json";
    private String getDataApiKey = "update?api_key=QS67V3WGN8Q1WNM4";
    private String field1 = "&field1=";
    private String field2 = "&field2=";
    private String postDataApiKey = "api_key=QS67V3WGN8Q1WNM4";
    private GetChannelData myGetData;
    private String field1Data,field2Data;
    private boolean postFlag;
    private UpdateData myUpdateData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        postFlag = false;


        editField1 = (EditText) findViewById(R.id.editText_field1);
        editField2 = (EditText) findViewById(R.id.editText_field2);

        textViewResult = (TextView) findViewById(R.id.textView_result);
        textViewResult.setText("");

        buttonGet = (Button) findViewById(R.id.button_get);
        buttonPost = (Button) findViewById(R.id.button_post);
        buttonGetData = (Button) findViewById(R.id.button_getdata);

        buttonGet.setOnClickListener(new MyClick());
        buttonPost.setOnClickListener(new MyClick());
        buttonGetData.setOnClickListener(new MyClick());
    }

    private class MyClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.button_get:
                case R.id.button_post:

                    if (editField1.length() != 0){
                        field1Data = editField1.getText().toString();
                    }else{
                        field1Data = "0";
                        Toast.makeText(context,"Please input field 1 data.",Toast.LENGTH_SHORT).show();
                    }

                    if (editField2.length() != 0){
                        field2Data = editField2.getText().toString();
                    }else{
                        field2Data = "0";
                        Toast.makeText(context,"Please input field 2 data.",Toast.LENGTH_SHORT).show();
                    }

                    if(v.getId() == R.id.button_get){
                        postFlag = false;
                    }else{
                        postFlag = true;
                    }

                    myUpdateData = new UpdateData();
                    myUpdateData.execute();


                    break;

                case R.id.button_getdata:
                    textViewResult.setText("");
                    myGetData = new GetChannelData();
                    myGetData.execute();
                    break;
            }
        }
    }

    private class GetChannelData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String dataString = null;

            thingSpeakURL = new StringBuilder();
            thingSpeakURL.append(webAddress);
            thingSpeakURL.append(channelLast);
            Log.d(TAG,"thingspeak addr = " + thingSpeakURL);

            try {

                URL url = new URL(thingSpeakURL.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int code = conn.getResponseCode();
                Log.d(TAG,"code = "+code);

                if (code == HttpURLConnection.HTTP_OK){

                    InputStream input = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(input);
                    BufferedReader stringReader = new BufferedReader(reader);
                    dataString = stringReader.readLine();
                    input.close();

                } else {
                    dataString = "The HTTP code = " + code;
                }

            } catch (MalformedURLException e) {

                e.printStackTrace();
                Log.d(TAG,"mal URL");

            } catch (IOException e) {

                e.printStackTrace();
                Log.d(TAG,"IO exception");

            }


            return dataString;

        }  //end of doInBackground

        @Override
        protected void onPostExecute(String s) {
            textViewResult.setText(s);
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context,"onPreExecute()",Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

    }

    private class UpdateData extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {

            String data = null;
            URL url;
            HttpURLConnection conn;
            String param;

            thingSpeakURL = new StringBuilder();
            thingSpeakURL.append(webAddress);
            try{

                if (postFlag){

                    thingSpeakURL.append("update");
                    url = new URL(thingSpeakURL.toString());
                    Log.d(TAG,"post url = "+url);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    param = postDataApiKey+field1+field1Data+field2+field2Data;

                    OutputStream output = conn.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(output);
                    writer.write(param);
                    writer.flush(); // makesure data output even if same data
                    output.close();

                }else{

                    param = getDataApiKey+field1+field1Data+field2+field2Data;
                    thingSpeakURL.append(param);
                    url = new URL(thingSpeakURL.toString());
                    Log.d(TAG,"get url = "+url);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                }

                int code = conn.getResponseCode();
                Log.d(TAG,"update code = "+code);

                if (code == HttpURLConnection.HTTP_OK){

                    InputStream input = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(input);
                    char[] buffer = new char[50];
                    int number = reader.read(buffer);
                    data = String.valueOf(buffer);
                    input.close();
                }

            } catch (MalformedURLException e){

                e.printStackTrace();

            } catch (IOException e){

                e.printStackTrace();

            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            textViewResult.setText(s);
            super.onPostExecute(s);
        }
    }
}
