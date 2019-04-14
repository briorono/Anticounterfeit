package com.example.brio.anti_counterfeit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activitylogin extends AppCompatActivity {

    private Button btn_login;
    private TextView textsignup;
    private FirebaseAuth mAuth;
    private EditText edit_email, edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitylogin);

        btn_login = findViewById(R.id.btn_login);
        textsignup = findViewById(R.id.textsignup);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        mAuth=FirebaseAuth.getInstance();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString().trim();
                ;
                String password = edit_password.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(Activitylogin.this, "please enter email", Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(Activitylogin.this, "please enter password", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6) {
                    Toast.makeText(Activitylogin.this, "Password should be longer than 6 characters", Toast.LENGTH_LONG).show();
                }

                if (!email.isEmpty() && !password.isEmpty()) {

                    final ProgressDialog progressDialog = new ProgressDialog(Activitylogin.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    btn_login.setVisibility(View.INVISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Activitylogin.this, new OnCompleteListener<AuthResult>() {
                                private static final String TAG = "Login activity";

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(Activitylogin.this, HomeActivity.class));
                                        ActivityCompat.finishAffinity(Activitylogin.this);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        btn_login.setVisibility(View.VISIBLE);
                                        Toast.makeText(Activitylogin.this, task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });


        textsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Activitylogin.this, Signup.class);
                startActivity(intent);
            }
        });

    }
}
