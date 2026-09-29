package lk.jiat.techmart.web.util;

import lk.jiat.techmart.dto.ProductDTO;

import java.util.List;

public final class ProductImageUtil {
    private ProductImageUtil() {
    }

    public static void applyLocalImages(List<ProductDTO> products) {
        if (products == null) {
            return;
        }
        for (ProductDTO product : products) {
            applyLocalImage(product);
        }
    }

    public static void applyLocalImage(ProductDTO product) {
        if (product == null) {
            return;
        }
        String sku = product.getSku();
        if (sku == null) {
            sku = "";
        }
        switch (sku.trim().toUpperCase()) {
            case "LAP-001":
                product.setImageUrl("ultrabook-pro-14.png");
                break;
            case "PHN-002":
                product.setImageUrl("nova-x-smartphone.png");
                break;
            case "HDP-003":
                product.setImageUrl("pulse-wireless-headphones.png");
                break;
            case "KBD-004":
                product.setImageUrl("orion-mechanical-keyboard.png");
                break;
            case "MON-005":
                product.setImageUrl("vision-27-monitor.png");
                break;
            case "WCH-006":
                product.setImageUrl("smart-watch-s5.png");
                break;
            case "MOU-007":
                if (isRemote(product.getImageUrl())) {
                    product.setImageUrl("swift-wireless-mouse.png");
                }
                break;
            case "SPK-008":
                product.setImageUrl("bassbox-bluetooth-speaker.png");
                break;
            default:
                if (isRemote(product.getImageUrl())) {
                    product.setImageUrl("product-default.png");
                }
                break;
        }
    }

    private static boolean isRemote(String imageUrl) {
        return imageUrl == null || imageUrl.trim().isEmpty()
                || imageUrl.startsWith("http://")
                || imageUrl.startsWith("https://");
    }
}
