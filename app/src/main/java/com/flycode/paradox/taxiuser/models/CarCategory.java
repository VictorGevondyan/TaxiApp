package com.flycode.paradox.taxiuser.models;

/**
 * Created by anhaytananun on 25.12.15.
 */
public class CarCategory {
    private String id;
    private String name;
    private String description;
    private double timePrice;
    private double routePrice;
    private double minPrice;

    public CarCategory(String id, String name, String description, double timePrice, double routePrice, double minPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timePrice = timePrice;
        this.routePrice = routePrice;
        this.minPrice = minPrice;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getTimePrice() {
        return timePrice;
    }

    public double getRoutePrice() {
        return routePrice;
    }

    public double getMinPrice() {
        return minPrice;
    }
}
