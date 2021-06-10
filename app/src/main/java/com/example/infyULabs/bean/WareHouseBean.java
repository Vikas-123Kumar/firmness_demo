package com.example.infyULabs.bean;

class wareHouseBean {
    public static final String wareHouseId = "id";
    public static final String warename = "wareHouseName";
    public static final String address = "address";

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public String getWareHouseAddress() {
        return wareHouseAddress;
    }

    public void setWareHouseAddress(String wareHouseAddress) {
        this.wareHouseAddress = wareHouseAddress;
    }

    String wareHouseName, wareHouseAddress;

}
