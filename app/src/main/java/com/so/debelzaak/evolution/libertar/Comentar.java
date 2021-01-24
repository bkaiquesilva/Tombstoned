package com.so.debelzaak.evolution.libertar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.so.debelzaak.evolution.libertar.Models.ChatMessage;
import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comentar extends AppCompatActivity {

    EditText input;
    RecyclerView chatRecView;
    DatabaseReference dbChatRef;
    ImageView img1;
    TextView txt1,txt2,txtnome, txtlike;
    String PostKey, nomess, descre, tittlo, fotok, likedd, ftst, nmpub, muumna, ueidii;
    private FirebaseAuth mAuth;
    private String usuer;
    LinearLayout lo1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentar);
        FloatingActionButton fab = findViewById(R.id.fab);

        chatRecView = findViewById(R.id.list_of_messages);
        mAuth = FirebaseAuth.getInstance();
        usuer = mAuth.getCurrentUser().getUid();
        dbChatRef = FirebaseDatabase.getInstance().getReference("Blog");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(false);
        chatRecView.setHasFixedSize(true);
        chatRecView.setLayoutManager(layoutManager);

        muumna = getIntent().getExtras().getString("muumn");
        PostKey = getIntent().getExtras().getString("postkeys");
        nomess = getIntent().getExtras().getString("nomme");
        descre = getIntent().getExtras().getString("descridau");
        tittlo = getIntent().getExtras().getString("titulo");
        fotok = getIntent().getExtras().getString("foto");
        likedd = getIntent().getExtras().getString("liko");
        ftst = getIntent().getExtras().getString("pffots");
        nmpub = getIntent().getExtras().getString("nommepub");
        ueidii = getIntent().getExtras().getString("ueidi");

        img1 = findViewById(R.id.post_imag);
        txt1 = findViewById(R.id.post_titl);
        txt2 = findViewById(R.id.post_tex);
        txtnome = findViewById(R.id.post_usernam);
        txtlike = findViewById(R.id.contslike);
        input = findViewById(R.id.input);

        lo1 = findViewById(R.id.lo1);

        Picasso.with(Comentar.this)
                .load(fotok)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(img1, new Callback() {
                    @Override
                    public void onSuccess() {
                        txt1.setText(tittlo);
                        txt2.setText(descre);
                        txtnome.setText(nmpub);
                        txtlike.setText(likedd);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(Comentar.this)
                                .load(fotok)
                                .into(img1, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        txt1.setText(tittlo);
                                        txt2.setText(descre);
                                        txtnome.setText(nmpub);
                                        txtlike.setText(likedd);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }
                });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(Comentar.this, Logn.class));
            overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabClick();
            }
        });
    }

    public void emojids(View view) {
        hideKeyboard();
        final AlertDialog ler;
        ler = new AlertDialog.Builder(this).create();
        View f = getLayoutInflater().inflate(R.layout.emoj, null);
        ler.setCanceledOnTouchOutside(true);
        ler.setCancelable(true);
        GridView g = (GridView) f.findViewById(R.id.GridView1);
        ler.setView(f);

        g.setAdapter(new ADT(this));

        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              if (i == 0){
                  String ejo = "❤";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 1){
                  String ejo = "\uD83D\uDD2D";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 2){
                  String ejo = "\uD83D\uDC9A";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 3){
                  String ejo = "\uD83C\uDD99";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 4){
                  String ejo = "☢";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 5){
                  String ejo = "\uD83D\uDE31";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 6){
                  String ejo = "\uD83D\uDE14";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 7){
                  String ejo = "\uD83D\uDE24";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 8){
                  String ejo = "⚛";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 9){
                  String ejo = "\uD83D\uDE02";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 10){
                  String ejo = "\uD83D\uDEF0";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 11){
                  String ejo = "\uD83E\uDD13";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 12){
                  String ejo = "\uD83E\uDD16";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 13){
                  String ejo = "\uD83D\uDD2C";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 14){
                  String ejo = "\uD83D\uDE37";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 15){
                  String ejo = "\uD83D\uDE12";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 16){
                  String ejo = "\uD83D\uDE2C";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 17){
                  String ejo = "❓";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 18){
                  String ejo = "\uD83D\uDC4D";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 19){
                  String ejo = "\uD83E\uDD14";
                  ejjim(ejo);
                  ler.dismiss();
              }
              if (i == 20){
                  String ejo = "\uD83D\uDD1F";
                  ejjim(ejo);
                  ler.dismiss();
              }
          }
        });

                ler.show();
    }

    private void ejjim(String ejo) {
        if (usuer != null) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(getApplicationContext(), "Você não está logado!", Toast.LENGTH_SHORT).show();
                logine();
            } else {
                    hideKeyboard();
                    lo1.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance()
                            .getReference("Blog")
                            .child(PostKey)
                            .child("uzComentario")
                            .push()
                            .setValue(new ChatMessage(
                                    "1",
                                    " ",
                                    ejo,
                                    ftst, "2", usuer, nomess)
                            );
                    Log.d("abcdabcd", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
                    layoutManager2.setStackFromEnd(true);
                    chatRecView.setHasFixedSize(true);
                    chatRecView.setLayoutManager(layoutManager2);
                }
            input.setText("");
        }
    }

    private void fabClick() {
        if (usuer != null) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(getApplicationContext(), "Você não está logado!", Toast.LENGTH_SHORT).show();
                logine();
            } else {
                String datae2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                hideKeyboard();
                lo1.setVisibility(View.VISIBLE);
                String message = input.getText().toString();
                if (message.isEmpty()) {
                    input.setError("Escreva algo!!");
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference("Blog")
                            .child(PostKey)
                            .child("uzComentario")
                            .push()
                            .setValue(new ChatMessage(
                                    input.getText().toString(), "1",
                                    "1",
                                    ftst, "1", usuer, nomess)
                            );
                    Log.d("abcdabcd", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
                    layoutManager3.setStackFromEnd(true);
                    chatRecView.setHasFixedSize(true);
                    chatRecView.setLayoutManager(layoutManager3);
                }
                input.setText("");
            }
        }
    }

    private void logine() {
        Intent logoutIntent = new Intent(Comentar.this, Logn.class);
        startActivity(logoutIntent);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(
                        ChatMessage.class,
                        R.layout.message_row,
                        ChatViewHolder.class,
                        dbChatRef.child(PostKey).child("uzComentario")) {
                    @Override
                    protected void populateViewHolder(final ChatViewHolder viewHolder, ChatMessage model, int position) {
                        final String chatKey = getRef(position).getKey();

                        viewHolder.setUidee(model.getUidee());
                        viewHolder.setMessageUser(model.getMessageUser());
                        viewHolder.setEout(model.getEout());
                        viewHolder.setEjin(model.getEjin());
                        viewHolder.setMessageText(model.getMessageText());
                        viewHolder.setMessageTime(model.getMessageTime());
                        viewHolder.setUserProfileImage(model.getProfileUrl(), getApplicationContext());

                        if (viewHolder.myIntVal == 2){
                            viewHolder.messageText.setVisibility(View.GONE);
                            viewHolder.ejinn.setVisibility(View.VISIBLE);
                            viewHolder.messageUsa.setVisibility(View.VISIBLE);
                        }else {
                            if (viewHolder.myIntVal == 1){
                                viewHolder.messageUsa.setVisibility(View.GONE);
                                viewHolder.ejinn.setVisibility(View.GONE);
                                viewHolder.messageText.setVisibility(View.VISIBLE);
                            }
                        }

                        viewHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (viewHolder.ohuid.equals(usuer)) {
                                    dbChatRef.child(PostKey).child("uzComentario").child(chatKey).removeValue();
                                }
                                return false;
                            }
                        });
                    }
                };
        chatRecView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTime;
        TextView messageText;
        TextView messageUsa;
        TextView ejinn;
        String ohuid;
        CircleImageView userProfileImage;
        private FirebaseAuth mAuth;
        String usueru;
        View v;
        int myIntVal;

        public ChatViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            messageTime = v.findViewById(R.id.message_time);
            messageText = v.findViewById(R.id.message_text);
            messageUsa = v.findViewById(R.id.message_usa);
            userProfileImage = v.findViewById(R.id.profile_image);
            ejinn = v.findViewById(R.id.emijj);

            mAuth = FirebaseAuth.getInstance();
            usueru = mAuth.getCurrentUser().getUid();
        }


        public void setMessageText(String message) {
            messageText.setText(message);
        }

        public void setUserProfileImage(String profile_url, Context mctx) {
                Picasso.with(mctx)
                        .load(profile_url)
                        .into(userProfileImage);

        }

        public void setEjin(String ejin) {
            ejinn.setText(ejin);
        }

        public void setEout(String eout) {
            int convertedVal = Integer.parseInt(eout);
            myIntVal = convertedVal;
        }

        public void setMessageUser(String messageUser) {
            messageUsa.setText(messageUser);
        }

        public void setUidee(String uidee) {
            ohuid = uidee;
        }

        public void setMessageTime(String messageTime2) {
            if (usueru != null) {
                if (ohuid.equals(usueru)) {
                    messageTime.setText("Eu comentei");
                } else {
                    messageTime.setText(messageTime2);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (muumna != null) {
            if (muumna.equals("1")) {
                Intent innyx = new Intent(Comentar.this, MainActivity.class);
                startActivity(innyx);
                overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                finish();
            } else if (muumna.equals("2")) {
                Intent innyx = new Intent(Comentar.this, MainSob.class);
                innyx.putExtra("ueid", ueidii);
                startActivity(innyx);
                overridePendingTransition(R.anim.volte, R.anim.volte_ii);
                finish();
            }
        }
    }
}
