package org.jruby.compiler.ir.operands;

import java.util.List;
import java.util.Map;

import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.RubyArray;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

// Represents a svalue node in Ruby code
//
// According to headius, svalue evaluates its value node and returns:
//  * nil if it does not evaluate to an array or if it evaluates to an empty array
//  * the first element if it evaluates to a one-element array
//  * the array if it evaluates to a >1 element array
//
// NOTE: This operand is only used in the initial stages of optimization
// Further down the line, it could get converted to calls
//
public class SValue extends Operand {
    final private Operand array;

    public SValue(Operand array) {
        this.array = array;
    }

    @Override
    public boolean isConstant() {
        return array.isConstant();
    }

    @Override
    public String toString() {
        return "SValue(" + array + ")";
    }

    @Override
    public Operand getSimplifiedOperand(Map<Operand, Operand> valueMap, boolean force) {
        Operand newArray = array.getSimplifiedOperand(valueMap, force);
        if (newArray instanceof Array) {
            Array a = (Array) newArray;
            return (a.elts.length == 1) ? a.elts[0] : a;
        } else {
            return (newArray == array) ? this : new SValue(newArray);
        }
    }

    @Override
    public boolean isNonAtomicValue() {
        return true;
    }

    /** Append the list of variables used in this operand to the input list */
    @Override
    public void addUsedVariables(List<Variable> l) {
        array.addUsedVariables(l);
    }
 
    @Override
    public Operand cloneForInlining(InlinerInfo ii) { 
        return isConstant() ? this : new SValue(array.cloneForInlining(ii));
    }

    @Override
    public Object retrieve(ThreadContext context, IRubyObject self, DynamicScope currDynScope, Object[] temp) {
        Object val = array.retrieve(context, self, currDynScope, temp);
        
        if (val instanceof RubyArray) {
            int n = ((RubyArray) val).getLength();
            
            if (n == 0) return context.nil;
            if (n == 1) return ((RubyArray) val).entry(0);
            
            return val;
        }

        return context.nil;
    }
}
