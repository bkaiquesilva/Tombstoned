package com.so.debelzaak.evolution.libertar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSubmitBtn, btn2;
    private static final int GALLERY_REQUEST = 999;
    private ImageView mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private TextView seguee;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private ProgressDialog mprogressbar;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUSer;
    private Spinner spinner1;
    private LinearLayout luk2;
    int spin;
    private boolean mAutorize = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mprogressbar = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mPostTitle = findViewById(R.id.editText1);
        mPostDesc = findViewById(R.id.editText2);
        seguee = findViewById(R.id.seguee);
        luk2 = findViewById(R.id.lik2);
        mSubmitBtn = findViewById(R.id.btn);
        mSelectImage = findViewById(R.id.imageButton2);
        spinner1 = findViewById(R.id.spinner1);
        btn2 = findViewById(R.id.btn2);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUSer = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.temmas1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seguee.setVisibility(View.VISIBLE);
                mPostTitle.setVisibility(View.VISIBLE);
                mPostDesc.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
                luk2.setVisibility(View.GONE);
                mSubmitBtn.setVisibility(View.GONE);
            }
        });

        seguee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title_val = mPostTitle.getText().toString().trim();
                final String desc_val = mPostDesc.getText().toString().trim();
                int lini = mPostDesc.getLineCount();
                if (!TextUtils.isEmpty(title_val)) {
                    if (!TextUtils.isEmpty(desc_val)) {
                        if (lini == 5 || lini > 5) {
                            seguee.setVisibility(View.GONE);
                            mPostTitle.setVisibility(View.GONE);
                            mPostDesc.setVisibility(View.GONE);
                            btn2.setVisibility(View.VISIBLE);
                            luk2.setVisibility(View.VISIBLE);
                            mSubmitBtn.setVisibility(View.VISIBLE);
                        }else {
                            mPostDesc.setError("Mínimo 5 linhas!");
                        }
                    }else {
                        Toast.makeText(PostActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(PostActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageUri != null){
                    if (spin != 0){
                        startPosting();
                    }else {
                        Toast.makeText(PostActivity.this, "Escolha um tema!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(PostActivity.this, "Adicione uma imagem!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                spin = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void startPosting() {
        mprogressbar.setMessage("Postando...");

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null && spin != 0) {
            mprogressbar.show();
            mAutorize = true;
            StorageReference filepath = mStorageRef.child("Blog_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (mAutorize) {
                        mAutorize = false;
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final DatabaseReference newPost = mDatabase.push();
                        mDatabaseUSer.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String sprn = String.valueOf(spin);
                                newPost.child("Title").setValue(title_val);
                                newPost.child("DESCRIPTION").setValue(desc_val);
                                newPost.child("IMAGE").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(mCurrentUser.getUid());
                                newPost.child("likke").setValue("0");
                                newPost.child("notifi").setValue("0");
                                newPost.child("temads").setValue(sprn);
                                newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mprogressbar.dismiss();
                                            Vibrator vibrator = (Vibrator) PostActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(50);
                                            Toast.makeText(PostActivity.this, "Sucesso", Toast.LENGTH_LONG).show();
                                            Intent inntyx = new Intent(PostActivity.this, Pendente.class);
                                            startActivity(inntyx);
                                            overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                                            finish();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "Erro no servidor",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
            getImageSize();
        }
    }

    private void getImageSize(){
        Bitmap bitmap = ((BitmapDrawable)mSelectImage.getDrawable()).getBitmap();

        int fwidth = bitmap.getWidth();
        int fwidt = bitmap.getHeight();

            if (fwidt < fwidth){
                if (fwidth > 1024 || fwidt > 500){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setMessage("A largura e a altura máxima da imagem aceita é de 1024 x 500, redimensione sua imagem ao tamanho especificado e tente novamente!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
            }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setMessage("A imagem deve está na horizontal!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    @Override
    public void onBackPressed() {
        Intent innyx = new Intent(PostActivity.this, MainActivity.class);
        startActivity(innyx);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }
}
