package org.scada_lts.web.mvc.api.datasources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VirtualDataSourceJson.class, name = "1"),
        @JsonSubTypes.Type(value = SnmpDataSourceJson.class, name = "5")
})
public interface DataSourceJson<T> {

    public T convertDataSourceToVO();

    public int getType();

}
