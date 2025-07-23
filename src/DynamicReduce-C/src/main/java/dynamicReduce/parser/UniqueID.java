package dynamicReduce.parser;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IType;

public class UniqueID {
    private final IBinding binding;

    public UniqueID(IBinding binding){
        this.binding=binding;
    }
    private static String buildUniqueId(IBinding binding) {
        StringBuilder sb = new StringBuilder();
        sb.append(binding.getClass().getSimpleName());
        sb.append(":");
        sb.append(binding.getName());

        IBinding owner = binding.getOwner();
        while (owner != null) {
            sb.append("@");
            sb.append(owner.getName());
            owner = owner.getOwner();
        }
        return sb.toString();
    }
    @Override
    public boolean equals(Object other){
        if(other instanceof UniqueID uniqueID){
            return binding.equals(uniqueID.binding);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return binding.hashCode();
    }

    @Override
    public String toString(){
        if(binding instanceof IFunction function){
            return function.getName();
        }
        if(binding instanceof IFunction function){
            StringBuilder sb=new StringBuilder("#");
            IType returnType = function.getType().getReturnType();
            sb.append(returnType.toString());
            sb.append("(");

            IParameter[] params = function.getParameters();
            boolean start=true;
            for (IParameter param : params) {
                if(start){
                    start=false;
                }else {
                    sb.append(",");
                }
                IType paramType = param.getType();
                sb.append(paramType.toString());
            }
            sb.append(")");
            return buildUniqueId(binding)+ sb;
        }else {
            return buildUniqueId(binding);
        }
    }
}
