package shobshared1.sharedkan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DetailActivity extends AppCompatActivity {
    public Button btn_click;
    public EditText txt_name, txt_line, txt_phone,textView;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_name = (EditText) findViewById(R.id.txt_name);
        txt_line = (EditText) findViewById(R.id.txt_line);
        txt_phone = (EditText) findViewById(R.id.txt_phone);
        progressBar.setVisibility(View.GONE);

        btn_click = (Button) findViewById(R.id.btn_submit);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String get_name = txt_name.getText().toString();
                String get_line = txt_line.getText().toString();
                String get_phone = txt_phone.getText().toString();
                if (TextUtils.isEmpty(get_name)) {
                    Toast.makeText(getApplicationContext(), "Please fill the form!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(get_line)) {
                    Toast.makeText(getApplicationContext(), "Please fill the Topic!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(get_phone)) {
                    Toast.makeText(getApplicationContext(), "Please fill the Content!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendToFireBase(get_name, get_line, get_phone);
                Intent b = new Intent (DetailActivity.this,HomeActivity.class);
                startActivity(b);

            }
            private void sendToFireBase(String get_name, String get_line, String get_phone) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference listName;
                DatabaseReference listTopic;
                DatabaseReference listContent;
                mAuth = FirebaseAuth.getInstance();
                mCurrentUser = mAuth.getCurrentUser();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
                listName = ref.child("name");
                listName.setValue(get_name);
                listTopic = ref.child("line");
                listTopic.setValue(get_line);
                listContent = ref.child("phone");
                listContent.setValue(get_phone);
                progressBar.setVisibility(View.GONE);

                finish();

            }


        });


    }

}
