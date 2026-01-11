package com.company.old.symbol_table.variable_types;

import java.util.Objects;

public class BagType extends VarType {
    public final String name;

    public BagType(String name) {
        super(false);
        this.name = name;
    }

    public BagType(String name, Boolean hasArray) {
        super(hasArray);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + name + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        BagType bagType = (BagType) object;
        if (name.equals("*") || bagType.name.equals("*")) {
            return true;
        }
        return Objects.equals(name, bagType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
