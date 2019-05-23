package CDHS.domain;


import CDHS.GA.AlleleF;

public class Order extends AlleleF {
    private long orderId;

    public Order(int _id, int _type, String _name) {
        super(_id, _type, _name);
    }

    public Order(int _id, int _type, String _name, long orderId) {
        super(_id, _type, _name);
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return  "Order-" + orderId;
    }
}
