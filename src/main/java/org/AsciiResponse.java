package org;

public class AsciiResponse {
    private String ascii;
    private String originalText;
    private String fontUsed;

    public AsciiResponse() {
    }

    public AsciiResponse(String ascii, String originalText, String fontUsed) {
        this.ascii = ascii;
        this.originalText = originalText;
        this.fontUsed = fontUsed;
    }

    public String getAscii() {
        return ascii;
    }

    public void setAscii(String ascii) {
        this.ascii = ascii;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getFontUsed() {
        return fontUsed;
    }

    public void setFontUsed(String fontUsed) {
        this.fontUsed = fontUsed;
    }
}