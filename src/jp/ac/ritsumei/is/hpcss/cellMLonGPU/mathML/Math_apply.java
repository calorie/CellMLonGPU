package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子applyクラス
 */
public class Math_apply extends MathOperator {

    /*-----コンストラクタ-----*/
    public Math_apply() {
        super("", eMathOperator.MOP_APPLY,
                MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY);
    }

    /*-----演算命令メソッド-----*/
    public double calculate() throws MathException {
        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY) {
            throw new MathException("Math_apply", "calculate",
                    "lack of operand");
        }

        /* 演算結果を返す */
        return m_vecFactor.get(0).getValue();
    }

    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws MathException {

        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_APPLY) {
            throw new MathException("Math_apply", "toLegalString",
                    "lack of operand");
        }

        return m_vecFactor.get(0).toLegalString();
    }

}
