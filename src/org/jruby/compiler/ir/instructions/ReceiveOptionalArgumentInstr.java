package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.UndefinedValue;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;

// Assign the 'index' argument to 'dest'.
public class ReceiveOptionalArgumentInstr extends ReceiveArgBase {
    public ReceiveOptionalArgumentInstr(Variable result, int index) {
        super(Operation.RECV_OPT_ARG, result, index);
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        throw new RuntimeException("Not implemented yet!");
	 }
}
