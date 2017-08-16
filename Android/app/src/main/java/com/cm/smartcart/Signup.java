package com.cm.smartcart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Signup new User
 */
public class Signup extends AppCompatActivity {
    // validate signup data
    boolean valid;
    // ready to verify signup data
    boolean ready = false;

    static final String KEY_NAME = "username";
    static final String KEY_PASS = "password";
    static final String KEY_EMAIL = "email";
    static final String KEY_CONTRIB = "contribuinte";
    static final String KEY_DIRETO = "debito_direto";

    // associating fields
    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_n_contrib) EditText _contribText;
    @InjectView(R.id.input_d_direto) EditText _diretoText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    // user data
    private String name;
    private String email;
    private String password;
    private String n_contrib;
    private String d_direto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        ButterKnife.inject(this);

        // lister for signup button
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // listener for back_to_login (cancel signup) button
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // signup button pressed
    public void signup() {
        // not valid user data input
        if (!validate()) {
            onSignupFailed();
            return;
        }

        // valid user data input
        _signupButton.setEnabled(false);

        // Creating Account dialog
        final ProgressDialog progressDialog = new ProgressDialog(Signup.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // signup process run
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // create new user in Parse database
                        ParseObject new_user = new ParseObject("Users");
                        new_user.put(KEY_NAME, name);
                        new_user.put(KEY_PASS, password);
                        new_user.put(KEY_EMAIL, email);
                        new_user.put(KEY_CONTRIB, n_contrib);
                        new_user.put(KEY_DIRETO, d_direto);
                        new_user.saveInBackground();

                        onSignupSuccess();

                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    // valid user
    public void onSignupSuccess() {
        // Message
        Toast.makeText(getBaseContext(), "Signup successful!", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    // not valid user
    public void onSignupFailed() {
        // Message
        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    // validate user data input
    public boolean validate() {
        // data valid?
        valid = true;

        // get data from form fields
        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        n_contrib = _contribText.getText().toString();
        d_direto = _diretoText.getText().toString();

        // Parsequery too see if username already exists
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo(KEY_NAME, name);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() != 0) {
                        _nameText.setError("Username already exists!");
                        valid = false;
                    }
                }
            }
        });
        // verify username field
        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters!");
            valid = false;
        } else{
            _nameText.setError(null);
        }

        // Parsequery to check if email already exists
        query = ParseQuery.getQuery("Users");
        query.whereEqualTo(KEY_EMAIL, email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() != 0){
                        _emailText.setError("Email already exists!");
                        valid = false;
                    }
                }
            }
        });
        // verify email field
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address!");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        // verify password field
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters!");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        // verify contrib number field
        boolean number = true;
        try{
            int contrib = Integer.parseInt(n_contrib);
        }catch (Exception e){
            number = false;
        }
        // Parsequery to check if contrib number already exists
        query = ParseQuery.getQuery("Users");
        query.whereEqualTo(KEY_CONTRIB, n_contrib);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() != 0){
                        _contribText.setError("Contribute number already exists!");
                        valid = false;
                    }
                }
            }
        });
        // validate contrib number field
        if (n_contrib.length() != 9 || !number){
            _contribText.setError("Enter a valid contribute number (9 digits)!");
            valid = false;
        }else{
            _contribText.setError(null);
        }

        // verify direct debit number field
        boolean is = true;
        try{
            long debito_1 = Long.parseLong(d_direto.substring(0,10));
            long debito_2 = Long.parseLong(d_direto.substring(10,20));
        }catch (Exception e){
            is = false;
        }
        // Parsequery to check if direct debit number already exists
        query = ParseQuery.getQuery("Users");
        query.whereEqualTo(KEY_DIRETO, d_direto);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() != 0){
                        _diretoText.setError("Bank account already exists!");
                        valid = false;
                    }
                }
                ready = true;
            }
        });
        // validate direct debit number field
        if (d_direto.length() != 21 || !is){
            _diretoText.setError("Enter a valid bank account number (21 digits)!");
            valid = false;
        }else{
            valid = true;
            _diretoText.setError(null);
        }

        // all data fields checked, check if all them are correct
        if(ready && valid){
            return true;
        }

        return false;
    }
}
