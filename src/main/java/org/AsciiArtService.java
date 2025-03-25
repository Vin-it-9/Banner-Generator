package org;

import com.github.lalyos.jfiglet.FigletFont;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

            loadFontsFromResources();

            LOGGER.info("Initialized ASCII Art Service with fonts: " + availableFonts);
        } catch (Exception e) {
            LOGGER.severe("Error initializing ASCII Art Service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFontsFromResources() {
        try {
            URL fontsDirUrl = getClass().getResource("/fonts");
            if (fontsDirUrl == null) {
                LOGGER.warning("Could not find fonts directory in resources");

                extractFontFromClasspath(DEFAULT_FONT);
                availableFonts.add(DEFAULT_FONT);
                return;
            }

            URI fontsUri = fontsDirUrl.toURI();

            Path fontsPath;
            if (fontsUri.getScheme().equals("jar")) {
                try (FileSystem fs = FileSystems.newFileSystem(fontsUri, Collections.emptyMap())) {
                    fontsPath = fs.getPath("/fonts");
                    processDirectoryContent(fontsPath);
                }
            } else {
                fontsPath = Paths.get(fontsUri);
                processDirectoryContent(fontsPath);
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.warning("Error loading fonts from resources: " + e.getMessage());
            extractFontFromClasspath(DEFAULT_FONT);
            if (!availableFonts.contains(DEFAULT_FONT)) {
                availableFonts.add(DEFAULT_FONT);
            }
        }
    }

    private void processDirectoryContent(Path fontsPath) throws IOException {
        try (Stream<Path> paths = Files.list(fontsPath)) {
            List<Path> fontFiles = paths
                    .filter(path -> path.toString().endsWith(".flf"))
                    .collect(Collectors.toList());

            for (Path fontFile : fontFiles) {
                String fontName = fontFile.getFileName().toString().replace(".flf", "");
                extractFontFromClasspath(fontName);
                availableFonts.add(fontName);
            }
        }

        if (availableFonts.isEmpty()) {
            extractFontFromClasspath(DEFAULT_FONT);
            availableFonts.add(DEFAULT_FONT);
        }
    }

    private void extractFontFromClasspath(String fontName) {
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

        fontName = (fontName != null) ? fontName.toLowerCase() : DEFAULT_FONT;
        if (!isFontAvailable(fontName)) {
            LOGGER.info("Font not available: " + fontName + ". Using default: " + DEFAULT_FONT);
            fontName = DEFAULT_FONT;
        }

        try {
            Path fontPath = Paths.get(tempFontDir.toString(), fontName + ".flf");

            if (!Files.exists(fontPath)) {
                LOGGER.warning("Font file not found: " + fontPath + ". Falling back to default.");
                fontPath = Paths.get(tempFontDir.toString(), DEFAULT_FONT + ".flf");
                if (!Files.exists(fontPath)) {
                    extractFontFromClasspath(DEFAULT_FONT);
                }
            }

            return FigletFont.convertOneLine(fontPath.toFile(), text);
        } catch (IOException e) {
            LOGGER.severe("Error generating ASCII art: " + e.getMessage());
            try {
                return FigletFont.convertOneLine(text);
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