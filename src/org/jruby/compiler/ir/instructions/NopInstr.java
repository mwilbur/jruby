package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class NopInstr extends Instr {
    public static NopInstr NOP = new NopInstr();

    private NopInstr() {
        super(Operation.NOP);
        this.markDead();
    }

    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }

    @Override
    public String toString() {
        return "NOP";
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return this;
    }

    @Override
    public Object interpret(ThreadContext context, DynamicScope currDynScope, IRubyObject self, Object[] temp, Block block) {
        return null;
    }
}
