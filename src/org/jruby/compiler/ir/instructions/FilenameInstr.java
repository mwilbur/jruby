package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class FilenameInstr extends Instr {
    private final String filename;

    public FilenameInstr(String filename) {
        super(Operation.FILE_NAME);
        
        this.filename = filename;
    }

    public Operand[] getOperands() {
        return EMPTY_OPERANDS;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + filename + ")";
    }

    public Instr cloneForInlining(InlinerInfo ii) { 
        return this;
    }

    @Override
    public Object interpret(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, Object exception, Object[] temp) {
        context.setFile(filename);
        return null;
    }
}
