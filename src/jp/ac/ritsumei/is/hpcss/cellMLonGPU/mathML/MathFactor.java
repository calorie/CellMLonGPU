package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;

/**
 * MathML演算要素クラス
 */
public abstract class MathFactor {

    /*-----表示用文字列-----*/
    protected String m_strPresentText;

    /*-----要素分類-----*/
    protected eMathMLClassification m_classification;

    /*-----コンストラクタ-----*/
    public MathFactor(String strPresentText,
            eMathMLClassification classification) {
        m_strPresentText = strPresentText;
        m_classification = classification;
    }

    /*-----演算結果取得メソッド-----*/
    public abstract double getValue() throws MathException;

    /*-----値格納メソッド-----*/
    public abstract void setValue(double dValue) throws MathException;

    /*-----数式複製メソッド-----*/
    public abstract MathFactor createCopy() throws MathException;

    /*-----文字列変換メソッド-----*/
    // public abstract String toString();
    public abstract String toLegalString() throws MathException;

    public String toStringInCondition() throws MathException {
        // return toString();
        return toLegalString();
    }

    /*-----オブジェクト比較メソッド-----*/
    public boolean matches(MathFactor pFactor) {
        return m_classification == pFactor.m_classification;
    }

    public boolean matches(eMathMLClassification classification) {
        return m_classification == classification;
    }

    /*-----数式比較メソッド-----*/
    public abstract boolean matchesExpression(MathFactor pFactor);

}
