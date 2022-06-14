package com.miituo.miituolibrary.activities.data;

import com.google.gson.annotations.SerializedName;

public class FacturasResponse {

    @SerializedName("PolicyId")
    private int policyID;

    @SerializedName("PolicyNumber")
    private String policyNumber;

    @SerializedName("EstadoCuentaId")
    private int EstadoCuentaId;

    @SerializedName("Month")
    private int month;

    @SerializedName("MonthBill")
    private int monthBill;

    @SerializedName("MonthName")
    private String monthName;

    @SerializedName("PaymentType")
    private String paymentyype;

    @SerializedName("Amount")
    private Double amount;

    private Boolean sending;

    public FacturasResponse(int policyID, String policyNumber, int estadoCuentaId, int month, int monthBill, String monthName, String paymentyype, Double amount) {
        this.policyID = policyID;
        this.policyNumber = policyNumber;
        EstadoCuentaId = estadoCuentaId;
        this.month = month;
        this.monthBill = monthBill;
        this.monthName = monthName;
        this.paymentyype = paymentyype;
        this.amount = amount;
        this.sending = false;
    }

    public int getPolicyID() {
        return policyID;
    }

    public void setPolicyID(int policyID) {
        this.policyID = policyID;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public int getEstadoCuentaId() {
        return EstadoCuentaId;
    }

    public void setEstadoCuentaId(int estadoCuentaId) {
        EstadoCuentaId = estadoCuentaId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonthBill() {
        return monthBill;
    }

    public void setMonthBill(int monthBill) {
        this.monthBill = monthBill;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getPaymentyype() {
        return paymentyype;
    }

    public void setPaymentyype(String paymentyype) {
        this.paymentyype = paymentyype;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getSending() {
        return sending;
    }

    public void setSending(Boolean sending) {
        this.sending = sending;
    }

    @Override
    public String toString() {
        return "FacturasResponse{" +
                "policyID=" + policyID +
                ", policyNumber='" + policyNumber + '\'' +
                ", EstadoCuentaId=" + EstadoCuentaId +
                ", month=" + month +
                ", monthBill=" + monthBill +
                ", monthName='" + monthName + '\'' +
                ", paymentyype='" + paymentyype + '\'' +
                ", amount=" + amount +
                ", sending=" + sending +
                '}';
    }
}
