package com.hubtel.hubtelprinters.receiptbuilder;

public class ReceiptOrderItem {

    String name;
    String quantity;
    String amount;

    public ReceiptOrderItem(String quantity, String name, String amount) {
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}