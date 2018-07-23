/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.view.stats;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;

/**
 * @author Matthew Lohbihler
 */
public class NumericDataQuantizer extends AbstractDataQuantizer {
    private double valueSum;

    public NumericDataQuantizer(long start, long end, int buckets, DataQuantizerCallback callback) {
        super(start, end, buckets, callback);
    }

    @Override
    protected void periodData(MangoValue value) {
        valueSum += value.getDoubleValue();
    }

    @Override
    protected MangoValue donePeriod(int valueCounter) {
        double result = valueSum / valueCounter;
        valueSum = 0;
        return new NumericValue(result);
    }
    //    
    // private static List<ReportDataValue> quantize(List<ReportDataValue> data, long start, long end, int buckets) {
    // final List<ReportDataValue> result = new ArrayList<ReportDataValue>();
    //        
    // NumericDataQuantizer dq = new NumericDataQuantizer(start, end, buckets, new DataQuantizerCallback() {
    // public void quantizedData(Number value, long time) {
    // result.add(new ReportDataValue(value, time));
    // }
    // });
    // for (ReportDataValue value : data)
    // dq.data((Double)value.getValue(), value.getTime());
    // dq.done();
    //        
    // return result;
    // }
    //    
    // public static void main(String[] args) {
    // List<ReportDataValue> data = new ArrayList<ReportDataValue>();
    // data.add(new ReportDataValue(14d, 1));
    // data.add(new ReportDataValue(13d, 2));
    // data.add(new ReportDataValue(10d, 3));
    // data.add(new ReportDataValue(17d, 4));
    // data.add(new ReportDataValue(18d, 10));
    // data.add(new ReportDataValue(11d, 11));
    // data.add(new ReportDataValue(12d, 15));
    // data.add(new ReportDataValue(13d, 16));
    // data.add(new ReportDataValue(14d, 17));
    // data.add(new ReportDataValue(20d, 20));
    // data.add(new ReportDataValue(25d, 21));
    // data.add(new ReportDataValue(27d, 30));
    // data.add(new ReportDataValue(29d, 50));
    // data.add(new ReportDataValue(30d, 51));
    // data.add(new ReportDataValue(28d, 52));
    // data.add(new ReportDataValue(27d, 53));
    // data.add(new ReportDataValue(26d, 54));
    // data.add(new ReportDataValue(25d, 55));
    // data.add(new ReportDataValue(24d, 56));
    // data.add(new ReportDataValue(23d, 57));
    // data.add(new ReportDataValue(22d, 58));
    // data.add(new ReportDataValue(20d, 90));
    //        
    // System.out.println(quantize(data, 0, 100, 800));
    // System.out.println(quantize(data, 0, 100, 100));
    // System.out.println(quantize(data, 0, 100, 90));
    // System.out.println(quantize(data, 0, 100, 50));
    // System.out.println(quantize(data, 0, 100, 7));
    // System.out.println(quantize(data, 0, 100, 6));
    // System.out.println(quantize(data, 0, 100, 5));
    // System.out.println(quantize(data, 0, 100, 4));
    // System.out.println(quantize(data, 0, 100, 1));
    // System.out.println(quantize(data, 1, 90, 13));
    // }
}
