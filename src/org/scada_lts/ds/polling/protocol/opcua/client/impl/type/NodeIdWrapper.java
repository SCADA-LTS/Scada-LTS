package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class NodeIdWrapper extends TypeWrapper<NodeId> {

    public NodeIdWrapper() {
        super(NodeId.class);
    }
}
