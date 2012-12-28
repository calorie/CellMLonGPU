package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

/**
 * statement構文クラス
 */
public abstract class SyntaxStatement extends Syntax {

    /*-----コンストラクタ-----*/
    public SyntaxStatement(eSyntaxClassification classification) {
        super(classification);
    }

}
