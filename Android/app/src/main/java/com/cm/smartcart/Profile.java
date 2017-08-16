package com.cm.smartcart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Profile Activity
 */
public class Profile extends BaseActivity {
    // validate data fields
    boolean valid;
    // ready for validate data fields
    boolean ready = false;
    // no changes made in user data
    boolean no_changes = false;
    // ready for validate data fields
    boolean ready_warning = false;

    static final String KEY_NAME = "username";
    static final String KEY_PASS = "password";
    static final String KEY_EMAIL = "email";
    static final String KEY_CONTRIB = "contribuinte";
    static final String KEY_DIRETO = "debito_direto";

    // associanting data fields
    @InjectView(R.id.ch_name) EditText _nameText;
    @InjectView(R.id.ch_email) EditText _emailText;
    @InjectView(R.id.ch_pass) EditText _passwordText;
    @InjectView(R.id.ch_contrib) EditText _contribText;
    @InjectView(R.id.ch_direto) EditText _diretoText;
    @InjectView(R.id.profile_button) Button _profileButton;

    // user new data
    private String name;
    private String email;
    private String password;
    private String n_contrib;
    private String d_direto;

    // user old data
    private String old_name;
    private String old_email;
    private String old_password;
    private String old_n_contrib;
    private String old_d_direto;
    private String objectID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.profile_page, null, false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.addView(contentView, 0);
        ButterKnife.inject(this);

        // Get actual username
        Bundle extras = getIntent().getExtras();
        String username = "Username";
        if (extras != null) {
            username = extras.getString("USER");
        }

        TextView user = (TextView)findViewById(R.id.nav_username);
        user.setText(username);

        // Parsequery to get actual username info
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (objects.size() != 0) {
                    ParseObject user = objects.get(0);
                    TextView email = (TextView) findViewById(R.id.nav_email);
                    email.setText(user.getString("email"));
                } else {
                    Log.e("Parser Error", "Data Base Error: not found or more than 1");
                }
            }
        });

        // listener for makeChanges button
        _profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_profile();
            }
        });

        // Parsequery to get actual data from actual User
        query = ParseQuery.getQuery("Users");
        query.whereEqualTo(KEY_NAME, username);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() != 0) {
                        // User id
                        objectID = scoreList.get(0).getObjectId();

                        // User name
                        TextView infos = (TextView) findViewById(R.id.ch_name);
                        infos.setText(scoreList.get(0).getString(KEY_NAME));
                        old_name = infos.getText().toString();

                        // User email
                        infos = (TextView) findViewById(R.id.ch_email);
                        infos.setText(scoreList.get(0).getString(KEY_EMAIL));
                        old_email = infos.getText().toString();

                        // User password
                        infos = (TextView) findViewById(R.id.ch_pass);
                        infos.setText(scoreList.get(0).getString(KEY_PASS).replaceAll("[a-z][0-9][A-Z]", "*"));
                        old_password = scoreList.get(0).getString(KEY_PASS);

                        // User contrib number
                        infos = (TextView) findViewById(R.id.ch_contrib);
                        infos.setText(scoreList.get(0).getString(KEY_CONTRIB));
                        old_n_contrib = infos.getText().toString();

                        // User direct debit number
                        infos = (TextView) findViewById(R.id.ch_direto);
                        infos.setText(scoreList.get(0).getString(KEY_DIRETO));
                        old_d_direto = infos.getText().toString();
                    }
                }
            }
        });
    }

    // Change user profile info
    public void change_profile() {
        // not valid data
        if (!validate()) {
            onChangeFailed();
            return;
        }

        // valid data
        _profileButton.setEnabled(false);

        // Making Changes dialog
        final ProgressDialog progressDialog = new ProgressDialog(Profile.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Making Changes...");
        progressDialog.show();

        // process run
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // Parsequery to save changes in Parse databse
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                        query.getInBackground(objectID, new GetCallback<ParseObject>() {
                            public void done(ParseObject obj, ParseException e) {
                                if (e == null) {
                                    obj.put(KEY_NAME, name);
                                    obj.put(KEY_EMAIL, email);
                                    obj.put(KEY_PASS, password);
                                    obj.put(KEY_CONTRIB, n_contrib);
                                    obj.put(KEY_DIRETO, d_direto);
                                    obj.saveInBackground();
                                }
                            }
                        });

                        onChangeSuccess();

                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    // Changes maked
    public void onChangeSuccess() {
        // Message
        Toast.makeText(getBaseContext(), "Changes done!", Toast.LENGTH_LONG).show();
        _profileButton.setEnabled(true);
        setResult(RESULT_OK, null);

        // start login activity
        Intent intent = new Intent(getApplicationContext(), Login.class);
        ShoppingCart.init();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // changes not maked
    public void onChangeFailed() {
        if(!no_changes)
            if (ready_warning)
                // Message
                Toast.makeText(getBaseContext(), "Cannot make changes!", Toast.LENGTH_LONG).show();
        _profileButton.setEnabled(true);
    }

    // validate fields data from form
    public boolean validate() {
        // valid data
        valid = true;

        // get fields data
        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        n_contrib = _contribText.getText().toString();
        d_direto = _diretoText.getText().toString();

        // check if there were any change in user data
        if (old_name.equals(name) && old_email.equals(email) && old_password.equals(password) && old_n_contrib.equals(n_contrib) && old_d_direto.equals(d_direto)) {
            Toast.makeText(getBaseContext(), "No changes done!", Toast.LENGTH_LONG).show();
            no_changes = true;
            return false;
        } else {
            no_changes = false;
            // Parsequery to check if username already exists
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
            query.whereEqualTo(KEY_NAME, name);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        if (scoreList.size() != 0 && !scoreList.get(0).getString(KEY_NAME).equals(old_name)) {
                            _nameText.setError("Username already exists!");
                            valid = false;
                        }
                    }
                }
            });
            // check username field
            if (name.isEmpty() || name.length() < 3) {
                _nameText.setError("At least 3 characters!");
                valid = false;
            } else {
                _nameText.setError(null);
            }

            // Parsequery to check if email already exists
            query = ParseQuery.getQuery("Users");
            query.whereEqualTo(KEY_EMAIL, email);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        if (scoreList.size() != 0 && !scoreList.get(0).getString(KEY_EMAIL).equals(old_email)) {
                            _emailText.setError("Email already exists!");
                            valid = false;
                        }
                    }
                }
            });
            // check email field
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailText.setError("Enter a valid email address!");
                valid = false;
            } else {
                _emailText.setError(null);
            }

            // check password field
            if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                _passwordText.setError("Between 4 and 10 alphanumeric characters!");
                valid = false;
            } else {
                _passwordText.setError(null);
            }

            // check contrib number field
            boolean number = true;
            try {
                int contrib = Integer.parseInt(n_contrib);
            } catch (Exception e) {
                number = false;
            }

            // Parsequery to check if contrib number already exists
            query = ParseQuery.getQuery("Users");
            query.whereEqualTo(KEY_CONTRIB, n_contrib);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        if (scoreList.size() != 0 && !scoreList.get(0).getString(KEY_CONTRIB).equals(old_n_contrib)) {
                            _contribText.setError("Contribute number already exists!");
                            valid = false;
                        }
                    }
                }
            });
            // check contrib number field
            if (n_contrib.length() != 9 || !number) {
                _contribText.setError("Enter a valid contribute number (9 digits)!");
                valid = false;
            } else {
                _contribText.setError(null);
            }

            // check direct debit number field
            boolean is = true;
            try {
                long debito_1 = Long.parseLong(d_direto.substring(0,10));
                long debito_2 = Long.parseLong(d_direto.substring(10, 20));
            } catch (Exception e) {
                is = false;
            }

            // Parsequery to check if direct debit already exists
            query = ParseQuery.getQuery("Users");
            query.whereEqualTo(KEY_DIRETO, d_direto);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        if (scoreList.size() != 0 && !scoreList.get(0).getString(KEY_DIRETO).equals(old_d_direto)) {
                            _diretoText.setError("Bank account already exists!");
                            valid = false;
                        }
                    }
                    ready = true;
                }
            });
            // check direct debit number field
            if (d_direto.length() != 21 || !is) {
                _diretoText.setError("Enter a valid bank account number (21 digits)!");
                valid = false;
            } else {
                _diretoText.setError(null);
            }
        }
        ready_warning = ready;

        if(ready && valid){
            return true;
        }
        return false;
    }
}
