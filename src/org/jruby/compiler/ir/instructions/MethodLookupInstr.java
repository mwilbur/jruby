package org.jruby.compiler.ir.instructions;

import java.util.Map;
import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.MethodHandle;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class MethodLookupInstr extends Instr implements ResultInstr {
    private Operand methodHandle;
    private Variable result;

    public MethodLookupInstr(Variable result, MethodHandle mh) {
        super(Operation.METHOD_LOOKUP);
        
        assert result != null: "MethodLookupInstr result is null";
        
        this.methodHandle = mh;
        this.result = result;
    }

    public MethodLookupInstr(Variable dest, Operand methodName, Operand receiver) {
        this(dest, new MethodHandle(methodName, receiver));
    }

    public MethodHandle getMethodHandle() {
        return (MethodHandle)methodHandle;
    }

    public Operand[] getOperands() {
        return new Operand[]{methodHandle};
    }
    
    public Variable getResult() {
        return result;
    }
    
    public void updateResult(Variable v) {
        this.result = v;
    }

    @Override
    public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
        methodHandle = methodHandle.getSimplifiedOperand(valueMap, force);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + methodHandle + ")";
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return new MethodLookupInstr(ii.getRenamedVariable(result), (MethodHandle)methodHandle.cloneForInlining(ii));
    }

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        result.store(context, self, temp, methodHandle.retrieve(context, self, temp));
        return null;
    }
}
