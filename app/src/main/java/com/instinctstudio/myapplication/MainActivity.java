package com.instinctstudio.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SHOW_RESPONCE = 0;
    private Button sendRequestGET;
    private Button sendRequestPOST;
    private Button sendRequestPUT;
    private Button sendRequestDELETE;
    private EditText username;
    private EditText password;
    private EditText input_URL;
    private EditText input_id;
    private String server_url;
    private TextView responseText;
    private String  printOnScreen;
    private String  method;


    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONCE:
                    String response = (String) msg.obj;




                    switch (method){
                        case "POST":
                            responseText.setText(response);
                            break;
                        case "GET":
                            printOnScreen = new String();
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String id = jsonObject.getString("id");
                                    String usn = jsonObject.getString("username");
                                    String pwd = jsonObject.getString("password");
                                    String created_at = jsonObject.getString("created_at");
                                    String updated_at = jsonObject.getString("updated_at");
                                    String url = jsonObject.getString("url");
                                    printOnScreen=printOnScreen+"id:"+id+"\nusername:"+usn+"\npassword:"+pwd+"\ncreated_at:"+created_at+"\nupdated_at:"+updated_at+"\n\n";

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            responseText.setText(null);
                            responseText.setText(printOnScreen);
                    }
            }
        }

    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        server_url = "http://52.53.156.250:3000/members/";
        sendRequestGET = (Button) findViewById(R.id.send_get);
        sendRequestPOST = (Button) findViewById(R.id.send_post);
        sendRequestPUT = (Button) findViewById(R.id.send_put);
        sendRequestDELETE = (Button) findViewById(R.id.send_delete);
        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);
        input_id = (EditText) findViewById(R.id.input_id);


        responseText = (TextView) findViewById(R.id.response_text);
        sendRequestGET.setOnClickListener(this);
        sendRequestPOST.setOnClickListener(this);
        sendRequestPUT.setOnClickListener(this);
        sendRequestDELETE.setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.




    }


    @Override
    public void onClick(View v) {
        String  userName = null;
        String  passWord = null;
        String  Theid;
        String  uesr_server_url;

        switch (v.getId()) {
            case R.id.send_post:
                userName = username.getText().toString();
                passWord = password.getText().toString();
                method="POST";
                sendRequestWithHttpURLConnection(method, server_url,"member[username]="+userName+"&member[password]="+passWord);
                Toast.makeText(MainActivity.this, "POST " + server_url + " with username:" + userName + " password:" + passWord, Toast.LENGTH_SHORT).show();

                break;
            case R.id.send_get:
                method="GET";
                sendRequestWithHttpURLConnection(method, server_url,null);
                Toast.makeText(MainActivity.this, "GET " + server_url, Toast.LENGTH_SHORT).show();
                break;
            case R.id.send_put:
                Theid = input_id.getText().toString();
                userName = username.getText().toString();
                passWord = password.getText().toString();
                method="PUT";
                Toast.makeText(MainActivity.this, "PUT " + server_url+Theid+ " with username:" + userName + " password:" + passWord, Toast.LENGTH_SHORT).show();
                sendRequestWithHttpURLConnection(method, server_url+Theid,"member[username]="+userName+"&member[password]="+passWord);
                break;
            case R.id.send_delete:
                method="DELETE";
                Theid = input_id.getText().toString();
                Toast.makeText(MainActivity.this, "DELETE " + server_url+Theid, Toast.LENGTH_SHORT).show();
                sendRequestWithHttpURLConnection(method, server_url+Theid,null);
                break;
        }
    }


    private void sendRequestWithHttpURLConnection(final String method, final String server_url, final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                DataOutputStream out;
                try {
                    URL url = new URL(server_url);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setConnectTimeout(80000);
                    connection.setReadTimeout(8000);


                    switch (method) {
                        case "POST":
                            connection.setRequestMethod(method);
                            out = new DataOutputStream(connection.getOutputStream());
                            out.writeBytes(data);
                            break;
                        case "GET":
                            break;
                        case "PUT":
                            connection.setRequestMethod(method);
                            out = new DataOutputStream(connection.getOutputStream());
                            out.writeBytes(data);
                            break;
                        case "DELETE":
                            connection.setRequestMethod(method);
                            break;
                    }

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = SHOW_RESPONCE;

                    message.obj = response.toString();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }



}