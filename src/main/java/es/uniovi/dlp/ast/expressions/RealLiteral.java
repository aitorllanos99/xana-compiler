package es.uniovi.dlp.ast.expressions;

public class RealLiteral extends AbstractExpression {
    private double value;

    public RealLiteral(int line, int column, double value) {
        super(line, column);
        this.value = value;
    }
}
