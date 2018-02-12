package br.com.ericksprengel.marmitop.data;


import android.content.res.Resources;

import br.com.ericksprengel.marmitop.R;

public class Order {

    private int quantity;
    private MtopMenuItem menuItem;
    private MtopMenuItem.Option option;

    // for firebase
    public Order() {}

    public Order(MtopMenuItem menuItem, int quantity, MtopMenuItem.Option option) {
        menuItem.setOptions(null);
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.option = option;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public MtopMenuItem.Option getOption() {
        return option;
    }

    public void setOption(MtopMenuItem.Option option) {
        this.option = option;
    }

    public MtopMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MtopMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public String getShortDescription(Resources res) {
        return res.getString(R.string.order_data_short_description, menuItem.getName(), option.getName());
    }
}
