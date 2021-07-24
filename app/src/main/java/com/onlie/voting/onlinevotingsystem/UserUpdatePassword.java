package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserUpdatePassword extends AppCompatActivity {

    EditText UpdatePassword,ConfirmPassword;
    Button UserBtn;
    ProgressDialog loadingBar;

    String pass1,pass2;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_password);

        loadingBar=new ProgressDialog(this);
        Intent i=getIntent();
        phone=i.getStringExtra("phone");

        UpdatePassword=(EditText)findViewById(R.id.updatepass1);
        ConfirmPassword=(EditText)findViewById(R.id.updatepass2);
        UserBtn=(Button)findViewById(R.id.userupdatepasswordbtn);

        UserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass1=UpdatePassword.getText().toString();
                pass2=ConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(pass1))
                {
                    Toast.makeText(UserUpdatePassword.this, "Please enter password...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(pass2))
                {
                    Toast.makeText(UserUpdatePassword.this, "Please confirm password...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(pass1.equals(pass2))
                    {
                        loadingBar.setTitle("Update Password");
                        loadingBar.setMessage("Please wait...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        ChangePassword(pass1,pass2);
                    }
                    else
                    {
                        Toast.makeText(UserUpdatePassword.this, "Please check your password ", Toast.LENGTH_SHORT).show();

                    }


                }


            }
        });
    }

    private void ChangePassword(final String pass1, String pass2) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RootRef.child("Users").child(phone).child("Password").setValue(pass1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(UserUpdatePassword.this, "Password update successful", Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent(UserUpdatePassword.this,welcomeActivity.class);
                                    startActivity(i);
                                }
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

