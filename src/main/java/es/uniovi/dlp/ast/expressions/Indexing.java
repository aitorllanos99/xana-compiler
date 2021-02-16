package es.uniovi.dlp.ast.expressions;



public class Indexing extends AbstractExpression {
    private Expressions expression1;
    private Expressions expression2;

    public Indexing(int line, int column, Expressions expressions1 , Expressions expressions2, String operator) {
        super(line, column);
        this.expression1 = expressions1;
        this.expression2 = expressions2;
    }
}
