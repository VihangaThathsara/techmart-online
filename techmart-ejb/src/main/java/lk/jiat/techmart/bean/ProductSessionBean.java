package lk.jiat.techmart.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import lk.jiat.techmart.interceptor.PerformanceInterceptor;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.remote.ProductRemote;
import lk.jiat.techmart.store.TechMartStore;
import lk.jiat.techmart.util.TimerUtil;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Stateless
@Interceptors(PerformanceInterceptor.class)
public class ProductSessionBean implements ProductRemote {
    private static final Logger LOGGER = Logger.getLogger(ProductSessionBean.class.getName());


    @PostConstruct
    public void init() {
        LOGGER.info("Product session bean initialized");
    }

    @Override
    public ProductDTO getProduct(Long id) {
        long start = TimerUtil.start();
        ProductDTO product = TechMartStore.findProduct(id);
        LOGGER.info("getProduct time = " + TimerUtil.elapsedMillis(start) + " ms");
        return product;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        long start = TimerUtil.start();
        List<ProductDTO> products = TechMartStore.products();
        LOGGER.info("getAllProducts time = " + TimerUtil.elapsedMillis(start) + " ms");
        return products;
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        String lower = keyword.toLowerCase(Locale.ROOT);
        return TechMartStore.products().stream()
                .filter(product -> product.getName().toLowerCase(Locale.ROOT).contains(lower)
                        || product.getSku().toLowerCase(Locale.ROOT).contains(lower))
                .collect(Collectors.toList());
    }
}
