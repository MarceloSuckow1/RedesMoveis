package com.example.redesmoveis;

import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class History {
    private Date data;
    private boolean entrada;

    public History(){}

    public History (Date data, boolean entrada){
        this.setData(data);
        this.setEntrada(entrada);
    }

    public String getDataString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

    public String getHoraString(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(data);
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isEntrada() {
        return entrada;
    }

    public void setEntrada(boolean entrada) {
        this.entrada = entrada;
    }
}
