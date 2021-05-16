package com.wisekrakr.wisesecurecomm.communication.user.id;

import com.google.common.primitives.Longs;

/**
 * Unique long ID generator for distributed environments.
 */
public class LongIDGenerator extends IDGenerator{
    public LongIDGenerator(long epochStartTime, long uniqueID, int timeBitsCount, int uniqueIdBitsCount, int sequenceBitsCount, IDMode idMode){
        super(epochStartTime, uniqueID, timeBitsCount, uniqueIdBitsCount, sequenceBitsCount, idMode);
        if(getTotalBitsCount()!=64){
            throw new IllegalArgumentException("Bits count isn't correct. Long has to have exactly 64 bits");
        }
    }

    public long generateLongId()  {
        return Longs.fromByteArray(generateId());
    }
}
