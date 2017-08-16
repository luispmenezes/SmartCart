package com.cm.smartcart;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;

/**
 * Created by Luis Menezes / Pedro Abade.
 */
public class Services extends BaseActivity {

    private MyGridAdapter gridAdapter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver broadcastReceiver;
    private Intent i;
    private boolean mBounded;
    private AutoUpdateService mServer;
    private static int itt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_services);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_services,null,false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addView(contentView, 0);

        if(itt==0){
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("services_0", "-1");
            editor.putString("services_1", "-1");
            editor.putString("services_2", "-1");
            editor.putString("services_3", "-1");
        }
        itt++;


        GridView gridView = (GridView) this.findViewById(R.id.service_grid);
        gridAdapter = new MyGridAdapter(this);
        gridView.setAdapter(gridAdapter);

        Bundle extras = getIntent().getExtras();
        String username = "Username";
        if (extras != null) {
            username = extras.getString("USER");
        }

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

        i = new Intent(this, AutoUpdateService.class);
        this.bindService(i, mConnection, BIND_AUTO_CREATE);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("update_service_numbers")) {
                    updateGrid();
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("update_service_numbers");
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    private void updateGrid(){
        gridAdapter = new MyGridAdapter(this);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mServer = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            AutoUpdateService.LocalBinder mLocalBinder = (AutoUpdateService.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();
        }
    };

}

class MyGridAdapter extends BaseAdapter {

    private Activity context;
    private String[] names;
    private int[][] numbers;
    private int[] myNumber =  {-1,-1,-1,-1};
    LayoutInflater inflater;

    public MyGridAdapter(Activity context) {
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        names = new String[4];
        numbers = new int[4][2];
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Services");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (objects.size() > 0) {
                    for (int i = 0; i < objects.size(); i++) {
                        names[i] = objects.get(i).getString("Name");
                        numbers[i][0] = (int) objects.get(i).getNumber("currentNumber");
                        numbers[i][1] = (int) objects.get(i).getNumber("availableNumber");
                    }
                } else {
                    Log.e("Parser Error", "Data Base Error: not found or more than 1");
                }
                notifyDataSetChanged();
            }
        });
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        myNumber[0] = Integer.parseInt(sharedPref.getString("services_0","-1"));
        myNumber[1] = Integer.parseInt(sharedPref.getString("services_1","-1"));
        myNumber[2] = Integer.parseInt(sharedPref.getString("services_2","-1"));
        myNumber[3] = Integer.parseInt(sharedPref.getString("services_3","-1"));
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.services_cell, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.service_name);
        TextView counter1 = (TextView) convertView.findViewById(R.id.senha1);
        TextView counter2 = (TextView) convertView.findViewById(R.id.senha2);

        if(names[position]!=null){
            if(myNumber[position] < (numbers[position][0]+3)){
                myNumber[position]=-1;
                SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("services_"+position, "-1");
                editor.commit();
            }

            title.setText(names[position]);
            counter1.setText(String.format("%02d",numbers[position][0]));
            counter2.setText(String.format("%02d",numbers[position][1]));

            Button button = (Button) convertView.findViewById(R.id.ticket_button);

            if(myNumber[position]!=-1){
                button.setEnabled(false);
                ((TextView) convertView.findViewById(R.id.counter2)).setText("Minha");
            }
            else {
                button.setTag(new Integer(position));
                button.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Integer idx = (Integer) v.getTag();
                        int number = numbers[idx][1];
                        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("services_" + idx, "" + number);
                        myNumber[idx] = number;
                        Log.e("DEBUG", "WRITE"+ idx);
                        editor.commit();

                        notifyDataSetChanged();
                    }
                });
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        checkNumbers();
    }

    public void checkNumbers(){
        for(int i = 0;i <4 ;i++){
            if(myNumber[i]!=-1 && (numbers[i][0]-myNumber[i])<=3 && (numbers[i][0]-myNumber[i])>-3 ){
                long when = System.currentTimeMillis();
                NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent=new Intent(context,Services.class);
                PendingIntent pending=PendingIntent.getActivity(context, 0, intent, 0);
                Notification notification= new Notification.Builder(context)
                        .setContentTitle("Senha para "+names[i])
                        .setContentText(
                                "A servir: "+numbers[i][0]+" e a sua é: "+myNumber[i]).setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pending).setWhen(when).setAutoCancel(true)
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults |= Notification.DEFAULT_SOUND;
                nm.notify(0, notification);
               }

        }

    }

}

