package com.serotonin.mango.rt.dataSource.snmp;

public enum TIME_JUNIT {
        TIME_EXISTS_ONLY_DURING_JUNIT(-1);
        private int time;
        TIME_JUNIT(int time){
            this.time = time;
        }
        public int getTime(){
            return time;
        }
}
