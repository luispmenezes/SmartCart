package com.cm.smartcart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Adapter for shopping list
 */
public class ShoppingListAdapter extends BaseAdapter{
    // current activity
    private Activity activity;
    // inflater
    private static LayoutInflater inflater=null;

    // constructor to inflate
    public ShoppingListAdapter(Activity activity){
        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Add new product
    public void addItem(HashMap<String,String> item, Bitmap img){
        // Save new product
        ShoppingCart.addItem(item, img);
        // Actualize total price amounted
        TextView price_total = (TextView) activity.findViewById(R.id.shop_totals);
        price_total.setText("Total:  " + ShoppingCart.getTotal() + " €");
    }

    @Override
    public int getCount() {
        return ShoppingCart.getCount();
    }

    @Override
    public Object getItem(int position) {
        return ShoppingCart.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView name = (TextView)vi.findViewById(R.id.prod_name); // name
        TextView qty = (TextView)vi.findViewById(R.id.qty); // quantity
        TextView price = (TextView)vi.findViewById(R.id.price); // price
        TextView price_total = (TextView)vi.findViewById(R.id.price_total); // price total
        TextView amount = (TextView)vi.findViewById(R.id.amount); // amount
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        HashMap<String,String> vItem;
        vItem = ShoppingCart.getItem(position);

        name.setText(vItem.get(Shopping.KEY_NAME));
        qty.setText(vItem.get(Shopping.KEY_QTY)+"    -    "+vItem.get(Shopping.KEY_PPU));
        price.setText(vItem.get(Shopping.KEY_PRICE));
        amount.setText("x" + ShoppingCart.getAmount(vItem.get(Shopping.KEY_ID)));
        price_total.setText("" + (Float.parseFloat(vItem.get(Shopping.KEY_PRICE)) * ShoppingCart.getAmount(vItem.get(Shopping.KEY_ID))));

        thumb_image.setImageBitmap(ShoppingCart.getThumbnail(position));

        return vi;
    }

    // Get total price amounted
    public String getTotalPrice(){
        TextView price_total = (TextView) activity.findViewById(R.id.shop_totals);
        String[] price = price_total.getText().toString().split(" ");
        return price[2];
    }

    // Remove product
    public void removeItem(int pos){
        ShoppingCart.removeItem(pos);
        TextView price_total = (TextView) activity.findViewById(R.id.shop_totals);
        price_total.setText("Total:  " + ShoppingCart.getTotal() + " €");
    }

    // Add product quantity
    public void addItem(int pos, int quant){
        ShoppingCart.addItem(pos, quant);
        TextView price_total = (TextView) activity.findViewById(R.id.shop_totals);
        price_total.setText("Total:  " + ShoppingCart.getTotal() + " €");
    }

    // Get all saved products
    public String getAllItem(int pos){ return ShoppingCart.getAllItem(pos); }
}
