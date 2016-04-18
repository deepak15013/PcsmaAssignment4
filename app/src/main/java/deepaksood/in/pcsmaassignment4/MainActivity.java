package deepaksood.in.pcsmaassignment4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import deepaksood.in.pcsmaassignment4.broadcastpackage.BroadcastActivity;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatMessage;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;
import deepaksood.in.pcsmaassignment4.servicepackage.RabbitMqService;
import deepaksood.in.pcsmaassignment4.tabfragments.ChatsFragment;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsFragment;
import deepaksood.in.pcsmaassignment4.tabfragments.TimelineFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    PrefManager prefManager;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String mobileNumText="";
    private String displayName = "";
    private String displayEmailId = "";
    private String photoUrl = "";
    private String coverUrl = "";

    TextView viewDisplayName;
    TextView viewEmailId;
    TextView mobileNum;
    ImageView displayPic;
    ImageView coverPic;

    private GoogleApiClient mGoogleApiClient;

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG,"OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        getDataFromPrefManager();
        setDataToNavigationDrawer();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        serviceIntent = new Intent(getBaseContext(), RabbitMqService.class);
        serviceIntent.putExtra("USER_QUEUE",mobileNumText);
        startService(serviceIntent);

    }


    public void getDataFromPrefManager() {
        prefManager = new PrefManager(getApplicationContext());
        mobileNumText = prefManager.getMobileNumber();
        displayName = prefManager.getDisplayName();
        displayEmailId = prefManager.getDisplayEmailId();
        photoUrl = prefManager.getPhotoUrl();
        coverUrl = prefManager.getCoverUrl();
    }

    public void setDataToNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        viewDisplayName = (TextView) header.findViewById(R.id.displayName);
        viewEmailId = (TextView) header.findViewById(R.id.displayEmailId);
        mobileNum = (TextView) header.findViewById(R.id.tv_mobile_num);
        displayPic = (ImageView) header.findViewById(R.id.displayPic);
        coverPic = (ImageView) header.findViewById(R.id.coverPic);

        viewDisplayName.setText(displayName);
        viewEmailId.setText(displayEmailId);
        mobileNum.setText(mobileNumText);

        if(!photoUrl.contentEquals(""))
            Picasso.with(this).load(photoUrl).into(displayPic);
        if(coverUrl != null)
            Picasso.with(this).load(coverUrl).fit().centerCrop().into(coverPic);
    }

    public String getMobileNumText() {
        return mobileNumText;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            Fragment contact = adapter.getItem(2);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(contact);
            ft.attach(contact);
            ft.commit();
            Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_broadcast) {
            Toast.makeText(MainActivity.this, "BroadCast Message", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BroadcastActivity.class);
            intent.putExtra("PROFILE_NUMBER",mobileNumText);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "gallery", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "slideshow", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_log_out) {
            Toast.makeText(MainActivity.this, "log out", Toast.LENGTH_SHORT).show();
            prefManager.clearSession();
            googleSignOut();
            Intent intent = new Intent(this, AddMobileActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "share", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_send) {
            Toast.makeText(MainActivity.this, "send", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.v(TAG,"Log out success: "+status);
                    }
                });
    }

    ViewPagerAdapter adapter;
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimelineFragment(), "TIMELINE");
        adapter.addFragment(new ChatsFragment(), "CHATS");
        adapter.addFragment(new ContactsFragment(), "CONTACTS");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG,"Cannot logout");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG,"onDestroy MainActivity Stopping SErvice");
        super.onDestroy();
        if(serviceIntent != null)
            stopService(serviceIntent);
    }
}
