package org;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
public class AsciiArtWebController {

    @Inject
    Template ascii;

    @Inject
    AsciiArtService asciiArtService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showForm() {
        return ascii.data("fonts", asciiArtService.getAvailableFonts())
                .data("defaultFont", "standard")
                .data("text", "")
                .data("selectedFont", "standard");
    }

    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance generateAsciiArt(
            @FormParam("text") String text,
            @FormParam("font") String font) {

        TemplateInstance template = ascii.data("fonts", asciiArtService.getAvailableFonts())
                .data("defaultFont", "standard");

        if (text == null || text.isEmpty()) {
            return template.data("error", "Text is required")
                    .data("text", "")
                    .data("selectedFont", font != null ? font : "standard");
        }

        String asciiArt = asciiArtService.generateAsciiArt(text, font);

        return template.data("asciiArt", asciiArt)
                .data("text", text)
                .data("selectedFont", font);
    }

}