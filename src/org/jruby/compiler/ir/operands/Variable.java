package org.jruby.compiler.ir.operands;

import org.jruby.compiler.ir.representations.InlinerInfo;

import java.util.List;
import java.util.Map;

public abstract class Variable extends Operand implements Comparable {
    public final static String BLOCK = "%block";

    public abstract String getName();

    @Override
    public Operand getSimplifiedOperand(Map<Operand, Operand> valueMap, boolean force) {
        Operand v = valueMap.get(this);
        // You can only value-replace atomic values
        return (v != null) && (force || !v.isNonAtomicValue()) ? v : this;
    }

    public boolean isImplicitBlockArg() {
        return getName().equals(BLOCK);
    }

    @Override
    public Operand getValue(Map<Operand, Operand> valueMap) {
        Operand v = valueMap.get(this);

        return (v == null) ? this : v;
    }

    /** Append the list of variables used in this operand to the input list */
    @Override
    public void addUsedVariables(List<Variable> l) {
        l.add(this);
    }

    @Override
    public Operand cloneForInlining(InlinerInfo ii) { 
        return ii.getRenamedVariable(this);
    }
}
