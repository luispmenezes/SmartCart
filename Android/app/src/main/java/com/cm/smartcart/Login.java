package com.cm.smartcart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Login activity
 */
public class Login extends AppCompatActivity {
    // Signup selected
    private static final int REQUEST_SIGNUP = 0;
    // Verify user
    private boolean correct_user = false;

    // associate fiels
    @InjectView(R.id.input_username) EditText _usernameText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        ButterKnife.inject(this);

        // save user in preferences for future logins
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password","");
        _usernameText.setText(username);
        _passwordText.setText(password);

        // Listener for login button
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // listener for signup button
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // start signup activity
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    // login button pressed
    public void login() {
        // if user not valid
        if (!validate()) {
            onLoginFailed();
            return;
        }

        // if user valid (disable login button)
        _loginButton.setEnabled(false);

        // authenticating dialog
        final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // save username and password to pass as Bundle argument
        final String username = _usernameText.getText().toString();
        final String password = _passwordText.getText().toString();

        // Parsequery to verify username
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (objects.size() != 0 && objects.get(0).getString("username").equals(username) && objects.get(0).getString("password").equals(password)) {
                    correct_user = true;
                }
            }
        });

        // Edit preferences for login and Services
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password",password);
        editor.remove("services_0");
        editor.remove("services_1");
        editor.remove("services_2");
        editor.remove("services_3");
        editor.commit();

        // activity runnning
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(correct_user)
                            onLoginSuccess();
                        else
                            onLoginFailed();

                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // user valid
    public void onLoginSuccess() {
        // Message
        Toast.makeText(getBaseContext(), "Login successful!", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        correct_user = false;

        // intent for new activity
        Intent intent = new Intent(getApplicationContext(), Shopping.class);
        intent.putExtra("USER", _usernameText.getText().toString());
        ShoppingCart.init();

        // save preferenced user
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password","");
        _usernameText.setText(username);
        _passwordText.setText(password);

        startActivity(intent);
    }

    // user not valid
    public void onLoginFailed() {
        correct_user = false;
        // Message
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    // Function to validate user
    public boolean validate() {
        // valid user
        boolean valid = true;

        // get username and password
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // validate username field
        if (username.isEmpty()) {
            _usernameText.setError("Username cannot be null!");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        // validate password field
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Password length need to be between 4 and 10 caracteres!");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}
