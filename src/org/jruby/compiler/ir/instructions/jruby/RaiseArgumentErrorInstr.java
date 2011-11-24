package org.jruby.compiler.ir.instructions.jruby;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.instructions.Instr;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 */
public class RaiseArgumentErrorInstr extends Instr {
    private final int required;
    private final int opt;
    private final int rest;
    private final int numArgs;
    
    public RaiseArgumentErrorInstr(int required, int opt, int rest, int numArgs) {
        super(Operation.RAISE_ARGUMENT_ERROR);
        
        this.required = required;
        this.opt = opt;
        this.rest = rest;
        this.numArgs = numArgs;
    }

    @Override
    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + required + ", " + opt + ", " + rest + ")";
    }

    @Override
    public Instr cloneForInlining(InlinerInfo ii) {
        return new RaiseArgumentErrorInstr(required, opt, rest, numArgs);
    }

    @Override
    public Object interpret(ThreadContext context, DynamicScope currDynScope, IRubyObject self, Object[] temp, Block block) {
        Arity.raiseArgumentError(context.getRuntime(), numArgs, required, required + opt);
        return null;
    }
    
}
