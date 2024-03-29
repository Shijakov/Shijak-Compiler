package com.company.symbol_table;

import java.util.Objects;

public class VarType {
    public enum PrimitiveType {
        BOOL,
        CHAR,
        INT,
        FLOAT,
        VOID
    }

    public PrimitiveType type;
    public boolean arrExt;
    public String value;    // If it has a constant value like PrimitiveConstant

    public VarType(PrimitiveType type, boolean arrExt) {
        this.type = type;
        this.arrExt = arrExt;
        value = "";
    }

    @Override
    public String toString() {
        return type.name() + (arrExt ? "[]" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarType varType = (VarType) o;
        return arrExt == varType.arrExt && type == varType.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, arrExt);
    }
}
