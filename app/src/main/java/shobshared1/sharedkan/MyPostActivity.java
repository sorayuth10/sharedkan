package shobshared1.sharedkan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

public class MyPostActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private FirebaseUser mCurrentUser;
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MyPostActivity.this, LoginActivity.class);
                    /*
                     * Intent.FLAG_ACTIVITY_CLEAR_TOP
                     *
                     * If set, and the activity being launched is already running in the current task, then instead of launching a new instance
                     * of that activity, all of the other activities on top of it will be closed and this Intent will be delivered to the
                     * (now on top) old activity as a new Intent.
                     *
                     */
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };



        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mCurrentUser = mAuth.getCurrentUser();
        String currentUserID = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Blog");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserID); //Query all blogs for current user
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers.keepSynced(true); //Stores Data Offline
        mDatabaseLike.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.rvBlogList);


        //mBlogList.setHasFixedSize(true);
        //mBlogList.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(mLayoutManager);
        checkUserExists();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> adapt = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class, //Blog Reference
                R.layout.blog_row, //CardView
                BlogViewHolder.class, //View Holder Reference
                //mDatabase, //Database Reference
                mQueryCurrentUser //Query all blogs for current user
        ) {


            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                //Get Post Key
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle()); //Get Title From Model
                viewHolder.setDescription(model.getDescription()); //Get Description From Model
                viewHolder.setImageURL(getApplicationContext(), model.getImageURL());
                viewHolder.setUsername(model.getUsername());

                viewHolder.setLikeBtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent detailsBlogIntent = new Intent(MyPostActivity.this, BlogDetailsActivity.class);
                        detailsBlogIntent.putExtra("Blog_ID", post_key);
                        startActivity(detailsBlogIntent);
                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;


                        mDatabaseLike.addValueEventListener(new ValueEventListener() {


                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(mProcessLike == true) {
                                    //If there is a like for the post by the current user Likes->Post_Key->UserID
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                    } else {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("UserID");
                                        mProcessLike = false;
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

        mBlogList.setAdapter(adapt);
    }

    private void checkUserExists(){

        if(mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) { //If child does notexist
                        Intent mainIntent = new Intent(MyPostActivity.this, SetupActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;


        public BlogViewHolder(View v){
            super(v);
            mView = v;
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
            mLikeBtn = (ImageButton) mView.findViewById(R.id.btnLike);
            mDatabaseLike.keepSynced(true);
        }

        public void setLikeBtn(final String postKey){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeBtn.setImageResource(R.mipmap.action_like_accent);
                    }
                    else{
                        mLikeBtn.setImageResource(R.mipmap.action_like_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.txtPostTitle);
            post_title.setText(title);
        }

        public void setDescription(String description){
            TextView post_description = (TextView) mView.findViewById(R.id.txtPostDescription);
            post_description.setText(description);
        }

        public void setImageURL(Context ctx, String imageURL){
            ImageView post_image = (ImageView) mView.findViewById(R.id.imgPostImage);
            Picasso.with(ctx).load(imageURL).into(post_image);
        }

        public void setUsername(String username){
            TextView post_username = (TextView) mView.findViewById(R.id.txtPostUsername);
            post_username.setText(username);
        }
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
            startActivity(new Intent(MyPostActivity.this, ChatActivity.class));
        }
        if(item.getItemId() == R.id.action_home) {
            startActivity(new Intent(MyPostActivity.this, HomeActivity.class));
        }
        if(item.getItemId() == R.id.action_food_post){
            startActivity(new Intent(MyPostActivity.this, FoodHomeActivity.class));
        }
        if(item.getItemId() == R.id.action_product_post){
            startActivity(new Intent(MyPostActivity.this, ProductHomeActivity.class));
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