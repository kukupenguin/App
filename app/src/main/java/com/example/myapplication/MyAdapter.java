package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;

    ArrayList<RoomHelperClass> list;

    public MyAdapter(Context context, ArrayList<RoomHelperClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RoomHelperClass room = list.get(position);
        holder.rName.setText(room.getrName());
        holder.pwd.setText(room.getPassword());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView rName, pwd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rName = itemView.findViewById(R.id.tvrName);
            pwd = itemView.findViewById(R.id.tvrPwd);
            View view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), EntryroomActivity.class);
                    intent.putExtra("pwd", pwd.getText().toString());
                    intent.putExtra("name", rName.getText().toString());
                    view.getContext().startActivity(intent);

                }
            });
        }
    }
}
