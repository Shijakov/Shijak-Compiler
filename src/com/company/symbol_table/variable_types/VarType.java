package com.company.symbol_table.variable_types;

import com.company.exceptions.dev_exceptions.GeneralDevException;

import java.util.Objects;

public abstract class VarType {
    public final boolean hasArrExt;

    protected VarType(Boolean hasArrExt) {
        this.hasArrExt = hasArrExt;
    }

    public static VarType arrayDrilled(VarType other) throws GeneralDevException {
        if (other instanceof BagType) {
            return new BagType(((BagType) other).name);
        } else if (other instanceof VoidType) {
            throw new GeneralDevException("Attempted to array drill void type");
        } else {
            try {
                return other.getClass().getConstructor().newInstance();
            } catch (Exception e) {
                throw new GeneralDevException(e.getMessage());
            }
        }
    }

    public static VarType withArrayExt(VarType other) throws GeneralDevException {
        if (other instanceof BagType) {
            return new BagType(((BagType) other).name, true);
        } else if (other instanceof VoidType) {
            throw new GeneralDevException("Attempted to withArray void type");
        } else {
            try {
                return other.getClass().getDeclaredConstructor(Boolean.class).newInstance(true);
            } catch (Exception e) {
                throw new GeneralDevException(e.getMessage());
            }
        }
    }

    public static boolean isPrimitive(VarType type) {
        return !type.hasArrExt && !(type instanceof BagType);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + (this.hasArrExt ? "[]" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarType varType = (VarType) o;
        return hasArrExt == varType.hasArrExt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasArrExt);
    }
}
