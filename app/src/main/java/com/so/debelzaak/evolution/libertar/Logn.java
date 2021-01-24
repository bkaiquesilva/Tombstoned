package com.so.debelzaak.evolution.libertar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;

public class Logn extends AppCompatActivity{

    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mNewAccount;
    private ImageButton mLoginButton;
    private ProgressDialog mProgressbar;
    private DatabaseReference mDatabaseUsers;

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private static final int MY_PERMISSION = 1;
    private Boolean emailcheck;
    private TextView toolbar;
    private CheckBox checkbox;
    SharedPreferences checkbox1 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mProgressbar = new ProgressDialog(this);
        checkbox1 = getSharedPreferences("checkbo1", MODE_PRIVATE);

        mLoginEmailField = findViewById(R.id.email);
        mLoginPasswordField = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.signin);
        mNewAccount = findViewById(R.id.signup);
        toolbar = findViewById(R.id.toolbar);
        checkbox = findViewById(R.id.checkbox);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkbox.isChecked()){
                    checkbox1.edit().putBoolean("checkbo1", true).apply();
                }else {
                    checkbox1.edit().putBoolean("checkbo1", false).apply();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(Logn.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Logn.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(Logn.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(Logn.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION);
            }
        } else {
        }

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innttt = new Intent(Logn.this,Regt.class);
                startActivity(innttt);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mProgressbar.setMessage("Processando.....");
        mProgressbar.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressbar.dismiss();
                            mAuth = FirebaseAuth.getInstance();
                            Toast.makeText(Logn.this, "BEM-VINDO!!!", Toast.LENGTH_SHORT).show();
                            checkUserExist();
                        } else {
                            mProgressbar.dismiss();
                            Toast.makeText(Logn.this, "Erro no servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
            }
        }
    }


    private void checkLogin() {
        String email = mLoginEmailField.getText().toString().trim();
        String password = mLoginPasswordField.getText().toString().trim();

        if (!email.isEmpty() || !password.isEmpty()) {
            if (checkbox.isChecked()) {
                SharedPreferences msharedpref = getSharedPreferences("editos", MODE_PRIVATE);
                SharedPreferences.Editor editor = msharedpref.edit();
                editor.putString("emme", email);
                editor.putString("sene", password);
                editor.apply();
            } else {
                SharedPreferences msharedpref = getSharedPreferences("editos", MODE_PRIVATE);
                SharedPreferences.Editor editor = msharedpref.edit();
                editor.clear();
                editor.apply();
            }

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mProgressbar.setMessage("Processando.....");
                mProgressbar.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressbar.dismiss();
                            verificacao();
                        } else {
                            mProgressbar.dismiss();
                            Toast.makeText(Logn.this, "Erro no servidor", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    private void verificacao(){
        FirebaseUser user = mAuth.getCurrentUser();
        emailcheck = user.isEmailVerified();
        if (emailcheck){
            checkUserExist();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Logn.this);
            builder.setMessage("Entre no e-mail e clique no link de verificação para ativar a sua conta.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog11 = builder.create();
            alertDialog11.setCanceledOnTouchOutside(false);
            alertDialog11.show();
            mAuth.signOut();
        }
    }

    private void checkUserExist() {
        mProgressbar.setMessage("Carregando...");
        mProgressbar.show();
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    mProgressbar.dismiss();
                    Intent mainIntent = new Intent(Logn.this, MainActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    mProgressbar.dismiss();
                    Intent setupIntent = new Intent(Logn.this, SetupActivity.class);
                    startActivity(setupIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION:{
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(Logn.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    }
                }else {
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (InternetConnection.checkConnection(Logn.this)) {
            if (checkbox1.getBoolean("checkbo1", true)) {
                checkbox.setChecked(true);
            }
            SharedPreferences prefs2 = this.getSharedPreferences("editos", Context.MODE_PRIVATE);

            if (prefs2.getString("emme", "") != null || prefs2.getString("sene", "") != null){
                mLoginEmailField.setText(prefs2.getString("emme", ""));
                mLoginPasswordField.setText(prefs2.getString("sene", ""));
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Logn.this);
            builder.setMessage("Você não está conectado à internet!");
            builder.setCancelable(false);
            builder.setPositiveButton("Checar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });
            AlertDialog alertDialog11 = builder.create();
            alertDialog11.setCanceledOnTouchOutside(false);
            alertDialog11.show();
        }
    }
}