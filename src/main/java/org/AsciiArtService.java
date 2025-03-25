package org;

import com.github.lalyos.jfiglet.FigletFont;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AsciiArtService {

    private static final Logger LOGGER = Logger.getLogger(AsciiArtService.class.getName());
    private static final String DEFAULT_FONT = "standard";
    private final List<String> availableFonts = new ArrayList<>();
    private Path tempFontDir;

    @PostConstruct
    public void init() {
        try {

            tempFontDir = Files.createTempDirectory("figlet-fonts");
            tempFontDir.toFile().deleteOnExit();

            String[] fontNames = {
                    "standard", "banner", "big",
                    "roman", "univers" , "slant"
            };

            for (String fontName : fontNames) {
                extractFont(fontName);
            }

            for (String fontName : fontNames) {
                availableFonts.add(fontName);
            }

            LOGGER.info("Initialized ASCII Art Service with fonts: " + availableFonts);
        } catch (Exception e) {
            LOGGER.severe("Error initializing ASCII Art Service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void extractFont(String fontName) {
        try {
            String fontResource = "/fonts/" + fontName + ".flf";
            InputStream is = getClass().getResourceAsStream(fontResource);

            if (is == null) {
                LOGGER.warning("Font resource not found: " + fontResource);
                return;
            }

            Path fontPath = Paths.get(tempFontDir.toString(), fontName + ".flf");
            Files.copy(is, fontPath, StandardCopyOption.REPLACE_EXISTING);
            is.close();

            LOGGER.info("Extracted font: " + fontName + " to " + fontPath);
        } catch (IOException e) {
            LOGGER.warning("Failed to extract font " + fontName + ": " + e.getMessage());
        }
    }

    public String generateAsciiArt(String text, String fontName) {
        if (text == null || text.isEmpty()) {
            return "No text provided";
        }

        try {
            // If font name is not provided or not available, use default font
            if (fontName == null || fontName.isEmpty() || !isFontAvailable(fontName)) {
                LOGGER.info("Using default font: " + DEFAULT_FONT);
                fontName = DEFAULT_FONT;
            }

            Path fontPath = Paths.get(tempFontDir.toString(), fontName + ".flf");
            if (!Files.exists(fontPath)) {
                LOGGER.warning("Font file not found: " + fontPath);
                fontPath = Paths.get(tempFontDir.toString(), DEFAULT_FONT + ".flf");
            }

            return FigletFont.convertOneLine(fontPath.toFile(), text);
        } catch (IOException e) {
            LOGGER.severe("Error generating ASCII art: " + e.getMessage());
            e.printStackTrace();

            // Try with renderable internal font as fallback
            try {
                return FigletFont.convertOneLine("small", text);
            } catch (IOException ex) {
                return "Error generating ASCII art: " + e.getMessage();
            }
        }
    }

    public List<String> getAvailableFonts() {
        return new ArrayList<>(availableFonts);
    }

    private boolean isFontAvailable(String fontName) {
        return availableFonts.contains(fontName.toLowerCase());
    }
}