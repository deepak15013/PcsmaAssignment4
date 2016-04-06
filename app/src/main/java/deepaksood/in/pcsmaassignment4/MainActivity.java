package deepaksood.in.pcsmaassignment4;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;

import deepaksood.in.pcsmaassignment4.tabfragments.ChatsFragment;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsFragment;
import deepaksood.in.pcsmaassignment4.tabfragments.TimelineFragment;
import deepaksood.in.pcsmaassignment4.tabfragments.UserObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    PrefManager prefManager;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String mobileNumText="";

    UserObject userObject;

    CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userObject = new UserObject();

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefManager = new PrefManager(getApplicationContext());



        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mobileNumText = bundle.getString("MOBILE_NUM");
            Log.v(TAG,"mobile Num TExt: "+mobileNumText);

            userObject.setMobileNum(mobileNumText);
            new db().execute();
        }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(MainActivity.this, "camera", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "gallery", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "slideshow", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_log_out) {
            Toast.makeText(MainActivity.this, "log out", Toast.LENGTH_SHORT).show();
            prefManager.clearSession();
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimelineFragment(), "TIMELINE");
        adapter.addFragment(new ChatsFragment(), "CHATS");
        adapter.addFragment(new ContactsFragment(), "CONTACTS");
        viewPager.setAdapter(adapter);
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

    private class db extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:9420ebde-0680-48b5-a18f-886d70725554", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            if(mapper != null) {
                mapper.save(userObject);
            }

            else
                Log.v(TAG,"not saved");



            return "Executed";
        }
    }

}
