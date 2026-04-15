package org.scada_lts.dao.report;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportPointInfo;
import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IReportInstancePointDAO {
    List<ReportPointInfo> getPointInfos(int instanceId);

    int insert(ReportInstance reportInstance, int dataType, MangoValue startValue, PointInfo pointInfo);

    /*
PointInfo class
*/
    public static class PointInfo {
        private final DataPointVO point;
        private final String colour;
        private final boolean consolidatedChart;

        public PointInfo(DataPointVO point, String colour, boolean consolidatedChart) {
            this.point = point;
            this.colour = colour;
            this.consolidatedChart = consolidatedChart;
        }

        public int getId() {
            return point.getId();
        }

        public DataPointVO getPoint() {
            return point;
        }

        public String getColour() {
            return colour;
        }

        public String getName() {
            return StringUtils.truncate(point.getName(), "", 100);
        }

        public String getDeviceName() {
            return StringUtils.truncate(point.getDeviceName(), "", 40);
        }

        public TextRenderer getTextRenderer() {
            return point.getTextRenderer();
        }


        public boolean isConsolidatedChart() {
            return consolidatedChart;
        }
    }
}
