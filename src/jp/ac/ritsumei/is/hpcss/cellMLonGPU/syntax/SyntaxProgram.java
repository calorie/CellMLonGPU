package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * プログラム構文クラス
 */
public class SyntaxProgram extends Syntax {

    /* 内部の構文 */
    protected Vector<SyntaxPreprocessor> m_vecSynPreprocessor;
    protected Vector<SyntaxDeclaration> m_vecSynDeclaration;
    protected Vector<SyntaxFunction> m_vecSynFunction;

    /*-----コンストラクタ-----*/
    public SyntaxProgram() {
        super(eSyntaxClassification.SYN_PROGRAM);
        m_vecSynPreprocessor = new Vector<SyntaxPreprocessor>();
        m_vecSynDeclaration = new Vector<SyntaxDeclaration>();
        m_vecSynFunction = new Vector<SyntaxFunction>();
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
    public String toLegalString() throws MathException, SyntaxException {
        /* 結果文字列 */
        String strPresentText = "";

        // ------------------------------------------
        // プリプロセッサ構文追加
        // ------------------------------------------
        {
            /* 順次追加 */
            for (SyntaxPreprocessor it : m_vecSynPreprocessor) {
                /* 文字列追加 */
                strPresentText += it.toLegalString() + StringUtil.lineSep;
            }
        }

        /* 改行 */
        strPresentText += StringUtil.lineSep;

        // ------------------------------------------
        // 宣言構文追加
        // ------------------------------------------
        {
            /* 順次追加 */
            for (SyntaxDeclaration it : m_vecSynDeclaration) {
                strPresentText += it.toLegalString() + StringUtil.lineSep;
            }
        }

        /* 改行 */
        strPresentText += StringUtil.lineSep;

        // ------------------------------------------
        // 関数プロトタイプ構文追加
        // ------------------------------------------
        {
            /* 順次追加 */
            for (SyntaxFunction it : m_vecSynFunction) {
                strPresentText += it.toStringPrototype() + ";"
                        + StringUtil.lineSep;
            }
        }

        /* 改行 */
        strPresentText += StringUtil.lineSep;

        // ------------------------------------------
        // 関数構文追加
        // ------------------------------------------
        {
            /* 順次追加 */
            for (SyntaxFunction it : m_vecSynFunction) {
                strPresentText += it.toLegalString() + StringUtil.lineSep;
            }
        }

        return strPresentText;
    }

    /*-----構文追加メソッド-----*/

    // ===================================================
    // addPreprocessor
    // プリプロセッサ構文追加メソッド
    //
    // @arg
    // SyntaxPreprocessor* pPreprocessor : 追加する構文インスタンス
    //
    // ===================================================
    public void addPreprocessor(SyntaxPreprocessor pPreprocessor) {
        // ------------------------------------------
        // 重複チェック
        // ------------------------------------------

        /* 先頭から照合 */
        for (SyntaxPreprocessor it : m_vecSynPreprocessor) {

            /* 照合 */
            if (it.matches(pPreprocessor)) {

                /* 重複であれば追加しない */
                return;
            }
        }

        // ------------------------------------------
        // ベクタに追加
        // ------------------------------------------
        /* 要素追加 */
        m_vecSynPreprocessor.add(pPreprocessor);
    }

    // ===================================================
    // addDeclaration
    // 宣言構文追加メソッド
    //
    // @arg
    // SyntaxDeclaration* pDeclaration : 追加する構文インスタンス
    //
    // ===================================================
    public void addDeclaration(SyntaxDeclaration pDeclaration) {
        // ------------------------------------------
        // 重複チェック
        // ------------------------------------------

        /* 先頭から照合 */
        for (SyntaxDeclaration it : m_vecSynDeclaration) {

            /* 照合 */
            if (it.matches(pDeclaration)) {

                /* 重複であれば追加しない */
                return;
            }
        }

        // ------------------------------------------
        // ベクタに追加
        // ------------------------------------------
        /* 要素追加 */
        m_vecSynDeclaration.add(pDeclaration);
    }

    // ===================================================
    // addFunction
    // 関数構文追加メソッド
    //
    // @arg
    // SyntaxFunction* pFunction : 追加する構文インスタンス
    //
    // ===================================================
    public void addFunction(SyntaxFunction pFunction) {
        /* 要素追加 */
        m_vecSynFunction.add(pFunction);
    }

}
