package com.example.infyULabs.fruit_adapter_view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infyULabs.ChooseTheData;
import com.example.infyULabs.Connection;
import com.example.infyULabs.R;
import com.example.infyULabs.RiceData;

import java.util.ArrayList;

public class Fruit_Adapter_View extends RecyclerView.Adapter<Fruit_Adapter_View.MyViewHolder> {
    ArrayList<String> listOfSearchingData;
    Context context;

    public Fruit_Adapter_View(ArrayList<String> listOfSearchingData) {
        this.context = context;
        this.listOfSearchingData = listOfSearchingData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // infalte the item Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_recycleview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder myViewHolder = new MyViewHolder(view); // pass the view to View Holder
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        // set the data in item
        holder.name.setText(listOfSearchingData.get(position));
        Log.e("value of position1", "" + position);
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                switch (position) {

                    case 0:
                        Log.e("value of position c0", "" + position);
// case 0 is the first item in the listView.
                        intent = new Intent(context, Connection.class);
                        intent.putExtra("apple", "Apple");
                        context.startActivity(intent);
                        break;
                    case 1:
                        Log.e("value of position c1", "" + position);
//case 1 is the second item in the listView.
                        intent = new Intent(context, Connection.class);
                        intent.putExtra("apple", "Chilli");
                        context.startActivity(intent);
                        break;
                    case 2:
                        Log.e("value of position c2", "" + position);

                        intent = new Intent(context, RiceData.class);
                        context.startActivity(intent);
                        break;
                    //add more if you have more items in listView
                }
                if (listOfSearchingData != null) {
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return listOfSearchingData.size();
    }

    public void filterList(ArrayList<String> filterdNames) {
        this.listOfSearchingData = filterdNames;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
