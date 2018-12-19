package shobshared1.sharedkan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

public class SetupEditActivity extends AppCompatActivity {

    private String mPostKey = null;
    private DatabaseReference mDatabase;
    private ImageView mBlogDetailsImage;
    private TextView mBlogDetailsName;
    private TextView mBlogDetailsPhone;
    private TextView mBlogDetailsLine;
    private FirebaseAuth mAuth;
    private Button mBtnRemovePost;
    private Button ChangeSetBtn1,MyEvent,Logout;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_edit);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        //String user_key = getIntent().getExtras().getString("User_ID");
        //mPostKey = user_key;

        mBlogDetailsImage = (ImageView) findViewById(R.id.profileImageView);
        mBlogDetailsName = (TextView) findViewById(R.id.profileNameView);
        mBlogDetailsPhone = (TextView) findViewById(R.id.profilePhoneView);
        mBlogDetailsLine = (TextView) findViewById(R.id.profileLineView);
        //mBtnRemovePost = (Button) findViewById(R.id.btnRemovePost);
        ChangeSetBtn1 = (Button) findViewById(R.id.ChangeSetBtn);
        MyEvent = (Button) findViewById(R.id.event);
        Logout = (Button) findViewById(R.id.LogoutBtn);

        mDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String post_name = (String)dataSnapshot.child("name").getValue();
                final String post_phone = (String)dataSnapshot.child("phone").getValue();
                final String post_line = (String)dataSnapshot.child("line").getValue();
                final String post_image = (String)dataSnapshot.child("image").getValue();
                //String post_uid = (String)dataSnapshot.child("uid").getValue();

                mBlogDetailsName.setText(post_name);
                mBlogDetailsPhone.setText(post_phone);
                mBlogDetailsLine.setText(post_line);
                Picasso.with(SetupEditActivity.this).load(post_image).into(mBlogDetailsImage);

                ChangeSetBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailsBlogIntent = new Intent(SetupEditActivity.this, SetupActivity.class);
                        detailsBlogIntent.putExtra("name", post_name);
                        detailsBlogIntent.putExtra("line", post_line);
                        detailsBlogIntent.putExtra("phone", post_phone);
                        detailsBlogIntent.putExtra("image", post_image);
                        startActivity(detailsBlogIntent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*mBtnRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(mPostKey).removeValue();
                Intent mainIntent = new Intent(SetupEditActivity.this, SetupActivity.class);
                startActivity(mainIntent);
            }
        });*/

        /*ChangeSetBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent (SetupEditActivity.this,SetupActivity.class);
                startActivity(x);
            }
        });*/

        MyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent (SetupEditActivity.this,MyPostActivity.class);
                startActivity(x);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                }
        });


    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_chat) {
            startActivity(new Intent(SetupEditActivity.this, ChatActivity.class));
        }
        if(item.getItemId() == R.id.action_food_post){
            startActivity(new Intent(SetupEditActivity.this, FoodHomeActivity.class));
        }
        if(item.getItemId() == R.id.action_product_post){
            startActivity(new Intent(SetupEditActivity.this, ProductHomeActivity.class));
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
