package br.com.ericksprengel.marmitop.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.ericksprengel.marmitop.ui.main.loyaltyprogram.LoyaltyProgramFragment;
import br.com.ericksprengel.marmitop.ui.main.menu.MenuFragment;
import br.com.ericksprengel.marmitop.ui.main.orders.OrdersFragment;


public class MainPagerAdapter extends FragmentStatePagerAdapter {

    static final int ITEM_MENU = 0;
    static final int ITEM_ORDERS = 1;
    static final int ITEM_LOYALTY_PROGRAM = 2;

    MainPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ITEM_MENU:
                return MenuFragment.newInstance();
            case ITEM_ORDERS:
                return OrdersFragment.newInstance();
            case ITEM_LOYALTY_PROGRAM:
                return LoyaltyProgramFragment.newInstance();
           default:
               throw new UnsupportedOperationException("Invalid item for position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
