package com.auction_system.products;

public class Jewelry extends Product {
    private String material;
    private boolean preciousStone;


    private Jewelry(int id, double minPrice) {
        super(id, minPrice);
    }

    @Override
    public String getCustom1() {
        return material;
    }

    @Override
    public String getCustom2() {
        return (preciousStone) ? "yes" : "no";
    }

    @Override
    public String toString() {
        return super.toString() + ", material='" + material + '\'' +
                ", preciousStone=" + preciousStone +
                '}';
    }

    public static class JewelryBuilder extends Product.ProductBuilder {

        private final Jewelry jewelry;

        public JewelryBuilder(int id, double minPrice) {
            product = new Jewelry(id, minPrice);
            jewelry = (Jewelry) product;
        }

        public JewelryBuilder withMaterial(String material) {
            jewelry.material = material;
            return this;
        }

        public JewelryBuilder withPreciousStone(boolean preciousStone) {
            jewelry.preciousStone = preciousStone;
            return this;
        }
    }
}
