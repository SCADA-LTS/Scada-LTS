package org.scada_lts;

import org.scada_lts.mango.service.CacheStatus;

public class CacheOrDatabase {

    public void insertUpdateRemoveEventDetectors(){

        if ( CacheStatus.isEnable ) {
            //check differences
            //sdo operations in cache
            //do operations in database
        }
        else
        {
            //check differences
            //do operations in database
        }
    }
}
