package com.so.debelzaak.evolution.libertar.ShowActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.so.debelzaak.evolution.libertar.Comentar;
import com.so.debelzaak.evolution.libertar.Editar_Post;
import com.so.debelzaak.evolution.libertar.InternetConnection;
import com.so.debelzaak.evolution.libertar.Logn;
import com.so.debelzaak.evolution.libertar.MainSob;
import com.so.debelzaak.evolution.libertar.Models.Blog;
import com.so.debelzaak.evolution.libertar.P_POLITICA;
import com.so.debelzaak.evolution.libertar.Pendente;
import com.so.debelzaak.evolution.libertar.PostActivity;
import com.so.debelzaak.evolution.libertar.ProfileActivity;
import com.so.debelzaak.evolution.libertar.R;
import com.so.debelzaak.evolution.libertar.SetupActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView floatingActionButton;
    private String peffoto;
    private String nonname;
    private File file77;
    private AutoCompleteTextView pesquiser;
    private ImageView btnsearch;
    int temalete;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> mhlist = new ArrayList<>();
    private boolean mProcessLike = false;
    private boolean mAutorize = false;
    private boolean mAutorize2 = false;
    private boolean mAutorize3 = false;
    private boolean mAutorize4 = false;

    private String sserch = "k";
    private CircleImageView stgsss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseInit();
        blogList();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentyx = new Intent(MainActivity.this, Pendente.class);
                startActivity(intentyx);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        checkUserExist();

        pesquiser = findViewById(R.id.pesquiser);
        btnsearch = findViewById(R.id.btnsearch);

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        temalete = sp.getInt("your_int_key", 0);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mhlist);
        pesquiser.setAdapter(adapter);

        ImageView barr = findViewById(R.id.barrar);
        barr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Saindo...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ImageView ejetarr = findViewById(R.id.ejetar);
        ejetarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        ImageView posts = findViewById(R.id.postar);
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                overridePendingTransition(0, 0);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        RelativeLayout likku = findViewById(R.id.likku);
        likku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutorize = true;
                mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mAutorize) {
                            mAutorize = false;
                            if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Intent intyx = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(intyx);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                Intent inty = new Intent(MainActivity.this, SetupActivity.class);
                                startActivity(inty);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sserch2 = pesquiser.getText().toString().trim();
                if (!sserch2.isEmpty()) {
                    if (mAuthListener != null) {
                        sserch = pesquiser.getText().toString().trim();
                        teclados();
                        mAuth.removeAuthStateListener(mAuthListener);
                        test();
                        mAuth.addAuthStateListener(mAuthListener);
                    }
                }
            }
        });

        pesquiser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1){
                    teclados();
                    sserch = "k";
                    if (mAuthListener != null) {
                        mAuth.removeAuthStateListener(mAuthListener);
                        test();
                        mAuth.addAuthStateListener(mAuthListener);
                    }
                }
            }
        });

    }

    private void teclados() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseInit() {
        mAutorize2 = true;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAutorize2) {
                    mAutorize2 = false;
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Intent loginIntent = new Intent(MainActivity.this, Logn.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);
    }

    private void blogList() {
        mBlogList = findViewById(R.id.blog_list);
        stgsss = findViewById(R.id.sstings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);
        floatingActionButton = findViewById(R.id.lixxo);
    }

    private void checkUserExist() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String user_id = user.getUid();
            mAutorize3 = true;
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAutorize3) {
                        mAutorize3 = false;
                        if (!dataSnapshot.hasChild(user_id)) {
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(setupIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        if (InternetConnection.checkConnection(MainActivity.this)) {
            cnnt();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
        mAutorize4 = true;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUsers.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAutorize4) {
                    mAutorize4 = false;
                    if (dataSnapshot.child("name").exists()) {
                        nonname = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("image").exists()) {
                        peffoto = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString()).into(stgsss);
                    } else {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

                            mhlist.add(viewHolder.titleds);
                            adapter.notifyDataSetChanged();

                            viewHolder.post_username.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent3 = new Intent(MainActivity.this, MainSob.class);
                                    intent3.putExtra("ueid", viewHolder.uidd);
                                    startActivity(intent3);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });

                            if (sserch.equals("k")) {
                            }else {
                                if (sserch.equals(viewHolder.titleds)) {
                                    viewHolder.mView.setVisibility(View.VISIBLE);
                                    pesquiser.setText(sserch);
                                } else {
                                    viewHolder.mView.setVisibility(View.GONE);
                                    viewHolder.mView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                                }
                                pesquiser.setFocusableInTouchMode(false);
                                pesquiser.setFocusable(false);
                                pesquiser.setFocusableInTouchMode(true);
                                pesquiser.setFocusable(true);
                            }

                            if (temalete != 0){
                                if (viewHolder.temi != 0) {
                                    if (temalete != viewHolder.temi) {
                                        viewHolder.mView.setVisibility(View.GONE);
                                        viewHolder.mView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                                    }else {
                                        viewHolder.mView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            viewHolder.partilhar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bitmap bitmap77 = ((BitmapDrawable)viewHolder.post_image.getDrawable()).getBitmap();
                                    String s77 = viewHolder.titleds + "\n" + "\n" + viewHolder.descs + "\n" + "\n" + "Veja mais postagens científicas no aplicativo tombstoned https://play.google.com/store/apps/details?id=com.so.debelzaak.evolution.libertar";
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
                                    Uri photo777 = FileProvider.getUriForFile(MainActivity.this,
                                            "com.so.debelzaak.evolution.libertar.ShowActivity.fileprovider",
                                            file77);
                                    sendIntent.putExtra(Intent.EXTRA_STREAM, photo777);
                                    sendIntent.setType("image/jpeg");
                                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    try {
                                        startActivity(sendIntent);
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(MainActivity.this, "Não foi possível", Toast.LENGTH_SHORT).show();
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
                                    Intent intent3 = new Intent(MainActivity.this, Comentar.class);
                                    intent3.putExtra("postkeys", post_key);
                                    intent3.putExtra("foto", viewHolder.foto2);
                                    intent3.putExtra("titulo", viewHolder.titleds);
                                    intent3.putExtra("descridau", viewHolder.descs);
                                    intent3.putExtra("liko", viewHolder.likedg);
                                    intent3.putExtra("nommepub", viewHolder.nameds);
                                    intent3.putExtra("nomme", nonname);
                                    intent3.putExtra("pffots", peffoto);
                                    intent3.putExtra("muumn", "1");
                                    intent3.putExtra("ueidi", viewHolder.uidd);
                                    startActivity(intent3);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            });

                            viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    if (viewHolder.uidd.equals(user.getUid())){
                                        Intent intent3 = new Intent(MainActivity.this, Editar_Post.class);
                                    intent3.putExtra("postkeys", post_key);
                                    intent3.putExtra("foto", viewHolder.foto2);
                                    intent3.putExtra("titulo", viewHolder.titleds);
                                    intent3.putExtra("descridau", viewHolder.descs);
                                    intent3.putExtra("liko", viewHolder.likedg);
                                    intent3.putExtra("temooo", viewHolder.temoo);
                                    startActivity(intent3);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                    return false;
                                }
                            });



                            if (viewHolder.notfi == 0) {
                                if (viewHolder.uidd.equals(user.getUid())) {
                                    int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, MainActivity.class),
                                            PendingIntent.FLAG_UPDATE_CURRENT);


                                    String channelId = "some_channel_id";
                                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder notificationBuilder =
                                            new NotificationCompat.Builder(MainActivity.this, channelId)
                                                    .setSmallIcon(R.drawable.ic_notifin)
                                                    .setContentTitle("Tombstoned - Mais Ciência.")
                                                    .setContentText("Sua postagem" + " '" + viewHolder.titleds + "' " + "foi aceita.")
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                                                    .setContentIntent(pendingIntent);


                                    NotificationManager notificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                            .setUsage(AudioAttributes.USAGE_ALARM)
                                            .build();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                                                "Sua postagem" + " '" + viewHolder.titleds + "' " + "foi aceita.",
                                                NotificationManager.IMPORTANCE_MAX);

                                        assert notificationManager != null;
                                        notificationManager.createNotificationChannel(channel);

                                        channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
                                    } else {
                                        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
                                    }

                                    assert notificationManager != null;
                                    notificationManager.notify(notificationId, notificationBuilder.build());

                                    mDatabase.child(post_key).child("notifi").setValue("2");
                                } else {
                                    int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, MainActivity.class),
                                            PendingIntent.FLAG_UPDATE_CURRENT);


                                    String channelId = "some_channel_id";
                                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder notificationBuilder =
                                            new NotificationCompat.Builder(MainActivity.this, channelId)
                                                    .setSmallIcon(R.drawable.ic_notifin)
                                                    .setContentTitle("Tombstoned - Mais Ciência.")
                                                    .setContentText(viewHolder.titleds)
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                                                    .setContentIntent(pendingIntent);


                                    NotificationManager notificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                            .setUsage(AudioAttributes.USAGE_ALARM)
                                            .build();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                                                viewHolder.titleds,
                                                NotificationManager.IMPORTANCE_MAX);

                                        assert notificationManager != null;
                                        notificationManager.createNotificationChannel(channel);

                                        channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
                                    } else {
                                        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
                                    }

                                    assert notificationManager != null;
                                    notificationManager.notify(notificationId, notificationBuilder.build());

                                }
                            }

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
        String fonntj;
        String foto2;
        String likedg, nameds, titleds, descs;
        ImageView post_image, post_coment, partilhar;
        int temi;
        String temoo;
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
            temoo = temads;
            temi = convtedVl;
        }

        public void setUid(String uid) {
            uidd = uid;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void logout() {
        mAuth.signOut();
        Intent logoutIntent = new Intent(MainActivity.this, Logn.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }

    public void privacidade(View view) {
        Intent inteny = new Intent(MainActivity.this, P_POLITICA.class);
        startActivity(inteny);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void excolhe(View view) {
        final AlertDialog dialo;
        dialo = new AlertDialog.Builder(this).create();
        View c = getLayoutInflater().inflate(R.layout.gerra, null);
        dialo.setCanceledOnTouchOutside(false);
        dialo.setCancelable(false);
        Spinner spinner = c.findViewById(R.id.spinner1);
        TextView bt = c.findViewById(R.id.bt);
        dialo.setView(c);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.temmas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(temalete);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                int n =  i;
                editor.putInt("your_int_key", n);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        dialo.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}