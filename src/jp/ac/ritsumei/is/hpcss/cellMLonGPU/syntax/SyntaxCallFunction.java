package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;

/**
 * 関数呼び出し構文クラス
 */
public class SyntaxCallFunction extends SyntaxStatement {

    /* 関数名 */
    protected String m_strFuncName;

    /* 引数 */
    protected Vector<MathFactor> m_vecArgFactor; // 引数
    protected Vector<MathFactor> m_vecKernelParam; // ブロック・グリッドサイズ，(共有メモリサイズ)

    /* キャスト型 */
    protected SyntaxDataType m_pCastDataType;

    /*-----コンストラクタ-----*/
    public SyntaxCallFunction(String strFuncName) {
        super(eSyntaxClassification.SYN_CALLFUNCTION);
        m_strFuncName = strFuncName;
        m_pCastDataType = null;
        m_vecArgFactor = new Vector<MathFactor>();
        m_vecKernelParam = new Vector<MathFactor>();
    }

    /*-----引数追加メソッド-----*/

    // ===================================================
    // addArgFactor
    // 関数呼び出しへの引数追加メソッド
    //
    // @arg
    // MathFactor* pArgFactor : 関数呼び出しの引数要素
    //
    // ===================================================
    public void addArgFactor(MathFactor pArgFactor) {
        /* ベクタへの追加 */
        m_vecArgFactor.add(pArgFactor);
    }

    // ===================================================
    // addKernelParam
    // カーネル関数呼び出しへのパラメータ追加メソッド
    //
    // @arg
    // MathFactor* pParamFactor : カーネル関数呼び出しのパラメータ要素(ブロックサイズなど)
    //
    // ===================================================
    public void addKernelParam(MathFactor pParamFactor) {
        /* ベクタへの追加 */
        m_vecKernelParam.add(pParamFactor);
    }

    // ===================================================
    // addCastDataType
    // 関数呼び出しの戻り値キャスト型追加メソッド
    //
    // @arg
    // SyntaxDataType* pDataType : キャスト先のデータ型
    //
    // ===================================================
    public void addCastDataType(SyntaxDataType pDataType) {
        /* キャスト先のデータ型を指定 */
        m_pCastDataType = pDataType;
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
    public String toLegalStringWithNoSemicolon() throws MathException {
        /* 結果文字列 */
        String strPresentText = "";

        /* キャスト型が指定されていればキャスト構文追加 */
        if (m_pCastDataType != null) {
            strPresentText += "(" + m_pCastDataType.toLegalString() + ")";
        }

        /* カーネル関数名追加 */
        strPresentText += m_strFuncName;

        // ------------------------------------------
        // カーネルパラメータ追加
        // ------------------------------------------
        if (m_vecKernelParam.size() > 0) {
            /* 開始括弧追加 */
            strPresentText += " <<< ";

            /* 順次パラメータを追加 */
            for (MathFactor it1 : m_vecKernelParam) {

                /* コンマ追加 */
                if (it1 != m_vecKernelParam.firstElement()) {
                    strPresentText += " , ";
                }

                /* パラメータ追加 */
                strPresentText += it1.toLegalString();
            }

            /* 終了括弧追加 */
            strPresentText += " >>> ";
        }

        // ------------------------------------------
        // 関数引数追加
        // ------------------------------------------
        /* 開始括弧追加 */
        strPresentText += " ( ";

        /* 順次引数を追加 */
        for (MathFactor it2 : m_vecArgFactor) {

            /* コンマ追加 */
            if (it2 != m_vecArgFactor.firstElement()) {
                strPresentText += " , ";
            }

            /* 引数追加 */
            strPresentText += it2.toLegalString();
        }

        /* 終了括弧追加 */
        strPresentText += " )";

        /* 文字列を返す */
        return strPresentText;
    }

    public String toLegalString() throws MathException {
        return toLegalStringWithNoSemicolon() + " ;";
    }
}
