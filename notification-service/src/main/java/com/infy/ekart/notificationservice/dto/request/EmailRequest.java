package com.infy.ekart.notificationservice.dto.request;

public class EmailRequest {

    private String to;
    private String subject;
    private String body;
    private String type;

    private String customerName;
    private String orderNumber;
    private String orderTotal;
    private String itemList;
    private String trackingNumber;
    private String paymentMethod;
    private String resetLink;

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getOrderTotal() { return orderTotal; }
    public void setOrderTotal(String orderTotal) { this.orderTotal = orderTotal; }

    public String getItemList() { return itemList; }
    public void setItemList(String itemList) { this.itemList = itemList; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getResetLink() { return resetLink; }
    public void setResetLink(String resetLink) { this.resetLink = resetLink; }
}