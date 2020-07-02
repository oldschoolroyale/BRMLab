package com.brm.uz.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.models.PharmacyNewOrdersPOJO;
import com.brm.uz.R;

import java.util.List;

public class PharmacyNewOrdersAdapter extends RecyclerView.Adapter<PharmacyNewOrdersAdapter.PharmacyNewOrdersViewHolder> {
    private Context context;
    private List<PharmacyNewOrdersPOJO> pharmacyOrdersPOJOS;

    public PharmacyNewOrdersAdapter(Context context, List<PharmacyNewOrdersPOJO> pharmacyOrdersPOJOS) {
        this.context = context;
        this.pharmacyOrdersPOJOS = pharmacyOrdersPOJOS;
    }

    @NonNull
    @Override
    public PharmacyNewOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pharmacy_new_orders_item_list, null);
        PharmacyNewOrdersAdapter.PharmacyNewOrdersViewHolder holder = new PharmacyNewOrdersAdapter.PharmacyNewOrdersViewHolder (view, new MyNewCustomEditTextListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyNewOrdersViewHolder holder, int position) {
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.medicationsText.setText(pharmacyOrdersPOJOS.get(position).getName());
        holder.fullTextView.setText("100% : " + pharmacyOrdersPOJOS.get(position).getFull());
        holder.halfTextView.setText("50% : " + pharmacyOrdersPOJOS.get(position).getHalf());
        holder.semiTextView.setText("25% : " + pharmacyOrdersPOJOS.get(position).getSemi());
        holder.stockText.setText("В складу осталось : "+pharmacyOrdersPOJOS.get(position).getStock());

    }

    @Override
    public int getItemCount() {
        return pharmacyOrdersPOJOS.size();
    }

    class PharmacyNewOrdersViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView medicationsText,  stockText, fullTextView, halfTextView, semiTextView;
        EditText editText;
        public MyNewCustomEditTextListener myCustomEditTextListener;
        public PharmacyNewOrdersViewHolder(@NonNull View itemView, MyNewCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            view = itemView;
            medicationsText = view.findViewById(R.id.pharmacy_new_orders_medications_name_text_view);
            editText = view.findViewById(R.id.pharmacy_new_orders_button_medications_edit_text);
            stockText = view.findViewById(R.id.pharmacy_new_orders_stock_text_view);
            fullTextView = view.findViewById(R.id.pharmacy_new_orders_full_buy);
            halfTextView= view.findViewById(R.id.pharmacy_new_orders_half_buy);
            semiTextView = view.findViewById(R.id.pharmacy_new_orders_semi_buy);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editText.addTextChangedListener(myCustomEditTextListener);
        }
    }

    private class MyNewCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")){
                pharmacyOrdersPOJOS.get(position).setQuantity(0);
            }
            else {
                pharmacyOrdersPOJOS.get(position).setQuantity(Integer.parseInt(s.toString()));
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}