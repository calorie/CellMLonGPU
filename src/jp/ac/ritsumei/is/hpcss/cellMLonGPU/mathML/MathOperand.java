package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;

/**
 * MathML被演算子クラス
 */
public abstract class MathOperand extends MathFactor {

    /* 要素の値変数 */
    protected double m_dValue;

    /* 初期化判定 */
    protected boolean m_bInitFlag;

    /* 被演算子種類 */
    protected eMathOperand m_operandKind;

    /*-----コンストラクタ-----*/
    public MathOperand(String strPresentText, double dValue,
            eMathOperand operandKind) {
        super(strPresentText, eMathMLClassification.MML_OPERAND);
        m_dValue = dValue;
        m_bInitFlag = true;
        m_operandKind = operandKind;
    }

    public MathOperand(String strPresentText, eMathOperand operandKind) {
        super(strPresentText, eMathMLClassification.MML_OPERAND);
        m_dValue = 0.0;
        m_bInitFlag = false;
        m_operandKind = operandKind;
    }

    /*-----演算結果取得メソッド-----*/
    public double getValue() throws MathException {
        /* 未初期化の場合は例外処理 */
        if (!m_bInitFlag) {
            throw new MathException("MathOperand", "getValue",
                    "uninitialized operand referenced");
        }

        return m_dValue;
    }

    /*-----数式複製メソッド-----*/
    public MathFactor createCopy() throws MathException {
        return MathFactory.createOperand(m_operandKind, m_strPresentText,
                m_dValue);
    }

    /*-----オブジェクト比較メソッド-----*/
    public boolean matches(MathOperand pOperand) {
        return m_strPresentText.equals(pOperand.m_strPresentText);
    }

    public boolean matches(eMathOperand operandKind) {
        return m_operandKind == operandKind;
    }

    /*-----数式比較メソッド-----*/
    public boolean matchesExpression(MathFactor pFactor) {

        /* オペランドの場合 */
        if (pFactor.matches(eMathMLClassification.MML_OPERAND)) {
            return matches((MathOperand) pFactor);
        }
        /* その他の要素とは比較しない */
        else {
            return false;
        }
    }

}
