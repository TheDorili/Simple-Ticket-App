package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static String nodeServerUrl = "http://10.0.2.2:3000/allTickets";

    private TextView queryResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryResultText = findViewById(R.id.query_result_text);
    }

    public void startConn(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new DatabaseConnectionTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DatabaseConnectionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Verbindung zum Node.js-Server herstellen und Daten abrufen
                URL url = new URL(nodeServerUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder resultStringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }

                reader.close();
                connection.disconnect();

                return resultStringBuilder.toString();
            } catch (IOException e) {
                Log.e("Node.js Connection", "Error: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            queryResultText.setText("Query Result:\n" + result);
            Toast.makeText(MainActivity.this, "Query executed", Toast.LENGTH_SHORT).show();
        }
    }
}
