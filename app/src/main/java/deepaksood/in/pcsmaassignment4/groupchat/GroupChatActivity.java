package deepaksood.in.pcsmaassignment4.groupchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import deepaksood.in.pcsmaassignment4.R;

public class GroupChatActivity extends AppCompatActivity {

    private String groupName = "";
    private String ownerName = "";
    private String photoUrl = "";

    ImageView toolbarContactPic;
    TextView toolbarDisplayName;
    TextView toolbarPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Bundle bundle = getIntent().getExtras();
        groupName = bundle.getString("GROUP_NAME");
        ownerName = bundle.getString("OWNER_NAME");
        photoUrl = bundle.getString("PHOTO_URL");

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_chat_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarContactPic = (ImageView) findViewById(R.id.iv_toolbar_group_pic);
        toolbarDisplayName = (TextView) findViewById(R.id.tv_toolbar_group_name);
        toolbarPhoneNumber = (TextView) findViewById(R.id.tv_group_toolbar_number);

        toolbarDisplayName.setText(groupName);
        toolbarPhoneNumber.setText(ownerName);
        Picasso.with(getApplicationContext()).load(photoUrl).into(toolbarContactPic);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }
}
