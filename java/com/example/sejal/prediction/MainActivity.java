package com.example.sejal.prediction;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
String line = "";
    String cvsSplitBy = ","; BufferedReader br;
    Hashtable MYHASH = new Hashtable();
    Hashtable MYHASH1 = new Hashtable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);final JsonHttpHandler handler = new JsonHttpHandler();
        final EditText e1=(EditText)findViewById(R.id.e1);
        final EditText e2=(EditText)findViewById(R.id.e2);
        final EditText e3=(EditText)findViewById(R.id.e3);
        Button but=(Button)findViewById(R.id.button);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        BufferedReader reader=null;
        try{
            reader=new BufferedReader(new InputStreamReader(getAssets().open("input.txt")));
            while (( line= reader.readLine()) != null) {
//System.out.println(line);
                // use comma as separator
                // String[] country = line.split(cvsSplitBy);
                MYHASH.put(line,1);
                //  System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }

            } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
/*        try {

           br = new BufferedReader(new InputStreamReader(getAssets().open("appended_output.csv")));
            while ((line = br.readLine()) != null) {


                String[] country = line.split(cvsSplitBy);
                for(int i=1;i<country.length;i++)

                {
Log.i("Gggggg",country[i]);
//////////////////********************count*********************?????????????

                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
Toast.makeText(getApplicationContext(),MYHASH+"",Toast.LENGTH_LONG).show();
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {

                    json.accumulate("name1", e1.getText().toString());
                    json.accumulate("name2",e2.getText().toString());
                    json.accumulate("name3",e3.getText().toString());

                    handler.postJSONfromUrl("http://172.16.85.152:5000/empdb/employee", json);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
               }
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            HttpClient client = new DefaultHttpClient();
                            HttpPut put = new HttpPut("http://172.16.85.152:5000/empdb/employee");
                            put.addHeader("Content-Type", "application/json");
                            put.addHeader("Accept", "application/json");
                            put.setEntity(new StringEntity("{ \"password\": \"iamal0ck\" }"));
                            HttpResponse response = client.execute(put);
                            HttpEntity entity = response.getEntity();
                            InputStream is = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            String result = sb.toString();
                            String com[] = result.split(cvsSplitBy);
                            for (int i = 0; i < com.length; i++) {
                                Log.i("*****hi**", com[i] + MYHASH.get(com[i].trim()) + "");
                                MYHASH1.put(com[i], MYHASH.get(com[i].trim()));


                            }
                            ///////////////**************sorting technique******************///////////////////////////////
                            Hashtable<String,Integer>hashtable=new Hashtable<String, Integer>();
                            String pattern[] = {null, null, null};int k=0;
                            Aho_corasick aho_corasick = new Aho_corasick(1000);int l[]=new int[10];
                            String match1[] = new String[10000];
                            for (int j = 0; j < 3; j++) {
                                Object firstKey = MYHASH1.keySet().toArray()[j];
                                Object valueForFirstKey = MYHASH1.get(firstKey);
                                pattern[j] = valueForFirstKey.toString();
                                try {

                                    br = new BufferedReader(new InputStreamReader(getAssets().open("appended_output.csv")));
                                    while ((line = br.readLine()) != null) {
                                        String main = line;
                                        Boolean b = aho_corasick.find(pattern[j], main);
                                        if(b)
                                          hashtable.put(main,0);

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

l[j]=k;
                            }generateNoteOnSD(getApplicationContext(),"hohare",hashtable+"");
//FPGrowth ff=new FPGrowth();
  //                          ff.construct_fpTree();

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }}).start();


            }
        });

    }
    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    }

