package br.com.ericksprengel.marmitop.ui.orderday;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.Order;
import br.com.ericksprengel.marmitop.views.QuantityView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public interface OnOrderQuantityListener {
        void onOrderQuantityChanged(String orderKey, Order order);
    }

    private Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private List<String> mOrderKeys;
    private List<Order> mOrders;
    private OnOrderQuantityListener mOnOrderQuantityListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements QuantityView.OnQuantityListener {
        String mOrderKey;
        Order mOrder;
        TextView mMenuItemName;
        TextView mMenuItemDescription;
        QuantityView mQuantity;
        TextView mPrice;

        ViewHolder(View v) {
            super(v);
            mMenuItemName = v.findViewById(R.id.order_day_ac_item_menu_item_name_textview);
            mMenuItemDescription = v.findViewById(R.id.order_day_ac_item_menu_item_description_textview);
            mQuantity = v.findViewById(R.id.order_day_ac_item_quantityview);
            mPrice = v.findViewById(R.id.order_day_ac_item_price_textview);
            mQuantity.setOnQuantityListener(this);
        }

        void updateData(String orderKey, Order order) {
            mOrderKey = orderKey;
            mOrder = order;
            mMenuItemName.setText(String.format("%s (%s)", mOrder.getMenuItem().getName(), mOrder.getOption().getName()));
            mMenuItemDescription.setText(mOrder.getMenuItem().getDescription());
            mQuantity.setQuantity(mOrder.getQuantity());
            mPrice.setText(NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(mOrder.getOption().getPrice()));
        }

        @Override
        public void onQuantityChanged(int quantity) {
            mOrder.setQuantity(quantity);
            OrdersAdapter.this.mOnOrderQuantityListener.onOrderQuantityChanged(mOrderKey, mOrder);
        }
    }

    OrdersAdapter(OnOrderQuantityListener listener) {
        this.mOnOrderQuantityListener= listener;
        this.mOrderKeys = new ArrayList<>();
        this.mOrders = new ArrayList<>();
    }

    void clear() {
        mOrderKeys.clear();
        mOrders.clear();
        notifyDataSetChanged();
    }

    void add(String orderKey, Order order) {
        this.mOrderKeys.add(orderKey);
        this.mOrders.add(order);
        notifyItemInserted(mOrders.size()-1);
    }

    String getOrderKey(int position) {
        return mOrderKeys.get(position);
    }

    Order getOrder(int position) {
        return mOrders.get(position);
    }

    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_order_day_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String orderKey = mOrderKeys.get(position);
        Order order = mOrders.get(position);

        holder.updateData(orderKey ,order);
    }

    @Override
    public int getItemCount() {
        return mOrders == null ? 0 : mOrders.size();
    }

}
