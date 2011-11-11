package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Interp;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Label;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.interpreter.InterpreterContext;
import org.jruby.RubyArray;
import org.jruby.compiler.ir.IRExecutionScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

// This instruction encodes the receive of an argument into a closure
//   Ex:  .. { |a| .. }
// The closure receives 'a' via this instruction
public class ReceiveClosureArgInstr extends Instr implements ResultInstr {
    private final int argIndex;
    boolean restOfArgArray;
    private Variable result;

    public ReceiveClosureArgInstr(Variable result, int argIndex, boolean restOfArgArray) {
        super(Operation.RECV_CLOSURE_ARG);
        
        assert result != null: "ReceiveClosureArgInstr result is null";
        
        this.argIndex = argIndex;
        this.restOfArgArray = restOfArgArray;
        this.result = result;
    }
    
    public boolean isRestOfArgArray() {
        return restOfArgArray;
    }

    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }
    
    public Variable getResult() {
        return result;
    }    

    @Override
    public String toString() {
        return super.toString() + "(" + argIndex + (restOfArgArray ? ", ALL" : "") + ")";
    }
    
    public int getArgIndex() {
        return argIndex;
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Interp
    @Override
    public Label interpret(InterpreterContext interp, IRExecutionScope scope, ThreadContext context, IRubyObject self, org.jruby.runtime.Block block) {
        Object o;
        int numArgs = interp.getParameterCount();
        if (restOfArgArray) {
            if (numArgs < argIndex) {
                o = RubyArray.newArrayNoCopy(context.getRuntime(), new IRubyObject[] {});
            } else {
                IRubyObject[] restOfArgs = new IRubyObject[numArgs-argIndex];
                int j = 0;
                for (int i = argIndex; i < numArgs; i++) {
                    restOfArgs[j] = (IRubyObject)interp.getParameter(i);
                    j++;
                }
                o = RubyArray.newArray(context.getRuntime(), restOfArgs);
            }
        } else {
            o = (argIndex < numArgs) ? interp.getParameter(argIndex) : context.getRuntime().getNil();
        }
        result.store(interp, context, self, o);
        return null;
    }
}
