package com.cm.smartcart;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by Luis Menezes / Pedro Abade.
 * List of purchases
 */
public class HistoryPurchaseList extends ListFragment {
    // Username
    private String username;
    // days of the purchases
    private String[] compras;
    // this activity context
    private Context contexto;
    // list of purchases
    ListView list_purchases;
    // adapter to the list of purchases
    ArrayAdapter<String> adapter;
    // callback when select item
    OnItemClickListener mCallback;

    // interface for itemClicked
    public interface OnItemClickListener {
        /** Called by HeadlinesFragment when a list item is selected */
        void OnItemClickListener(int pos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_days_list, null);
        contexto = this.getContext();
        list_purchases = (ListView) v.findViewById(android.R.id.list);

        Bundle extras = getActivity().getIntent().getExtras();
        username = extras.getString("USER");

        // Parsequery to get purchases for this user
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Purchases");
        query2.whereEqualTo("Name", username);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> List, ParseException e) {
                if (e == null) {
                    if (List.size() != 0) {
                        compras = new String[List.size() + 1];
                        compras[0] = "filler";

                        // filter data and format string to present in list
                        Date date;
                        for (int c = 0; c < List.size(); c++) {
                            date = List.get(c).getCreatedAt();
                            String[] data_splited = date.toString().split(" ");

                            String mes = "";
                            switch (data_splited[1]) {
                                case "Jan": {
                                    mes = "January";
                                    break;
                                }
                                case "Feb": {
                                    mes = "February";
                                    break;
                                }
                                case "Mar": {
                                    mes = "March";
                                    break;
                                }
                                case "Apr": {
                                    mes = "April";
                                    break;
                                }
                                case "May": {
                                    mes = "May";
                                    break;
                                }
                                case "Jun": {
                                    mes = "June";
                                    break;
                                }
                                case "Jul": {
                                    mes = "July";
                                    break;
                                }
                                case "Aug": {
                                    mes = "August";
                                    break;
                                }
                                case "Sep": {
                                    mes = "September";
                                    break;
                                }
                                case "Oct": {
                                    mes = "October";
                                    break;
                                }
                                case "Nov": {
                                    mes = "November";
                                    break;
                                }
                                case "Dec": {
                                    mes = "December";
                                    break;
                                }
                            }
                            String data_apresentar = data_splited[2] + " " + mes + " " + data_splited[5];

                            compras[c + 1] = data_apresentar;
                        }

                        // add purchase to list
                        adapter = new ArrayAdapter<String>(contexto, R.layout.single_row, R.id.hist_day, compras);
                        list_purchases.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        // define callback
                        try{
                            mCallback = (OnItemClickListener) getActivity();
                        }catch (Exception ex){
                            Log.e("DEBUG","Error in callback!");
                        }

                        // set Listener to list items
                        list_purchases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mCallback.OnItemClickListener(position);

                            }
                        });
                    }
                }
            }
        });

        return v;
    }
}
