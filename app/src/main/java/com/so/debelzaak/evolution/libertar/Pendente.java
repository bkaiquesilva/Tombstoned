package com.so.debelzaak.evolution.libertar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.so.debelzaak.evolution.libertar.Models.Blog;
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Pendente extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, mDatabaseUSer;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView gh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pendente);

        gh = findViewById(R.id.gh);

        mBlogList = findViewById(R.id.blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUSer = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent loginIntent = new Intent(Pendente.this, Logn.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase.child("Pendente").keepSynced(true);

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
                mDatabase.child("Pendente")) {
            @Override
            protected void populateViewHolder(final BloViewHolder viewHolder, final Blog model, final int position) {
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDESCRIPTION());
                viewHolder.setImage(getApplicationContext(), model.getIMAGE());
                viewHolder.setUid(model.getUid());
                viewHolder.setLikke(model.getLikke());
                viewHolder.setNotifia(model.getNotifi());
                viewHolder.setTemads(model.getTemads());
                viewHolder.setUsername(model.getUsername());

                if (viewHolder.uidd.equals(mCurrentUser.getUid())) {
                    viewHolder.mView.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.mView.setVisibility(View.GONE);
                    viewHolder.mView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                }

                viewHolder.post_textk.setVisibility(View.VISIBLE);
                viewHolder.post_description.setVisibility(View.GONE);

            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public void ifse(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Pendente.this);
        builder.setMessage("Todas as postagens são analisadas pela equipe que administra o aplicativo xenko.");
        builder.setCancelable(false);
        builder.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog11 = builder.create();
        alertDialog11.setCanceledOnTouchOutside(false);
        alertDialog11.show();
    }

    public static class BloViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView posts_imagem;
        TextView post_description, contslikess, post_textk;
        FirebaseAuth mAuth;
        LinearLayout layty;
        String titl, nome, desc, uidd, imagge, spons;

        public BloViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            post_textk = mView.findViewById(R.id.post_textk);
            post_description = mView.findViewById(R.id.post_text);
            posts_imagem = mView.findViewById(R.id.post_image);
            contslikess = mView.findViewById(R.id.contslikes);
            layty = mView.findViewById(R.id.layty);

            mAuth = FirebaseAuth.getInstance();

        }


        public void setLikke(String likke) {
            contslikess.setText(likke);
        }

        public void setTitle(String title) {
            titl = title;
        }

        public void setDesc(String DESCRIPTION) {
            post_textk.setText(DESCRIPTION);
            desc = DESCRIPTION;
            post_description.setText(DESCRIPTION);
        }

        public void setUsername(String username) {
            TextView post_username = mView.findViewById(R.id.post_username);
            nome = username;
            post_username.setText(username);
        }

        public void setImage(final Context ctx, final String IMAGE) {
            Picasso.with(ctx)
                    .load(IMAGE)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(posts_imagem, new Callback() {
                        @Override
                        public void onSuccess() {
                            imagge = IMAGE;
                            layty.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx)
                                    .load(IMAGE)
                                    .into(posts_imagem, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            imagge = IMAGE;
                                            layty.setVisibility(View.GONE);
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

        public void setTemads(String temads) {
            spons = temads;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentyw = new Intent(Pendente.this, MainActivity.class);
        startActivity(intentyw);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }
}