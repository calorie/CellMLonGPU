package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子timesクラス
 */
public class Math_times extends MathOperator {

    /*-----コンストラクタ-----*/
    public Math_times() {
        super("*", eMathOperator.MOP_TIMES,
                MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES);
    }

    /*-----演算命令メソッド-----*/
    public double calculate() throws MathException {
        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES) {
            throw new MathException("Math_times", "calculate",
                    "lack of operand");
        }

        /* 積算 */
        double dValueSum = 1.0;

        for (MathFactor it : m_vecFactor) {
            dValueSum *= it.getValue();
        }

        /* 演算結果を返す */
        return dValueSum;
    }

    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws MathException {

        /* 非演算子がない場合は例外 */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_TIMES) {
            throw new MathException("Math_times", "toLegalString",
                    "lack of operand");
        }

        /* 文字列を追加していく */
        String strExpression = " ( ";

        for (MathFactor it : m_vecFactor) {

            /* *演算子を追加 */
            if (it != m_vecFactor.firstElement()) {
                strExpression += " * ";
            }

            /* 項を追加 */
            strExpression += it.toLegalString();
        }

        /* 閉じ括弧を追加 */
        strExpression += " ) ";

        return strExpression;
    }

}
