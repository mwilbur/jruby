package org.jruby.compiler.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.compiler.ir.compiler_pass.CompilerPass;
import org.jruby.compiler.ir.instructions.ReceiveSelfInstruction;
import org.jruby.compiler.ir.operands.LocalVariable;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.parser.IRStaticScopeFactory;
import org.jruby.parser.StaticScope;

public class IRModule extends IRScope {
    private final static StaticScope rootObjectScope = IRStaticScopeFactory.newIRLocalScope(null);

    // The "root" method of a class -- the scope in which all definitions, and class code executes, equivalent to java clinit
    private final static String ROOT_METHOD_PREFIX = "[root]:";
    private static Map<String, IRClass> coreClasses;

    private CodeVersion version;    // Current code version for this module

    // Modules, classes, and methods that belong to this scope 
    //
    // LEXICAL scoping, but when a class, method, module definition is
    // encountered in a closure or a method in Ruby code, that definition
    // is pushed up to the nearest containing module!
    //
    // In most cases, this lexical scoping also matches actual class/module hierarchies
    // SSS FIXME: An example where they might be different?
    private List<IRModule> modules = new ArrayList<IRModule>();
    private List<IRClass> classes = new ArrayList<IRClass>();
    private List<IRMethod> methods = new ArrayList<IRMethod>();
    
    static {
        bootStrap();
    }
    

    public IRModule(IRScope lexicalParent, String name, StaticScope scope) {
        super(lexicalParent, name, scope);
        
        addInstr(new ReceiveSelfInstruction(getSelf()));   // Set up self!
        
        updateVersion();
    }    

    static private IRClass addCoreClass(String name, IRScope parent, String[] coreMethods, StaticScope staticScope) {
        IRClass c = new IRClass(parent, null, name, staticScope);
        coreClasses.put(c.getName(), c);
        if (coreMethods != null) {
            for (String m : coreMethods) {
                IRMethod meth = new IRMethod(c, m, true, null);
                meth.setCodeModificationFlag(false);
                c.addMethod(meth);
            }
        }
        return c;
    }

    // SSS FIXME: These should get normally compiled or initialized some other way ... 
    // SSS FIXME: Parent/super-type info is incorrect!
    // These are just placeholders for now .. this needs to be updated with *real* class objects later!
    static public void bootStrap() {
        coreClasses = new HashMap<String, IRClass>();
        IRScript boostrapScript = new IRScript("[bootstrap]", "[bootstrap]", null);
        addCoreClass("Object", boostrapScript, null, null);
        addCoreClass("Module", boostrapScript, null, null);
        addCoreClass("Class", boostrapScript, null, null);
        addCoreClass("Fixnum", boostrapScript, new String[]{"+", "-", "/", "*"}, null);
        addCoreClass("Float", boostrapScript, new String[]{"+", "-", "/", "*"}, null);
        addCoreClass("Array", boostrapScript, new String[]{"[]", "each", "inject"}, null);
        addCoreClass("Range", boostrapScript, new String[]{"each"}, null);
        addCoreClass("Hash", boostrapScript, new String[]{"each"}, null);
        addCoreClass("String", boostrapScript, null, null);
        addCoreClass("Proc", boostrapScript, null, null);
    }

    public static StaticScope getRootObjectScope() {
        return rootObjectScope;
    }

    public static IRClass getCoreClass(String n) {
        return coreClasses.get(n);
    }

    public static boolean isAModuleRootMethod(IRMethod m) {
        return m.getName().startsWith(ROOT_METHOD_PREFIX);
    }

    public List<IRModule> getModules() {
        return modules;
    }

    public List<IRClass> getClasses() {
        return classes;
    }

    public List<IRMethod> getMethods() {
        return methods;
    }

    public void addModule(IRModule m) {
        modules.add(m);
    }

    public void addClass(IRClass c) {
        classes.add(c);
    }

    public void addMethod(IRMethod method) {
        assert !IRModule.isAModuleRootMethod(method);

        methods.add(method);
    }

    @Override
    public void runCompilerPassOnNestedScopes(CompilerPass p) {
        for (IRScope m : modules) {
            m.runCompilerPass(p);
        }

        for (IRScope c : classes) {
            c.runCompilerPass(p);
        }

        for (IRScope meth : methods) {
            meth.runCompilerPass(p);
        }
    }

    @Override
    public IRModule getNearestModule() {
        return this;
    }

    public void updateVersion() {
        version = CodeVersion.getClassVersionToken();
    }

    public String getScopeName() {
        return "Module";
    }

    public CodeVersion getVersion() {
        return version;
    }

    public IRMethod getInstanceMethod(String name) {
        for (IRMethod m : methods) {
            if (m.isInstanceMethod && m.getName().equals(name)) return m;
        }

        return null;
    }

    public IRMethod getClassMethod(String name) {
        for (IRMethod m : methods) {
            if (!m.isInstanceMethod && getName().equals(name)) return m;
        }

        return null;
    }

    public boolean isACoreClass() {
        return this == IRClass.getCoreClass(getName());
    }

    public boolean isCoreClassType(String className) {
        return this == IRClass.getCoreClass(className);
    }

    public RubyModule getCoreClassModule(Ruby runtime) {
        // SSS FIXME: Here, I dont really care if this is a core class module or not .. so, why the charade?
        String n = getName();
        if (n.equals("Object")) return runtime.getObject();
        else return runtime.getClass(n);
    }

    public LocalVariable getLocalVariable(String name, int scopeDepth) {
        LocalVariable lvar = findExistingLocalVariable(name);
        if (lvar == null) {
            lvar = new LocalVariable(name, scopeDepth, localVars.nextSlot);
            localVars.putVariable(name, lvar);
        }

        return lvar;
    }

    @Override
    protected StaticScope constructStaticScope(StaticScope unused) {
        this.requiredArgs = 0;
        this.optionalArgs = 0;
        this.restArg = -1;

        return IRStaticScopeFactory.newIRLocalScope(null); // method scopes cannot see any lower
    }

    @Override
    public LocalVariable getImplicitBlockArg() {
        return getLocalVariable(Variable.BLOCK, 0);
    }

    @Override
    public LocalVariable findExistingLocalVariable(String name) {
        return localVars.getVariable(name);
    }
}
