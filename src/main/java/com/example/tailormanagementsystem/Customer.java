package com.example.tailormanagementsystem;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customer")
public class Customer {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String mobile;
    private String address;
    private String garment;
    private int quantity;
    private double price;


    //  Measurement fields
    private Float chest;
    private Float waist;
    private Float hip;
    private Float shoulder;
    private Float sleeve;
    private Float inseam;


    // 🔹 Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getGarment() {
        return garment;
    }

    public int getQuantity(){
        return quantity;
    }

    public double getPrice(){
        return price;
    }

    public Float getChest() {
        return chest;
    }
    public Float getWaist() {
        return waist;
    }
    public Float getHip() {
        return hip;
    }
    public Float getShoulder() {
        return shoulder;
    }
    public Float getSleeve() {
        return sleeve;
    }
    public Float getInseam() {
        return inseam;
    }


    // 🔹 Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGarment(String garment) {
        this.garment = garment;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setPrice(double price){
        this.price = price;
    }
    public void setChest(Float chest) {
        this.chest = chest;
    }
    public void setWaist(Float waist) {
        this.waist = waist;
    }
    public void setHip(Float hip) {
        this.hip = hip;
    }
    public void setShoulder(Float shoulder) {
        this.shoulder = shoulder;
    }
    public void setSleeve(Float sleeve) {
        this.sleeve = sleeve;
    }
    public void setInseam(Float inseam) {
        this.inseam = inseam;
    }

}