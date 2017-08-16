package com.cm.smartcart;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Show History of purchases
 */
public class History extends BaseActivity implements HistoryPurchaseList.OnItemClickListener{
    // ListView of purchases
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.history_list, null, false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addView(contentView, 0);

        // Intent Arguments
        Bundle extras = getIntent().getExtras();
        String username = "Username";
        if (extras != null) {
            username = extras.getString("USER");
        }

        // Set username info on nav_bar
        TextView user = (TextView)findViewById(R.id.nav_username);
        user.setText(username);

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

        // List of purchases fragment
        HistoryPurchaseList list_purchases_fragment = new HistoryPurchaseList();

        Bundle args = new Bundle();
        args.putString("USER",username);
        list_purchases_fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.history_list_fragment, list_purchases_fragment).commit();
    }

    @Override
    public void onStart(){
        super.onStart();
        list=(ListView)findViewById(android.R.id.list);
    }

    @Override
    public void OnItemClickListener(int pos) {
        // Arguments from list of purchases fragment
        Bundle args = new Bundle();
        args.putInt("POS", pos);

        // New fragment to show selected purchase
        HistoryPurchase purchase = new HistoryPurchase();
        purchase.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.history_list_fragment, purchase);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
