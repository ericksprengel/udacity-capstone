package br.com.ericksprengel.marmitop.data;


public class Order {

    private int quantity;
    private String optionId;

    public Order(int quantity, String optionId) {
        this.quantity = quantity;
        this.optionId = optionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
}
