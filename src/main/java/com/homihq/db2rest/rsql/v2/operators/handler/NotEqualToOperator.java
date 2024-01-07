package com.homihq.db2rest.rsql.v2.operators.handler;

import com.homihq.db2rest.rest.read.model.RCondition;
import com.homihq.db2rest.rsql.v2.operators.Operator;

public class NotEqualToOperator implements Operator {

    private static final String OPERATOR = " != ";
    @Override
    public RCondition handle(String columnName, String value, Class type) {

        return RCondition.builder()
                .columnName(columnName)
                .operator(OPERATOR)
                .values(new Object[]{parseValue(value,type)})
                .build();
    }

}