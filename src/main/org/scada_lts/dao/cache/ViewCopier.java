package org.scada_lts.dao.cache;

import com.serotonin.mango.view.View;
import org.ehcache.spi.copy.Copier;

public class ViewCopier implements Copier<View> {

    @Override
    public View copyForRead(View view) {
        return view.copy();
    }

    @Override
    public View copyForWrite(View view) {
        return view;
    }
}
