package com.locksmith.fpl.Dentist.DTO;

import java.io.Serializable;

public class VPDTO1 implements Serializable {
    String qty = "";
    String unit_price = "";
    String total_price = "";

    public VPDTO1(String qty, String unit_price, String total_price) {
        this.qty = qty;
        this.unit_price = unit_price;
        this.total_price = total_price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }
}
