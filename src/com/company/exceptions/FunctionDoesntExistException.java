package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionDoesntExistException extends Exception{
    public FunctionDoesntExistException(String funName, List<VarType> params) {
        super(String.format("Function %s(%s) doesn't exist",
                funName,
                params.stream().map(VarType::toString).collect(Collectors.joining(", "))));
    }
}
