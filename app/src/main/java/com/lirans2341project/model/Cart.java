package com.lirans2341project.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart implements Serializable {

    /// unique id of the cart
    private String id;

    private String title;

    private final List<Item> items;

    /// the user ID of the cart owner
    private String uid;

    public Cart() {
        items = new ArrayList<>();
    }

    public Cart(String id, String title, List<Item> items, String uid) {
        this.id = id;
        this.title = title;
        this.items = items;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addItems(List<Item> items) {
        this.items.addAll(items);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public Item removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.remove(index);
    }

    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public List<Item> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Item item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    public void clear() {
        items.clear();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", items=" + items +
                ", uid='" + uid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Cart cart = (Cart) object;
        return Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
