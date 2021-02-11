package com.auction_system.products;

public class ProductFactory {

    private ProductFactory() {
    }

    public static Product getProduct(String type, String[] data) {
        Product.ProductBuilder builder;

        int productId = Integer.parseInt(data[0]);
        double minPrice = Double.parseDouble(data[2]);
        int year = Integer.parseInt(data[3]);

        if ("furniture".equalsIgnoreCase(type))
            builder = new Furniture.FurnitureBuilder(productId, minPrice)
                    .withType(data[4])
                    .withMaterial(data[5]);
        else if ("jewelry".equalsIgnoreCase(type))
            builder = new Jewelry.JewelryBuilder(productId, minPrice)
                    .withMaterial(data[4])
                    .withPreciousStone("yes".equals(data[5]));
        else if ("painting".equalsIgnoreCase(type))
            builder = new Painting.PaintingBuilder(productId, minPrice)
                    .withArtistName(data[4])
                    .withColor(Painting.Colors.valueOf(data[5]));
        else
            return null;

        return builder.withName(data[1])
                .withYear(year)
                .build();
    }
}
