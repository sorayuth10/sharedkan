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

public class BlogDetailsActivity extends AppCompatActivity {

    private String mPostKey = null;
    private DatabaseReference mDatabase;
    private ImageView mBlogDetailsImage;
    private TextView mBlogDetailsTitle;
    private TextView mBlogDetailsDescription;
    private FirebaseAuth mAuth;
    private Button mBtnRemovePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        String post_key = getIntent().getExtras().getString("Blog_ID");
        mPostKey = post_key;

        mAuth = FirebaseAuth.getInstance();
        mBlogDetailsImage = (ImageView) findViewById(R.id.imgPostImage);
        mBlogDetailsTitle = (TextView) findViewById(R.id.txtPostTitle);
        mBlogDetailsDescription = (TextView) findViewById(R.id.txtPostDescription);
        mBtnRemovePost = (Button) findViewById(R.id.btnRemovePost);

        mDatabase.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String)dataSnapshot.child("title").getValue();
                String post_description = (String)dataSnapshot.child("description").getValue();
                String post_image = (String)dataSnapshot.child("imageURL").getValue();
                String post_uid = (String)dataSnapshot.child("uid").getValue();

                mBlogDetailsTitle.setText(post_title);
                mBlogDetailsDescription.setText(post_description);
                Picasso.with(BlogDetailsActivity.this).load(post_image).into(mBlogDetailsImage);

                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mBtnRemovePost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBtnRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(mPostKey).removeValue();
                Intent mainIntent = new Intent(BlogDetailsActivity.this, HomeActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}
