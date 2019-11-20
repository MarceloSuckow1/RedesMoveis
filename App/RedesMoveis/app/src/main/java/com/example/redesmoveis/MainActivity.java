package com.example.redesmoveis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button pontoButton, historicoButton;
    TextView textNome, textPonto;
    SharedPreferences sharedPreferences;
    String ssid;
    boolean pontoAberto;
    String macAddress;


    private static final int LOADER = 19;
    private LoaderManager.LoaderCallbacks<String> callbacks;


    private static final int LOADER2 = 17;
    private LoaderManager.LoaderCallbacks<String> callbacks2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

        if (!sharedPreferences.getBoolean("logged", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        getPontoStatus();
        getMacAddress();

        textNome = findViewById(R.id.textNome);
        historicoButton = findViewById(R.id.botaoHistorico);
        pontoButton = findViewById(R.id.botaoPonto);
        textPonto = findViewById(R.id.textPonto);


        textNome.setText("Olá, " + sharedPreferences.getString("nome", ""));


        pontoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSSID();

                if (!ssid.equals("\"eduroam\"")) {
                    Toast.makeText(MainActivity.this, "Você não está conectado à rede da empresa", Toast.LENGTH_LONG).show();
                } else {
                    baterPonto();
                }
            }
        });


        historicoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

    }

    public void getSSID() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        List<WifiConfiguration> listOfConfigurations = manager.getConfiguredNetworks();

        for (int index = 0; index < listOfConfigurations.size(); index++) {
            WifiConfiguration configuration = listOfConfigurations.get(index);
            if (configuration.networkId == info.getNetworkId()) {
                ssid = configuration.SSID;
            }
        }

    }

    public void getPontoStatus() {
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
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://suckow.com.br/marcelo/redes_status.php")
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
                                JSONObject object = jsonArray.getJSONObject(0);
                                if (object != null) {

                                    int aberto = object.getInt("aberto");


                                    if (aberto == 0) {
                                        pontoAberto = false;
                                    } else {
                                        pontoAberto = true;
                                    }

                                    sharedPreferences.edit()
                                            .putInt("aberto", aberto)
                                            .apply();

                                    if (pontoAberto) {
                                        pontoButton.setText("Fechar Ponto");
                                        textPonto.setText("Ponto Aberto");
                                        textPonto.setTextColor(getResources().getColor(R.color.green));
                                    } else {
                                        pontoButton.setText("Abrir Ponto");
                                        textPonto.setText("Ponto Fechado");
                                        textPonto.setTextColor(getResources().getColor(R.color.red));
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();
                                    Log.e("CONNECTION_FAILURE", "Erro");
                                }
                            } else {
                                pontoAberto = false;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        pontoAberto = false;
                    }
                } else {
                    pontoAberto = false;
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(LOADER, null, callbacks);
    }


    public void baterPonto() {
        callbacks2 = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<String>(getApplicationContext()) {

                    int id = sharedPreferences.getInt("idFuncionario", 0);
                    int aberto = 1 - sharedPreferences.getInt("aberto", 0);

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
                                    .add("macAddress", macAddress + "")
                                    .add("aberto", aberto + "")
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://suckow.com.br/marcelo/redes_ponto.php")
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

                if (responseString != null && !responseString.equals("null") && !responseString.equals("")) {
                    Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();
                    Log.e("CONNECTION_FAILURE", "Erro");
                } else {

                    pontoAberto = !pontoAberto;

                    if(pontoAberto){
                        pontoButton.setText("Fechar Ponto");
                        textPonto.setText("Ponto Aberto");
                        textPonto.setTextColor(getResources().getColor(R.color.green));
                        sharedPreferences.edit()
                                .putInt("aberto", 1)
                                .apply();
                    }else{
                        pontoButton.setText("Abrir Ponto");
                        textPonto.setText("Ponto Fechado");
                        textPonto.setTextColor(getResources().getColor(R.color.red));
                        sharedPreferences.edit()
                                .putInt("aberto", 0)
                                .apply();
                    }

                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(LOADER2, null, callbacks2);

    }


    public void getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                macAddress = res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }

    }

}
