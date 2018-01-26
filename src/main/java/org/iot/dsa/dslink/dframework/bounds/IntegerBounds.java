package org.iot.dsa.dslink.dframework.bounds;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSLong;

import java.util.Random;

/**
 * @author James (Juris) Puchin
 * Created on 1/24/2018
 */
public class IntegerBounds implements ParameterBounds<Integer> {

    private Integer min;
    private Integer max;

    private final static int RAND_MAX = 100;
    private final static int RAND_MIN = -100;

    public IntegerBounds() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
    }

    /**
     * Creates an Integer bounds object with min and max set.
     * @param min inclusive
     * @param max inclusive
     */
    public IntegerBounds(Integer min, Integer max) {
        if (min >= max) throw new RuntimeException("Min bound has to be less than max.");
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validBounds(DSElement val) {
        Long iVal;
        if ( val.isLong() || val.isInt() )
            iVal = val.toLong();
        else
            return false;

        return iVal <= max && iVal >= min;
    }

    @Override
    public DSElement generateRandom(Random rand) {
        Integer rMax = max > RAND_MAX && min < RAND_MAX ? RAND_MAX : max;
        Integer rMin = min < RAND_MIN && max > RAND_MIN ? RAND_MIN : min;
        Double answer = rand.nextDouble() * (rMax - rMin) + rMin;
        return DSLong.valueOf(answer.intValue());
    }
}
