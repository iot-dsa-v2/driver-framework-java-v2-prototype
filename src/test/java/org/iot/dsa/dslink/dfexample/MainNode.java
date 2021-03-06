package org.iot.dsa.dslink.dfexample;

import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.dslink.dframework.DFUtil;


public class MainNode extends DSMainNode {

    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Connection", DFUtil.getAddAction(TestConnectionNode.class));
    }

}
