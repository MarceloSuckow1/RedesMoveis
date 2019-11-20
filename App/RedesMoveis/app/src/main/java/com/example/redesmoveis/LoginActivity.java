package com.example.redesmoveis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText textId, textSenha;
    SharedPreferences sharedPreferences;
    String macAddress;

    private static final int LOADER = 18;
    private LoaderManager.LoaderCallbacks<String> callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.buttonLogin);
        textId = findViewById(R.id.textID);
        textSenha = findViewById(R.id.textSenha);

        sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

        getMacAddress();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textId.getText().toString().equals("") && !textSenha.getText().toString().equals("")) {
                    login();
                }else{
                    Toast.makeText(getApplicationContext(), "Preencha os Campos", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getMacAddress(){
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


    public void login() {
        callbacks = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<String>(getApplicationContext()) {

                    int id = Integer.parseInt(textId.getText().toString());
                    String senha = textSenha.getText().toString();

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
                                    .add("senha", senha + "")
                                    .add("macAddress", macAddress + "")
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://suckow.com.br/marcelo/redes_login.php")
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

                Log.e("REPSONSE",responseString + "");
                if (responseString != null) {
                    if (responseString.length() > 0) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseString);
                            if (jsonArray.length() > 0) {
                                JSONObject object = jsonArray.getJSONObject(0);
                                if (object != null) {

                                    int idFuncionario = object.getInt("idFuncionario");
                                    String nome = object.getString("nome");

                                    sharedPreferences.edit()
                                            .putBoolean("logged", true)
                                            .putString("nome", nome)
                                            .putInt("idFuncionario", idFuncionario)
                                            .apply();

                                    openMainActivity();


                                } else {
                                    Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();
                                    Log.e("CONNECTION_FAILURE", "Erro");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_LONG).show();
                                Log.e("CONNECTION_FAILURE", "Erro");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Id ou senha erradas", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("CONNECTION_FAILURE", "Falha na Conexão");
                    Toast.makeText(getApplicationContext(), "Erro ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(LOADER, null, callbacks);
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
