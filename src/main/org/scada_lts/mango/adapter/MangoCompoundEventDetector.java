package org.scada_lts.mango.adapter;

import java.util.List;

import com.serotonin.mango.vo.event.CompoundEventDetectorVO;

public interface MangoCompoundEventDetector {

	String generateUniqueXid();

	boolean isXidUnique(String xid, int excludeId);

	List<CompoundEventDetectorVO> getCompoundEventDetectors();

	CompoundEventDetectorVO getCompoundEventDetector(int id);

	CompoundEventDetectorVO getCompoundEventDetector(String xid);

	void saveCompoundEventDetector(CompoundEventDetectorVO ced);

	void deleteCompoundEventDetector(int compoundEventDetectorId);

}