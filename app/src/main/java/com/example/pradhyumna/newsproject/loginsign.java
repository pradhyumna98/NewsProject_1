package com.example.pradhyumna.newsproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pradhyumna.newsproject.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loginsign extends AppCompatActivity {
    EditText usersp , passsp , emailsp;
    EditText usersi , passsi;
    Button login , signup;
    FirebaseDatabase database;
    DatabaseReference users;
    SignInButton gbutton;
    FirebaseAuth mauth;
    private final static int RC_SIGN_IN = 2;
    GoogleApiClient mclient;
    FirebaseAuth.AuthStateListener malistener;

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(malistener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsign);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mclient = new GoogleApiClient.Builder(this).
                enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(loginsign.this , "Sign In Failed" , Toast.LENGTH_SHORT).show();
                    }
                }).
                addApi(Auth.GOOGLE_SIGN_IN_API , gso).build();

        gbutton = findViewById(R.id.gsign);

        usersi = findViewById(R.id.useret);
        passsi = findViewById(R.id.passet);

        login = findViewById(R.id.logbt);
        signup = findViewById(R.id.signbt);

        mauth = FirebaseAuth.getInstance();

        malistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent allgood = new Intent(loginsign.this , newspage.class);
                    startActivity(allgood);
                }
            }
        };

        database = FirebaseDatabase.getInstance();

        users = database.getReference("Users");
        
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showsignup();
            }
        });
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                signin(usersi.getText().toString() , passsi.getText().toString());
                
            }
        });

    }

    private void signin(final String user, final String pass) {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).exists()){
                    if(!user.isEmpty()){

                    User login = dataSnapshot.child(user).getValue(User.class);
                    if(login.getPassword().equals(pass)){
                        Toast.makeText(loginsign.this , "Login Success" , Toast.LENGTH_SHORT).show();
                        Intent homeactivity = new Intent(loginsign.this , newspage.class);
                        Common.currentuser = login;
                        startActivity(homeactivity);
                        finish();
                    }
                    else
                        Toast.makeText(loginsign.this , "Incorrect Password" , Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(loginsign.this , "Enter The Username" , Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(loginsign.this , "User Doesn't Exists!!" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showsignup() {

        AlertDialog.Builder alert = new AlertDialog.Builder(loginsign.this);
        alert.setTitle("Sign Up!");
        alert.setMessage("Please Fill Up the Deatials");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.signup , null);

        usersp = sign_up_layout.findViewById(R.id.useret1);
        passsp = sign_up_layout.findViewById(R.id.passet1);
        emailsp = sign_up_layout.findViewById(R.id.emailet1);

        alert.setView(sign_up_layout);
        alert.setIcon(R.drawable.ic_account_box_black_24dp);

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final User user = new User(usersp.getText().toString() , passsp.getText().toString() , emailsp.getText().toString());
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUsername()).exists())
                            Toast.makeText(loginsign.this , "User Already Exists!!" ,Toast.LENGTH_SHORT ).show();

                        else {
                            users.child(user.getUsername()).setValue(user);
                            Toast.makeText(loginsign.this , "Registration Successful" , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                dialogInterface.dismiss();
            }
        });
        alert.show();

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mclient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                Toast.makeText(loginsign.this , "Sign In Failed" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mauth.getCurrentUser();
                            //updateUI(user);
                        } else {

                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(loginsign.this , "Authentication Failed" , Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}
