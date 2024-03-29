package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration.eDeclarationSpecifier;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * 関数構文クラス
 */
public class SyntaxFunction extends Syntax {

    /* 関数名 */
    protected String m_strFuncName;

    /* 関数宣言時修飾子 */
    protected eDeclarationSpecifier m_decSpecifier;

    /* 引数 */
    protected Vector<SyntaxDeclaration> m_vecFuncParam;

    /* 戻り値型 */
    protected SyntaxDataType m_pFuncType;

    /* 内部の構文 */
    protected Vector<SyntaxDeclaration> m_vecSynDeclaration;
    protected Vector<SyntaxStatement> m_vecSynStatement;

    /*-----コンストラクタ-----*/
    public SyntaxFunction(String strFuncName, SyntaxDataType pFuncType) {
        super(eSyntaxClassification.SYN_FUNCTION);
        m_strFuncName = strFuncName;
        m_decSpecifier = eDeclarationSpecifier.DS_NONE;
        m_pFuncType = pFuncType;
        m_vecFuncParam = new Vector<SyntaxDeclaration>();
        m_vecSynDeclaration = new Vector<SyntaxDeclaration>();
        m_vecSynStatement = new Vector<SyntaxStatement>();
    }

    // ===================================================
    // toLegalString
    // 文字列型変換メソッド
    //
    // @return
    // 文字列型表現 : string
    //
    // @throws
    // SyntaxException
    //
    // ===================================================
    /*-----文字列変換メソッド-----*/
    public String toLegalString() throws SyntaxException, MathException {
        /* 結果文字列 */
        String strPresentText = "";

        /* 宣言指定子追加 */
        switch (m_decSpecifier) {

        case DS_STATIC:
            strPresentText += SyntaxDeclaration.DS_STR_STATIC + " ";
            break;

        case DS_CONST:
            strPresentText += SyntaxDeclaration.DS_STR_CONST + " ";
            break;

        case DS_CUDA_HOST:
            strPresentText += SyntaxDeclaration.DS_STR_HOST + " ";
            break;

        case DS_CUDA_DEVICE:
            strPresentText += SyntaxDeclaration.DS_STR_DEVICE + " ";
            break;

        case DS_CUDA_GLOBAL:
            strPresentText += SyntaxDeclaration.DS_STR_GLOBAL + " ";
            break;

        case DS_CUDA_CONSTANT:
            strPresentText += SyntaxDeclaration.DS_STR_CONSTANT + " ";
            break;
        }

        /* 戻り値型文字列追加 */
        if (m_pFuncType != null) {
            strPresentText += m_pFuncType.toLegalString()
                    + m_pFuncType.toStringSuffix() + " ";
        } else {
            throw new SyntaxException("SyntaxFunction", "toLegalString",
                    "lack of data type");
        }

        /* 関数名追加 */
        strPresentText += m_strFuncName + " ( ";

        // ------------------------------------------
        // 引数宣言追加
        // ------------------------------------------

        {
            /* 引数なし(void) */
            if (m_vecFuncParam.size() == 0) {
                strPresentText += "void";
            }
            /* 引数あり */
            else {
                /* 順次追加 */
                for (SyntaxDeclaration it : m_vecFuncParam) {

                    /* はじめの引数以外の前に,を追加 */
                    if (it != m_vecFuncParam.firstElement()) {
                        strPresentText += " , ";
                    }

                    strPresentText += it.toStringParam() + "";
                }
            }
        }

        /* 関数内部構文開始 */
        strPresentText += " ) {" + StringUtil.lineSep + StringUtil.lineSep;

        /* インデントインクリメント */
        incIndent();
        // ------------------------------------------
        // 宣言構文追加
        // ------------------------------------------
        {
            /* 順次追加 */
            for (SyntaxDeclaration it : m_vecSynDeclaration) {
                strPresentText += getIndentString() + it.toLegalString()
                        + StringUtil.lineSep;
            }
        }

        /* 改行 */
        strPresentText += StringUtil.lineSep;

        // ------------------------------------------
        // statement構文追加
        // ------------------------------------------
        {
            /* 順次追加 */
            for (SyntaxStatement it : m_vecSynStatement) {
                strPresentText += getIndentString() + it.toLegalString()
                        + StringUtil.lineSep;
            }
        }

        /* インデントデクリメント */
        decIndent();

        /* 関数内部構文終了 */
        strPresentText += getIndentString() + "}" + StringUtil.lineSep
                + StringUtil.lineSep;

        return strPresentText;
    }

    // ===================================================
    // toStringPrototype
    // 文字列型変換メソッド(関数プロトタイプ用)
    //
    // @return
    // 文字列型表現 : string
    //
    // ===================================================
    public String toStringPrototype() throws MathException, SyntaxException {
        /* 結果文字列 */
        String strPresentText = "";

        /* 宣言指定子追加 */
        switch (m_decSpecifier) {

        case DS_STATIC:
            strPresentText += SyntaxDeclaration.DS_STR_STATIC + " ";
            break;

        case DS_CUDA_HOST:
            strPresentText += SyntaxDeclaration.DS_STR_HOST + " ";
            break;

        case DS_CUDA_DEVICE:
            strPresentText += SyntaxDeclaration.DS_STR_DEVICE + " ";
            break;

        case DS_CUDA_GLOBAL:
            strPresentText += SyntaxDeclaration.DS_STR_GLOBAL + " ";
            break;
        }

        /* 戻り値型文字列追加 */
        if (m_pFuncType != null) {
            strPresentText += m_pFuncType.toLegalString()
                    + m_pFuncType.toStringSuffix() + " ";
        } else {
            throw new SyntaxException("SyntaxFunction", "toLegalString",
                    "lack of data type");
        }

        /* 関数名追加 */
        strPresentText += m_strFuncName + " ( ";

        // ------------------------------------------
        // 引数宣言追加
        // ------------------------------------------
        {
            /* 引数なし(void) */
            if (m_vecFuncParam.size() == 0) {
                strPresentText += "void";
            }
            /* 引数あり */
            else {
                /* 順次追加 */
                for (SyntaxDeclaration it : m_vecFuncParam) {

                    /* はじめの引数以外の前に,を追加 */
                    if (it != m_vecFuncParam.firstElement()) {
                        strPresentText += " , ";
                    }

                    strPresentText += it.toStringParam() + "";
                }
            }
        }

        /* 関数内部構文開始 */
        strPresentText += " ) ";

        return strPresentText;
    }

    /*-----構文追加メソッド-----*/

    // ===================================================
    // addParam
    // 関数引数追加メソッド
    //
    // @arg
    // SyntaxDeclaration* pDeclaration : 追加する構文インスタンス
    //
    // ===================================================
    public void addParam(SyntaxDeclaration pDeclaration) {
        // ------------------------------------------
        // 重複チェック
        // ------------------------------------------
        /* ローカル変数宣言と照合 */
        for (SyntaxDeclaration it : m_vecSynDeclaration) {

            /* 照合 */
            if (it.matches(pDeclaration)) {

                /* 重複であれば追加しない */
                return;
            }
        }

        /* 引数宣言と照合 */
        for (SyntaxDeclaration it : m_vecFuncParam) {

            /* 照合 */
            if (it.matches(pDeclaration)) {

                /* 重複であれば追加しない */
                return;
            }
        }

        // ------------------------------------------
        // ベクタに追加
        // ------------------------------------------
        /* 引数宣言の追加 */
        m_vecFuncParam.add(pDeclaration);
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
        /* ローカル変数宣言と照合 */
        for (SyntaxDeclaration it : m_vecSynDeclaration) {

            /* 照合 */
            if (it.matches(pDeclaration)) {

                /* 重複であれば追加しない */
                return;
            }
        }

        /* 引数宣言と照合 */
        for (SyntaxDeclaration it : m_vecFuncParam) {

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
    // addStatement
    // statement構文追加メソッド
    //
    // @arg
    // SyntaxStatement* pStatement : 追加する構文インスタンス
    //
    // ===================================================
    public void addStatement(SyntaxStatement pStatement) {
        /* 要素追加 */
        m_vecSynStatement.add(pStatement);
    }

    // ===================================================
    // addDeclarationSpecifier
    // 宣言指定子追加メソッド
    //
    // @arg
    // eDeclarationSpecifier decSpecifier : 宣言指定子列挙型
    //
    // ===================================================
    public void addDeclarationSpecifier(eDeclarationSpecifier decSpecifier) {
        /* 宣言指定子追加 */
        m_decSpecifier = decSpecifier;
    }

    // ===================================================
    // callFunction
    // 関数コール
    //
    // ===================================================
    public SyntaxCallFunction callFunction(MathFactor... pArgVar) {
        /* 宣言指定子追加 */
        SyntaxCallFunction pCallFunc = new SyntaxCallFunction(m_strFuncName);
        /* 引数宣言の追加 */
        for (int i = 0; i < pArgVar.length; i++) {
            pCallFunc.addArgFactor(pArgVar[i]);
        }
        return pCallFunc;
    }
}
