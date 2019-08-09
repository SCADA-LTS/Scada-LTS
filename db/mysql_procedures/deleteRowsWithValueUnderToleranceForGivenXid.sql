--
--    Mango - Open Source M2M - http://mango.serotoninsoftware.com
--    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
--    @author Matthew Lohbihler
--
--    This program is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    This program is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with this program.  If not, see <http://www.gnu.org/licenses/>.


-- please change here database name and uncomment below line to create procedure on the target database
-- use scadalts_prod;

CREATE PROCEDURE `deleteRowsWithValueUnderToleranceForGivenXid`(in givenXid VARCHAR(20),in tolerance double)
BEGIN
declare counterOfAllRows int default 0;
declare actualPointId int;
declare actualcounter int default 0;
declare actual_value double;
declare abs_value double;
declare previous_ double default 0.00;
declare next_ double default 0.00;
declare first_ boolean default 1;
declare idToDelete int;

DECLARE cur CURSOR FOR SELECT pv.id,pv.pointValue  FROM pointValues pv,dataPoints dp where
 dp.id = pv.dataPointId and
 dp.xid=givenXid;

 SELECT count(*) into counterOfAllRows  FROM pointValues pv,dataPoints dp where
 dp.id = pv.dataPointId and
 dp.xid=givenXid;-- 'DP_883360';

 OPEN cur;
	while actualcounter < counterOfAllRows do
	SELECT concat('Row:',actualcounter,' / ',counterOfAllRows) AS ValuesToCompare;
	FETCH cur INTO actualPointId,actual_value;

	if first_ = 1 then
		set first_ = 0 ;
		set previous_ = actual_value;
		set next_ = actual_value;

	else
		set previous_ = next_;
        set next_ = actual_value;
        if idToDelete != 0 then
			delete from pointValues where id = idToDelete;
            SELECT concat('Delete lower value.')AS DeletedRow;
            set idToDelete = 0;
        end if;
        set abs_value = abs((next_ - previous_));
		if abs_value < tolerance then
			SELECT concat('Given tolerance:',tolerance )AS GivenTolerance;
            SELECT concat('Value lower than tolerance:',abs_value) AS ValueLowerThanTolerance;
			set idToDelete = actualPointId;
		end if;
	end if;
    set actualcounter = actualcounter +1;
	end while;
CLOSE cur;
commit;
SELECT concat('Finish') AS ValuesToCompare;

END