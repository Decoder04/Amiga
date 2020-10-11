package com.dedoder.amiga;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dedoder.amiga.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL = "email";

    CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LoginButton loginButton = findViewById(R.id.login_button);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
                @Override
                public void onSuccess(com.facebook.login.LoginResult loginResult) {
                    // App code
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Log.d("FBLOGIN", "Login successful");
                    Log.d("FBLOGIN", "Successful: " + loginResult.getAccessToken());
                    Log.d("FBLOGIN", "User ID: " + Profile.getCurrentProfile().getId());
                    Log.d("FBLOGIN", "User Profile Pic Link: " + Profile.getCurrentProfile().getProfilePictureUri(500, 500));
                    performGraphRequest(loginResult);
                    Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(LoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
                    Log.d("FBLOGIN", "Login error: "  + exception.toString());
                }
            });
        }

        LoginManager.getInstance().retrieveLoginStatus(this, new LoginStatusCallback() {
            @Override
            public void onCompleted(AccessToken accessToken) {
                // User was previously logged in, can log them in directly here.
                // If this callback is called, a popup notification appears that says
                // "Logged in as <User Name>"
            }
            @Override
            public void onFailure() {
                // No access token could be retrieved for the user
            }
            @Override
            public void onError(Exception exception) {
                // An error occurred
            }
        });



    }
    private void performGraphRequest(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json, GraphResponse response) {
                // Application code
                if (response.getError() != null) {
                    System.out.println("ERROR");
                } else {
                    System.out.println("Success");
                    String jsonresult = String.valueOf(json);
                    Log.d("mylog", "JSON Result" + jsonresult);

                    String fbUserFirstName = json.optString("name");
                    String fbUserEmail = json.optString("email");
                    String fbBirthday = json.optString("birthday");
                }
                Log.v("FaceBook Response :", response.toString());
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}