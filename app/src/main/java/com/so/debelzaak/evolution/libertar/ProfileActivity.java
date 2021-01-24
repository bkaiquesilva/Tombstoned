package com.so.debelzaak.evolution.libertar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.so.debelzaak.evolution.libertar.Models.Blog;
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView circleImageViewProfile;
    TextView textViewProfile;
    String uimage;
    String uname;
    private ImageView edit;

    private RecyclerView mBlogList7;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, mDatabaseUSer;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    File file77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImageViewProfile = findViewById(R.id.ProfilecircleImageView);
        textViewProfile = (TextView) findViewById(R.id.profileTextView);
        edit = findViewById(R.id.editProfile);
        mBlogList7 = findViewById(R.id.blog_list7);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList7.setHasFixedSize(true);
        mBlogList7.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUSer = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent loginIntent = new Intent(ProfileActivity.this, Logn.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        DatabaseReference mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    uname = String.valueOf(dataSnapshot.child("name").getValue());
                    textViewProfile.setText("Olá, " + uname + "!");

                }

                if (dataSnapshot.hasChild("image")) {
                    uimage = String.valueOf(dataSnapshot.child("image").getValue());
                    Picasso.with(getApplicationContext()).load(uimage).into(circleImageViewProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inteyx = new Intent(ProfileActivity.this, SetupActivity.class);
                startActivity(inteyx);
                overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        final FirebaseRecyclerAdapter<Blog, BloViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BloViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BloViewHolder.class,
                mDatabase) {
            @Override
            protected void populateViewHolder(final BloViewHolder viewHolder, final Blog model, final int position) {
                final String post_key = getRef(position).getKey();

                viewHolder.setUid(model.getUid());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDESCRIPTION());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikke(model.getLikke());
                viewHolder.setImage(getApplicationContext(), model.getIMAGE());
                viewHolder.setNotifia(model.getNotifi());

                if (viewHolder.uidd.equals(mCurrentUser.getUid())) {
                    viewHolder.mView.setVisibility(View.VISIBLE);
                    viewHolder.mLikebtn.setImageResource(R.drawable.ic_yes_heart_colored);
                }else {
                    viewHolder.mView.setVisibility(View.GONE);
                    viewHolder.mView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                }

                viewHolder.deletr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setMessage("Quer mesmo deletar esta postagem?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                mDatabase.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Vibrator vibrator = (Vibrator) ProfileActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(50);
                                            Toast.makeText(ProfileActivity.this, "Sucesso", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertDialog11 = builder.create();
                        alertDialog11.setCanceledOnTouchOutside(false);
                        alertDialog11.show();
                    }
                });

                viewHolder.partilhar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap77 = ((BitmapDrawable)viewHolder.posts_imagem.getDrawable()).getBitmap();
                        String s77 = viewHolder.titleds + "\n" + "\n" + viewHolder.descs + "\n" + "\n" + "Veja mais postagens científicas no aplicativo https://play.google.com/store/apps/details?id=com.so.debelzaak.evolution.libertar";
                        try {
                            file77 = new File(getExternalCacheDir(), "tombstoned.jpeg");
                            FileOutputStream stream77 = new FileOutputStream(file77);
                            bitmap77.compress(Bitmap.CompressFormat.JPEG,100,stream77);
                            stream77.flush();
                            stream77.close();
                            file77.setReadable(true,false);

                        }catch (Exception e){
                        }
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, s77);
                        Uri photo777 = FileProvider.getUriForFile(ProfileActivity.this,
                                "com.so.debelzaak.evolution.libertar.ShowActivity.fileprovider",
                                file77);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, photo777);
                        sendIntent.setType("image/jpeg");
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                        }
                    }
                });

                viewHolder.post_coment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent3 = new Intent(ProfileActivity.this, Comentar.class);
                        intent3.putExtra("postkeys", post_key);
                        intent3.putExtra("foto", viewHolder.foto2);
                        intent3.putExtra("titulo", viewHolder.titleds);
                        intent3.putExtra("descridau", viewHolder.descs);
                        intent3.putExtra("liko", viewHolder.likedg);
                        intent3.putExtra("nomme", uname);
                        intent3.putExtra("nommepub", viewHolder.nameds);
                        intent3.putExtra("pffots", uimage);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                    }
                });

            }
        };
        mBlogList7.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BloViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView posts_imagem, deletr, post_coment, partilhar;
        TextView post_description, contslikess;
        FirebaseAuth mAuth;
        String uidd;
        ImageButton mLikebtn;
        String likedg, nameds, titleds, descs, foto2;
        LinearLayout layty;

        public BloViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            post_description = mView.findViewById(R.id.post_text);
            posts_imagem = mView.findViewById(R.id.post_image);
            contslikess = mView.findViewById(R.id.contslikes);
            mLikebtn = mView.findViewById(R.id.post_like);
            deletr = mView.findViewById(R.id.deletr);
            layty = mView.findViewById(R.id.layty);
            post_coment = mView.findViewById(R.id.post_coment);
            partilhar = mView.findViewById(R.id.post_share);

            mAuth = FirebaseAuth.getInstance(); }


        public void setLikke(String likke) {
            likedg = likke;
            contslikess.setText(likke);
        }

        public void setTitle(String title) {
            titleds = title;
        }

        public void setDesc(String DESCRIPTION) {
            descs = DESCRIPTION;
            post_description.setText(DESCRIPTION);
        }

        public void setUsername(String username) {
            nameds = username;
            TextView post_username = mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

        public void setImage(final Context ctx, final String IMAGE) {
            foto2 = IMAGE;
            Picasso.with(ctx)
                    .load(IMAGE)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(posts_imagem, new Callback() {
                        @Override
                        public void onSuccess() {
                            layty.setVisibility(View.GONE);
                            deletr.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx)
                                    .load(IMAGE)
                                    .into(posts_imagem, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            layty.setVisibility(View.GONE);
                                            deletr.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }
                    });
        }

        public void setNotifia(String notifi) {
        }

        public void setUid(String uid) {
            uidd = uid;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intyx = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intyx);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }
}
