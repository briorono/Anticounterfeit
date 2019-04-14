package com.example.brio.anti_counterfeit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView messagesContainer;
    private EditText messageEdit;
    private Button chatSendButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayAdapter<String> messagesAdapter;
    private List<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        messagesContainer = findViewById(R.id.messagesContainer);
        messageEdit = findViewById(R.id.messageEdit);
        chatSendButton = findViewById(R.id.chatSendButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        messages = new ArrayList<>();
        messagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messagesContainer.setAdapter(messagesAdapter);

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEdit.getText().toString().trim();
                if (!message.isEmpty()) {
                    final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    db.collection("messages")
                            .add(new Message(mAuth.getCurrentUser().getUid(), message))
                            .addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        fetchMessages();
                                        new AlertDialog.Builder(HomeActivity.this)
                                                .setMessage("Reported successfully")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        messageEdit.setText("");
                                                    }
                                                })
                                                .show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, "Reporting failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        fetchMessages();
    }

    private void fetchMessages() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        db.collection("messages")
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            messages.clear();
                            messagesAdapter.notifyDataSetChanged();
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                messages.add(documentSnapshot.toObject(Message.class).getMessage());
                                messagesAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}
