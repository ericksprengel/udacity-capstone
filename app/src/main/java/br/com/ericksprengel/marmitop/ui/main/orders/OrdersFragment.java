package br.com.ericksprengel.marmitop.ui.main.orders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.ericksprengel.marmitop.R;
import butterknife.ButterKnife;


public class OrdersFragment extends Fragment {

    public OrdersFragment() {}

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
