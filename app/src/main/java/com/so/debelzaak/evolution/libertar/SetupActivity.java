package com.so.debelzaak.evolution.libertar;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.so.debelzaak.evolution.libertar.FCMthings.MyfirebaseInstanceServices;
import com.so.debelzaak.evolution.libertar.FCMthings.SharedPrefManager;
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImg;
    private CircleImageView mSetupImage;
    private EditText mNameField;
    private ImageButton mFinishBtn;
    private Uri mImageUri = null;
    private DatabaseReference mDatabseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private static final int GALLARY_REQUEST = 1;
    private ProgressDialog mProgress;
    private BroadcastReceiver broadcastReceiver;
    private FirebaseUser currentUser;
    private String tokenString = null;
    private boolean mAutorize = false;
    private int cheimg = 0;

    public SetupActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        registerReceiver(broadcastReceiver, new IntentFilter(MyfirebaseInstanceServices.TOKEN_BROADCAST));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tokenString = SharedPrefManager.getInstance(SetupActivity.this).getToken();
            }
        };
        tokenString = SharedPrefManager.getInstance(SetupActivity.this).getToken();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mNameField = (EditText) findViewById(R.id.setupName);
        mSetupImage = (CircleImageView) findViewById(R.id.setupImagebtn);
        mSetupImg = (ImageButton) findViewById(R.id.imgImagebtn);
        mFinishBtn = findViewById(R.id.finishbtn);
        mProgress = new ProgressDialog(this);

        if (currentUser != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabseUsers.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("name").exists()) {
                            String nme = dataSnapshot.child("name").getValue().toString();
                            mNameField.setText(nme);
                        }
                        if (dataSnapshot.child("image").exists()) {
                            cheimg = 3;
                            final String image = dataSnapshot.child("image").getValue().toString();

                            Picasso.with(SetupActivity.this)
                                    .load(image)
                                    .into(mSetupImage, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            mSetupImg.setVisibility(View.GONE);
                                            mSetupImage.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError() {
                                            mSetupImg.setVisibility(View.VISIBLE);
                                            mSetupImage.setVisibility(View.GONE);
                                        }
                                    });
                    }else {
                            cheimg = 4;
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mSetupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLARY_REQUEST);
            }
        });

        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLARY_REQUEST);
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mSetupImg.setVisibility(View.GONE);
                mSetupImage.setVisibility(View.VISIBLE);
                mImageUri = result.getUri();
                mSetupImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("setupError", error + "");
            }
        }
    }

    private void startSetupAccount() {
        final String name = mNameField.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();

        if (name.contains("edebelzaakso") || name.contains("{E}DEBELZAAK S.O") || name.contains("{E}DEBELZAAKSO") || name.contains("EDEBELZAAKSO") || name.contains("EDEBELZAAK S.O") || name.contains("EDEBELZAAK SO")|| name.contains("{e}debelzaak s.o") || name.contains("{e}debelzaakso") || name.contains("edebelzaak s.o") || name.contains("edebelzaak so")){
            AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
            builder.setMessage("Variações da palavra \"{e}debelzaak s.o\" são proibidas.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mNameField.getText().clear();
                }
            });
            AlertDialog alertDialog11 = builder.create();
            alertDialog11.show();
        }else {
            if (cheimg == 3){
                if (mImageUri == null) {
                    if (!TextUtils.isEmpty(name)) {
                        mProgress.setMessage("Salvando Perfil.....");
                        mProgress.show();

                        mDatabseUsers.child(user_id).child("name").setValue(name);
                        mDatabseUsers.child(user_id).child("data").setValue("0");
                        mDatabseUsers.child(user_id).child("token").setValue(tokenString);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.dismiss();
                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Toast.makeText(SetupActivity.this, "Sucesso", Toast.LENGTH_LONG).show();
                                startActivity(mainIntent);
                                overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                            }
                        }, 600);
                    }
                }else {
                    if (!TextUtils.isEmpty(name) && mImageUri != null) {
                        mProgress.setMessage("Salvando Perfil.....");
                        mProgress.show();
                        mAutorize = true;

                        StorageReference filepath = mStorageRef.child(mImageUri.getLastPathSegment());
                        filepath.putFile(mImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if (mAutorize) {
                                            mAutorize = false;
                                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                            mDatabseUsers.child(user_id).child("name").setValue(name);
                                            mDatabseUsers.child(user_id).child("data").setValue("0");
                                            mDatabseUsers.child(user_id).child("image").setValue(downloadUri);
                                            mDatabseUsers.child(user_id).child("token").setValue(tokenString);

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mProgress.dismiss();
                                                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    Toast.makeText(SetupActivity.this, "Sucesso", Toast.LENGTH_LONG).show();
                                                    startActivity(mainIntent);
                                                    overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                                                }
                                            }, 600);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        mProgress.dismiss();
                                        Toast.makeText(SetupActivity.this, "Erro no servidor", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }else if (cheimg == 4){
                if (!TextUtils.isEmpty(name) && mImageUri != null) {
                    mProgress.setMessage("Salvando Perfil.....");
                    mProgress.show();
                    mAutorize = true;

                    StorageReference filepath = mStorageRef.child(mImageUri.getLastPathSegment());
                    filepath.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if (mAutorize) {
                                        mAutorize = false;
                                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                        mDatabseUsers.child(user_id).child("name").setValue(name);
                                        mDatabseUsers.child(user_id).child("data").setValue("0");
                                        mDatabseUsers.child(user_id).child("image").setValue(downloadUri);
                                        mDatabseUsers.child(user_id).child("token").setValue(tokenString);

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgress.dismiss();
                                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                Toast.makeText(SetupActivity.this, "Sucesso", Toast.LENGTH_LONG).show();
                                                startActivity(mainIntent);
                                                overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                                            }
                                        }, 600);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    mProgress.dismiss();
                                    Toast.makeText(SetupActivity.this, "Erro no servidor", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
         Intent ineyx = new Intent(SetupActivity.this, MainActivity.class);
         startActivity(ineyx);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
         finish();
    }
}
