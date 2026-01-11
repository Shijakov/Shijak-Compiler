package com.company.old.symbol_table.variable_types;

public class BoolType extends VarType {
    public BoolType() {
        super(false);
    }

    public BoolType(Boolean hasArrExt) {
        super(hasArrExt);
    }
}
