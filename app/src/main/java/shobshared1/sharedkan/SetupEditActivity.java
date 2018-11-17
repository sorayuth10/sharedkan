package shobshared1.sharedkan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Button ChangeSetBtn1;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_edit);

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

        mDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_name = (String)dataSnapshot.child("name").getValue();
                String post_phone = (String)dataSnapshot.child("phone").getValue();
                String post_line = (String)dataSnapshot.child("line").getValue();
                String post_image = (String)dataSnapshot.child("image").getValue();
                //String post_uid = (String)dataSnapshot.child("uid").getValue();

                mBlogDetailsName.setText(post_name);
                mBlogDetailsPhone.setText(post_phone);
                mBlogDetailsLine.setText(post_line);
                Picasso.with(SetupEditActivity.this).load(post_image).into(mBlogDetailsImage);

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

        ChangeSetBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent (SetupEditActivity.this,SetupActivity.class);
                startActivity(x);
            }
        });

    }

}
