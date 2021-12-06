package org.scada_lts.web.mvc.api.dto.view.components;

import br.org.scadabr.view.component.ChartComparatorComponent;
import com.serotonin.mango.vo.User;

public class ChartComparatorComponentDTO extends HtmlComponentDTO{
    private Integer width;
    private Integer height;

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

    @Override
    public ChartComparatorComponent createFromBody(User user) {
        ChartComparatorComponent c = new ChartComparatorComponent();
        c.setHeight(height);
        c.setWidth(width);

        c.setContent(getContent());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
