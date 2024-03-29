package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子floorクラス
 */
public class Math_floor extends MathOperator {

    /*-----コンストラクタ-----*/
    public Math_floor() {
        super("floor", eMathOperator.MOP_FLOOR,
                MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR);
    }

    /*-----演算命令メソッド-----*/
    public double calculate() throws MathException {
        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR) {
            throw new MathException("Math_floor", "calculate",
                    "lack of operand");
        }

        /* 演算結果を返す */
        return Math.floor(m_vecFactor.get(0).getValue());
    }

    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws MathException {

        /* 被演算子の個数チェック */
        if (m_vecFactor.size() < MathMLDefinition.MATH_OPERATOR_MIN_FACTOR_FLOOR) {
            throw new MathException("Math_floor", "toLegalString",
                    "lack of operand");
        }

        return "floor( " + m_vecFactor.get(0).toLegalString() + " )";
    }

}
