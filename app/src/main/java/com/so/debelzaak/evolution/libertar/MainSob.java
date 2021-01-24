package com.so.debelzaak.evolution.libertar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class MainSob extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String peffoto;
    String nonname;
    File file77;
    private boolean mProcessLike = false;
    private TextView gh;
    String Ueidd;

    private CircleImageView stgsss;
    private boolean mAutorize = false;
    private boolean mAutorize2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sob_main);

        Ueidd = getIntent().getExtras().getString("ueid");

        mBlogList = findViewById(R.id.blog_list);
        stgsss = findViewById(R.id.sstings);
        gh = findViewById(R.id.gh);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainSob.this, Logn.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        checkUserExist();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void checkUserExist() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String user_id = user.getUid();
            mAutorize = true;
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAutorize) {
                        mAutorize = false;
                        if (!dataSnapshot.hasChild(user_id)) {
                            Intent setupIntent = new Intent(MainSob.this, SetupActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(setupIntent);
                            overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                        } else {
                            test();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void test() {
        if (InternetConnection.checkConnection(MainSob.this)) {
            cnnt();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainSob.this);
            builder.setMessage("Você não está conectado à internet!");
            builder.setCancelable(true);
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
            alertDialog11.show();
        }
    }

    private void cnnt() {
        mAutorize2 = true;
        mDatabaseUsers.child(Ueidd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAutorize2) {
                    mAutorize2 = false;
                    if (dataSnapshot.child("name").exists()) {
                        nonname = dataSnapshot.child("name").getValue().toString();
                        gh.setText("Estas são as contribuições feitas por " + nonname + "!");
                    }
                    if (dataSnapshot.child("image").exists()) {
                        peffoto = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString()).into(stgsss);
                    } else {
                        Intent setupIntent = new Intent(MainSob.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseRecyclerAdapter<Blog, BloViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BloViewHolder>(
                            Blog.class,
                            R.layout.blog_row,
                            BloViewHolder.class,
                            mDatabase) {
                        @Override
                        protected void populateViewHolder(final BloViewHolder viewHolder, final Blog model, final int position) {
                            final String post_key = getRef(position).getKey();

                            viewHolder.setTitle(model.getTitle());
                            viewHolder.setDesc(model.getDESCRIPTION());
                            viewHolder.setImage(getApplicationContext(), model.getIMAGE());
                            viewHolder.setUid(model.getUid());
                            viewHolder.setLikke(model.getLikke());
                            viewHolder.setNotik(model.getNotifi());
                            viewHolder.setTemads(model.getTemads());
                            viewHolder.setUsername(model.getUsername());

                            if (Ueidd.equals(viewHolder.uidd)){
                                viewHolder.itemView.setVisibility(View.VISIBLE);
                            }else {
                                viewHolder.itemView.setVisibility(View.GONE);
                                viewHolder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                            }

                            viewHolder.partilhar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bitmap bitmap77 = ((BitmapDrawable)viewHolder.post_image.getDrawable()).getBitmap();
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
                                    Uri photo777 = FileProvider.getUriForFile(MainSob.this,
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

                            viewHolder.post_description.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewHolder.post_description.setVisibility(View.GONE);
                                    viewHolder.post_textk.setVisibility(View.VISIBLE);
                                }
                            });

                            viewHolder.post_textk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewHolder.post_description.setVisibility(View.VISIBLE);
                                    viewHolder.post_textk.setVisibility(View.GONE);
                                    viewHolder.post_description.setMaxLines(2);
                                    viewHolder.post_description.setLines(2);
                                    viewHolder.post_description.setEllipsize(TextUtils.TruncateAt.END);
                                }
                            });

                            viewHolder.post_coment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent3 = new Intent(MainSob.this, Comentar.class);
                                    intent3.putExtra("postkeys", post_key);
                                    intent3.putExtra("foto", viewHolder.foto2);
                                    intent3.putExtra("titulo", viewHolder.titleds);
                                    intent3.putExtra("descridau", viewHolder.descs);
                                    intent3.putExtra("liko", viewHolder.likedg);
                                    intent3.putExtra("nommepub", viewHolder.nameds);
                                    intent3.putExtra("nomme", nonname);
                                    intent3.putExtra("pffots", peffoto);
                                    intent3.putExtra("muumn", "2");
                                    intent3.putExtra("ueidi", Ueidd);
                                    startActivity(intent3);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });

                            viewHolder.setLikeBtn(post_key);

                            viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mProcessLike = true;
                                    viewHolder.mLikebtn.setEnabled(false);
                                    viewHolder.mLikebtn.setClickable(false);
                                    mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (mProcessLike) {
                                                mProcessLike = false;
                                                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                                    mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    if (viewHolder.myIntValue > 0) {
                                                        int pts = viewHolder.myIntValue - 1;
                                                        String sol = String.valueOf(pts);
                                                        viewHolder.myIntValue = pts;
                                                        mDatabase.child(post_key).child("likke").setValue(sol).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    viewHolder.mLikebtn.setEnabled(true);
                                                                    viewHolder.mLikebtn.setClickable(true);
                                                                }
                                                            }
                                                        });
                                                    }

                                                } else {

                                                    mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                                    if (viewHolder.myIntValue > 0 || viewHolder.myIntValue == 0) {
                                                        int pts = viewHolder.myIntValue + 1;
                                                        String sol = String.valueOf(pts);
                                                        viewHolder.myIntValue = pts;
                                                        mDatabase.child(post_key).child("likke").setValue(sol).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    viewHolder.mLikebtn.setEnabled(true);
                                                                    viewHolder.mLikebtn.setClickable(true);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }
                    };
                    mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BloViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView contslikess, post_username, post_description, post_textk;
        ImageButton mLikebtn;
        FirebaseAuth mAuth;
        DatabaseReference mDatabaseLike;
        int myIntValue;
        int notfi;
        String foto2;
        String likedg, nameds, titleds, descs;
        ImageView post_image, post_coment, partilhar;
        int temi;
        String uidd;
        LinearLayout layty;

        public BloViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mLikebtn = mView.findViewById(R.id.post_like);
            contslikess = mView.findViewById(R.id.contslikes);
            post_coment = mView.findViewById(R.id.post_coment);
            partilhar = mView.findViewById(R.id.post_share);
            layty = mView.findViewById(R.id.layty);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseLike.keepSynced(true);
            mAuth = FirebaseAuth.getInstance();
            post_username = mView.findViewById(R.id.post_username);
            post_description = mView.findViewById(R.id.post_text);
            post_textk = mView.findViewById(R.id.post_textk);

        }

        public void setLikeBtn(final String post_key) {
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        mLikebtn.setImageResource(R.drawable.ic_yes_heart_colored);

                    } else {
                        mLikebtn.setImageResource(R.drawable.ic_no_heart_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setLikke(String likke) {
            int convertedVal = Integer.parseInt(likke);
            myIntValue = convertedVal;
            likedg = likke;
            contslikess.setText(likke);
        }

        public void setTitle(String title) {
            titleds = title;
        }

        public void setDesc(String DESCRIPTION) {
            post_textk.setText(DESCRIPTION);
            post_description.setText(DESCRIPTION);
            descs = DESCRIPTION;
        }

        public void setUsername(String username) {
            post_username.setText(username);
            nameds = username;
        }

        public void setImage(final Context ctx, final String IMAGE) {
            foto2 = IMAGE;
            post_image = mView.findViewById(R.id.post_image);
            Picasso.with(ctx)
                    .load(IMAGE)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(post_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            layty.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx)
                                    .load(IMAGE)
                                    .into(post_image, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            layty.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }
                    });
        }

        public void setNotik(String notifi) {
            int convertedVl = Integer.parseInt(notifi);
            notfi = convertedVl;
        }

        public void setTemads(String temads) {
            int convtedVl = Integer.parseInt(temads);
            temi = convtedVl;
        }

        public void setUid(String uid) {
            uidd = uid;
        }
    }

    @Override
    public void onBackPressed() {
        Intent innyx = new Intent(MainSob.this, MainActivity.class);
        startActivity(innyx);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}