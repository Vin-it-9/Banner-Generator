package org;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@QuarkusTest
class AsciiArtResourceTest {

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

//    @Test
//    public void testGetFontsEndpoint() {
//        given()
//                .when().get("/api/ascii/fonts")
//                .then()
//                .statusCode(200)
//                .contentType("application/json")
//                .body("size()", is(17));
//    }
}
