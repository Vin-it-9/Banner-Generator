package org;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/ascii")
public class AsciiArtResource {

    @Inject
    AsciiArtService asciiArtService;

    @GET
    @Path("/convert")
    @Produces(MediaType.TEXT_PLAIN)
    public Response convertTextToAsciiGet(
            @QueryParam("text") String text,
            @QueryParam("font") @DefaultValue("standard") String font) {

        if (text == null || text.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Text parameter is required")
                    .build();
        }

        String asciiArt = asciiArtService.generateAsciiArt(text, font);
        return Response.ok(asciiArt).build();
    }

    @POST
    @Path("/convert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response convertTextToAsciiPost(AsciiRequest request) {
        if (request.getText() == null || request.getText().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Text is required")
                    .build();
        }

        String fontToUse = request.getFont() != null && !request.getFont().isEmpty()
                ? request.getFont() : "standard";

        String asciiArt = asciiArtService.generateAsciiArt(request.getText(), fontToUse);
        AsciiResponse response = new AsciiResponse(asciiArt, request.getText(), fontToUse);

        return Response.ok(response).build();
    }

    @GET
    @Path("/fonts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableFonts() {
        List<String> fonts = asciiArtService.getAvailableFonts();
        return Response.ok(fonts).build();
    }

    @POST
    @Path("/plain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPlainAsciiArt(AsciiRequest request) {
        if (request.getText() == null || request.getText().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Text is required")
                    .build();
        }

        String fontToUse = request.getFont() != null && !request.getFont().isEmpty()
                ? request.getFont() : "standard";

        String asciiArt = asciiArtService.generateAsciiArt(request.getText(), fontToUse);
        return Response.ok(asciiArt).build();
    }
}