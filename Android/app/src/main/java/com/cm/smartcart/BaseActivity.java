package com.cm.smartcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Activity for lateral Navigation Bar
 */
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Press Profile option
        if (id == R.id.nav_profile) {
            // Intent to new activity
            Intent intent = new Intent(getApplicationContext(), Profile.class);

            TextView user = (TextView) findViewById(R.id.nav_username);

            intent.putExtra("USER", user.getText().toString());
            HistoryData.init();
            startActivity(intent);

        // Press Shopping option
        } else if(id == R.id.nav_shopping){
            // Intent to new activity
            Intent intent = new Intent(getApplicationContext(), Shopping.class);

            TextView user = (TextView) findViewById(R.id.nav_username);

            intent.putExtra("USER", user.getText().toString());
            HistoryData.init();
            startActivity(intent);

        // Press History option
        } else if (id == R.id.nav_history) {
            // Intent to new activity
            Intent intent = new Intent(getApplicationContext(), History.class);

            TextView user = (TextView) findViewById(R.id.nav_username);

            intent.putExtra("USER", user.getText().toString());
            HistoryData.init();
            startActivity(intent);

        // Press Services option
        } else if (id == R.id.nav_services) {
            // Intent to new activity
            Intent intent = new Intent(getApplicationContext(), Services.class);

            TextView user = (TextView) findViewById(R.id.nav_username);

            intent.putExtra("USER", user.getText().toString());
            HistoryData.init();
            startActivity(intent);

        // Press Logout option
        } else if (id == R.id.nav_logout) {
            // Intent to new activity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            ShoppingCart.init();
            HistoryData.init();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
