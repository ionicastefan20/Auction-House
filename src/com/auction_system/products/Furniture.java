package com.auction_system.products;

public class Furniture extends Product {

    private String type;

    private String material;

    private Furniture(int id, double minPrice) {
        super(id, minPrice);
    }

    @Override
    public String getCustom1() {
        return type;
    }

    @Override
    public String getCustom2() {
        return material;
    }

    @Override
    public String toString() {
        return super.toString() + ", type='" + type + '\'' +
                ", material='" + material + '\'' +
                '}';
    }

    public static class FurnitureBuilder extends Product.ProductBuilder {

        private final Furniture furniture;

        public FurnitureBuilder(int id, double minPrice) {
            product = new Furniture(id, minPrice);
            furniture = (Furniture) product;
        }

        public FurnitureBuilder withType(String type) {
            furniture.type = type;
            return this;
        }

        public FurnitureBuilder withMaterial(String material) {
            furniture.material = material;
            return this;
        }
    }
}
