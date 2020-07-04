package com.brm.uz.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.brm.uz.models.PharmacyNewOrdersPOJO;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersViewHolder> {
    private Context context;
    private List<PharmacyNewOrdersPOJO> ordersList;

    public OrderAdapter(Context context, List<PharmacyNewOrdersPOJO> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pharmacy_new_orders_item_list, null);
        OrderAdapter.OrdersViewHolder holder = new OrderAdapter.OrdersViewHolder (view, new OrdersEditListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        holder.ordersEditListener.updatePosition(holder.getAdapterPosition());
        holder.medicationsText.setText(ordersList.get(position).getName());
        holder.editText.setText(""+ordersList.get(position).getQuantity());
        holder.fullTextView.setText("100% : "+ ordersList.get(position).getFull());
        holder.halfTextView.setText("50% : " + ordersList.get(position).getHalf());
        holder.semiTextView.setText("25% : " + ordersList.get(position).getSemi());
        holder.stockText.setText("В складу осталось : "+ ordersList.get(position).getStock());
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView medicationsText,  stockText, fullTextView, halfTextView, semiTextView;
        private EditText editText;
        private OrdersEditListener ordersEditListener;
        public OrdersViewHolder(@NonNull View itemView, OrdersEditListener ordersEditListener) {
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

    private class OrdersEditListener implements TextWatcher {
        private int position;

        private void updatePosition(int position) {
            this.position = position;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")){
                ordersList.get(position).setQuantity(0);
            }
            else {
                ordersList.get(position).setQuantity(Integer.parseInt(s.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
