package shobshared1.sharedkan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseUser;
import shobshared1.sharedkan.ChatMessage;
import shobshared1.sharedkan.R;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText input,pic;
    RecyclerView chatRecView;
    DatabaseReference dbChatRef;
    DatabaseReference mDatabase;
    FirebaseUser mCurrentUser;
    String image=null;
    String xxx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        FloatingActionButton fab = findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        chatRecView = findViewById(R.id.list_of_messages);
        dbChatRef = FirebaseDatabase.getInstance().getReference("/chat");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(false);
        chatRecView.setHasFixedSize(true);
        chatRecView.setLayoutManager(layoutManager);
        mCurrentUser = mAuth.getCurrentUser();
        pic = (EditText) findViewById(R.id.editText2);
        mDatabase.child("Users").child(mCurrentUser.getUid()).child("image").orderByValue().toString();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabClick();
            }
        });
    }

    public void fabClick() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "Not logged in!", Toast.LENGTH_SHORT).show();
            onStart();
        } else {
            input = findViewById(R.id.input);
            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            String message = input.getText().toString();
            if (message.isEmpty()) {
                input.setError("You can't post an empty Message. !!");
            } else {
                if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("chat")
                            .push()
                            .setValue(new ChatMessage(
                                    input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail(), "null")
                            );
                    Log.d("abcdabcd", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                    // Clear the input
                } else {

                    mDatabase.child("Users").child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {

                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String post_image = dataSnapshot.child("image").getValue().toString();
                            krub(post_image);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                    mDatabase.child("chat")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail(),xxx));

                    //Log.d("abcdabcd", String.valueOf(post_image));

                    /*checkpointtttttt*/
                }
            }
            input.setText("");
        }
    }
    public String krub(String x){
        pic.setText(x);
        xxx = pic.getText().toString();
        //Toast.makeText(ChatActivity.this, xxx, Toast.LENGTH_LONG).show();
        return xxx;
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(
                        ChatMessage.class,
                        R.layout.message_row,
                        ChatViewHolder.class,
                        dbChatRef) {
                    @Override
                    protected void populateViewHolder(ChatViewHolder viewHolder, ChatMessage model, int position) {
                        final String chatKey = getRef(position).getKey();

                        viewHolder.setMessageText(model.getMessageText());
                        viewHolder.setMessageTime(model.getMessageTime());
                        viewHolder.setUserName(model.getMessageUser());
                        viewHolder.setProfileUrl(model.getProfileUrl(), getApplicationContext());
                    }
                };
        chatRecView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView messageTime;
        TextView messageText;
        CircleImageView userProfileImage;



        public ChatViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.message_user);
            messageTime = itemView.findViewById(R.id.message_time);
            messageText = itemView.findViewById(R.id.message_text);
            userProfileImage = itemView.findViewById(R.id.profile_image);
        }

        public void setUserName(String usr) {
            userName.setText(usr);
        }

        public void setMessageTime(long time) {
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    time));
        }

        public void setMessageText(String message) {
            messageText.setText(message);
        }

        public void setProfileUrl(String profile_url, final Context mctx) {
            DatabaseReference mDatabase;
            FirebaseAuth mAuth;
            FirebaseUser mCurrentUser;
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();

            if (profile_url == "null"){
                userProfileImage.setImageResource(R.mipmap.baseline_person_black_24);
            }else{
                Picasso.with(mctx)
                        .load(profile_url)
                        .into(userProfileImage);
            }

        }
    }

    /*
    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }*/

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_home) {
            startActivity(new Intent(ChatActivity.this, HomeActivity.class));
        }
        if(item.getItemId() == R.id.action_food_post){
            startActivity(new Intent(ChatActivity.this, FoodHomeActivity.class));
        }
        if(item.getItemId() == R.id.action_product_post){
            startActivity(new Intent(ChatActivity.this, ProductHomeActivity.class));
        }
        if(item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(ChatActivity.this, SetupEditActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void logout(){
        mAuth.signOut();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
