package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.IRScope;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class LineNumberInstr extends Instr {
    public final int lineNumber;
    public final IRScope scope; // We need to keep scope info here so that line number is meaningful across inlinings.

    public LineNumberInstr(IRScope scope, int lineNumber) {
        super(Operation.LINE_NUM);
        this.scope = scope;
        this.lineNumber = lineNumber;
    }

    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + lineNumber + ")";
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return this;
    }

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        context.setLine(lineNumber);
        return null;
    }
}
