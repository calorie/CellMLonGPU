package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子plusクラス
 */
public class Math_plus extends MathOperator {

	/*-----コンストラクタ-----*/
	public Math_plus() {
		super("+", eMathOperator.MOP_PLUS, MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_PLUS);
	}

	/*-----演算命令メソッド-----*/
	public double calculate() throws MathException {
		/*被演算子の個数チェック*/
		if(m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_PLUS){
			throw new MathException("Math_plus","calculate","lack of operand");
		}

		/*加算*/
		double dValueSum = 0.0;

		for (MathFactor it: m_vecFactor) {
			dValueSum += it.getValue();
		}

		/*演算結果を返す*/
		return dValueSum;
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*非演算子がない場合は例外*/
		if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_PLUS) {
			throw new MathException("Math_plus","toLegalString","lack of operand");
		}
		/*単項演算子*/
		else if (m_vecFactor.size() == 1) {
			return " ( + " + m_vecFactor.get(0).toLegalString() + " ) ";
		}
		/*多項演算子*/
		else{

			/*文字列を追加していく*/
			String strExpression = " ( ";

			for(MathFactor it: m_vecFactor) {

				/* +演算子を追加 */
				if(it != m_vecFactor.firstElement()){
					strExpression += " + ";
				}

				/*項を追加*/
				strExpression += it.toLegalString();
			}

			/*閉じ括弧を追加*/
			strExpression += " ) ";

			return strExpression;
		}
	}

}
