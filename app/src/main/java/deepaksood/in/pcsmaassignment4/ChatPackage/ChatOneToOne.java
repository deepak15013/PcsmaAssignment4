package deepaksood.in.pcsmaassignment4.ChatPackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import deepaksood.in.pcsmaassignment4.R;

public class ChatOneToOne extends AppCompatActivity {

    private static final String TAG = ChatOneToOne.class.getSimpleName();

    String userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_one_to_one);

        Bundle bundle = getIntent().getExtras();
        userNumber = bundle.getString("USER_NUMBER");

        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        toolbar.setTitle(userNumber);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
