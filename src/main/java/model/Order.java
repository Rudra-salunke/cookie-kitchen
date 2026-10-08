package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    public enum Status { PLACED, BAKING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED }
    public enum PaymentMethod { COD, ONLINE }

    private int id;
    private int userId;
    private int addressId;
    private BigDecimal total;
    private Status status;
    private PaymentMethod paymentMethod;
    private Timestamp createdAt;
    public Order(int id, int userId, int addressId, BigDecimal total,
                 Status status, PaymentMethod paymentMethod, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.addressId = addressId;
        this.total = total;
        this.status = (status == null) ? Status.PLACED : status;
        this.paymentMethod = (paymentMethod == null) ? PaymentMethod.COD : paymentMethod;
        this.createdAt = createdAt;   // your version never assigned this line
    }

    public Order(int userId, int addressId, BigDecimal total, PaymentMethod paymentMethod) {
        this(0, userId, addressId, total, Status.PLACED, paymentMethod, null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}