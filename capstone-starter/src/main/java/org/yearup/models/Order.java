package org.yearup.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    int orderId;
    int userId;
    String date;
    String address;
    String city;
    String state;
    String zip;
    double shippingAmount;
    private List<OrderLineItem> lineItemList = new ArrayList<>();

    public Order(int orderId, int userId, String date, String address, String city, String state, String zip, double shippingAmount, ShoppingCart cart, List<OrderLineItem> lineItemList) {
        this.orderId = orderId;
        this.userId = userId;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.shippingAmount = shippingAmount;
        this.lineItemList = lineItemList;
    }

    public Order() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        this.date = currentDate.format(formatter);
    }

    public List<OrderLineItem> getLineItemList() {
        return lineItemList;
    }

    public void setLineItemList(List<OrderLineItem> lineItemList) {
        this.lineItemList = lineItemList;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public double getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(double shippingAmount) {
        this.shippingAmount = shippingAmount;
    }
}
