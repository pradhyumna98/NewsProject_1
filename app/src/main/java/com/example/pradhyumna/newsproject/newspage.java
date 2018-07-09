package com.example.pradhyumna.newsproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class newspage extends AppCompatActivity {

    Button logoutbt;
    FirebaseAuth mauth;
    FirebaseAuth.AuthStateListener masl;

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(masl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspage);

        mauth = FirebaseAuth.getInstance();
        logoutbt = findViewById(R.id.logoubt);

        masl = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent allgood = new Intent(newspage.this , loginsign.class);
                    startActivity(allgood);
                }
            }
        };

        logoutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mauth.signOut();
            }
        });
    }
}
