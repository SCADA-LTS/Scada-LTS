package com.serotonin.mango.view;

import com.serotonin.mango.util.ExportCodes;

public class ResolutionView {
	
	public static final int R640x480 = 0;         // width 640, height 480
	public static final int R800x600 = 1;         // width 800, height 600
	public static final int	R1024x768 = 2;        // width 1024, height 768
	public static final int R1600x1200 = 3;       // width 1600, height 1200
	public static final int R1920x1080 = 4;       // width 1920, height 1080

	// New ViewResolution based on the Most Common Screen Resolutions in 2020.
	public static final int R1280x720 = 5;
	public static final int R1280x800 = 6;
	public static final int R1300x700 = 7;
	public static final int R1366x768 = 8;
	public static final int R1440x900 = 9;
	public static final int R1680x1050 = 10;
	public static final int R2560x1440 = 11;

	public static ExportCodes RESOLUTION_VIEW_CODES = new ExportCodes();
	static {
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R640x480, "R640x480");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R800x600, "R800x600");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1024x768, "R1024x768");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1600x1200, "R1600x1200");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1920x1080, "R1920x1080");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1280x720, "R1280x720");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1280x800, "R1280x800");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1300x700, "R1300x700");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1366x768, "R1366x768");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1440x900, "R1440x900");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R1680x1050, "R1680x1050");
		RESOLUTION_VIEW_CODES.addElement(ResolutionView.R2560x1440, "R2560x1440");
	}
	
}
