package com.example.brio.anti_counterfeit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1234;
    private static final int CAMERA_PERMISSION_REQUEST = 1706;
    private final String TAG = getClass().getSimpleName();
    private EditText textSerial;
    private Button buttonscan;
    private Button buttonSubmit;
    private ProgressBar progressBar;
    private TextView textreport;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        textSerial = findViewById(R.id.textserial);
        buttonscan = findViewById(R.id.buttonscan);
        buttonSubmit = findViewById(R.id.buttonsubmit);
        progressBar = findViewById(R.id.progress_bar);
        textreport = findViewById(R.id.textreport);

        textreport.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, Activitylogin.class);
                startActivity(intent);
            }
        });

        buttonscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.CAMERA)) {
                        Toast.makeText(MainActivity.this,
                                "Please grant camera permission to use the barcode Scanner",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    }
                } else {
                    startActivityForResult(new Intent(MainActivity.this,
                            SimpleScannerActivity.class), CAMERA_REQUEST_CODE);
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serialNo = textSerial.getText().toString().trim();
                if (serialNo.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.enter_serial_number), Toast.LENGTH_SHORT).show();
                } else {
                    checkSerialNo(serialNo);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            textSerial.setText(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(MainActivity.this,
                            SimpleScannerActivity.class), CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please grant camera permission to use the barcode Scanner",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void checkSerialNo(String serialNo) {
        progressBar.setVisibility(View.VISIBLE);
        buttonSubmit.setVisibility(View.GONE);
        db.collection("items")
                .whereEqualTo("serialNo", serialNo)
                .get()
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        buttonSubmit.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                List<DocumentSnapshot> documents = result.getDocuments();
                                if (documents.size() > 0) {
                                    Item item = documents.get(0).toObject(Item.class);
                                    Log.d(TAG, item.toString());
                                    SimpleDateFormat simpleDateFormat = new
                                            SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle(R.string.item_found)
                                            .setMessage(String.format(Locale.getDefault(),
                                                    "%s\n%s\nManufacturing Date: %s\nExpiry Date: %s",
                                                    item.getName(),
                                                    item.getDescription(),
                                                    simpleDateFormat.format(item.getMfgDate()),
                                                    simpleDateFormat.format(item.getExpDate())
                                            ))
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                 @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            getString(R.string.item_not_found), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

