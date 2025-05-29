package com.lirans2341project.model;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {

    public enum PurchaseStatus {
        AVAILABLE("זמין לקנייה"),
        SOLD("נרכש");

        private final String displayName;

        PurchaseStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    protected String id, name, type, size, color, fabric, pic, userId;
    protected double price;
    protected PurchaseStatus status;

    // אתרי בנאי


    public Item(String id, String name, String type, String size,
                String color, String fabric, String pic, String userId, double price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.color = color;
        this.fabric = fabric;
        this.pic = pic;
        this.userId = userId;
        this.price = price;
        this.status = PurchaseStatus.AVAILABLE; // ברירת מחדל: זמין לקנייה
    }

    public Item() {
        this.status = PurchaseStatus.AVAILABLE; // ברירת מחדל: זמין לקנייה
    }

    // הגדרת getters ו-setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", fabric='" + fabric + '\'' +
                ", pic='" + (pic != null ? "exist" : "not exist") + '\'' +
                ", userId='" + userId + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
