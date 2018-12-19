package shobshared1.sharedkan;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity {
    private Button btn_signup, btn_login;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private TextView btn_register;

    ConnectivityManager connectivityManager;
    NetworkInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
//----------------------------------------------Auto Login<Start>---------------------------------------------------------//
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        if (mCurrentUser != null)
        {
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Users").child(mCurrentUser.getUid()).child("phone").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String x = dataSnapshot.getValue(String.class);
                    if(x!=null){
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }else {Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            // [assume we are signed-out (or have never signed in).]
            //go to LoginActivity..
        }
//----------------------------------------------Auto Login<End>-----------------------------------------------------------//
        Timer t = new Timer();
        boolean checkConnection=new LoginActivity().checkConnection(this);
        if (checkConnection) {
        } else {
            Toast.makeText(LoginActivity.this,
                    "Please check your connection", 1000).show();
            t.schedule(new splash(), 1000);
        }

        /*if (auth.getCurrentUser() == null) {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            finish();
        }*//*else{
            startActivity(new Intent(LoginActivity.this, DetailActivity.class));
            finish();
        }*/
        /*if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this,LoginActivity.class));
            finish();
        }*/
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btn_register = (TextView) findViewById(R.id.textViewRegister);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent (LoginActivity.this,MainActivity.class);
                startActivity(x);
            }
        });


        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //authenticate user
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) { // there was an error
                            if (password.length() < 6) {
                                progressBar.setVisibility(View.GONE);
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            mAuth = FirebaseAuth.getInstance();
                            mCurrentUser = mAuth.getCurrentUser();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("Users").child(mCurrentUser.getUid()).child("phone").addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String x = dataSnapshot.getValue(String.class);
                                    if(x!=null){
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }else {Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
            }
        });
    }

    class splash extends TimerTask {

        @Override
        public void run() {
            Intent i = new Intent(LoginActivity.this,RetryActivity.class);
            finish();
            startActivity(i);
        }
    }
    public boolean checkConnection(Context context) {
        // TODO Auto-generated method stub
        boolean flag = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                System.out.println(info.getTypeName());
                flag = true;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                System.out.println(info.getTypeName());
                flag = true;
            }
        } catch (Exception exception) {
            System.out.println("Exception at network connection.."
                    + exception);
        }
        return flag;

        //return false;
    }
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}


