package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;

/**
 * 構文基底クラス
 */
public abstract class Syntax {

    // ========================================================
    // ENUM
    // ========================================================

    // ------------------------------------------構文分類列挙
    public enum eSyntaxClassification {
        SYN_DECLARATION, SYN_EXPRESSION, SYN_PREPROCESSOR, SYN_CONTROL, SYN_FUNCTION, SYN_PROGRAM, SYN_CONDITION, SYN_CALLFUNCTION,
    };

    /* 構文分類 */
    protected eSyntaxClassification m_classification;

    /* 現在のインデント */
    protected static int m_ushIndent = 0;

    /*-----コンストラクタ-----*/
    public Syntax(eSyntaxClassification classification) {
        m_classification = classification;
    }

    /*-----文字列変換メソッド-----*/
    abstract public String toLegalString() throws MathException,
            SyntaxException;

    /*-----インデント操作メソッド-----*/

    // ===================================================
    // initIndent
    // インデント初期化メソッド
    //
    // ===================================================
    protected void initIndent() {
        m_ushIndent = 0;
    }

    // ===================================================
    // incIndent
    // インデントインクリメントメソッド
    //
    // ===================================================
    protected void incIndent() {
        m_ushIndent++;
    }

    // ===================================================
    // decIndent
    // インデントデクリメントメソッド
    //
    // ===================================================
    protected void decIndent() {
        if (m_ushIndent > 0) {
            m_ushIndent--;
        }
    }

    // ===================================================
    // getIndentString
    // インデントデクリメントメソッド
    //
    // @return
    // インデント文字列 : string
    //
    // ===================================================
    protected String getIndentString() {
        /* 結果文字列 */
        String strPresentText = "";

        /* インデント追加 */
        for (int i = 0; i < m_ushIndent; i++) {
            strPresentText += "\t";
        }

        return strPresentText;
    }

}
