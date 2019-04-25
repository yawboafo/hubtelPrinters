package com.hubtel.hubtelprinters.receiptbuilder;

public class CardDetails {


    private  String schema;
    private  String card;
    private  String tid;
    private  String authorization;
    private  String mid;
    private  String transID;


    public CardDetails() {
    }

    public CardDetails(String schema, String card, String tid, String authorization, String mid, String transID) {
        this.schema = schema;
        this.card = card;
        this.tid = tid;
        this.authorization = authorization;
        this.mid = mid;
        this.transID = transID;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }
}
