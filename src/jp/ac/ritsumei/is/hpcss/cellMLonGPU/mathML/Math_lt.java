package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子ltクラス
 */
public class Math_lt extends MathOperator {

    /*-----コンストラクタ-----*/
    public Math_lt() {
        super("<", eMathOperator.MOP_LT,
                MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LT);
    }

    /*-----演算命令メソッド-----*/
    public double calculate() throws MathException {
        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LT) {
            throw new MathException("Math_lt", "calculate", "lack of operand");
        }

        /* 左辺値・右辺値取得 */
        double dLeftValue = m_vecFactor.get(0).getValue();
        double dRightValue = m_vecFactor.get(1).getValue();

        /* 左辺値の値を返す */
        return (dLeftValue < dRightValue) ? 1 : 0;
    }

    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws MathException {

        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_LT) {
            throw new MathException("Math_lt", "toLegalString",
                    "lack of operand");
        }

        return " ( " + m_vecFactor.get(0).toLegalString() + " < "
                + m_vecFactor.get(1).toLegalString() + " ) ";
    }

}
