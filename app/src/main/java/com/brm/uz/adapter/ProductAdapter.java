package com.brm.uz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.brm.uz.view.RecyclerViewClickInterface;
import com.brm.uz.models.ProductPOJO;

import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private ArrayList<ProductPOJO> productList;
    private RecyclerViewClickInterface recyclerViewClickInterface;


    public ProductAdapter(Context context, ArrayList<ProductPOJO> p, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.productList = p;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_missions_card_view, null);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.adapterName.setText(productList.get(position).getName());
        holder.adapterType.setText(productList.get(position).getType());
        holder.adapterAddress.setText(productList.get(position).getAddress());
        holder.visitName.setText(productList.get(position).getVisit());
        holder.timeText.setText(productList.get(position).getTime());
        holder.timeStart.setText(productList.get(position).getTimeStart());
        holder.timeEnd.setText(productList.get(position).getTimeEnd());

        logic(holder, position);


    }

    void logic(ProductViewHolder holder, int position){
        if (holder.adapterType.getText().equals("Визит к врачу")){
            holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(R.color.blue_elipse));
        }
        else {
            holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(R.color.ping_lovando));
        }
        if (!productList.get(position).getTimeStart().equals("null") && productList.get(position).getTimeEnd().equals("null")){
            holder.playImage.setVisibility(View.GONE);
            holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(R.color.light_green));
            holder.deleteImage.setVisibility(View.GONE);
            holder.stopImage.setVisibility(View.VISIBLE);
            holder.timeEnd.setVisibility(View.GONE);
            holder.timeStart.setVisibility(View.GONE);
            holder.view.setClickable(false);
        }
        if (productList.get(position).getTimeStart().equals("null") && productList.get(position).getTimeEnd().equals("null")){
            holder.playImage.setVisibility(View.VISIBLE);
            holder.deleteImage.setVisibility(View.VISIBLE);
            holder.stopImage.setVisibility(View.VISIBLE);
            holder.timeEnd.setVisibility(View.GONE);
            holder.timeStart.setVisibility(View.GONE);
        }
        if (productList.get(position).getVisit().equals("Визит окончен")){
            holder.visitName.setTextColor(Color.parseColor("#d22136"));
            holder.playImage.setVisibility(View.GONE);
            holder.stopImage.setVisibility(View.GONE);
            holder.deleteImage.setVisibility(View.GONE);
            holder.timeStart.setVisibility(View.VISIBLE);
            holder.timeEnd.setVisibility(View.VISIBLE);
            holder.editImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{
        ImageView playImage, stopImage, deleteImage, editImage;
        TextView adapterType, adapterAddress, adapterName, visitName, timeText, timeStart, timeEnd;
        LinearLayout linearLayout;
        View view;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            adapterType = itemView.findViewById(R.id.card_view_missions_type_textView);
            adapterAddress = itemView.findViewById(R.id.card_view_missions_address_textVw);
            adapterName = itemView.findViewById(R.id.card_view_missions_doctor_textView);
            visitName = itemView.findViewById(R.id.fragment_mission_card_view_visit);
            timeText = itemView.findViewById(R.id.card_view_add_time);
            linearLayout = view.findViewById(R.id.card_view_ll1);
            playImage = itemView.findViewById(R.id.card_view_button_play);
            stopImage = itemView.findViewById(R.id.card_view_button_stop);
            deleteImage = itemView.findViewById(R.id.card_view_delete);
            timeStart = itemView.findViewById(R.id.card_view_time_start);
            timeEnd = itemView.findViewById(R.id.card_view_time_end);
            editImage = itemView.findViewById(R.id.card_view_edit);

            playImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onPlayClick(getAdapterPosition());
                }
            });
            stopImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onStopClick(getAdapterPosition());
                }
            });
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onDeleteClick(getAdapterPosition());
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemEdit(getAdapterPosition());
                }
            });
        }
    }
}
