package org.jruby.compiler.ir.instructions;

import java.util.Map;
import org.jruby.compiler.ir.IRExecutionScope;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Label;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.interpreter.InterpreterContext;
import org.jruby.interpreter.IRBreakJump;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

// NOTE: breaks that jump out of while/until loops would have
// been transformed by the IR building into an ordinary jump.
//
// A break instruction is not just any old instruction.
// Like a return instruction, it exits a scope and returns a value
//
// Ex: (1..5).collect { |n| break if n > 3; n } returns nil
//
// All break instructions like returns have an associated return value
// In the absence of an explicit value to return, nil is returned
//
// Ex: (1..5).collect { |n| break "Hurrah" if n > 3; n } returns "Hurrah"
//
// But, whereas a return exits the innermost method it is in,
// a break only exits out of the innermost non-method scope it is in.
// So, an exposed/naked break inside a method throws an exception!
//
// def foo(n); break if n > 5; end; foo(100) will throw an exception
//
public class BreakInstr extends Instr {
    private final IRExecutionScope scopeToReturnTo;
    protected Operand returnValue;

    public BreakInstr(Operand rv, IRExecutionScope s) {
        super(Operation.BREAK);
        this.scopeToReturnTo = s;
        this.returnValue = rv;
    }
    
    public Operand[] getOperands() {
        return new Operand[] { returnValue };
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return new BreakInstr(returnValue.cloneForInlining(ii), scopeToReturnTo);
    }

    @Override
    public Label interpret(InterpreterContext interp, IRExecutionScope scope, ThreadContext context, IRubyObject self, org.jruby.runtime.Block block) {
        throw new IRBreakJump(scopeToReturnTo, returnValue.retrieve(interp, context, self));
    }

    @Override
    public String toString() {
        return getOperation() + "(" + returnValue + (scopeToReturnTo == null ? "" : ", " + scopeToReturnTo) + ")";
    }

    @Override
    public void simplifyOperands(Map<Operand, Operand> valueMap) {
        returnValue = returnValue.getSimplifiedOperand(valueMap);
    }
}
