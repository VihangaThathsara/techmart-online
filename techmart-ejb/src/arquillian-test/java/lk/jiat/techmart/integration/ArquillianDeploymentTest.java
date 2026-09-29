package lk.jiat.techmart.integration;

import jakarta.ejb.EJB;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.remote.ProductRemote;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ArquillianExtension.class)
class ArquillianDeploymentTest {

    @Deployment
    static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "techmart-ejb-test.jar")
                .addPackages(true, "lk.jiat.techmart")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    private ProductRemote productRemote;

    @Test
    void statelessProductBeanShouldBeAvailableInsideContainer() {
        assertNotNull(productRemote);
        List<ProductDTO> products = productRemote.getAllProducts();
        assertFalse(products.isEmpty());
    }
}
