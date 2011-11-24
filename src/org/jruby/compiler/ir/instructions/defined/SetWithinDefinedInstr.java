package org.jruby.compiler.ir.instructions.defined;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.instructions.Instr;
import org.jruby.compiler.ir.operands.BooleanLiteral;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 */
public class SetWithinDefinedInstr extends Instr {
    private final BooleanLiteral define;
    
    public SetWithinDefinedInstr(BooleanLiteral define) {
        super(Operation.SET_WITHIN_DEFINED);
        
        this.define = define;
    }

    @Override
    public Operand[] getOperands() {
        return new Operand[] { define };
    }

    @Override
    public Instr cloneForInlining(InlinerInfo ii) {
        return this;
    }

    @Override
    public Object interpret(ThreadContext context, DynamicScope currDynScope, IRubyObject self, Object[] temp, Block block) {
        context.setWithinDefined(define.isTrue());
        return null;
    }
}
