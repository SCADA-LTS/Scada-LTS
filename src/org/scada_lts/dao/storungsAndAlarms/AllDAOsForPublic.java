package org.scada_lts.dao.storungsAndAlarms;


import org.scada_lts.dao.DataPointDAO;

public class AllDAOsForPublic {

    public static DataPointDAO getDataPointDAO(){
        return DAOs.getDataPointDAO();
    }
}
