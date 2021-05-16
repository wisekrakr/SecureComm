package com.wisekrakr.wisesecurecomm.communication.user.id;

import com.google.common.primitives.Ints;

/**
 * Unique int ID generator for distributed environments.
 */
public class IntIDGenerator extends IDGenerator {
    public IntIDGenerator(long epochStartTime, long uniqueID, int timeBitsCount, int uniqueIdBitsCount, int sequenceBitsCount, IDMode idMode)  {
        super(epochStartTime, uniqueID, timeBitsCount, uniqueIdBitsCount, sequenceBitsCount, idMode);
        if(getTotalBitsCount()!=32){
            throw new IllegalArgumentException("Bits count isn't correct. Integer has to have exactly 32 bits");
        }
    }

    public int generateIntId()  {
        return Ints.fromByteArray(generateId());
    }
}