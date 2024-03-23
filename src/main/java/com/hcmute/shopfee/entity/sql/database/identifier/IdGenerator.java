package com.hcmute.shopfee.entity.sql.database.identifier;

import com.hcmute.shopfee.utils.RandomUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class IdGenerator implements IdentifierGenerator {


    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return RandomUtils.randomTimeId();
    }
}