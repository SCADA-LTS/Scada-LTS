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

        List<MultiChangeHistoryValues> data = multiChangesHistoryDAO.getHistory(idViewAndCmpId);

        Set<MultiChangeHistoryDTO> result = new HashSet<>();
        MultiChangeHistoryDTO mch = new MultiChangeHistoryDTO();
        for (MultiChangeHistoryValues mchv: data) {
            if (mch.getId() == MultiChangeHistoryDTO.NEW_ID) {

                helpTransform(mch, mchv);
            } else if (mchv.getId() == mch.getId()) {

                ValuesMultiChangesHistoryDTO vmch = new ValuesMultiChangesHistoryDTO();
                vmch.setValue(mchv.getValue());
                vmch.setXidPoint(mchv.getXidPoint());
                mch.getValues().add(vmch);
            } else if (mchv.getId() != mch.getId()) {

                result.add(new MultiChangeHistoryDTO(mch));
                mch = new MultiChangeHistoryDTO();
                helpTransform(mch, mchv);
            }
        }
        result.add(mch);
        return result;

    }

    public void addToCmpHistory(Integer userId, String xIdViewAndIdCmp, String interpretedState, SetValuePointDTO[] values) {

        ObjectMapper mapper = new ObjectMapper();

        String json;
        try {
            json = mapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            Log.error(e);
            return;
        }

        multiChangesHistoryDAO.prcAddCmpHistory(
               userId,
               xIdViewAndIdCmp,
                interpretedState,
                new Date().getTime() * 1000,
               json
        );
    }

    private void helpTransform(MultiChangeHistoryDTO mch, MultiChangeHistoryValues mchv) {

        mch.setId(mchv.getId());
        mch.setUserId(mchv.getUserId());
        mch.setUserName(mchv.getUserName());
        mch.setInterpretedState(mchv.getInterpretedState());
        mch.setUnixTime(mchv.getTs()/1000);

        ValuesMultiChangesHistoryDTO vmch = new ValuesMultiChangesHistoryDTO();
        vmch.setValue(mchv.getValue());
        vmch.setXidPoint(mchv.getXidPoint());

        mch.getValues().add(vmch);
    }

}
