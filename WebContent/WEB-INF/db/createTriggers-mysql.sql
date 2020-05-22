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
--
--

-- Make sure that everything get created with utf8 as the charset.


DROP TRIGGER if exists filterStorungsAndAlarms;
DELIMITER $$

CREATE
    TRIGGER filterStorungsAndAlarms AFTER INSERT
    ON pointValues
    FOR EACH ROW BEGIN
		DECLARE VARIABLE1 VARCHAR(100);
        DECLARE VARIABLE2 VARCHAR(100);
        SET VARIABLE1 = (SELECT UNIX_TIMESTAMP(NOW()));
        SET VARIABLE2 = (select xid from dataPoints where id=new.dataPointId);
		SET VARIABLE2 = (select if(LOCATE('AL',VARIABLE2) <> 0 ,'AL', if(LOCATE('ST',VARIABLE2) <> 0 ,'ST','undefined')));
		insert into pointValuesValue (test,old_ts,new_ts,dataPointId,xid) values (
        new.id,
        (select max(ts) from pointValues where dataPointId=new.dataPointId),
        new.ts,
        new.dataPointId,
        VARIABLE2
        );
        SET VARIABLE2 = (SELECT UNIX_TIMESTAMP(NOW()));
        
    END$$
    
DELIMITER ;