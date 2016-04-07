package deepaksood.in.pcsmaassignment4;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;


    @Bind(R.id.btn_register) Button register;
    @Bind(R.id.sign_in_status) TextView mStatusTextView;
    @Bind(R.id.registered_mobile_num) TextView registeredMobileNum;

    private static String mobileNumText;

    UserObject userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);

        userObject = new UserObject();

        register.setOnClickListener(RegistrationActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            Toast.makeText(RegistrationActivity.this, "Error getting mobile NUm intent", Toast.LENGTH_SHORT).show();
        }
        else {
            mobileNumText = bundle.getString("MOBILE_NUM");
            registeredMobileNum.setText(mobileNumText);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                checkName();
                checkEmail();

                saveDataToAmazon();
                saveDataToSharedPreference();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("MOBILE_NUM",mobileNumText);
                startActivity(intent);
                finish();
                break;

            case R.id.sign_in_button:
                signIn();
                break;

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;
            mStatusTextView.setText(acct.getDisplayName());

        } else {
            // Signed out, show unauthenticated UI.
            mStatusTextView.setText("Signed Out");
        }
    }

    public void checkName() {

    }

    public void checkEmail() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG,"ConnectionFAiled");
    }

    private PrefManager pref;
    public void saveDataToSharedPreference() {
        pref = new PrefManager(getApplicationContext());
        Log.v(TAG,"prefRegistrationActivity: "+pref.isLoggedIn());
        pref.createLogin(mobileNumText);
        Log.v(TAG,"MoblieNum: "+pref.getMobileNumber());
    }

    public void saveDataToAmazon() {
        userObject.setMobileNum(mobileNumText);
        new db().execute();
    }

    private class db extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            CognitoCachingCredentialsProvider credentialsProvider;

            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:9420ebde-0680-48b5-a18f-886d70725554", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            if(mapper != null) {
                mapper.save(userObject);
                Log.v(TAG,"UserObject SAved: "+userObject.getMobileNum());
            }

            else
                Log.v(TAG,"not saved");

            return "Executed";
        }
    }

}
