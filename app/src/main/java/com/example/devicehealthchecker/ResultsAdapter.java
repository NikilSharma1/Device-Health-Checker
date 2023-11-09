package com.example.devicehealthchecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    Context context;
    ArrayList<String> arrayList;
    public ResultsAdapter(Context context,ArrayList<String> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }
@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.results_item_view,parent,false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.test_result.setText(arrayList.get(position));
}

@Override
public int getItemCount() {
        return arrayList.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder{

    TextView test_result;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        test_result=itemView.findViewById(R.id.test_result);
    }
}
}