package org.wqz.disruptordemo.SpringBootPlusDistruptor;

public class OrderEvent {
    private String orderId;
    private double amount;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }
}    