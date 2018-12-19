package shobshared1.sharedkan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class BlogDetailsActivity extends AppCompatActivity {

    String mPostKey = null;
    private DatabaseReference mDatabase,UserRef;
    private ImageView mBlogDetailsImage,mBlogDetailsImageProfile;
    private TextView mBlogDetailsTitle;
    private TextView mBlogDetailsDescription, mBlogDetailsName,mBlogDetailsLine,mBlogDetailsPhone;
    private FirebaseAuth mAuth;
    private Button mBtnRemovePost,mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String post_key = getIntent().getExtras().getString("Blog_ID");
        mPostKey = post_key;

        mAuth = FirebaseAuth.getInstance();
        mBlogDetailsImage = (ImageView) findViewById(R.id.imgPostImage);
        mBlogDetailsTitle = (TextView) findViewById(R.id.txtPostTitle);
        mBlogDetailsDescription = (TextView) findViewById(R.id.txtPostDescription);
        mBtnRemovePost = (Button) findViewById(R.id.btnRemovePost);

        mBlogDetailsImageProfile = (ImageView) findViewById(R.id.profileImageView);
        mBlogDetailsName = (TextView) findViewById(R.id.txtName);
        mBlogDetailsLine = (TextView) findViewById(R.id.txtLine);
        mBlogDetailsPhone = (TextView) findViewById(R.id.txtPhone);
        mChat = (Button) findViewById(R.id.button2);


        mDatabase.child("Blog").child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_description = (String) dataSnapshot.child("description").getValue();
                String post_image = (String) dataSnapshot.child("imageURL").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                mBlogDetailsTitle.setText(post_title);
                mBlogDetailsDescription.setText(post_description);
                Picasso.with(BlogDetailsActivity.this).load(post_image).into(mBlogDetailsImage);

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Map<String, Object> valuesMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    // Get push id value.
                    String key = valuesMap.get("uid").toString();

                    mDatabase.child("Users").orderByKey().equalTo(key).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ref : dataSnapshot.getChildren()){
                                        String post_name = (String) ref.child("name").getValue();
                                        String post_line = (String) ref.child("line").getValue();
                                        String post_phone = (String) ref.child("phone").getValue();
                                        String post_imageprofile = (String) ref.child("image").getValue();

                                        mBlogDetailsName.setText(post_name);
                                        mBlogDetailsLine.setText(post_line);
                                        mBlogDetailsPhone.setText(post_phone);
                                        if (post_imageprofile == "null"){
                                            mBlogDetailsImageProfile.setImageResource(R.drawable.profle);
                                        }else{
                                            Picasso.with(BlogDetailsActivity.this).load(post_imageprofile).into(mBlogDetailsImageProfile);}
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                        mBtnRemovePost.setVisibility(View.VISIBLE);
                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mBtnRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Blog").child(mPostKey).removeValue();
                Intent mainIntent = new Intent(BlogDetailsActivity.this, HomeActivity.class);
                startActivity(mainIntent);
            }
        });
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailsBlogIntent = new Intent(BlogDetailsActivity.this, ChatBlogActivity.class);
                detailsBlogIntent.putExtra("Blog_ID", post_key);
                startActivity(detailsBlogIntent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}