package com.auction_system.products;

public class Painting extends Product {

    private String artistName;
    private Colors color;

    private Painting(int id, double minPrice) {
        super(id, minPrice);
    }

    @Override
    public String getCustom1() {
        return artistName;
    }

    @Override
    public String getCustom2() {
        return color.toString();
    }

    @Override
    public String toString() {
        return super.toString() + ", artistName='" + artistName + '\'' +
                ", color=" + color +
                '}';
    }

    public enum Colors {
        oil,
        tempera,
        acrylic
    }

    public static class PaintingBuilder extends Product.ProductBuilder {


        private final Painting painting;

        public PaintingBuilder(int id, double minPrice) {
            product = new Painting(id, minPrice);
            painting = (Painting) product;
        }

        public PaintingBuilder withArtistName(String artistName) {
            painting.artistName = artistName;
            return this;
        }

        public PaintingBuilder withColor(Colors color) {
            painting.color = color;
            return this;
        }
    }
}
