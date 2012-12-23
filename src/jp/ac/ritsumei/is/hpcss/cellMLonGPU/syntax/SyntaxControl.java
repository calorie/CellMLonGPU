package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * 制御構文クラス
 */
public class SyntaxControl extends SyntaxStatement {

    // ========================================================
    // ENUM
    // ========================================================

    // ------------------------------------------制御文種類列挙
    public enum eControlKind {
        CTRL_IF, CTRL_ELSEIF, CTRL_ELSE, CTRL_FOR, CTRL_WHILE,
    };

    /* 制御文種類 */
    protected eControlKind m_controlKind;

    /* 内部の構文 */
    protected Vector<SyntaxStatement> m_vecSynStatement;

    /* 条件式 */
    protected SyntaxCondition m_pSynCondition;

    /*-----コンストラクタ-----*/
    public SyntaxControl(eControlKind controlKind, SyntaxCondition pSynCondition) {
        super(eSyntaxClassification.SYN_CONTROL);
        m_controlKind = controlKind;
        m_pSynCondition = pSynCondition;
        m_vecSynStatement = new Vector<SyntaxStatement>();
    }

    // ===================================================
    // toLegalString
    // 文字列型変換メソッド
    //
    // @return
    // 文字列型表現 : string
    //
    // ===================================================
    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws SyntaxException, MathException {
        /* 結果文字列 */
        String strPresentText = "";

        /* 制御文の種類ごとの処理 */
        switch (m_controlKind) {

        /* if文 */
        case CTRL_IF:
            strPresentText += "if(" + m_pSynCondition.toLegalString() + ")";
            break;

        // else if
        case CTRL_ELSEIF:
            strPresentText += "else if(" + m_pSynCondition.toLegalString()
                    + ")";
            break;

        // else
        case CTRL_ELSE:
            strPresentText += "else";
            break;

        /* for文 */
        case CTRL_FOR:
            strPresentText += "for(" + m_pSynCondition.toStringFor() + ")";
            break;

        /* while文 */
        case CTRL_WHILE:
            strPresentText += "while(" + m_pSynCondition.toLegalString() + ")";
            break;

        /* 予期しない種類 */
        default:
            throw new SyntaxException("SyntaxControl", "toLegalString",
                    "not declared control kind used");
        }

        /* ブロック開始括弧追加 */
        strPresentText += "{" + StringUtil.lineSep + StringUtil.lineSep;

        /* インデントインクリメント */
        incIndent();

        // ------------------------------------------
        // statement構文追加
        // ------------------------------------------
        /* 順次ステートメントを追加 */
        for (SyntaxStatement it : m_vecSynStatement) {
            strPresentText += getIndentString() + it.toLegalString()
                    + StringUtil.lineSep;
        }

        /* インデントデクリメント */
        decIndent();

        /* ブロック終了括弧追加 */
        strPresentText += StringUtil.lineSep + getIndentString() + "}"
                + StringUtil.lineSep;

        return strPresentText;
    }

    // ===================================================
    // addStatement
    // statement構文追加メソッド
    //
    // @arg
    // SyntaxStatement* pStatement : 追加する構文インスタンス
    //
    // ===================================================
    /*-----構文追加メソッド-----*/
    public void addStatement(SyntaxStatement pStatement) {
        /* 要素追加 */
        m_vecSynStatement.add(pStatement);
    }

}
