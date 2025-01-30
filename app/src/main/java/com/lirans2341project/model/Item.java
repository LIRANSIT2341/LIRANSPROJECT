package com.lirans2341project.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Item implements Serializable {

    protected String id, name, type, size, color, fabric, pic, status;
    protected Integer price;
    User user;

    // אתרי בנאי
    public Item(String id, String name, String type, String size, String color, String fabric, String pic, Integer price, String userId, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.color = color;
        this.fabric = fabric;
        this.pic = pic;
        this.price = price;
        this.user = user;
        this.status = status;
    }

    public Item() {}

    // הגדרת getters ו-setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getFabric() { return fabric; }
    public void setFabric(String fabric) { this.fabric = fabric; }

    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", fabric='" + fabric + '\'' +
                ", pic='" + pic + '\'' +
                ", price=" + price +
                ", user=" + user +
                ", status='" + status + '\'' +
                '}';
    }


}
