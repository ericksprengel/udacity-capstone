package br.com.ericksprengel.marmitopadmin.data;

import java.util.Map;


public class OrderDay {
    private String menuId;
    private Map<String, Order> orders;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public Map<String, Order> getOrders() {
        return orders;
    }

    public void setOrders(Map<String, Order> orders) {
        this.orders = orders;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Order order : orders.values()) {
            totalPrice += order.getQuantity() * order.getOption().getPrice();
        }
        return totalPrice;
    }
}
