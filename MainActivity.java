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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    CallbackManager callbackManager;
    GoogleSignInOptions gso;
    FirebaseAuth mAuth;
    String default_web_client = "225700139864-21so6m5pt7o4gbpakc3fkimophgavrs6.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);

        initialize(); // Set up Basic Google and Facebook APIs to get it running

        FirebaseUser currentUser = mAuth.getCurrentUser();

        routeToDashboardIfLoggedIn(currentUser);

        LoginButton loginButton = findViewById(R.id.login_button);
        handleFacebookLogIn(loginButton);


        SignInButton signInButton = findViewById(R.id.sign_in_button);
        handleGoogleSignIn(signInButton);

        Button btn = findViewById(R.id.signIn);
        handleRegularLogIn(btn, mAuth);

        TextView signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setCancelable(false);
        progress.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            routeToDashboardIfLoggedIn(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "signInWithCredential:failure", task.getException());
                        }

                        progress.dismiss();

                    }
                });
    }

    private void handleFacebookLogIn(LoginButton loginButton) {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("demo", "Facebook Login successful");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("demo", "Facebook Login Failure - Error");
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "signInWithCredential:success - Facebook Firebase");
                            FirebaseUser user = mAuth.getCurrentUser();
                            routeToDashboardIfLoggedIn(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "signInWithCredential:failure - Facebook Firebase", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void handleGoogleSignIn(SignInButton signInButton) {
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent signInIntent = GoogleSignIn.getClient(MainActivity.this, gso).getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

        });

    }


    private void handleRegularLogIn(Button btn, final FirebaseAuth mAuth) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText emailEditText = (EditText)findViewById(R.id.LogInEmail);
                String email = emailEditText.getText().toString();

                EditText passwordEditText = (EditText)findViewById(R.id.LogInPassword);
                String password = passwordEditText.getText().toString();

                final ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Loading");
                progress.setCancelable(false);
                progress.show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("demo", "signInWithEmail:success");
                                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                                    startActivity(i);
                                    progress.dismiss();
                                } else {
                                    Log.w("demo", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }
                            }
                        });
            }
        });

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("demo","Account details: "+account.getDisplayName());
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("demo","Signed in through google FAILED  ------ "+e);

        }
    }


    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client)
                .requestEmail()
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(MainActivity.this);

        callbackManager = CallbackManager.Factory.create();
    }

    private void routeToDashboardIfLoggedIn(FirebaseUser user) {
        if(user!=null){
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }
}
