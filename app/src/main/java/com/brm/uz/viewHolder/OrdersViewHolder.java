package com.brm.uz.viewHolder;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.brm.uz.adapter.OrderAdapter;

public  class OrdersViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView medicationsText,  stockText, fullTextView, halfTextView, semiTextView;
    public EditText editText;
    public OrderAdapter.OrdersEditListener ordersEditListener;
    public OrdersViewHolder(@NonNull View itemView, OrderAdapter.OrdersEditListener ordersEditListener) {
        super(itemView);
        view = itemView;
        medicationsText = view.findViewById(R.id.orders_medications_name_text_view);
        editText = view.findViewById(R.id.orders_medications_edit_text);
        stockText = view.findViewById(R.id.orders_stock_text_view);
        fullTextView = view.findViewById(R.id.orders_full_buy);
        halfTextView= view.findViewById(R.id.orders_half_buy);
        semiTextView = view.findViewById(R.id.orders_semi_buy);
        this.ordersEditListener = ordersEditListener;
        this.editText.addTextChangedListener(ordersEditListener);
    }
}
