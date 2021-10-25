package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntryroomActivity extends AppCompatActivity {

    private String pwd, name;
    EditText eNickname, ePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entryroom);

        Intent intent = getIntent();
        pwd = intent.getStringExtra("pwd");
        name = intent.getStringExtra("name");

        eNickname = findViewById(R.id.nickname);
        ePwd = findViewById(R.id.tryPwd);
    }

    public void toCheck(View view) {


        String nickname = eNickname.getText().toString();
        String sPwd = ePwd.getText().toString();


        if (!sPwd.equals(pwd)){
            AlertDialog.Builder alertDialog =
                    new AlertDialog.Builder(EntryroomActivity.this);
            alertDialog.setMessage("密碼錯誤!!");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.setCancelable(false);
            alertDialog.show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("memberLoc").child(name).child(nickname);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent intent = new Intent(EntryroomActivity.this, MapsActivity.class);
                    intent.putExtra("from", "entry");
                    intent.putExtra("name", name);
                    intent.putExtra("nickname", nickname);
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder alertDialog =
                            new AlertDialog.Builder(EntryroomActivity.this);
                    alertDialog.setMessage("暱稱重複!!");
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