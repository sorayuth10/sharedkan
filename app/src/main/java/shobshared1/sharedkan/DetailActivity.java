package shobshared1.sharedkan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class DetailActivity extends AppCompatActivity {

    private ImageButton mSetupImageBtn;
    private EditText mNameField,mLineField,mPhoneField;
    private Button mFinishSetupBtn;
    private Uri mImageUri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private ProgressBar progressBar;
    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users"); //Database Reference to Users Child
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images"); //Storage Reference to Profile Image child
        mProgress = new ProgressDialog(this);
        mSetupImageBtn = (ImageButton) findViewById(R.id.profileImageButton);
        mNameField = (EditText) findViewById(R.id.txt_name);
        mLineField = (EditText) findViewById(R.id.txt_line);
        mPhoneField = (EditText) findViewById(R.id.txt_phone);
        mFinishSetupBtn = (Button) findViewById(R.id.btn_submit);

        mFinishSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });


        //Open Gallery when Image Button is clicked
        mSetupImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

    }

    private void startSetupAccount(){
        final String name = mNameField.getText().toString().trim();
        final String line = mLineField.getText().toString().trim();
        final String phone = mPhoneField.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && mImageUri != null && !TextUtils.isEmpty(line) && !TextUtils.isEmpty(phone)){

            mProgress.setMessage("Finishing Setup...");
            mProgress.show();
            final StorageReference filePath = mStorage.child("Profile_images").child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()  {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    final String downloadUri = task.getResult().toString();

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mDatabaseUsers.child(user_id).child("name").setValue(name); //Set Name Field
                            mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);
                            mDatabaseUsers.child(user_id).child("line").setValue(line);
                            mDatabaseUsers.child(user_id).child("phone").setValue(phone);
                            mProgress.dismiss();
                            Intent mainIntent = new Intent(DetailActivity.this, HomeActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(mainIntent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

              /*  @Override
                public void onComplete(UploadTask.TaskSnapshot taskSnapshot) {
                    final String downloadUri = taskSnapshot.getStorage().getDownloadUrl().toString();

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mDatabaseUsers.child(user_id).child("name").setValue(name); //Set Name Field
                            mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);
                            mProgress.dismiss();
                            Intent mainIntent = new Intent(SetupActivity.this, HomeActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(mainIntent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }*/
            });
        }else {
            Toast.makeText(this, "Please enter all field", Toast.LENGTH_SHORT).show();}
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1) //Set 1,1 for square pixels
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImageBtn.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mSetupImageBtn.setImageURI(mImageUri);
        }
    }

}
