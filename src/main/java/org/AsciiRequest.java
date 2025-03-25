package org;


public class AsciiRequest {

    private String text;
    private String font;

    public AsciiRequest() {
    }

    public AsciiRequest(String text, String font) {
        this.text = text;
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}