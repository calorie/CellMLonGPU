package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子tanクラス
 */
public class Math_tan extends MathOperator {

    /*-----コンストラクタ-----*/
    public Math_tan() {
        super("tan", eMathOperator.MOP_TAN,
                MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TAN);
    }

    /*-----演算命令メソッド-----*/
    public double calculate() throws MathException {
        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TAN) {
            throw new MathException("Math_tan", "calculate", "lack of operand");
        }

        /* 演算結果を返す */
        return Math.tan(m_vecFactor.get(0).getValue());
    }

    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws MathException {

        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TAN) {
            throw new MathException("Math_tan", "toLegalString",
                    "lack of operand");
        }

        return "tan( " + m_vecFactor.get(0).toLegalString() + " )";
    }

}
