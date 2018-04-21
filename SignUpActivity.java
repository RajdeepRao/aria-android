package com.example.rajdeeprao.aria;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        Button createUser = findViewById(R.id.signIn);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText firestNameEditText = (EditText)findViewById(R.id.firstName);
                EditText lastNameEditText = (EditText)findViewById(R.id.lastName);
                EditText emailEditText = (EditText)findViewById(R.id.SignUpEmail);
                EditText passwordEditText = (EditText)findViewById(R.id.SignUpPassword);

                final String firstName= firestNameEditText.getText().toString();
                final String lastName = lastNameEditText .getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                final ProgressDialog progress = new ProgressDialog(SignUpActivity.this);
                progress.setTitle("Loading");
                progress.setCancelable(false);
                progress.show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("demo", "createUserWithEmail:success");
                                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstName).build();

                                    user.updateProfile(profileUpdates);
                                    startActivity(i);
                                    progress.dismiss();
                                } else {
                                    Log.w("demo", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }
                            }
                        });
            }
        });

    }
}
