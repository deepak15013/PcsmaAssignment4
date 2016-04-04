package deepaksood.in.pcsmaassignment4;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddMobileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddMobileActivity.class.getSimpleName();

    private static final String API_KEY = "108929AruSfSMy3v4q56fe48cc";
    private static final String countryCode = "91";
    private static final String SENDER = "MUSTER";
    private static final String ROUTE = "4";
    private static String MESSAGE = "";
    private static int OTP = 123456;

    @Bind(R.id.btn_next) Button next;
    @Bind(R.id.et_mobile_num) EditText mobileNum;
    @Bind(R.id.btn_otp_submit) Button submit;
    @Bind(R.id.et_otp) EditText etOtp;
    @Bind(R.id.viewPagerVertical) ViewPager viewPager;

    private ViewPagerAdapter adapter;

    String mobileNumText;

    private static String url = "";

    private PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mobile);

        pref = new PrefManager(this);

        Log.v(TAG,"Logged in: "+pref.isLoggedIn());

        if(pref.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(0);

        next.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if(mobileNum.getText().toString().length() == 10) {
                    Toast.makeText(AddMobileActivity.this, "next", Toast.LENGTH_SHORT).show();
                    generateMessage();
                    mobileNumText = countryCode + mobileNum.getText().toString();
                    Log.v(TAG,"MoblieNUM: "+mobileNumText);
                    generateURL();
                    //sendOTP();
                    viewPager.setCurrentItem(1);
                    pref.createLogin(mobileNumText);
                    Log.v(TAG,"Logged in: "+pref.isLoggedIn());

                }
                else {
                    Toast.makeText(AddMobileActivity.this, "Enter a 10 digit number", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_otp_submit:
                Toast.makeText(AddMobileActivity.this, "submit clickd", Toast.LENGTH_SHORT).show();
                if(etOtp.getText().toString().length() == 6) {
                    if(etOtp.getText().toString().equals(String.valueOf(OTP))) {
                        Toast.makeText(AddMobileActivity.this, "validate otp", Toast.LENGTH_SHORT).show();
                        Toast.makeText(AddMobileActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(AddMobileActivity.this, "wrong otp", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AddMobileActivity.this, "enter a six digit otp", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void generateMessage() {
        Random random = new Random();
        //OTP = 100000 + random.nextInt(900000);
        Log.v(TAG,"OTP: "+OTP);
        MESSAGE = "Your Muster login OTP is "+ OTP + " Treat this as confidential. Use this to authenticate yourself to Muster.";
        Log.v(TAG,"Message: "+MESSAGE);
    }

    public void generateURL() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.msg91.com")
                .appendPath("api")
                .appendPath("sendhttp.php")
                .appendQueryParameter("authkey",API_KEY)
                .appendQueryParameter("mobiles",mobileNumText)
                .appendQueryParameter("message",MESSAGE)
                .appendQueryParameter("sender",SENDER)
                .appendQueryParameter("route",ROUTE)
                .appendQueryParameter("country",countryCode)
                .build();

        url = builder.toString();
        Log.v(TAG,"URL: "+url);
    }

    public void sendOTP() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v(TAG,"Response: "+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddMobileActivity.this, "Error sending data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.add_mobile_layout;
                    break;
                case 1:
                    resId = R.id.enter_otp_layout;
                    break;
            }
            return findViewById(resId);
        }
    }

}
