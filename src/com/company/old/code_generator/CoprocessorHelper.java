package com.company.old.code_generator;

import com.company.old.symbol_table.variable_types.FloatType;
import com.company.old.symbol_table.variable_types.VarType;

public class CoprocessorHelper {

    public static Coprocessor convertVarType(VarType type) {
        if (type.equals(new FloatType())) {
            return Coprocessor.FLOAT;
        }
        return Coprocessor.WORD;
    }
}
