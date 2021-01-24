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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Editar_Post extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 979;
    private String PostKey, descre, tittlo, fotok, likedd, temoo;
    private EditText editDesk, edittiti;
    private ProgressDialog mprogressbar;
    private TextView txlikes;
    private ImageView imageadd2;
    private Spinner spinr1;
    private Uri mImageUri;
    int spin;
    private boolean mAutorize = false;
    DatabaseReference dbChatRef;
    private FirebaseAuth mAuth;
    private String usuer;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        PostKey = getIntent().getExtras().getString("postkeys");
        descre = getIntent().getExtras().getString("descridau");
        tittlo = getIntent().getExtras().getString("titulo");
        fotok = getIntent().getExtras().getString("foto");
        likedd = getIntent().getExtras().getString("liko");
        temoo = getIntent().getExtras().getString("temooo");

        mAuth = FirebaseAuth.getInstance();
        usuer = mAuth.getCurrentUser().getUid();
        dbChatRef = FirebaseDatabase.getInstance().getReference("Blog");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mprogressbar = new ProgressDialog(this);
        editDesk = findViewById(R.id.editDesk);
        txlikes = findViewById(R.id.txlikes);
        edittiti = findViewById(R.id.edittiti);
        imageadd2 = findViewById(R.id.imageadd2);
        spinr1 = findViewById(R.id.spinr1);
        edittiti.setText(tittlo);
        txlikes.setText(likedd);
        editDesk.setText(descre);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.temmas1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinr1.setAdapter(adapter);

        spin = Integer.parseInt(temoo);
        spinr1.setSelection(spin);

        Picasso.with(Editar_Post.this)
                .load(fotok)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageadd2, new Callback() {
                    @Override
                    public void onSuccess() { }

                    @Override
                    public void onError() {
                        Picasso.with(Editar_Post.this)
                                .load(fotok)
                                .into(imageadd2, new Callback() {
                                    @Override
                                    public void onSuccess() { }
                                    @Override
                                    public void onError() { }
                                });
                    }
                });


        imageadd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        spinr1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                spin = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent inteny = new Intent(Editar_Post.this, MainActivity.class);
        startActivity(inteny);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }

    public void volterrr(View view) {
        onBackPressed();
    }

    public void Atualizee(View view) {
        mprogressbar.setMessage("Atualizando...");

        final String title_val = edittiti.getText().toString().trim();
        final String desc_val = editDesk.getText().toString().trim();

        if (mImageUri != null) {
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
                            final DatabaseReference newPost = dbChatRef.child(PostKey);
                            String sprn = String.valueOf(spin);
                            newPost.child("Title").setValue(title_val);
                            newPost.child("DESCRIPTION").setValue(desc_val);
                            newPost.child("IMAGE").setValue(downloadUrl.toString());
                            newPost.child("temads").setValue(sprn).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mprogressbar.dismiss();
                                        Vibrator vibrator = (Vibrator) Editar_Post.this.getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(50);
                                        Toast.makeText(Editar_Post.this, "Sucesso", Toast.LENGTH_LONG).show();
                                        Intent inntyx = new Intent(Editar_Post.this, MainActivity.class);
                                        startActivity(inntyx);
                                        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Editar_Post.this, "Erro no servidor",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else {
            if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && spin != 0) {
                mprogressbar.show();
                final DatabaseReference newPost = dbChatRef.child(PostKey);
                String sprn = String.valueOf(spin);
                newPost.child("Title").setValue(title_val);
                newPost.child("DESCRIPTION").setValue(desc_val);
                newPost.child("temads").setValue(sprn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mprogressbar.dismiss();
                            Vibrator vibrator = (Vibrator) Editar_Post.this.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(50);
                            Toast.makeText(Editar_Post.this, "Sucesso", Toast.LENGTH_LONG).show();
                            Intent inntyx = new Intent(Editar_Post.this, MainActivity.class);
                            startActivity(inntyx);
                            overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                            finish();
                        }
                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            imageadd2.setImageURI(mImageUri);
            getImageSize();
        }
    }

    private void getImageSize(){
        Bitmap bitmap = ((BitmapDrawable)imageadd2.getDrawable()).getBitmap();

        int fwidth = bitmap.getWidth();
        int fwidt = bitmap.getHeight();

        if (fwidt < fwidth){
            if (fwidth > 1024 || fwidt > 500){
                AlertDialog.Builder builder = new AlertDialog.Builder(Editar_Post.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(Editar_Post.this);
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
}
