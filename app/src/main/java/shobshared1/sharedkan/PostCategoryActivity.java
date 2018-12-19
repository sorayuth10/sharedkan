package shobshared1.sharedkan;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

public class PostCategoryActivity extends AppCompatActivity {
    private ImageButton FoodBtn,ProductBtn;
    private Drawable mActionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_category);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FoodBtn = (ImageButton) findViewById(R.id.food);
        ProductBtn = (ImageButton) findViewById(R.id.product);

        FoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent (PostCategoryActivity.this,PostActivity.class);
                startActivity(b);
            }
        });

        ProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent (PostCategoryActivity.this,ProductPostActivity.class);
                startActivity(x);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
