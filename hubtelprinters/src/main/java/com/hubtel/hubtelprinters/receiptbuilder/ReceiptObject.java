package com.hubtel.hubtelprinters.receiptbuilder;



import android.graphics.Bitmap;


import java.util.List;

public class ReceiptObject {



    private String businessName;
    private String businessBranch;
    private String businessPhone;
    private  String businessAddress;
    private  String businessWebUrl;
    private  String paymentDate;
    private  String paymentReceiptNumber;
    private  String paymentType;
    private  Bitmap logo;
    private  List<ReceiptOrderItem> items;
    private  String subtotal;
    private  String discount;
    private  String tax;
    private  String total;
    private  String amountPaid;
    private  String change;
    private  String gratisPoint;
    private  String employeeName;

    private  Bitmap qrcode;
    private  String customer;
    private  boolean isDuplicate;

    private CardDetails cardDetails;

    private String netSaleTotal;
    private String cashTotal;
    private String mobileMoneyTotal;
    private String cardTotal;
    private String hubtelTotal;
    private Boolean isOrder;
    private String orderNumber;

    private  List<ReceiptOrderDayItem> itemsPicked;
    private  List<ReceiptOrderDayItem> dayItemList;

    public ReceiptObject(){}



    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessBranch() {
        return businessBranch;
    }

    public void setBusinessBranch(String businessBranch) {
        this.businessBranch = businessBranch;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessWebUrl() {
        return businessWebUrl;
    }

    public void setBusinessWebUrl(String businessWebUrl) {
        this.businessWebUrl = businessWebUrl;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentReceiptNumber() {
        return paymentReceiptNumber;
    }

    public void setPaymentReceiptNumber(String paymentReceiptNumber) {
        this.paymentReceiptNumber = paymentReceiptNumber;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<ReceiptOrderItem> getItems() {
        return items;
    }

    public void setItems(List<ReceiptOrderItem> items) {
        this.items = items;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getGratisPoint() {
        return gratisPoint;
    }

    public void setGratisPoint(String gratisPoint) {
        this.gratisPoint = gratisPoint;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }


    public Bitmap getQrcode() {
        return qrcode;
    }

    public void setQrcode(Bitmap qrcode) {
        this.qrcode = qrcode;
    }

    public Bitmap getLogo() {



        return logo;
    }

    public void setLogo(Bitmap logo) {

        if(logo ==null){


        }else{
            this.logo = logo;
        }

    }

    public Boolean getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(Boolean order) {
        isOrder = order;
    }

    public String getRONumber(){


        if (getIsOrder() == true) {


            return  "Order No : " + getOrderNumber()+"\n";
        }else {

            return  "Receipt No : " + getPaymentReceiptNumber()+"\n";

        }

    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getNetSaleTotal() {
        return netSaleTotal;
    }

    public void setNetSaleTotal(String netSaleTotal) {
        this.netSaleTotal = netSaleTotal;
    }

    public String getCashTotal() {
        return cashTotal;
    }

    public void setCashTotal(String cashTotal) {
        this.cashTotal = cashTotal;
    }

    public String getMobileMoneyTotal() {
        return mobileMoneyTotal;
    }

    public void setMobileMoneyTotal(String mobileMoneyTotal) {
        this.mobileMoneyTotal = mobileMoneyTotal;
    }

    public String getCardTotal() {
        return cardTotal;
    }

    public void setCardTotal(String cardTotal) {
        this.cardTotal = cardTotal;
    }

    public String getHubtelTotal() {
        return hubtelTotal;
    }

    public void setHubtelTotal(String hubtelTotal) {
        this.hubtelTotal = hubtelTotal;
    }

    public List<ReceiptOrderDayItem> getItemsPicked() {
        return itemsPicked;
    }

    public void setItemsPicked(List<ReceiptOrderDayItem> itemsPicked) {
        this.itemsPicked = itemsPicked;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }


    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetails cardDetails) {
        this.cardDetails = cardDetails;
    }

    public String isDuplicate() {
        return (isDuplicate == true ? "(DUPLICATE)": "" );
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public List<ReceiptOrderDayItem> getDayItemList() {
        return dayItemList;
    }

    public void setDayItemList(List<ReceiptOrderDayItem> dayItemList) {
        this.dayItemList = dayItemList;
    }
}


