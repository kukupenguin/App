package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    EditText rName, pwd;
    ArrayList<RoomHelperClass> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        rName = findViewById(R.id.rName);
        pwd = findViewById(R.id.pwd);

    }

    public void toMap(View view) {

        String name = rName.getText().toString();
        String password = pwd.getText().toString();


        if(name.equals("") || password.equals("") ) {
            AlertDialog.Builder alertDialog =
                    new AlertDialog.Builder(this);
            alertDialog.setMessage("名稱或密碼為空!!");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.setCancelable(false);
            alertDialog.show();
            return;
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("room").child(name);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                        RoomHelperClass helperClass = new RoomHelperClass(name, password);
                        myRef.setValue(helperClass);
                        Intent intent = new Intent(CreateActivity.this, MapsActivity.class);
                        intent.putExtra("from", "create");
                        intent.putExtra("name", name);
                        startActivity(intent);
                }
                else  {
                    AlertDialog.Builder alertDialog =
                            new AlertDialog.Builder(CreateActivity.this);
                    alertDialog.setMessage("名稱重複!!");
                    alertDialog.setPositiveButton("OK", null);
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addListenerForSingleValueEvent(postListener);
        }



}