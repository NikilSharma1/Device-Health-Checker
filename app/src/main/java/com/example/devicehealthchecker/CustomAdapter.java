package com.example.devicehealthchecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private DetailInterface detailInterface;
    Context context;
    ArrayList<Checks> arrayList;
    public CustomAdapter(Context context,ArrayList<Checks> arrayList,DetailInterface detailInterface){
        this.context=context;
        this.arrayList=arrayList;
        this.detailInterface=detailInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.desc.setText(arrayList.get(position).getDescription());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.checkButton.setVisibility(View.VISIBLE);
//                holder.skipButton.setVisibility(View.VISIBLE);
                  detailInterface.sendDetail(holder.getAdapterPosition());
            }
        });
        holder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Button is pressed",Toast.LENGTH_SHORT).show();
            }
        });
        holder.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button checkButton;
        Button skipButton;
        TextView name;
        TextView desc;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkButton=itemView.findViewById(R.id.check);
            skipButton=itemView.findViewById(R.id.skip);
            name=itemView.findViewById(R.id.check_name);
            desc=itemView.findViewById(R.id.check_desc);
            cardView=itemView.findViewById(R.id.card_view);
        }
    }
}
