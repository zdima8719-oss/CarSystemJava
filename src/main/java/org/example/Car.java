package org.example;

public class Car {
    private int id;
    private String brand;
    private String model;
    private int year;
    private double price;
    private int mileage;
    private String color;
    private double engineVolume;
    private boolean inStock;

    // Пустой конструктор нужен для Gson
    public Car() {
    }

    public Car(int id, String brand, String model, int year, double price, int mileage, String color, double engineVolume, boolean inStock) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.mileage = mileage;
        this.color = color;
        this.engineVolume = engineVolume;
        this.inStock = inStock;
    }

    // Геттеры и сеттеры (нужны для Gson и логики)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public double getEngineVolume() { return engineVolume; }
    public void setEngineVolume(double engineVolume) { this.engineVolume = engineVolume; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    @Override
    public String toString() {
        String status = inStock ? "В наличии" : "Продан";
        return String.format("ID: %d | %s %s | %d г. | %,.0f руб. | Пробег: %d км | Цвет: %s | Объём: %.1f л | %s",
                id, brand, model, year, price, mileage, color, engineVolume, status);
    }
}