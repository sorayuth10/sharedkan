package shobshared1.sharedkan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RetryActivity extends AppCompatActivity {
    private Button buttonRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retry);

        buttonRetry = (Button) findViewById(R.id.button);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(RetryActivity.this, LoginActivity.class);
                startActivity(x);
            }
        });
    }
}
