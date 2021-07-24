package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    ImageView Auth,Tick;
    private ProgressDialog LoadingBar;
    private DatabaseReference mref;
    String Phone;
    public String isVote="0";
    TextView VoteNowTv,VotedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i=getIntent();
        Phone = i.getStringExtra("phone");
        mref= FirebaseDatabase.getInstance().getReference();

        Auth=(ImageView)findViewById(R.id.authenticate);
        Tick=(ImageView)findViewById(R.id.votedicon);
        VoteNowTv=(TextView)findViewById(R.id.votenowtv);
        VotedTv=(TextView)findViewById(R.id.votedtv);

        mref.child("Users").child(Phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isVote=dataSnapshot.child("Vote").getValue().toString();
                if(isVote.equals("1"))
                {
                    Tick.setVisibility(View.VISIBLE);
                    VotedTv.setVisibility(View.VISIBLE);
                    VoteNowTv.setVisibility(View.INVISIBLE);
                    Auth.setVisibility(View.INVISIBLE);

                }
                else
                {
                    Tick.setVisibility(View.INVISIBLE);
                    VotedTv.setVisibility(View.INVISIBLE);
                    VoteNowTv.setVisibility(View.VISIBLE);
                    Auth.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        LoadingBar=new ProgressDialog(this);



        final Executor executor = Executors.newSingleThreadExecutor();





        final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Fingerprint Authentication")
                .setSubtitle("")
                .setDescription("place you finger to vote")
                .setNegativeButton("Cancel", executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).build();

        final HomeActivity activity=this;

        Auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent i=new Intent(HomeActivity.this, VerifiyId.class);
                                i.putExtra("phone",Phone);
                                startActivity(i);

                                /**LoadingBar.setTitle("Please Wait");
                                LoadingBar.setMessage("Please wait while your vote is submitting in our database..");
                                LoadingBar.setCanceledOnTouchOutside(false);
                                LoadingBar.show();


                                mref.child("Users").child(Phone).child("Vote").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        LoadingBar.dismiss();

                                        Tick.setVisibility(View.VISIBLE);
                                        VotedTv.setVisibility(View.VISIBLE);
                                        VoteNowTv.setVisibility(View.INVISIBLE);
                                        Auth.setVisibility(View.INVISIBLE);
                                    }
                                });**/

                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.logout2:

                Intent intent=new Intent(HomeActivity.this,welcomeActivity.class);
                startActivity(intent);
                return true;

            case R.id.updatepassword2:
                Intent intent2=new Intent(HomeActivity.this,UserUpdatePassword.class);
                intent2.putExtra("phone",Phone);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
}
