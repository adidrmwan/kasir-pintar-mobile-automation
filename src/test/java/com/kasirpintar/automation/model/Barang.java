package com.kasirpintar.automation.model;

public record Barang(String name, String code, String basicPrice, String sellingPrice, String stock) {

    public static Barang defaultItem(String name, String sellingPrice, String stock) {
        String code = deriveCode(name);
        return new Barang(name, code, sellingPrice, sellingPrice, stock);
    }

    public static Barang of(String name, String code, String basicPrice, String sellingPrice, String stock) {
        return new Barang(name, code, basicPrice, sellingPrice, stock);
    }

    private static String deriveCode(String name) {
        String base = name.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (base.length() > 8) {
            base = base.substring(0, 8);
        }
        return base + (System.currentTimeMillis() % 10000);
    }
}
