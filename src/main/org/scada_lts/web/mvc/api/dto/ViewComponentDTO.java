package org.scada_lts.web.mvc.api.dto;

import java.io.Serializable;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewComponentDTO implements Serializable {
    private String id;
    private int index;
    private String defName;
    private String idSuffix;
    private String style;
    private int X;
    private int Y;

    public ViewComponentDTO() {
    }

    public ViewComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y) {
        this.id = id;
        this.index = index;
        this.defName = defName;
        this.idSuffix = idSuffix;
        this.style = style;
        X = x;
        Y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDefName() {
        return defName;
    }

    public void setDefName(String defName) {
        this.defName = defName;
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public void setIdSuffix(String idSuffix) {
        this.idSuffix = idSuffix;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }
}
