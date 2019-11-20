package org.scada_lts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.util.Log;
import org.scada_lts.dao.MultiChangesHistory;
import org.scada_lts.dao.model.multichangehistory.MultiChangeHistoryValues;
import org.scada_lts.service.model.MultiChangeHistoryDTO;
import org.scada_lts.service.model.ValuesMultiChangesHistoryDTO;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValuePointDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author  grzegorz.bylica@abilit.eu on 15.10.2019
 */

@Service
public class MultiChangesHistoryService {

    @Autowired
    private MultiChangesHistory multiChangesHistoryDAO;

    public Set<MultiChangeHistoryDTO> getHistory(String idViewAndCmpId) {

        List<MultiChangeHistoryValues> multiChangeHistoryValuesList = multiChangesHistoryDAO.getHistory(idViewAndCmpId);

        Set<MultiChangeHistoryDTO> result = new HashSet<>();
        MultiChangeHistoryDTO multiChangeHistoryDTO = new MultiChangeHistoryDTO();
        if (multiChangeHistoryValuesList != null) {
            for (MultiChangeHistoryValues item : multiChangeHistoryValuesList) {
                if (multiChangeHistoryDTO.getId() == MultiChangeHistoryDTO.NEW_ID) {

                    helpTransform(multiChangeHistoryDTO, item);
                } else if (item.getId() == multiChangeHistoryDTO.getId()) {

                    ValuesMultiChangesHistoryDTO valuesMultiChangesHistoryDTO = new ValuesMultiChangesHistoryDTO();
                    valuesMultiChangesHistoryDTO.setValue(item.getValue());
                    valuesMultiChangesHistoryDTO.setXidPoint(item.getXidPoint());
                    multiChangeHistoryDTO.getValues().add(valuesMultiChangesHistoryDTO);
                } else if (item.getId() != multiChangeHistoryDTO.getId()) {

                    result.add(new MultiChangeHistoryDTO(multiChangeHistoryDTO));
                    multiChangeHistoryDTO = new MultiChangeHistoryDTO();
                    helpTransform(multiChangeHistoryDTO, item);
                }
            }
            result.add(multiChangeHistoryDTO);
        }
        return result;

    }

    public void addToCmpHistory(Integer userId, String viewAndCmpId, String interpretedState, SetValuePointDTO[] values) {

        ObjectMapper mapper = new ObjectMapper();

        String json;
        try {
            json = mapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            Log.error(e);
            return;
        }

        multiChangesHistoryDAO.addHistoryFromCMPComponent(
               userId,
                viewAndCmpId,
                interpretedState,
                new Date().getTime() * 1000,
               json
        );
    }

    private void helpTransform(MultiChangeHistoryDTO multiChangeHistoryDTO, MultiChangeHistoryValues multiChangeHistoryValues) {

        multiChangeHistoryDTO.setId(multiChangeHistoryValues.getId());
        multiChangeHistoryDTO.setUserId(multiChangeHistoryValues.getUserId());
        multiChangeHistoryDTO.setUserName(multiChangeHistoryValues.getUserName());
        multiChangeHistoryDTO.setInterpretedState(multiChangeHistoryValues.getInterpretedState());
        multiChangeHistoryDTO.setUnixTime(multiChangeHistoryValues.getTimeStamp() / 1000);

        ValuesMultiChangesHistoryDTO valuesMultiChangesHistoryDTO = new ValuesMultiChangesHistoryDTO();
        valuesMultiChangesHistoryDTO.setValue(multiChangeHistoryValues.getValue());
        valuesMultiChangesHistoryDTO.setXidPoint(multiChangeHistoryValues.getXidPoint());

        multiChangeHistoryDTO.getValues().add(valuesMultiChangesHistoryDTO);
    }

}
