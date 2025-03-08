package com.company.symbol_table;

import com.company.dev_exceptions.GeneralDevException;
import com.company.dev_exceptions.ScopeNotFoundException;
import com.company.exceptions.*;
import com.company.model.Pair;
import com.company.symbol_table.variable_types.VarType;

import java.util.HashMap;
import java.util.List;

public class SymbolTable {
    public static abstract class GeneralTable {
        public final int id;

        public GeneralTable firstChild;
        public GeneralTable parent;
        public GeneralTable neighbor;

        public GeneralTable(int id) {
            this.id = id;
        }

        public void addChildTable(Table table) {
            table.parent = this;
            if (this.firstChild == null) {
                this.firstChild = table;
                return;
            }
            GeneralTable curr = this.firstChild;
            GeneralTable next = curr.neighbor;
            while (next != null) {
                curr = next;
                next = curr.neighbor;
            }
            curr.neighbor = table;
        }
    }

    public static abstract class VarOrConstInfo {
        public String name;
        public VarType type;
    }

    public static class VarInfo extends VarOrConstInfo {
        public boolean declared;
        public int timeDeclared;
        public boolean isArgument;

        public VarInfo(String varName, VarType type, boolean declared, int timeDeclared, boolean isArgument) {
            this.name = varName;
            this.type = type;
            this.declared = declared;
            this.timeDeclared = timeDeclared;
            this.isArgument = isArgument;
        }
    }

    public static class FunInfo {
        public String funName;
        public List<FunParam> params;
        public VarType returnType;
        public int scopeId;

        public FunInfo(String funName, List<FunParam> params, VarType returnType, int scopeId) {
            this.funName = funName;
            this.params = params;
            this.returnType = returnType;
            this.scopeId = scopeId;
        }
    }

    public static class FunParam {
        public String name;
        public VarType type;

        public FunParam(String name, VarType type) {
            this.name = name;
            this.type = type;
        }
    }

    public static class BagInfo {
        public String bagName;
        public List<BagParam> params;
        public HashMap<String, Pair<Integer, VarType>> fieldToIndexMap;

        public BagInfo(String bagName, List<BagParam> params) {
            this.bagName = bagName;
            this.params = params;

            fieldToIndexMap = new HashMap<>();
            int nextIdx = 0;
            for (BagParam param : params) {
                fieldToIndexMap.put(param.name, new Pair<>(nextIdx, param.type));
                nextIdx++;
            }
        }

        public Pair<Integer, VarType> getField(String fieldName) throws GeneralDevException {
            var rez = fieldToIndexMap.get(fieldName);
            if (rez == null) {
                throw new GeneralDevException(String.format("Field %s doesnt exist on bag %s", fieldName, bagName));
            }
            return rez;
        }

        public Integer getNumFields() {
            return params.size();
        }
    }

    public static class BagParam {
        public String name;
        public VarType type;

        public BagParam(String name, VarType type) {
            this.name = name;
            this.type = type;
        }
    }

    public static class ConstInfo extends VarOrConstInfo {
        public String value;

        public ConstInfo(String constName, VarType constType, String value) {
            this.name = constName;
            this.type = constType;
            this.value = value;
        }
    }

    public static class GlobalTable extends GeneralTable {
        public HashMap<String, FunInfo> funRow;
        public HashMap<String, BagInfo> bagRow;
        public HashMap<String, ConstInfo> constRow;

        public GlobalTable(int id) {
            super(id);
            funRow = new HashMap<>();
            bagRow = new HashMap<>();
            constRow = new HashMap<>();
        }

        public void defineFunction(String funName, FunInfo info) {
            funRow.put(funName, info);
        }

        public void defineBag(String bagName, BagInfo info) {
            bagRow.put(bagName, info);
        }

        public void defineConstant(String constName, ConstInfo info) {
            constRow.put(constName, info);
        }
    }

    public static class Table extends GeneralTable {
        public HashMap<String, VarInfo> row;

        public Table(int id) {
            super(id);
            row = new HashMap<>();
        }

        public void declareVar(String varName, VarInfo varInfo) {
            row.put(varName, varInfo);
        }
    }

    public GlobalTable root;
    private int nextAvailableScopeId;

    public SymbolTable() {
        root = new GlobalTable(0);
        nextAvailableScopeId = 1;
    }

    public int addScope(int scopeId) throws ScopeNotFoundException {
        GeneralTable scope = findScope(scopeId);
        Table table = new Table(nextAvailableScopeId);
        nextAvailableScopeId += 1;
        scope.addChildTable(table);
        return nextAvailableScopeId - 1;
    }

    public void defineFunction(String funName, List<FunParam> params, VarType returnType, int scopeId)
            throws FunctionDefinedMultipleTimesException {
        if (root.funRow.containsKey(funName)) {
            throw new FunctionDefinedMultipleTimesException(funName);
        }
        FunInfo funInfo = new FunInfo(funName, params, returnType, scopeId);
        root.defineFunction(funName, funInfo);
    }

    public void defineBag(String bagName, List<BagParam> params) throws BagDefinedMultipleTimesException {
        if (root.bagRow.containsKey(bagName)) {
            throw new BagDefinedMultipleTimesException(bagName);
        }
        BagInfo bagInfo = new BagInfo(bagName, params);
        root.defineBag(bagName, bagInfo);
    }

    public void defineConstant(String constName, VarType type, String value)
            throws ConstantWithSameNameExistsException {
        if (root.constRow.containsKey(constName)) {
            throw new ConstantWithSameNameExistsException(constName);
        }
        ConstInfo constInfo = new ConstInfo(constName, type, value);
        root.defineConstant(constName, constInfo);
    }

    public void declareVar(int scopeId, String varName, VarType type, int timeDeclared, boolean isArgument)
            throws VariableAlreadyDeclaredException, ScopeNotFoundException, ConstantWithSameNameExistsException {
        Table table = (Table) findScope(scopeId);
        if (table.row.containsKey(varName)) {
            throw new VariableAlreadyDeclaredException(varName);
        }
        if (root.constRow.containsKey(varName)) {
            throw new ConstantWithSameNameExistsException(varName);
        }
        VarInfo varInfo = new VarInfo(varName, type, true, timeDeclared, isArgument);
        table.declareVar(varName, varInfo);
    }

    public VarOrConstInfo varLookup(int scopeId, String varName, int time) throws ScopeNotFoundException, VariableNotDeclaredException {
        GeneralTable generalTable = findScope(scopeId);
        while (!(generalTable instanceof GlobalTable)) {
            VarInfo varInfo = ((Table) generalTable).row.get(varName);
            if (varInfo != null && varInfo.timeDeclared < time) {
                return varInfo;
            }
            generalTable = generalTable.parent;
        }
        ConstInfo result = constLookup(varName);
        if (result == null) {
            throw new VariableNotDeclaredException(varName);
        }
        return result;
    }

    public ConstInfo constLookup(String constName) throws VariableNotDeclaredException {
        var constant = root.constRow.get(constName);
        if (constant == null) {
            throw new VariableNotDeclaredException(constName);
        }
        return constant;
    }

    public FunInfo funLookup(String funName) throws FunctionDoesntExistException {
        FunInfo fun = root.funRow.get(funName);
        if (fun == null) {
            throw new FunctionDoesntExistException(funName, List.of());
        }
        return fun;
    }

    public BagInfo bagLookup(String bagName) throws BagDoesntExistException {
        BagInfo bag = root.bagRow.get(bagName);
        if (bag == null) {
            throw new BagDoesntExistException(bagName);
        }

        return bag;
    }

    public VarType getFunctionReturnType(String funName) {
        FunInfo fun = root.funRow.get(funName);
        return fun.returnType;
    }

    private GeneralTable findScopeRecursive(GeneralTable curr, int scopeId) {
        if (curr.id == scopeId) {
            return curr;
        }
        GeneralTable currTable = curr.firstChild;
        while (currTable != null) {
            GeneralTable result = findScopeRecursive(currTable, scopeId);
            if (result != null) {
                return result;
            }
            currTable = currTable.neighbor;
        }
        return null;
    }

    public GeneralTable findScope(int scopeId) throws ScopeNotFoundException {
        GeneralTable table = findScopeRecursive(root, scopeId);
        if (table == null) {
            throw new ScopeNotFoundException(scopeId);
        }
        return table;
    }
}
