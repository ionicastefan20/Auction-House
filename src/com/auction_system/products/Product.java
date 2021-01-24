package com.auction_system.products;

import com.auction_system.auction_house.Auction;
import lombok.Getter;

@Getter
public abstract class Product {

    private final int id;
    private String name;

    // TODO UPDATE SALE PRICE
    private double salePrice;

    private final double minPrice;
    private int year;
    private final Auction auction;

    protected Product(int id, double minPrice) {
        this.id = id;
        this.minPrice = minPrice;
        auction = new Auction(id);
    }

    public abstract String getCustom1();

    public abstract String getCustom2();

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", minPrice=" + minPrice +
                ", year=" + year +
                ", auction=" + auction;
    }

    public abstract static class ProductBuilder {
        protected Product product;

        protected ProductBuilder() {}

        public ProductBuilder withName(String name) {
            product.name = name;
            return this;
        }

        public ProductBuilder withYear(int year) {
            product.year = year;
            return this;
        }

        public Furniture.FurnitureBuilder asFurnitureBuilder() {
            return (Furniture.FurnitureBuilder) this;
        }

        public Jewelry.JewelryBuilder asJewelryBuilder() {
            return (Jewelry.JewelryBuilder) this;
        }

        public Painting.PaintingBuilder asPaintingBuilder() {
            return (Painting.PaintingBuilder) this;
        }

        public Product build() {
            return product;
        }
    }
}

