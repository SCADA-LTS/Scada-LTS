package com.serotonin.mango.vo;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.vo.dataSource.virtual.ChangeTypeVO;

import java.util.HashMap;
import java.util.Map;

public class StringToIntConverter extends StdConverter<String, Integer> {

    private Map<String, Integer> types;

    public StringToIntConverter(Map<String, Integer> types) {
        this.types = new HashMap<>(types);
    }

    public StringToIntConverter() {
        this.types = new HashMap<>();
        types.put("INTERVAL", DataPointVO.LoggingTypes.INTERVAL);
        types.put("MAXIMUM", DataPointVO.IntervalLoggingTypes.MAXIMUM);
        types.put("YEARS", Common.TimePeriods.YEARS);
        types.put("WEEKS", DataPointVO.PurgeTypes.WEEKS);
        types.put("MULTISTATE", DataTypes.MULTISTATE);
        types.put("ALTERNATE_BOOLEAN", ChangeTypeVO.Types.ALTERNATE_BOOLEAN);
        types.put("BARS", EngineeringUnits.bars.intValue());
    }

    public void registerType(String code, int number) {
        types.put(code, number);
    }

    @Override
    public Integer convert(String s) {
        if(types.containsKey(s))
            return types.get(s);
        return Integer.parseInt(s);
    }
}
