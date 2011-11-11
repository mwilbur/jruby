/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jruby.compiler.ir.instructions.calladapter;

import org.jruby.RubyMethod;
import org.jruby.RubyProc;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.interpreter.InterpreterContext;
import org.jruby.runtime.Block;
import org.jruby.runtime.CallSite;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.TypeConverter;

/**
 *
 * @author enebo
 */
public abstract class ClosureCallAdapter extends CallAdapter {
    private Operand closure;
    
    public ClosureCallAdapter(CallSite callSite, Operand closure) {
        super(callSite);
        
        this.closure = closure;
    }
    
    protected Block prepareBlock(InterpreterContext interp, ThreadContext context, IRubyObject self) {
        if (closure == null) return Block.NULL_BLOCK;
        
        Object value = closure.retrieve(interp, context, self);
        
        Block block;
        if (value instanceof Block) {
            block = (Block) value;
        } else if (value instanceof RubyProc) {
            block = ((RubyProc) value).getBlock();
        } else if (value instanceof RubyMethod) {
            block = ((RubyProc)((RubyMethod)value).to_proc(context, null)).getBlock();
        } else if ((value instanceof IRubyObject) && ((IRubyObject)value).isNil()) {
            block = Block.NULL_BLOCK;
        } else if (value instanceof IRubyObject) {
            block = ((RubyProc)TypeConverter.convertToType((IRubyObject)value, context.getRuntime().getProc(), "to_proc", true)).getBlock();
        } else {
            throw new RuntimeException("Unhandled case in CallInstr:prepareBlock.  Got block arg: " + value);
        }

        // Blocks passed in through calls are always normal blocks, no matter where they came from
        block.type = Block.Type.NORMAL;
        
        return block;
    }    
}
