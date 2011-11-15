package org.jruby.compiler.ir.operands;

import java.util.List;

// Represents a $1 .. $9 node in Ruby code

import org.jruby.RubyRegexp;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

//
// NOTE: This operand is only used in the initial stages of optimization
// Further down the line, it could get converted to calls
//
public class NthRef extends Operand {
    final public int matchNumber;

    public NthRef(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    @Override
    public void addUsedVariables(List<Variable> l) { 
        /* Nothing to do */
    }

    @Override
    public String toString() {
        return "$" + matchNumber;
    }

    @Override
    public Object retrieve(ThreadContext context, IRubyObject self, Object[] temp) {
        return RubyRegexp.nth_match(matchNumber,
                context.getCurrentScope().getBackRef(context.getRuntime()));
    }
}
