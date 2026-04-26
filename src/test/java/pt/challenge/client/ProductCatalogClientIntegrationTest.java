package pt.challenge.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import pt.challenge.dto.external.product.ProductCategoryResponse;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "external.services.product-catalog.url=http://localhost:${wiremock.server.port}",
        "resilience4j.circuitbreaker.instances.productCatalog.sliding-window-size=2",
        "resilience4j.circuitbreaker.instances.productCatalog.minimum-number-of-calls=2"
})
@ActiveProfiles("test")
class ProductCatalogClientIntegrationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        System.setProperty("wiremock.server.port", String.valueOf(wireMockServer.port()));
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Autowired
    private ProductCatalogClient productCatalogClient;

    @Test
    void shouldFetchProductsSuccessfully() {
        String category = "electronics";
        stubFor(get(urlEqualTo("/api/products/category/" + category))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "category": "electronics",
                                  "products": [
                                    {
                                      "productId": "p1",
                                      "name": "Phone",
                                      "currentPrice": 900.0,
                                      "originalPrice": 1000.0,
                                      "availability": "IN_STOCK"
                                    }
                                  ]
                                }
                                """)));

        ProductCategoryResponse response = productCatalogClient.getProductsByCategory(category);

        assertThat(response.category()).isEqualTo(category);
        assertThat(response.products()).hasSize(1);
    }

    @Test
    void shouldFallbackOnFailure() {
        String category = "error-cat";
        stubFor(get(urlEqualTo("/api/products/category/" + category))
                .willReturn(aResponse().withStatus(500)));

        ProductCategoryResponse response = productCatalogClient.getProductsByCategory(category);

        assertThat(response).isNotNull();
        assertThat(response.category()).isEqualTo(category);
        assertThat(response.products()).isEmpty();
    }
}
