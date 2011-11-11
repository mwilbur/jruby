package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.IRExecutionScope;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Label;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.interpreter.InterpreterContext;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class ThreadPollInstr extends Instr {
    public ThreadPollInstr() {
        super(Operation.THREAD_POLL);
    }

    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return this;
    }

    // Can this instruction raise exceptions?
    @Override
    public boolean canRaiseException() {
        return false;
    }
    
    @Override
    public Label interpret(InterpreterContext interp, IRExecutionScope scope, ThreadContext context, IRubyObject self, org.jruby.runtime.Block block) {
        context.callThreadPoll();
        return null;
    }
}
