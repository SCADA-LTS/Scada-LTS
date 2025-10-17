package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;

public class QualifiedNameWrapper extends TypeWrapper<QualifiedName> {

    public QualifiedNameWrapper() {
        super(QualifiedName.class);
    }
}
