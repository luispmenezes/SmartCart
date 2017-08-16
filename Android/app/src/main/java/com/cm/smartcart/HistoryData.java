package com.cm.smartcart;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Save information from History purchases
 */
public class HistoryData {
    // Products info
    private static ArrayList<HashMap<String,String>> data =  new ArrayList<HashMap<String, String>>();
    // Total money spent
    private static double total = 0;

    // Reset of data
    public static void init(){
        data.clear();
        total = 0;
    }

    // Add a product to the list
    public static void addItem(HashMap<String,String> item){
        data.add(item);
        total += Double.parseDouble(item.get("payed"));
        total = Math.round(total*100.0)/100.0;
    }

    // Get how many Products
    public static int getCount(){return data.size();}
    // Get specific Product
    public static HashMap<String, String> getItem(int index){return data.get(index);}
    // Get total money spent
    public static double getTotal(){return total;}
}
