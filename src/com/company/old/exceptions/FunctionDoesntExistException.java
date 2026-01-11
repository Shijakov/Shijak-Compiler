package com.company.old.exceptions;

import com.company.old.symbol_table.variable_types.VarType;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionDoesntExistException extends LineInfoException {
    public FunctionDoesntExistException(String funName, List<VarType> params, int line) {
        super(String.format(
                "Function %s(%s) doesn't exist",
                funName,
                params.stream().map(VarType::toString).collect(Collectors.joining(", "))
                ), line
        );
    }
}
