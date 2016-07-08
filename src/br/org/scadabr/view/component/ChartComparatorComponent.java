package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class ChartComparatorComponent extends HtmlComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition(
			"chartComparator", "CHART_COMPARATOR", "graphic.chartComparator",
			null);

	@JsonRemoteProperty
	private int width = 480;

	@JsonRemoteProperty
	private int height = 320;

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	private void createContent() {
		String idPrefix = "chartComparator" + getId();

		StringBuilder sb = new StringBuilder();
		// sb.append("<div style='width:" + width + "px; height:" + height
		// + "px; border: 1px solid black;'>");
		sb.append("<div>");

		sb.append(createDataPointsSelectComponent(idPrefix + "_dp1"));
		sb.append(createDataPointsSelectComponent(idPrefix + "_dp2"));
		sb.append(createDataPointsSelectComponent(idPrefix + "_dp3"));
		sb.append(createDataPointsSelectComponent(idPrefix + "_dp4"));
		sb.append("<div style='float:right;'><input type='button' style='width: 100%;' value='Atualizar' onclick=\"updateChartComparatorComponent('"
				+ idPrefix + "'," + width + "," + height + ");\" /> </div>");
		sb.append("<div style='clear:both;'> </div>");
		Calendar defaultFromDate1 = Calendar.getInstance();
		defaultFromDate1.add(Calendar.MINUTE, -2);
		Calendar defaultToDate1 = Calendar.getInstance();
		defaultToDate1.add(Calendar.MINUTE, -1);

		sb.append("<div style='width: 50%; float:left;'> ");
		sb.append(createDateRangeComponent(idPrefix, idPrefix + "_fromDate1",
				idPrefix + "_toDate1", defaultFromDate1, defaultToDate1));
		sb.append("<img id='"
				+ idPrefix
				+ "_chart1' style='float:left; margin: 5px 5px 5px 5px;' > </img>");
		sb.append("</div>");

		Calendar defaultFromDate2 = Calendar.getInstance();
		defaultFromDate2.add(Calendar.MINUTE, -1);
		Calendar defaultToDate2 = Calendar.getInstance();
		sb.append("<div style='width: 50%; float:left;'> ");
		sb.append(createDateRangeComponent(idPrefix, idPrefix + "_fromDate2",
				idPrefix + "_toDate2", defaultFromDate2, defaultToDate2));
		sb.append("<img id='"
				+ idPrefix
				+ "_chart2' style='float:left; margin: 3px 3px 3px 3px;' > </img>");
		sb.append("</div>");

		sb.append("<div style='clear:both;'> </div>");

		sb.append("</div>");

		setContent(sb.toString());
	}

	private String createDataPointsSelectComponent(String idPrefix) {
		List<DataPointVO> dataPoints = new DataPointDao().getDataPoints(null,
				false);

		StringBuilder sb = new StringBuilder();
		sb.append("<select style='float:left;'  id='" + idPrefix + "'>");

		sb.append("<option value='0'> &nbsp; </option>");

		for (DataPointVO dp : dataPoints) {
			sb.append("<option value='" + dp.getId() + "'> " + dp.getName()
					+ "</option>");
		}
		sb.append("</select>");

		return sb.toString();
	}

	private String createDateRangeComponent(String idPrefix, String fromDateId,
			String toDateId, Calendar defaultFromDate, Calendar defaultToDate) {
		StringBuilder sb = new StringBuilder();

		Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		String defaultFromDateString = (formatter.format(new Date(
				defaultFromDate.getTimeInMillis())));
		String defaultToDateString = (formatter.format(new Date(defaultToDate
				.getTimeInMillis())));

		sb.append("<table>");
		sb.append("<tr> <td> De </td> <td> A </td> </tr>");
		sb.append("<tr> <td><input type='text' class='formField' id='"
				+ fromDateId + "' value='" + defaultFromDateString
				+ "'/> </td> "
				+ "<td> <input type='text' class='formField' id='" + toDateId
				+ "' value='" + defaultToDateString + "'/> </td> </tr>");
		sb.append("</table>");
		return sb.toString();
	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);

		createContent();

		SerializationHelper.writeSafeUTF(out, getContent());
		out.writeInt(width);
		out.writeInt(height);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			setContent(SerializationHelper.readSafeUTF(in));
			width = in.readInt();
			height = in.readInt();
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
