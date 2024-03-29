package es.uniovi.dlp.ast.definitions;


import es.uniovi.dlp.ast.types.Type;

import es.uniovi.dlp.visitor.Visitor;

public class VarDefinition extends AbstractDefinition implements Definition {
    private String name;
    private Type type;
    private int offset;

    public VarDefinition(int line, int column, String name, Type type) {
        super(line, column);
        this.name = name;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public <ParamType, ReturnType> ReturnType accept(Visitor<ReturnType, ParamType> returnTypeParamTypeAbstractVisitor, ParamType param) {
        return returnTypeParamTypeAbstractVisitor.visit(this, param);
    }
}
