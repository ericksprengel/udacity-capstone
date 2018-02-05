package br.com.ericksprengel.marmitop.ui.main.orders;


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
import br.com.ericksprengel.marmitop.data.OrderDay;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public interface OnOrderDayClickListener {
        void onOrderDayClick(OrderDay orderDay);
    }

    private Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private List<OrderDay> mOrderDays;
    private OnOrderDayClickListener mOnOrderDayClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OrderDay mOrderDay;
        TextView mMenu;
        TextView mDescriptionItem1;
        TextView mDescriptionMore;
        TextView mTotal;

        ViewHolder(View v) {
            super(v);
            mMenu = v.findViewById(R.id.orders_fg_item_menu_textview);
            mDescriptionItem1 = v.findViewById(R.id.orders_fg_item_description_1_textview);
            mDescriptionMore = v.findViewById(R.id.orders_fg_item_description_more_textview);
            mTotal = v.findViewById(R.id.orders_fg_item_total_textview);
            v.setOnClickListener(this);
        }

        void updateData(OrderDay orderDay) {
            mOrderDay = orderDay;
            Order order = orderDay.getOrders().values().iterator().next();
            mMenu.setText(mOrderDay.getMenuId());
            mDescriptionItem1.setText(order.getShortDescription());
            mDescriptionMore.setVisibility(orderDay.getOrders().size() > 1 ? View.VISIBLE : View.INVISIBLE);
            mTotal.setText(NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(mOrderDay.getTotalPrice()));
        }

        @Override
        public void onClick(View view) {
            OrdersAdapter.this.mOnOrderDayClickListener.onOrderDayClick(mOrderDay);
        }
    }

    OrdersAdapter(OnOrderDayClickListener listener) {
        this.mOnOrderDayClickListener = listener;
        this.mOrderDays = new ArrayList<>();
    }

    void clear() {
        mOrderDays.clear();
        notifyDataSetChanged();
    }

    void add(OrderDay orderDay) {
        this.mOrderDays.add(orderDay);
        notifyItemInserted(mOrderDays.size()-1);
    }

    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_orders_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderDay orderDay = mOrderDays.get(position);

        holder.updateData(orderDay);
    }

    @Override
    public int getItemCount() {
        return mOrderDays == null ? 0 : mOrderDays.size();
    }

}
