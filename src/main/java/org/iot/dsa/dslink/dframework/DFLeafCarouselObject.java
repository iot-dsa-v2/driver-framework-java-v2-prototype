package org.iot.dsa.dslink.dframework;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSRootNode;

import java.util.HashSet;
import java.util.Set;

public class DFLeafCarouselObject extends DFCarouselObject {
    private Set<DFPointNode> homeNodes = new HashSet<DFPointNode>();
    private final DFDeviceNode homeDevice;
    private DFLeafDelayCalculator calculator;

    DFLeafCarouselObject(DFPointNode homePoint, DFDeviceNode homeDev) {
        homeDevice = homeDev;
        this.calculator = homeDevice.getPollCalculator(this);
        synchronized (homeDevice) {
            homeNodes.add(homePoint);
        }
        homeDevice.addPollBatch(this, homePoint.getPollRate());
        DSRuntime.run(this);
    }

    void addHomeNode(DFPointNode node) {
        synchronized (homeDevice) {
            homeNodes.add(node);
        }
    }

    DFPointNode getAHomeNode() {
        return homeNodes.iterator().next();
    }

    private boolean iAmAnOrphan() {
        DFPointNode homeNode = getAHomeNode();
        if (homeNode.getParent() instanceof DFDeviceNode) {
            DFDeviceNode par = (DFDeviceNode) homeNode.getParent();
            return !par.isNodeConnected();
        } else if (homeNode.getParent() instanceof DSRootNode) {
            return false;
        } else {
            throw new RuntimeException("Wrong parent class");
        }
    }

    void close(DFPointNode node) {
        long pollRate = node.getPollRate();
        node.onDfStopped();
        if (!homeNodes.remove(node)) {
            System.out.println("Node is missing!");
        }
        if (homeNodes.isEmpty()) {
            running = false;
            homeDevice.removePollBatch(this, pollRate);
        }
    }

    @Override
    public void run() {
        synchronized (homeDevice) {
            if (!running) {
                return;
            }

            if (iAmAnOrphan()) {
                for (DFPointNode n : homeNodes) {
                    n.stopCarObject();
                }
                return;
            }

            //Can add redundant check for isNodeStopped here
            boolean success = homeDevice.batchPoll(homeNodes);

            if (!success) {
                for (DFPointNode n : homeNodes) {
                    n.onFailed();
                }
            } else {
                for (DFPointNode n : homeNodes) {
                    n.onConnected();
                }
            }
            DSRuntime.runDelayed(this, calculator.getDelay());
        }
    }
    
}
