package org.scada_lts.archiving;

public enum ArchiveFunction {
    COPY_TO_ARCHIVE("Archive"),
    DELETE_IF_IN_ARCHIVE("Delete");

    private final String labelKey;

    ArchiveFunction(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
