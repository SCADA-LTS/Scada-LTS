package org.scada_lts.service.model;

import java.util.Set;

/**
 * @author  grzegorz.bylica@abilit.eu on 17.10.2019
 */
public class MultiChangeHistoryComponentDTO {

    private String xIdViewAndComponent;
    private Set<MultiChangeHistoryDTO> history;

    public MultiChangeHistoryComponentDTO(String xIdViewAndComponent, Set<MultiChangeHistoryDTO> history) {
        this.xIdViewAndComponent = xIdViewAndComponent;
        this.history = history;
    }

    public String getxIdViewAndComponent() {
        return xIdViewAndComponent;
    }

    public void setxIdViewAndComponent(String xIdViewAndComponent) {
        this.xIdViewAndComponent = xIdViewAndComponent;
    }

    public Set<MultiChangeHistoryDTO> getHistory() {
        return history;
    }

    public void setHistory(Set<MultiChangeHistoryDTO> history) {
        this.history = history;
    }
}
