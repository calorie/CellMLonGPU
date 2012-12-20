package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;

/**
 * 式構文クラス
 */
public class SyntaxExpression extends SyntaxStatement {

	/*数式*/
	protected MathExpression m_pMathExpression;

	/*-----コンストラクタ-----*/
	public SyntaxExpression(MathExpression pMathExp) {
		super(eSyntaxClassification.SYN_EXPRESSION);
		m_pMathExpression = null;
		setExpression(pMathExp);
	}

	/*-----数式設定メソッド-----*/
	public void setExpression(MathExpression pMathExp) {
		/*数式を与える*/
		m_pMathExpression = pMathExp;
	}

	/*-----数式取得メソッド-----*/
	public MathExpression getExpression() {
		/*内部の数式を返す*/
		return m_pMathExpression;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {
		return m_pMathExpression.toLegalString() + ";";
	}

}
