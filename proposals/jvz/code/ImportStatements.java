package org.apache.turbine.util;

import de.fub.bytecode.Repository;
import de.fub.bytecode.classfile.ClassParser;
import de.fub.bytecode.classfile.Code;
import de.fub.bytecode.classfile.Constant;
import de.fub.bytecode.classfile.ConstantClass;
import de.fub.bytecode.classfile.ConstantPool;
import de.fub.bytecode.classfile.ConstantUtf8;
import de.fub.bytecode.classfile.ConstantCP;
import de.fub.bytecode.classfile.ConstantMethodref;
import de.fub.bytecode.classfile.ConstantInterfaceMethodref;
import de.fub.bytecode.classfile.ConstantNameAndType;
import de.fub.bytecode.classfile.JavaClass;
import de.fub.bytecode.classfile.LocalVariable;
import de.fub.bytecode.classfile.LocalVariableTable;
import de.fub.bytecode.classfile.Method;
import de.fub.bytecode.classfile.Utility;

import java.io.PrintStream;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Utility class for creating a list of fully qualified
 * import statements.
 *
 * Class fields
 * Method return types
 * Method argument types
 * Method local variables
 * Static method references
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 *
 * Problems:
 *
 * TurbineConfig.java:
 * initParams.put(TurbineServices.PROPERTIES_PATH_KEY, properties);
 * TurbineServices is not picked up.
 *
 * public RequestDispatcher getNamedDispatcher( String s)
 * RequestDispatcher is not picked up. weird.
 *
 * public Servlet getServlet(String s)
 * Servlet is not picked up. weird.
 *
 * StringStackBuffer.java:
 * pulling in java.util.AbstractList & java.util.Vector
 * when not required. Looks like a superclass problem.
 *
 * Check return values on public methods.
 *
 * SecurityCheck.java:
 * pulling in org.apache.turbine.util.security.AccessControlList
 *
 * RunData.java
 * totally screwed up. looks like an interface thang. though i got that.
 * oh the joys of dep checking.
 */
public class ImportStatements
{
    public ImportStatements(String className)
    {
        execute(className);
    }
    
    public void execute(String className)
    {
        Hashtable classes = new Hashtable();
    
        try
        {
            TreeMap classNames = new TreeMap();
            classNames.putAll(classReferences(className));
            classNames.putAll(localVariables(className));
            
            /*
             * Don't need reference to the class
             * being analysed.
             */
            classNames.remove(className);            
            
            Iterator iterator = classNames.values().iterator();
            StringBuffer sb = new StringBuffer();
            while (iterator.hasNext())
            {
                String signature = (String) iterator.next();
                
                /*
                 *  Get rid of imports that are of the form
                 * import java.lang.X;
                 */
                if (signature.indexOf("java.lang.") != -1)
                {
                    if (signature.substring("java.lang.".length()).indexOf(".") == -1)
                    {
                        continue;
                    }                        
                }
                
                if (basePackage(className).equals(basePackage(signature)))
                {
                    continue;
                }                    
                
                /*
                 * We also need to get rid of imports for classes
                 * in the same package as the class being
                 * analysed.
                 */
                
                sb.append("import ")
                  .append(signature)
                  .append(";")
                  .append("\n");
            }                
        
            System.out.println(sb);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String basePackage(String className)
    {
        return className.substring(0, className.lastIndexOf(".") - 1);
    }

    /**
     * Resolve all class references in a class file by
     * examining the constant pool in the class.
     *
     * @param String className
     * @return Hashtable Fully qualified class references.
     */
    public static Hashtable classReferences(String className) 
        throws Exception
    {
        JavaClass javaClass;
        
        if ((javaClass = Repository.lookupClass(className)) == null)
        {
            javaClass = new ClassParser(className).parse();
        }            
        
        ConstantPool constantPool = javaClass.getConstantPool();
                        
        return processConstantPool(constantPool);
    }
    
    /**
     * Determine the class references in a constant pool.
     *
     * @param ConstantPool constant pool
     * @return Hashtable class references
     */
    private static Hashtable processConstantPool(ConstantPool constantPool)
    {
        Hashtable classReferences = new Hashtable();
        Constant[] constantPoolArray = constantPool.getConstantPool();
        String signature;
        int index;
        
        for (int i = 0; i < constantPoolArray.length; i++)
        {
            if (constantPoolArray[i] instanceof ConstantClass)
            {
                index = ((ConstantClass) 
                    constantPoolArray[i]).getNameIndex();
                
                ConstantUtf8 constant = (ConstantUtf8) 
                    constantPool.getConstant(index);
                
                signature = new String(constant.getBytes());
                signature = signature.replace('/', '.');
                
                if (signature.startsWith("L") && signature.endsWith(";"))
                {
                    /*
                     * Reference of the form Ljava.util.Hashtable;
                     */
                    signature = signature.substring(1,signature.length()-1);
                } 
                else if (signature.startsWith("[L") && signature.endsWith(";"))
                {
                    /*
                     * Reference of the form Ljava.util.Hashtable;
                     */
                    signature = signature.substring(2,signature.length()-1);
                } 
                else if (signature.startsWith("["))
                {
                    continue;
                }
                else if (signature.indexOf("$") != -1)
                {
                    continue;
                }                    
                
                if (signature.endsWith("[]"))
                {
                    signature = signature.substring(0, signature.length()-2);
                }                   
                
                classReferences.put(signature,signature);
            }

            if (constantPoolArray[i] instanceof ConstantCP)
            {
                index = ((ConstantCP)
                    constantPoolArray[i]).getNameAndTypeIndex();
                
                ConstantNameAndType ntConstant = (ConstantNameAndType)
                    constantPool.getConstant(index);
                
                index = ntConstant.getSignatureIndex();

                ConstantUtf8 constant = (ConstantUtf8) 
                    constantPool.getConstant(index);

                signature = new String(constant.getBytes());
                String returnType = Utility.methodSignatureReturnType(signature);
                if (returnType.indexOf(".") != -1)
                {
                    if (returnType.endsWith("[]"))
                    {
                        returnType = returnType.substring(0, returnType.length()-2);
                    }                   
                    
                    if (returnType.indexOf("$") == -1)
                    {
                        classReferences.put(returnType,returnType);
                    }                        
                }                    

                if (constantPoolArray[i] instanceof ConstantMethodref ||
                    constantPoolArray[i] instanceof ConstantInterfaceMethodref)
                {
                    String[] argumentTypes = Utility.methodSignatureArgumentTypes(signature);
                
                    for (int j = 0; j < argumentTypes.length; j++)
                    {
                        String argType = argumentTypes[j];
                        if (argType.indexOf(".") != -1)
                        {
                            if (argType.endsWith("[]"))
                            {
                                argType = argType.substring(0, argType.length()-2);
                            }                   
                            if (argType.indexOf("$") == -1)
                            {
                                classReferences.put(argType,argType);
                            }                                
                        }                    
                    }
                }
            }
        }
    
        return classReferences;
    }
    
    /**
     * Find all the local variable references within a method.
     *
     * @param String name of java class
     * @return Hashtable
     */
    public static Hashtable localVariables(String name) throws Exception
    {
        Hashtable vars = new Hashtable();
        
        JavaClass javaClass;
        
        if ((javaClass = Repository.lookupClass(name)) == null)
        {
            javaClass = new ClassParser(name).parse();
        }            
        
        Method[] methods = javaClass.getMethods();
        
        for (int i = 0; i < methods.length; i++)
        {
            Code code = methods[i].getCode();
            if (code != null)
            {
                LocalVariableTable table = code.getLocalVariableTable();
                
                if (table == null)
                {
                    continue;
                }                    
                
                LocalVariable[] lvt = table.getLocalVariableTable();
                
                for (int j = 0; j < lvt.length; j++)
                {
                    String lvar = Utility.signatureToString(lvt[j].getSignature());
                    
                    if (lvar.indexOf(".") == -1)
                    {
                        continue;
                    }                        
                    
                    if (lvar.indexOf("$") != -1)
                    {
                        continue;
                    }                        
                    
                    /*
                     * Get rid of the array signature
                     * if present we just need the base type
                     * signature.
                     */
                    if (lvar.endsWith("[]"))
                    {
                        lvar = lvar.substring(0, lvar.length() - 2);
                    }                        
                        
                    vars.put(lvar,lvar);
                    
                    /* 
                     * Process the constant pool of the method.
                     */
                    Hashtable methodClassReferences = 
                        processConstantPool(code.getConstantPool());
                        
                    vars.putAll(methodClassReferences);
                }                    
            }                
        }
    
        return vars;
    }

    public static void main(String[] args)
    {
        ImportStatements is = new ImportStatements(args[0]);
    }
}
