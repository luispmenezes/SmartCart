package com.cm.smartcart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import org.xmlpull.v1.XmlSerializer;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

/**
 * Created by Luis Menezes / Pedro Abade.
 * Shopping activity
 */
public class Shopping extends BaseActivity {
    // XML node keys
    static final String KEY_ID = "ID";
    static final String KEY_NAME = "Name";
    static final String KEY_QTY = "Quantity";
    static final String KEY_PRICE = "Price";
    static final String KEY_PPU = "PPU";

    // shopping list
    private ListView list;
    // shopping list adapter
    private ShoppingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_shopping, null, false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addView(contentView, 0);

        // get arguments (username)
        Bundle extras = getIntent().getExtras();
        String username = "Username";
        if (extras != null) {
            username = extras.getString("USER");
        }

        // Ser username and email on side navigation bar
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
    }

    @Override
    public void onStart(){
        super.onStart();
        list=(ListView)findViewById(R.id.listView);
        adapter = new ShoppingListAdapter(this);
        list.setAdapter(adapter);
        // list items are selectable for a new menu for each item
        registerForContextMenu(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Checkout operation
        if (item.getTitle().equals("Checkout")){
            // Only if the shopping list isnt empty
            if (list.getCount() > 0){
                // Message dialog for purchase
                final AlertDialog.Builder checkout_dialog  = new AlertDialog.Builder(this);
                checkout_dialog.setMessage("Confirm purchase! Show QRCode to employee!");
                checkout_dialog.setTitle("Purchase");

                // Parsequery for getting number of purchases in Parse database
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Purchases");
                int how_many=0;
                try {
                    how_many = query.count();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final int how_many2 = how_many;

                // generate qrcode for the employee
                final ImageView qrcode = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                qrcode.setLayoutParams(lp);
                try{
                    Bitmap bm = encodeAsBitmap(String.valueOf(how_many+1), BarcodeFormat.QR_CODE, 800, 800);

                    if(bm != null) {
                        qrcode.setImageBitmap(bm);
                    }
                } catch(WriterException e){
                    Log.e("DEBUG","Cant generate QRCode!");
                }

                // show qr code and purchase message
                checkout_dialog.setView(qrcode);
                // press OK button
                checkout_dialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TextView user = (TextView) findViewById(R.id.nav_username);
                                ParseObject purchase = new ParseObject("Purchases");
                                purchase.put("Id", how_many2 + 1);
                                purchase.put("Name", user.getText());

                                // generate a xml_file with shopping list products
                                String xml_file = null;
                                try {
                                    xml_file = convert_to_xml();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Calendar c = Calendar.getInstance();
                                // create a ParseFile to save generated .xml file
                                ParseFile file = new ParseFile("p_" + user.getText() + "_" + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.MONTH) + c.get(Calendar.YEAR) + ".xml", xml_file.getBytes());
                                file.saveInBackground();
                                purchase.put("Products", file);
                                purchase.saveInBackground();

                                // Message
                                Toast.makeText(getBaseContext(), "Operation concluded!", Toast.LENGTH_LONG).show();
                                ShoppingCart.init();
                                finish();
                                startActivity(getIntent());
                            }
                        });

                // press Cancel button
                checkout_dialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Message
                                Toast.makeText(getBaseContext(), "Operation canceled!", Toast.LENGTH_LONG).show();
                            }
                        });

                checkout_dialog.setCancelable(true);
                checkout_dialog.create().show();
            } else {
                // Message
                Toast.makeText(getBaseContext(), "No items in the shopping list!", Toast.LENGTH_LONG).show();
            }
        // press Reset option
        } else if (item.getTitle().equals("Reset")){
            // Reset shopping list
            if (list.getCount() > 0){
                ShoppingCart.init();
                finish();
                startActivity(getIntent());
            } else {
                // Message
                Toast.makeText(getBaseContext(), "No items in the shopping list!", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    // Scan barcode or qrcode
    public void scanCode(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Result of scanner
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        IntentResult scanninFormat = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        // Something scanned
        if (scanningResult != null) {
            // scanned content
            String scanContent = scanningResult.getContents();

            // Parsequery to fetch scanned product from Parse database
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
            query.whereEqualTo(KEY_ID,scanContent);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (objects.size() == 1) {
                        // Save scanned product info
                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put(KEY_NAME, objects.get(0).getString("Name"));
                        item.put(KEY_QTY, objects.get(0).getString("Quantity"));
                        item.put(KEY_PRICE, objects.get(0).getString("Price"));
                        item.put(KEY_PPU, objects.get(0).getString("PPU"));
                        item.put(KEY_ID, objects.get(0).getString("ID"));

                        // Get product image
                        ParseFile picture = objects.get(0).getParseFile("thumbnail");
                        byte[] data={};
                        try{
                            data = picture.getData();
                        }catch (com.parse.ParseException pe){
                            Log.e("PARSE ERROR","PARSING IMAGE: "+pe);
                        }

                        Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);

                        // add product to shopping list
                        adapter.addItem(item,bmp);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e("Parser Error", "Data Base Error: not found or more than 1");
                    }
                }
            });
        }else {
            // Message
            Toast toast = Toast.makeText(getApplicationContext(), "No Barcode data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // on list product select show a menu
        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

            int pos = (int)info.id;
            Object o = adapter.getItem(pos);

            String[] object_split = o.toString().split(",");
            String[] object_name_split = object_split[4].split("=");
            String[] object_name_split2 = object_name_split[1].split(" ");

            menu.setHeaderTitle(object_name_split2[0]);

            String[] menuItems = getResources().getStringArray(R.array.items_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        // selecting Quantity in product menu
        if (item.getTitle().equals("Quantity")){
            // dialog for entering desired quantity
            final AlertDialog.Builder add_dialog  = new AlertDialog.Builder(this);
            add_dialog.setMessage("Enter quantity of this product");
            add_dialog.setTitle("Quantity");

            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setInputType(0x00000002);
            add_dialog.setView(input);

            // press OK button
            add_dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // verify inserted number
                            int quant = 0;
                            try {
                                quant = Integer.parseInt(input.getText().toString());
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), "Invalid number!", Toast.LENGTH_LONG).show();
                            }

                            // add more quantity of product to list
                            if (quant > 0) {
                                ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
                                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                                int pos = (int) info.id;
                                Object o = adapter.getItem(pos);

                                adapter.addItem(pos, quant);
                                adapter.notifyDataSetChanged();

                            // if quantity equal to zero remove product from list
                            } else if (quant == 0){
                                ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
                                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                                int pos = (int) info.id;
                                Object o = adapter.getItem(pos);
                                adapter.removeItem(pos);
                            }
                        }
                    });

            add_dialog.setCancelable(true);
            add_dialog.create().show();

        // press REMOVE button
        } else if (item.getTitle().equals("Remove")){
            // remove product from list
            ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            int pos = (int)info.id;
            Object o = adapter.getItem(pos);

            adapter.removeItem(pos);
            adapter.notifyDataSetChanged();
        }

        return true;
    }

    // Function to generate a .xml File format returned in a String
    public String convert_to_xml() throws Exception {
        // XML Serializer
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);
        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);
        // open tag: <purchase>
        xmlSerializer.text("\n");
        xmlSerializer.startTag("", "purchase");
        xmlSerializer.attribute("", "total", adapter.getTotalPrice());

        Calendar c = Calendar.getInstance();
        xmlSerializer.attribute("", "day", c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.YEAR));

        // foreach product in shopping list
        int c_items;
        for (c_items=0 ; c_items < adapter.getCount() ; c_items++){
            String info = adapter.getAllItem(c_items);
            String[] info_total = info.split(",");

            int quant=0;
            double price=0;
            try {
                quant = Integer.parseInt(info_total[3]);
                price = Double.parseDouble(info_total[2]);
            } catch (Exception e) {
                Log.e("DEBUG","Error converting number!");
            }
            double tot_price = quant*price;
            tot_price = Math.round(tot_price*100.0)/100.0;

            // open tag: <product>
            xmlSerializer.text("\n\t");
            xmlSerializer.startTag("", "product");

            // open tag: <id>
            xmlSerializer.text("\n\t\t");
            xmlSerializer.startTag("","id");
            xmlSerializer.text(info_total[0]);
            // close tag: </id>
            xmlSerializer.endTag("","id");

            // open tag: <name>
            xmlSerializer.text("\n\t\t");
            xmlSerializer.startTag("", "name");
            xmlSerializer.text(info_total[1]);
            // close tag: </name>
            xmlSerializer.endTag("", "name");

            // open tag: <price>
            xmlSerializer.text("\n\t\t");
            xmlSerializer.startTag("", "price");
            xmlSerializer.text(info_total[2]);
            // close tag: </price>
            xmlSerializer.endTag("", "price");

            // open tag: <quant>
            xmlSerializer.text("\n\t\t");
            xmlSerializer.startTag("", "quant");
            xmlSerializer.text(info_total[3]);
            // close tag: </quant>
            xmlSerializer.endTag("", "quant");

            // open tag: <payed>
            xmlSerializer.text("\n\t\t");
            xmlSerializer.startTag("", "payed");
            xmlSerializer.text(String.valueOf(tot_price));
            // close tag: </payed>
            xmlSerializer.endTag("", "payed");

            // close tag: </product>
            xmlSerializer.text("\n\t");
            xmlSerializer.endTag("", "product");
        }

        // close tag: </purchase>
        xmlSerializer.text("\n");
        xmlSerializer.endTag("", "purchase");

        // end DOCUMENT
        xmlSerializer.endDocument();

        return writer.toString();
    }

    // encode product images
    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) throws WriterException {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        HashMap<EncodeHintType, String> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new HashMap<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, desiredWidth,
                desiredHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    // know byte[] encode format
    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
