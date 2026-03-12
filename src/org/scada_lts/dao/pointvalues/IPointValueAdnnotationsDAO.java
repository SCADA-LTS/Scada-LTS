package org.scada_lts.dao.pointvalues;

public interface IPointValueAdnnotationsDAO extends org.scada_lts.dao.GenericDaoCR<org.scada_lts.dao.model.point.PointValueAdnnotation> {
    public abstract java.util.List<org.scada_lts.dao.model.point.PointValueAdnnotation> findAll();
    public abstract org.scada_lts.dao.model.point.PointValueAdnnotation findById(java.lang.Object[] arg0);
    public abstract java.util.List<org.scada_lts.dao.model.point.PointValueAdnnotation> filtered(java.lang.String arg0, java.lang.Object[] arg1, long arg2);
    public abstract java.lang.Object[] create(org.scada_lts.dao.model.point.PointValueAdnnotation arg0);
    public abstract void update(int arg0);
    public abstract void updateAnnotations(java.util.List<org.scada_lts.dao.model.point.PointValue> arg0);
}

