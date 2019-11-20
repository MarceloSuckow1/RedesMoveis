package com.example.redesmoveis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{

    private ArrayList<History> historyList;
    private static RecyclerViewClickListener item;
    private Context context;

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }

    public HistoryAdapter(Context context, ArrayList<History> list, RecyclerViewClickListener itemListener){
        item = itemListener;
        this.context = context;
        historyList = list;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textData, textHora, textEntrada;
        View view;

        public HistoryViewHolder(View view) {
            super(view);
            textData =  view.findViewById(R.id.textData);
            textHora =  view.findViewById(R.id.textHora);
            textEntrada =  view.findViewById(R.id.textEntrada);
            this.view = view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            item.recyclerViewListClicked(view, this.getLayoutPosition());
        }
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = null;
        listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout, parent, false);

        return new HistoryViewHolder(listItem);
    }


    public void updateList(ArrayList<History> newlist) {
        historyList.clear();
        historyList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder layoutItem, int position) {

        boolean entrada = historyList.get(position).isEntrada();
        if(entrada){
            layoutItem.textEntrada.setText("Entrada");
            layoutItem.textEntrada.setTextColor(context.getResources().getColor(R.color.green));
        }else{
            layoutItem.textEntrada.setText("Saída");
            layoutItem.textEntrada.setTextColor(context.getResources().getColor(R.color.red));
        }

        layoutItem.textData.setText(historyList.get(position).getDataString());
        layoutItem.textHora.setText(historyList.get(position).getHoraString());
    }



    @Override
    public int getItemCount() {
        return historyList.size();
    }

}