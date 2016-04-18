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
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;


    @Bind(R.id.btn_register) Button register;
    @Bind(R.id.sign_in_status) TextView mStatusTextView;
    @Bind(R.id.registered_mobile_num) TextView registeredMobileNum;
    @Bind(R.id.et_register_profile_name) TextView etRegisterProfileName;
    @Bind(R.id.et_register_email_id) TextView etRegisterEmailId;

    private static String mobileNumText;

    private String displayName="";
    private String displayEmailId="";
    private String photoUrl="https://drive.google.com/uc?id=1SI4M20f-etI-8wv1uwgFmFNiI-TFHZlshg";
    private String coverUrl="https://drive.google.com/uc?id=0B1jHFoEHN0zfek43ajZrMDZSSms";

    UserObject userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);

        userObject = new UserObject();

        register.setOnClickListener(RegistrationActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestProfile()
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
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
                startActivity();

                break;

            case R.id.sign_in_button:
                signIn();
                break;

        }
    }

    public void startActivity() {
        saveDataToAmazon();
        saveDataToSharedPreference();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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

            displayName = acct.getDisplayName();
            displayEmailId = acct.getEmail();
            try {
                if(acct.getPhotoUrl() != null) {
                    photoUrl = acct.getPhotoUrl().toString();
                }
            } catch (Exception e) {
                Log.v(TAG,"exception e from: "+e);
            }


            if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                Person.Cover.CoverPhoto coverPhoto = null;
                if(person.getCover() != null) {
                    coverPhoto = person.getCover().getCoverPhoto();
                    coverUrl = coverPhoto.getUrl().toString();
                }
                else {
                    coverUrl = "https://drive.google.com/uc?id=0B1jHFoEHN0zfek43ajZrMDZSSms";
                }

            }

            assert acct != null;
            mStatusTextView.setText(acct.getDisplayName());
            startActivity();

        } else {
            // Signed out, show unauthenticated UI.
            mStatusTextView.setText("Signed Out");
        }
    }

    public void checkName() {
        displayName = etRegisterProfileName.getText().toString();
    }

    public void checkEmail() {
        displayEmailId = etRegisterEmailId.getText().toString();
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
        pref.saveUserData(displayName, displayEmailId, photoUrl, coverUrl);
        Log.v(TAG,"MoblieNum: "+pref.getMobileNumber());
    }

    public void saveDataToAmazon() {
        userObject.setMobileNum(mobileNumText);
        userObject.setDisplayName(displayName);
        userObject.setDisplayEmailId(displayEmailId);
        userObject.setPhotoUrl(photoUrl);
        userObject.setCoverUrl(coverUrl);
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
                Log.v(TAG,"name: "+userObject.getDisplayName());
                Log.v(TAG,"email: "+userObject.getDisplayEmailId());
                Log.v(TAG,"photo: "+userObject.getPhotoUrl());
                Log.v(TAG,"cover: "+userObject.getCoverUrl());
            }

            else
                Log.v(TAG,"not saved");

            return "Executed";
        }
    }

}
