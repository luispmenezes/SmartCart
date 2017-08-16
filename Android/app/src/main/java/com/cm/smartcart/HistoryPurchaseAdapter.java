package com.cm.smartcart;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.HashMap;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Adapter for list of products from the selected purchase
 */
public class HistoryPurchaseAdapter extends BaseAdapter {
    // actual activity
    private Activity activity;
    // inflater
    private static LayoutInflater inflater = null;

    // const. to inflate
    public HistoryPurchaseAdapter(Activity activity) {
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Add a Product
    public void addItem(HashMap<String, String> item) {
        HistoryData.addItem(item);
        // Set total money spent
        TextView hist_price_total = (TextView) activity.findViewById(R.id.hist_shop_totals);
        hist_price_total.setText("Total:  " + HistoryData.getTotal() + " €");
    }

    @Override
    public int getCount() {
        return HistoryData.getCount();
    }

    @Override
    public Object getItem(int position) {
        return HistoryData.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.history_list_row, null);

        // Show Product info
        TextView name = (TextView) vi.findViewById(R.id.hist_prod_name); // name
        TextView price = (TextView) vi.findViewById(R.id.hist_prod_price); // price
        TextView price_total = (TextView) vi.findViewById(R.id.hist_price_total); // price total
        TextView amount = (TextView) vi.findViewById(R.id.hist_prod_amount); // amount

        HashMap<String, String> vItem;
        vItem = HistoryData.getItem(position);

        name.setText(vItem.get("name"));
        price.setText(vItem.get("price"));
        amount.setText("x" + vItem.get("quant"));
        price_total.setText("" + vItem.get("payed"));

        return vi;
    }
}
