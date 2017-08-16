package com.cm.smartcart;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Save shopping list products data
 */
public final class ShoppingCart {
    // product images
    private static ArrayList<Bitmap> thumbs = new ArrayList<Bitmap>();
    // product info
    private static ArrayList<HashMap<String,String>> data =  new ArrayList<HashMap<String, String>>();
    // product quantity
    private static HashMap<String,Integer> amounts = new HashMap<String,Integer>();
    // total price amounted
    private static double total = 0;

    // Initialize data
    public static void init(){
        thumbs.clear();
        data.clear();
        amounts.clear();
        total = 0;
    }

    // Add new product
    public static void addItem(HashMap<String,String> item, Bitmap img){
        String key = item.get(Shopping.KEY_ID);

        // if product is already saved add one more quantity
        if(amounts.containsKey(key)){
            amounts.put(key,Integer.valueOf(amounts.get(key).intValue()+1));
        // if new product
        }else{
            amounts.put(key,Integer.valueOf(1));
            data.add(item);
            thumbs.add(img);
        }

        // calculate new total price amounted
        total += Double.parseDouble(item.get(Shopping.KEY_PRICE));
        total = Math.round(total*100.0)/100.0;
    }

    // Add more product quantity
    public static void addItem(int pos, int quant){
        HashMap<String,String> data_prov = data.get(pos);
        String key = data_prov.get(Shopping.KEY_ID);
        int quant_ant = amounts.get(data_prov.get(Shopping.KEY_ID));
        amounts.put(key,quant);
        total += (quant-quant_ant) * Double.parseDouble(data_prov.get(Shopping.KEY_PRICE));
        total = Math.round(total*100.0)/100.0;
    }

    // Get total products number
    public static int getCount(){return data.size();}
    // Get specific product
    public static HashMap<String, String> getItem(int index){return data.get(index);}
    // Get specific product quantity
    public static int getAmount(String item_name){return amounts.get(item_name); }
    // Get specific product image
    public static Bitmap getThumbnail(int index){return thumbs.get(index);}
    // Get total price amounted
    public static double getTotal(){return total;}

    // Remove specific product from list
    public static void removeItem(int pos){
        HashMap<String,String> data_prov = data.get(pos);
        data.remove(pos);
        thumbs.remove(pos);
        int quant = amounts.get(data_prov.get(Shopping.KEY_ID));
        amounts.remove(data_prov.get(Shopping.KEY_ID));
        total -= Double.parseDouble(data_prov.get(Shopping.KEY_PRICE))*quant;
        total = Math.round(total*100.0)/100.0;
    }

    // Get all products from list
    public static String getAllItem(int pos){
        HashMap<String,String> info_geral = data.get(pos);
        int quant = amounts.get(info_geral.get(Shopping.KEY_ID));

        return info_geral.get(Shopping.KEY_ID)+","+info_geral.get(Shopping.KEY_NAME)+","+info_geral.get(Shopping.KEY_PRICE)+","+quant;
    }
}
