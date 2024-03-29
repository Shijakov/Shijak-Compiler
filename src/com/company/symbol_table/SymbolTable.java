package com.company.symbol_table;

import com.company.dev_exceptions.ScopeNotFoundException;
import com.company.exceptions.*;

import java.util.HashMap;
import java.util.List;

public class SymbolTable {
    public static abstract class GeneralTable {
        int id;
        public GeneralTable firstChild;
        public GeneralTable parent;
        public GeneralTable neighbor;

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
        public HashMap<String, ConstInfo> constRow;

        public GlobalTable() {
            funRow = new HashMap<>();
            constRow = new HashMap<>();
        }
    }

    public static class Table extends GeneralTable {
        public HashMap<String, VarInfo> row;

        public Table() {
            row = new HashMap<>();
        }
    }

    public GlobalTable root;
    private int nextAvailableScopeId;

    public SymbolTable() {
        root = new GlobalTable();
        root.id = 0;
        nextAvailableScopeId = 1;
    }

    public int addScope(int scopeId) throws ScopeNotFoundException {
        GeneralTable scope = findScope(scopeId);
        Table table = new Table();
        table.id = nextAvailableScopeId;
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
        root.funRow.put(funName, funInfo);
    }

    public void defineConstant(String constName, VarType type, String value)
            throws ConstantWithSameNameExistsException {
        if (root.constRow.containsKey(constName)) {
            throw new ConstantWithSameNameExistsException(constName);
        }
        ConstInfo constInfo = new ConstInfo(constName, type, value);
        root.constRow.put(constName, constInfo);
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
        table.row.put(varName, varInfo);
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

    public ConstInfo constLookup(String constName) {
        return root.constRow.get(constName);
    }

    public FunInfo funLookup(String funName, List<VarType> parameterTypes) throws FunctionDoesntExistException {
        FunInfo fun = root.funRow.get(funName);
        if (fun == null || fun.params.size() != parameterTypes.size()) {
            throw new FunctionDoesntExistException(funName, parameterTypes);
        }
        for (int i = 0 ; i < fun.params.size() ; i++) {
            if (!parameterTypes.get(i).equals(fun.params.get(i).type)) {
                throw new FunctionDoesntExistException(funName, parameterTypes);
            }
        }
        return fun;
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
