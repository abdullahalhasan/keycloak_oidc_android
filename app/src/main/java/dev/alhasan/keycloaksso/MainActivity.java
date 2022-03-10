package dev.alhasan.keycloaksso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TOKEN_URL = "http://192.168.202.79:8080/auth/realms/demo/protocol/openid-connect/token";
    //private static final String TOKEN_URL = "http://192.168.102.227:8080/auth/realms/demo/protocol/openid-connect/token";

    private ProgressDialog mProgressDialog;

    private TextInputEditText usernameET;
    private TextInputEditText passwordET;
    private TextView errorMsgTV;
    private Button loginButton;

    private String username;
    private String password;

    private RequestQueue queue;
    private JsonObjectRequest objRequest;
    private JSONObject tokenObject;

    private static final String clientID = "android-sso";
    private static final String clientSecret = "I0HSP60pbmjl0xs3afNk7Xsgxv1xxfTH";
    private static final String grantType = "password";
    private static final String scope = "openid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenObject = new JSONObject();
        queue = Volley.newRequestQueue(this);
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        errorMsgTV = findViewById(R.id.errorMessageTV);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameET.getText().toString().trim();
                password = passwordET.getText().toString().trim();
                //getAccessToken();
                //sendToServer();
                createStringRequest();
                showProgressDialog("Verifying User ...");
            }
        });

    }

    public void getAccessToken() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<AccessToken> call = service.getAccessToken(clientID, clientSecret, grantType, scope, username, password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    String token = response.body().getAccessToken();
                    Integer expiresIn = response.body().getExpiresIn();

                    Log.e(TAG, "KeycloakResponse- Token: " + token + ", ExpiresIn: " + expiresIn);
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "KeycloakError: " + t.toString());
                Toast.makeText(MainActivity.this, "Error: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createStringRequest() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, TOKEN_URL,
                response -> {
                    hideProgressDialog();
                    //Log.e(TAG, "VolleyResponseSuccess: " + response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String accessToken = object.getString("access_token");
                        String scope = object.getString("scope");
                        //Toast.makeText(this, "scope: "+scope, Toast.LENGTH_SHORT).show();
                        if (!accessToken.isEmpty()) {
                            usernameET.getText().clear();
                            passwordET.getText().clear();
                            errorMsgTV.setText("");
                            startActivity(new Intent(MainActivity.this,HomeActivity.class).putExtra("access-token",accessToken));
                            Toast.makeText(this, "Authentication Successful!", Toast.LENGTH_SHORT).show();
                        }
                        Log.e(TAG, "ObjectSuccess: " + accessToken);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();
                    Log.e(TAG, "VolleyResponseError: " + error);
                    Log.e(TAG, "VolleyResponseErrorCode: " + error.networkResponse.statusCode);
                    NetworkResponse response = error.networkResponse;
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        String errorMessage = obj.getString("error");
                        String errorDescription = obj.getString("error_description");
                        //Toast.makeText(this, "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
                        errorMsgTV.setText("Error: "+errorMessage+"\nDescription: "+errorDescription);
                        Log.e(TAG, "NetworkResponse: " + obj);

                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("grant_type", grantType);
                params.put("client_id", clientID);
                params.put("client_secret", clientSecret);
                params.put("scope", scope);
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}