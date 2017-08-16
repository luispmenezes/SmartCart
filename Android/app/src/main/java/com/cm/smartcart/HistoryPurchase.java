package com.cm.smartcart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Luis Menezes / Pedro Abade.
 * List of Products from selected purchase
 */
public class HistoryPurchase extends Fragment {
    // aux variable (selected purchase)
    int position;
    // save data from .xml file
    String xml_data;
    // Product
    HashMap<String, String> item;
    // List of Products
    private ListView list;
    // Adapter to the ListView of Products
    private HistoryPurchaseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_purchase, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        HistoryData.init();

        list=(ListView)this.getView().findViewById(R.id.purchase_list);
        adapter = new HistoryPurchaseAdapter(this.getActivity());
        list.setAdapter(adapter);

        // get argument (item selected)
        Bundle args = getArguments();
        position = args.getInt("POS");

        // Parse query to get selected purchase
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Purchases");
        query.whereEqualTo("Id", position);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (objects.size() != 0) {
                    ParseFile xml_info = (ParseFile)objects.get(0).get("Products");
                    xml_info.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {

                                // Retrieve .xml data to a String
                                try {
                                    xml_data = new String(data, "UTF-8");
                                } catch (UnsupportedEncodingException e1) {
                                    e1.printStackTrace();
                                }

                                // Interpret data as a .xml file
                                Document doc = getDomElement(xml_data);
                                // Childs of <Product> on .xml
                                NodeList nl = doc.getElementsByTagName("product");
                                // each Child
                                for (int i = 0; i < nl.getLength(); i++) {
                                    String id = getValue((Element)nl.item(i), "id");
                                    String name = getValue((Element)nl.item(i), "name");
                                    String price = getValue((Element)nl.item(i), "price");
                                    String quant = getValue((Element)nl.item(i), "quant");
                                    String payed = getValue((Element)nl.item(i), "payed");

                                    item = new HashMap<String, String>();
                                    item.put("id", id);
                                    item.put("name", name);
                                    item.put("price", price);
                                    item.put("quant", quant);
                                    item.put("payed", payed);

                                    // add Product to list
                                    adapter.addItem(item);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.e("DEBUG","Error retrieving File data!");
                            }
                        }
                    });
                }
            }
        });
    }

    // Function to interpret data as a .xml file
    public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    // Function to get values from <...> items
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    // Function to get values from <...> elements
    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}
