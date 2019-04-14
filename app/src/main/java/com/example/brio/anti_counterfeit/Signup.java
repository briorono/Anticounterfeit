package com.example.brio.anti_counterfeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signup extends AppCompatActivity {
    private static final String TAG = "Signup";

    EditText edit_firstname, edit_lastname, edit_email, edit_phone, edit_password, edit_location, edit_Cpassword;
    Button btn_sign_up;
    RadioButton radio_male, radio_female;
    RadioGroup radioGroup;
    String gender;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);//editText initialization
        edit_firstname = findViewById(R.id.edit_firstname);
        edit_lastname = findViewById(R.id.edit_lastname);
        edit_email = findViewById(R.id.edit_emailAddress);
        edit_phone = findViewById(R.id.edit_phone);
        edit_password = findViewById(R.id.edit_password);
        edit_Cpassword = findViewById(R.id.edit_Cpassword);
        edit_location = findViewById(R.id.edit_location);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        radio_male = findViewById(R.id.radio_male);
        radio_female = findViewById(R.id.radio_female);

        radioGroup = findViewById(R.id.radio_group);
        progressBar = findViewById(R.id.simpleProgressBar);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtaining input values
                final String firstname = edit_firstname.getText().toString().trim();
                final String lastname = edit_lastname.getText().toString().trim();
                final String email = edit_email.getText().toString().trim();
                final String phone = edit_phone.getText().toString();
                String password = edit_password.getText().toString();
                String pass = edit_Cpassword.getText().toString();
                final String location = edit_location.getText().toString();

                int selectedId = radioGroup.getCheckedRadioButtonId();


                if (selectedId == radio_male.getId()) {
                    gender = "Male";
                } else if (selectedId == radio_female.getId()) {
                    gender = "Female";
                }


                //validation of input values
                if (firstname.isEmpty()) {
                    Toast.makeText(Signup.this, "please input firstname", Toast.LENGTH_LONG).show();
                } else if (lastname.isEmpty()) {
                    Toast.makeText(Signup.this, "please input lastname", Toast.LENGTH_LONG).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(Signup.this, "please enter email", Toast.LENGTH_LONG).show();
                } else if (phone.isEmpty()) {
                    Toast.makeText(Signup.this, "please enter phone number", Toast.LENGTH_LONG).show();
                } else if (pass.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Signup.this, "please enter password", Toast.LENGTH_LONG).show();
                } else if (location.isEmpty()) {
                    Toast.makeText(Signup.this, "please fill the location field", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6) {
                    Toast.makeText(Signup.this, "please ensure password is at least six characters", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(Signup.this, "please enter a valid email", Toast.LENGTH_SHORT).show();
                }


                if (!firstname.isEmpty()
                        && !lastname.isEmpty()
                        && !email.isEmpty()
                        && !phone.isEmpty()
                        && !password.isEmpty()
                        && isValidEmail(email)) {

                    progressBar.setVisibility(View.VISIBLE);
                    btn_sign_up.setVisibility(View.INVISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        final FirebaseUser mUser = mAuth.getCurrentUser();
                                        // Add a new document with a generated ID
                                        User user = new User(mUser.getUid(), firstname, lastname, email, phone, location, gender);
                                        db.collection("users")
                                                .document(user.getId())
                                                .set(user)
                                                .addOnCompleteListener(Signup.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            startActivity( new Intent(Signup.this, HomeActivity.class));
                                                            ActivityCompat.finishAffinity(Signup.this);
                                                        } else {
                                                            mUser.delete();
                                                            Toast.makeText(Signup.this, "Failed to register user", Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        btn_sign_up.setVisibility(View.VISIBLE);
                                        Toast.makeText(Signup.this, task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity( new Intent(Signup.this, HomeActivity.class));
            ActivityCompat.finishAffinity(Signup.this);
        }
    }

    public boolean isValidEmail(CharSequence target){
        return(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
