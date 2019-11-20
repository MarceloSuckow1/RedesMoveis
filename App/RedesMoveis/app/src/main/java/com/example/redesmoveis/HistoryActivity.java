package com.example.redesmoveis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;

public class HistoryActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    ArrayList<History> historyList;
    String data;



    private static final int LOADER = 9;
    private LoaderManager.LoaderCallbacks<String> callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        data = intent.getExtras().getString("date");

        sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerViewHistory);

        historyList = new ArrayList<>();
        getHistory();

    }


    public void getHistory() {
        callbacks = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<String>(getApplicationContext()) {

                    int id = sharedPreferences.getInt("idFuncionario", 0);

                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                    }

                    @Override
                    public String loadInBackground() {

                        try {
                            OkHttpClient client = new OkHttpClient();

                            RequestBody body = new FormBody.Builder()
                                    .add("idFuncionario", id + "")
                                    .add("data", data + "")
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://suckow.com.br/marcelo/redes_historico.php")
                                    .post(body)
                                    .build();

                            Response response = client.newCall(request).execute();
                            String responseString = response.body().string();
                            if (response.isSuccessful()) {
                                return responseString;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        return null;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String responseString) {

                Log.e("RESPONSE", responseString + "");
                if (responseString != null && !responseString.equals("null")) {
                    if (responseString.length() > 0) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseString);
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    if (object != null) {

                                        boolean entrada;
                                        int aberto = object.getInt("aberto");


                                        if (aberto == 0) {
                                            entrada = false;
                                        } else {
                                            entrada = true;
                                        }


                                        String horarioString = object.getString("horario");
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        java.util.Date date = sdf.parse(horarioString);
                                        Date horario = new java.sql.Date(date.getTime());


                                        History history = new History(horario, entrada);
                                        historyList.add(history);


                                    } else {
                                        Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();
                                        Log.e("CONNECTION_FAILURE", "Erro");
                                    }
                                }

                                historyAdapter = new HistoryAdapter(getApplicationContext(), historyList, new HistoryAdapter.RecyclerViewClickListener() {
                                    @Override
                                    public void recyclerViewListClicked(View v, int position) {
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(historyAdapter);
                            }



                        } catch (JSONException |ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(LOADER, null, callbacks);
    }
}
