package com.example.lenovo.cafecanteenadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.lenovo.cafecanteenadmin.Common.Common;
import com.example.lenovo.cafecanteenadmin.viewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_total;
    String order_id_value="";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = (TextView)findViewById(R.id.order_id);
        order_phone = (TextView)findViewById(R.id.order_phone);
        order_total = (TextView)findViewById(R.id.order_total);

        lstFoods = (RecyclerView)findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);

        if(getIntent()!=null)
            order_id_value = getIntent().getStringExtra("OrderId");

        //Set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);
    }
}
