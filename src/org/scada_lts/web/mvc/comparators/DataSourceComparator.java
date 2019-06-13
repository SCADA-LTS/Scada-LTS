package org.scada_lts.web.mvc.comparators;

import java.util.ResourceBundle;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.ListParent;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.web.comparators.BaseComparator;
import com.serotonin.web.i18n.I18NUtils;

public class DataSourceComparator extends BaseComparator<ListParent<DataSourceVO<?>, DataPointVO>> {
    private static final int SORT_NAME = 1;
    private static final int SORT_TYPE = 2;
    private static final int SORT_CONN = 3;
    private static final int SORT_ENABLED = 4;

    private final ResourceBundle bundle;

    public DataSourceComparator(ResourceBundle bundle, String sortField, boolean descending) {
        this.bundle = bundle;
        if ("name".equals(sortField))
            sortType = SORT_NAME;
        else if ("type".equals(sortField))
            sortType = SORT_TYPE;
        else if ("conn".equals(sortField))
            sortType = SORT_TYPE;
        else if ("enabled".equals(sortField))
            sortType = SORT_ENABLED;
        this.descending = descending;
    }

    public int compare(ListParent<DataSourceVO<?>, DataPointVO> o1, ListParent<DataSourceVO<?>, DataPointVO> o2) {
        DataSourceVO<?> ds1 = o1.getParent();
        DataSourceVO<?> ds2 = o2.getParent();

        int result = 0;
        if (sortType == SORT_NAME)
            result = ds1.getName().compareToIgnoreCase(ds2.getName());
        else if (sortType == SORT_TYPE) {
            String desc1 = I18NUtils.getMessage(bundle, ds1.getType().getKey());
            String desc2 = I18NUtils.getMessage(bundle, ds2.getType().getKey());
            result = desc1.compareToIgnoreCase(desc2);
        }
        else if (sortType == SORT_CONN) {
            String desc1 = ds1.getConnectionDescription().getLocalizedMessage(bundle);
            String desc2 = ds2.getConnectionDescription().getLocalizedMessage(bundle);
            result = desc1.compareToIgnoreCase(desc2);
        }
        else if (sortType == SORT_ENABLED)
            result = new Boolean(ds1.isEnabled()).compareTo(new Boolean(ds2.isEnabled()));

        if (descending)
            return -result;
        return result;
    }
}
