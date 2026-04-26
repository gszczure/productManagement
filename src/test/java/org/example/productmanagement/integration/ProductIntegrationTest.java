package org.example.productmanagement.integration;

import io.restassured.RestAssured;
import org.example.productmanagement.dto.request.ProductRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIntegrationTest {

    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(200);
    private static final String BASE_PATH = "/products";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
    }

    @Test
    @DisplayName("Should create product and fetch it by id")
    void createAndGet_shouldWorkCorrectly() {
        var request = request();

        int id = given()
                .contentType("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("name", equalTo("Laptop"))
                .extract()
                .path("id");

        given()
                .when()
                .get("/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Laptop"))
                .body("price", equalTo(PRICE.floatValue()));
    }

    @Test
    @DisplayName("Should return list of products")
    void getAll_shouldReturnProducts() {
        given()
                .contentType("application/json")
                .body(request())
                .when()
                .post();

        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("", not(empty()));
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void getById_shouldReturn404() {

        given()
                .when()
                .get("/{id}", 999)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should update product successfully")
    void update_shouldUpdateProduct() {
        int id = given()
                .contentType("application/json")
                .body(request())
                .when()
                .post()
                .then()
                .extract()
                .path("id");

        var updated = updatedRequest();

        given()
                .contentType("application/json")
                .body(updated)
                .when()
                .put("/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("UpdatedLaptop"));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void delete_shouldRemoveProduct() {
        int id = given()
                .contentType("application/json")
                .body(request())
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/{id}", id)
                .then()
                .statusCode(404);
    }

    private  ProductRequestDto updatedRequest() {
        return new ProductRequestDto(
                "UpdatedLaptop",
                "UpdatedDesc",
                UPDATED_PRICE,
                "IT"
        );
    }

    private ProductRequestDto request() {
        return new ProductRequestDto(
                "Laptop",
                "desc",
                PRICE,
                "IT"
        );
    }
}
