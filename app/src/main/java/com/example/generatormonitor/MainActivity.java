package com.example.generatormonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView tempvalue,petrollevel,currenttxt,generatortxt;
    Switch generatorswitch;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if internet is not available
        if (checkInternet()!=true)
        {
            Intent i = new Intent(MainActivity.this,No_internet.class);
            startActivity(i);
            finish();
        }
        else
        {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        }

        setviews();
        setdata();
    }

    private void setdata() {
        reference= FirebaseDatabase.getInstance().getReference("Generator_Data");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()) {
                        String tt =""+snapshot.child("temp").getValue();
                        String petrollevell =""+snapshot.child("petlevel").getValue();
                        String currentt = ""+snapshot.child("current").getValue();
                        String statusren = ""+snapshot.child("status").getValue();

                        tempvalue.setText(tt);
                        currenttxt.setText(currentt);
                        petrollevel.setText(petrollevell);
                        if(statusren.equals("1")){
                            generatortxt.setText("Generator is On");
                            generatortxt.setTextColor(R.color.red);
                            generatorswitch.setChecked(true);
                        }
                        else {
                            generatortxt.setText("Generator is Off");
                            generatortxt.setTextColor(R.color.purple_200);
                            generatorswitch.setChecked(false);
                        }
                    }
                }
                else {
                    tempvalue.setText("0");
                    currenttxt.setText("0");
                    petrollevel.setText("Undefine");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setviews() {
        tempvalue = findViewById(R.id.tempvalue);
        currenttxt = findViewById(R.id.currentvalue);
        petrollevel = findViewById(R.id.petrollevel);
        generatorswitch = findViewById(R.id.genswitch);
        generatortxt = findViewById(R.id.gentext);
    }

    public void ongenerator(View view) {
        if (generatorswitch.isChecked()) {
            reference = FirebaseDatabase.getInstance().getReference("Generator_Data");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status","1");

            reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Update Successfully
                    Toast.makeText(MainActivity.this, "Generator On", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            reference = FirebaseDatabase.getInstance().getReference("Generator_Data");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", "0");

            reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Update Successfully
                    Toast.makeText(MainActivity.this, "Generator Off", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UserConttol.this, "Updation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public  boolean checkInternet(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo()!= null && manager.getActiveNetworkInfo().isConnectedOrConnecting();

    }
}