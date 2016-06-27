package com.example.is2.test2qrventory;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChooseBarcodeTypeActivity extends Activity {

    ListView listview;
    List<String> barcode_types = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_barcode_type);

        listview = (ListView) findViewById(R.id.choose_barcode_type_list_view);

        barcode_types.add("Code-EAN8");
        barcode_types.add("Code-EAN13");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, barcode_types);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView lv = (ListView) parent;
                String item = (String) lv.getItemAtPosition(position);
                Intent intent = getIntent();
                intent.putExtra("Barcode Type", item);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
