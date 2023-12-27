package org.scada_lts.web.mvc.api.dto;

public class UploadImage {
    private String name;
    private String imgUrl;
    private Integer width;
    private Integer height;

    public UploadImage(String name, String imgUrl, Integer width, Integer height) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
