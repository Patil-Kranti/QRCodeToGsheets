package com.example.qrcodetogsheets;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    String scannedData, qrData, text;


    Button scanBtn, viewBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity activity = this;
        scanBtn = findViewById(R.id.scan_btn);
        viewBtn = findViewById(R.id.view_btn);

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayData();
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(true);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            text = result.getContents();

            ArrayList<String> tokens = new ArrayList<String>();

            Scanner tokenize = new Scanner(text);
            String[] words = text.split("[\\(||//)]");

            Toast.makeText(this, words[0], Toast.LENGTH_SHORT).show();
            while (tokenize.hasNext()) {
                tokens.add(tokenize.next());

            }

            Toast.makeText(this, "TOKEN!!!!!" + tokens, Toast.LENGTH_LONG).show();


            if (tokens.get(0).equals("Iamlegit")) {

                String msg = tokens.get(1);


                byte[] dedata = Base64.decode(msg, Base64.DEFAULT);
                qrData = new String(dedata, StandardCharsets.UTF_8);
                Toast.makeText(this, qrData, Toast.LENGTH_LONG).show();


                Pattern pattern = Pattern.compile("(\\d{4})");
                Matcher matcher = pattern.matcher(qrData);
                if (matcher.find()) {
                    System.out.println(matcher.group(1));

                    scannedData = matcher.group(1);
                }
                if (scannedData != null) {
                    // Here we need to handle scanned data...
                    new SendRequest().execute();

                    //Toast.makeText(this, scannedData, Toast.LENGTH_LONG).show();
                } else {

                }
            } else {
                Toast.makeText(this, "This QR code is Invalid", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayData() {

        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        Toast.makeText(this, "Data:" + qrData, Toast.LENGTH_SHORT).show();
        intent.putExtra("data", qrData);
        startActivity(intent);
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            Log.d(TAG, "getPostDataString: " + result.toString());
        }
        return result.toString();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {

                //Enter script URL Here
                URL url = new URL("https://script.google.com/macros/s/AKfycbybokgDYCUdq0dyeILBMDNYG16vwVSdGQdwjMJ5UB7i-UCwvGU/exec");

                JSONObject postDataParams = new JSONObject();

                //Passing scanned code as parameter

                postDataParams.put("sdata", scannedData);


                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result.getBytes().toString(),
                    Toast.LENGTH_LONG).show();

        }
    }
}









