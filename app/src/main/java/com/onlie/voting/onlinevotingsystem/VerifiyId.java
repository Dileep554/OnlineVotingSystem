package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifiyId extends AppCompatActivity {

    String Phone;
    EditText Id;
    Button IdButton;
    private DatabaseReference mref;

    String myId,myAge,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifiy_age);

        Intent i=getIntent();
        Phone=i.getStringExtra("phone");

        Id=(EditText) findViewById(R.id.idproof);
        IdButton=(Button)findViewById(R.id.verifyagebutton);
        mref= FirebaseDatabase.getInstance().getReference();

        IdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(Id.getText().toString()))
                {
                    Toast.makeText(VerifiyId.this, "Please Enter you id..", Toast.LENGTH_LONG).show();
                }
                else
                {
                    mref.child("Users").child(Phone).child("ID").setValue(Id.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    Intent i=new Intent(VerifiyId.this,CameraActivity.class);
                                    i.putExtra("phone",Phone);
                                    startActivity(i);
                                }
                            });
                }
            }
        });

    }
}