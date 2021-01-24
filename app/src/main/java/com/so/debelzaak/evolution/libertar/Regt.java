package com.so.debelzaak.evolution.libertar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Regt extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ImageButton mRegBtn;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String name;
    private Button signin;
    private TextView toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signin = findViewById(R.id.signin);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mProgress = new ProgressDialog(this);
        mRegBtn = findViewById(R.id.signup);
        toolbar = findViewById(R.id.toolbar2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mNameField = findViewById(R.id.name);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setupIntent = new Intent(Regt.this, Logn.class);
                startActivity(setupIntent);
                overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                finish();
            }
        });
    }

    private void startRegister() {
        name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgress.setMessage("Criando conta......");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        enviarverificar();
                        Toast.makeText(Regt.this, "Sucesso", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    } else {
                        Toast.makeText(Regt.this, "Erro no servidor", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }

                }
            });

        }

    }

    private void enviarverificar(){
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Regt.this);
                                    builder.setMessage("Email de verificação enviado!");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Intent mainIntent = new Intent(Regt.this, Logn.class);
                                            startActivity(mainIntent);
                                            overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                                            finish();
                                        }
                                    });
                                    AlertDialog alertDialog11 = builder.create();
                                    alertDialog11.setCanceledOnTouchOutside(false);
                                    alertDialog11.show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(Regt.this, "Erro no servidor", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }
}
