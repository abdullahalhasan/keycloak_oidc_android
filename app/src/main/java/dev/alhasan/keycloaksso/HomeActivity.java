package dev.alhasan.keycloaksso;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String accessToken;
    private String name;
    private String gender;
    private String mobile;
    private String designation;
    private String emailVerified;

    private Button logoutButton;
    private TextView nameTV;
    private TextView genderTV;
    private TextView mobileTV;
    private TextView designationTV;
    private TextView isEmailVerifiedTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        accessToken = getIntent().getStringExtra("access-token");
        nameTV = findViewById(R.id.nameTV);
        genderTV = findViewById(R.id.genderTV);
        mobileTV = findViewById(R.id.mobileTV);
        designationTV = findViewById(R.id.designationTV);
        isEmailVerifiedTV = findViewById(R.id.emailVerifiedTV);
        logoutButton = findViewById(R.id.logoutButton);

        JWT jwt = new JWT(accessToken);
        Claim nameData = jwt.getClaim("name");
        Claim genderData = jwt.getClaim("gender");
        Claim mobileData = jwt.getClaim("mobile");
        Claim designationData = jwt.getClaim("designation");
        Claim emailVerifiedData = jwt.getClaim("email_verified");

        name = nameData.asString();
        gender = genderData.asString();
        mobile = mobileData.asString();
        designation = designationData.asString();
        emailVerified = emailVerifiedData.asString();

        nameTV.setText(name);
        genderTV.setText(gender);
        mobileTV.setText(mobile);
        designationTV.setText(designation);
        isEmailVerifiedTV.setText(emailVerified);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Logout is successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Log.e(TAG,"Token: "+accessToken);
        Log.e(TAG,"Name: "+name);
        Log.e(TAG,"Gender: "+gender);
        Log.e(TAG,"Mobile: "+mobile);
        Log.e(TAG,"Designation: "+designation);
        Log.e(TAG,"EmailVerified: "+emailVerified);
    }
}