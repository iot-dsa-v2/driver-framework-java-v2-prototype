package org.iot.dsa.dslink.example;

import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

/**
 * @author James (Juris) Puchin
 * Created on 10/25/2017
 */
public abstract class DFAbstractNode extends DSNode {

    protected static long REFRESH_DEF;
    protected static DFWHelpers.DFWConnStrat CONN_STRAT_DEF;
    protected static DFWHelpers.DFWRefChangeStrat REFRESH_CHANGE_STRAT_DEF;

    DFCarouselObject carObject;

    private final DSInfo is_stopped = getInfo(DFWHelpers.IS_STOPPED);
    boolean getIsStopped() {
        return is_stopped.getValue().equals(DSBool.TRUE);
    }

    //Carousel Management Methods
    abstract boolean createConnection();
    abstract boolean ping();
    abstract void closeConnection();

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(DFWHelpers.STATUS, DSString.valueOf("unknown")).setReadOnly(true);
        //TODO: add full timestamp reporting
        declareDefault(DFWHelpers.STOP, makeStopAction());
        declareDefault(DFWHelpers.START, makeStartAction());
        declareDefault(DFWHelpers.RESTART, makeRestartAction());

        declareDefault(DFWHelpers.IS_STOPPED, DSBool.FALSE).setReadOnly(true).setHidden(true);
    }

    DSAction makeStopAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFConnectionNode) info.getParent()).stopConnection();
                return null;
            }
        };
        return act;
    }

    void stopConnection() {
        if (carObject != null) {
            carObject.close();
            carObject = null;
        }
    }

    DSAction makeStartAction() {
        return new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFConnectionNode) info.getParent()).startConnection();
                return null;
            }
        };
    }

    void startConnection() {
        if (carObject == null) {
            carObject = new DFCarouselObject(this);
        }
    }

    DSAction makeRestartAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DFConnectionNode) info.getParent()).stopConnection();
                ((DFConnectionNode) info.getParent()).startConnection();
                return null;
            }
        };
        return act;
    }

    public long getRefresh() {
        return REFRESH_DEF;
    }

    public DFWHelpers.DFWConnStrat getConnStrat() {
        return CONN_STRAT_DEF;
    }

    public DFWHelpers.DFWRefChangeStrat getRefreshChangeStrat() {
        return REFRESH_CHANGE_STRAT_DEF;
    }
}
