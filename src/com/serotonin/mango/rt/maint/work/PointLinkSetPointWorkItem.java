package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.rt.dataImage.PointLinkSetPointSource;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;

public class PointLinkSetPointWorkItem extends SetPointWorkItem {

	private PointLinkSetPointSource plSource;

	/**
	 * @param targetPointId
	 * @param pvt
	 * @param source
	 */
	public PointLinkSetPointWorkItem(int targetPointId, PointValueTime pvt,
			PointLinkSetPointSource source) {
		super(targetPointId, pvt, (SetPointSource) source);
		this.plSource = source;
	}

	@Override
	public void work() {
		super.work();
		plSource.pointSetComplete();
	}

	@Override
	public String toString() {
		return "PointLinkSetPointWorkItem{" +
				"plSource=" + plSource +
				super.toString() +
				'}' ;
	}

	@Override
	public String getDetails() {
		return this.toString();
	}
}
