package org.scada_lts.web.mvc.api.dto;

public class ImageSetIdentifier {
    private String id;
    private String name;
    private Integer imageCount;

    public ImageSetIdentifier(String id, String name, Integer imageCount) {
        this.id = id;
        this.name = name;
        this.imageCount = imageCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }
}
