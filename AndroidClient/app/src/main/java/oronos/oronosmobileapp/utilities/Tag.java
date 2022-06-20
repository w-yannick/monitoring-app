package oronos.oronosmobileapp.utilities;

/**
 * Tag.java
 * Enum pour les tags des logs
 */
public enum Tag {

    HTTP("HTTP"),
    SOCKET("SOCKET"),
    UPDATOR("UPDATOR"),
    APPLICATION("APPLICATION"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    LAYOUT_TREE("LAYOUT_TREE"),
    PDF("PDF")
    // Ajoutez vos tags au besoin ici
    ;
    private final String text;

    Tag(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
