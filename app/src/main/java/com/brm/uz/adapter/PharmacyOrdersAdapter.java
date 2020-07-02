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

import com.brm.uz.models.PharmacyOrdersPOJO;
import com.brm.uz.R;

import java.util.List;

public class PharmacyOrdersAdapter extends RecyclerView.Adapter<PharmacyOrdersAdapter.PharmacyViewHolder> {
    private Context context;
    private List<PharmacyOrdersPOJO> pharmacyOrdersPOJOS;


    public PharmacyOrdersAdapter(Context context, List<PharmacyOrdersPOJO> pharmacyOrdersPOJOS) {
        this.context = context;
        this.pharmacyOrdersPOJOS = pharmacyOrdersPOJOS;
    }

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pharmacy_orders_item_list, null);
        PharmacyOrdersAdapter.PharmacyViewHolder holder = new PharmacyOrdersAdapter.PharmacyViewHolder (view, new MyCustomEditTextListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.medicationsText.setText(pharmacyOrdersPOJOS.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return pharmacyOrdersPOJOS.size();
    }


    class PharmacyViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView medicationsText;
        EditText medicationsEdit;
        public MyCustomEditTextListener myCustomEditTextListener;
        public PharmacyViewHolder(@NonNull View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            view = itemView;
            medicationsText = view.findViewById(R.id.pharmacy_orders_medications_name_text_view);
            medicationsEdit = view.findViewById(R.id.pharmacy_orders_medications_edit_text);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.medicationsEdit.addTextChangedListener(myCustomEditTextListener);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
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
