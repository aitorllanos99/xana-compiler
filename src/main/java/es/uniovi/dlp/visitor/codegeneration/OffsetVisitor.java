package es.uniovi.dlp.visitor.codegeneration;

import es.uniovi.dlp.ast.definitions.FuncDefinition;
import es.uniovi.dlp.ast.definitions.VarDefinition;
import es.uniovi.dlp.ast.types.ArrayType;
import es.uniovi.dlp.ast.types.FuncType;
import es.uniovi.dlp.ast.types.RecordType;
import es.uniovi.dlp.visitor.AbstractVisitor;
import es.uniovi.dlp.visitor.Visitor;

import java.util.stream.Collectors;

public class OffsetVisitor extends AbstractVisitor<Object,Object> {
    private int desplazamientoGlobal = 0;
    private int desplazamientoLocal = 0;

    @Override
    public Object visit(VarDefinition varDefinition, Object param) {
        super.visit(varDefinition, param);
        if (varDefinition.getScope() == 0) {
            varDefinition.setOffset(desplazamientoGlobal);
            desplazamientoGlobal += varDefinition.getType().getNumberOfBytes();
        } else {
            desplazamientoLocal -= varDefinition.getType().getNumberOfBytes();
            varDefinition.setOffset(desplazamientoLocal);
        }
        return null;
    }

    @Override
    public Object visit(FuncDefinition funcDefinition, Object param) {
        desplazamientoLocal = 0;
        super.visit(funcDefinition, param);
        return null;
    }

    @Override
    public Object visit(FuncType funcType, Object param) {
        int desplazamiento = 4;
        for (int i = funcType.getParameters().size() - 1; i >= 0; i--) {
            funcType.getParameters().get(i).setOffset(desplazamiento);
            desplazamiento += funcType.getParameters().get(i).getType().getNumberOfBytes();
        }
        funcType.getReturnType().accept(this, param);
        return null;
    }

    @Override
    public Object visit(RecordType recordType, Object param) {
        int offsetFields = 0;
        for(var f: recordType.getFields()){
            f.setOffset(offsetFields);
            offsetFields += f.getType().getNumberOfBytes();
            if(f.getType() instanceof RecordType)
                visit(f,param);
        }
        return null;
    }

}
