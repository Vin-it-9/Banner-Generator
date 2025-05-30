package org;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ExampleResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello from Quarkus REST"));
    }

    @Test
    public void testGetConvertEndpoint() {
        given()
                .param("text", "Hello")
                .when().get("/api/ascii/convert")
                .then()
                .statusCode(200)
                .contentType("text/plain");
    }

    @Test
    public void testGetConvertEndpointWithoutText() {
        given()
                .when().get("/api/ascii/convert")
                .then()
                .statusCode(400);
    }


}