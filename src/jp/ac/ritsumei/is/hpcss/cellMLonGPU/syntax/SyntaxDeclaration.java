package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;

/**
 * 宣言構文クラス
 */
public class SyntaxDeclaration extends Syntax {

    // ========================================================
    // DEFINE
    // ========================================================
    public static final String DS_STR_STATIC = "static";
    public static final String DS_STR_CONST = "const";
    public static final String DS_STR_HOST = "__host__";
    public static final String DS_STR_DEVICE = "__device__";
    public static final String DS_STR_GLOBAL = "__global__";
    public static final String DS_STR_CONSTANT = "__constant__";

    // ========================================================
    // ENUM
    // ========================================================
    /* 宣言時修飾子列挙型 */
    public enum eDeclarationSpecifier {
        DS_NONE, DS_STATIC, DS_CONST, DS_CUDA_HOST, DS_CUDA_DEVICE, DS_CUDA_GLOBAL, DS_CUDA_CONSTANT,
    };

    /* 内部の構文 */
    protected SyntaxDataType m_pSynDataType;
    protected MathOperand m_pMathOperand;

    /* 変数宣言時修飾子 */
    protected eDeclarationSpecifier m_decSpecifier;

    /* 初期化式 */
    MathExpression m_pInitExpression;

    /* コンストラクタ引数 */
    Vector<MathExpression> m_vecConstructArgExpression;

    /*-----コンストラクタ-----*/
    public SyntaxDeclaration(SyntaxDataType pSynDataType,
            MathOperand pMathOperand) {
        super(eSyntaxClassification.SYN_DECLARATION);
        m_pSynDataType = pSynDataType;
        m_pMathOperand = pMathOperand;
        m_decSpecifier = eDeclarationSpecifier.DS_NONE;
        m_pInitExpression = null;
        m_vecConstructArgExpression = new Vector<MathExpression>();
    }

    /*-----文字列変換メソッド-----*/

    // ===================================================
    // toLegalString
    // 文字列型変換メソッド
    //
    // @return
    // 文字列型表現 : string
    //
    // ===================================================
    public String toLegalString() throws MathException {
        /* 結果文字列 */
        String strPresentText = "";

        /* 宣言指定子追加 */
        switch (m_decSpecifier) {

        case DS_STATIC:
            strPresentText += DS_STR_STATIC + " ";
            break;

        case DS_CONST:
            strPresentText += DS_STR_CONST + " ";
            break;

        case DS_CUDA_HOST:
            strPresentText += DS_STR_HOST + " ";
            break;

        case DS_CUDA_DEVICE:
            strPresentText += DS_STR_DEVICE + " ";
            break;

        case DS_CUDA_GLOBAL:
            strPresentText += DS_STR_GLOBAL + " ";
            break;

        case DS_CUDA_CONSTANT:
            strPresentText += DS_STR_CONSTANT + " ";
            break;
        }

        /* 宣言文追加 */
        strPresentText += m_pSynDataType.toLegalString() + " "
                + m_pMathOperand.toLegalString()
                + m_pSynDataType.toStringSuffix();

        /* 初期化式追加1 */
        if (m_pInitExpression != null) {
            strPresentText += " = " + m_pInitExpression.toLegalString();
        } else if (m_vecConstructArgExpression.size() > 0) {

            /* 開始括弧追加 */
            strPresentText += " ( ";

            /* 順次追加 */
            for (MathExpression it : m_vecConstructArgExpression) {

                /* はじめの引数以外の前に,を追加 */
                if (it != m_vecConstructArgExpression.firstElement()) {
                    strPresentText += " , ";
                }

                strPresentText += it.toLegalString();
            }

            /* 終了括弧追加 */
            strPresentText += " ) ";
        }

        /* 終端セミコロン追加 */
        strPresentText += ";";

        /* 結果を戻す */
        return strPresentText;
    }

    // ===================================================
    // toStringParam
    // 引数宣言用文字列型変換メソッド
    //
    // @return
    // 文字列型表現 : string
    //
    // ===================================================
    public String toStringParam() throws MathException {
        return m_pSynDataType.toLegalString() + " "
                + m_pMathOperand.toLegalString()
                + m_pSynDataType.toStringSuffix();
    }

    /*-----構文追加メソッド-----*/

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
    // addInitExpression
    // 初期化式追加メソッド
    //
    // @arg
    // MathExpression* pExpression : 初期化式インスタンス
    //
    // ===================================================
    public void addInitExpression(MathExpression pExpression) {
        m_pInitExpression = pExpression;
    }

    // ===================================================
    // addConstructArgExpression
    // コンストラクタ引数追加メソッド
    //
    // @arg
    // MathExpression* pExpression : 引数インスタンス
    //
    // ===================================================
    public void addConstructArgExpression(MathExpression pExpression) {
        m_vecConstructArgExpression.add(pExpression);
    }

    /*-----変数取得メソッド-----*/

    // ===================================================
    // getDeclaredVariable
    // 宣言より変数を取り出すメソッド
    //
    // @return
    // 宣言より取り出された変数インスタンス : Math_ci*
    // （Syntax構文クラス群が完成すれば差し替えられる）
    //
    // @throws
    // SyntaxException
    //
    // ===================================================
    public Math_ci getDeclaredVariable() throws SyntaxException {
        /* Math_ciでない場合は例外を投げる */
        if (!m_pMathOperand.matches(eMathOperand.MOPD_CI)) {
            throw new SyntaxException("SyntaxDeclaration",
                    "getDeclaredVariable", "invalid variable");
        }

        /* 変数インスタンスを返す */
        return (Math_ci) m_pMathOperand;
    }

    // ===================================================
    // matches
    // インスタンス照合メソッド
    //
    // @arg
    // SyntaxDeclaration* pDeclaration : 比較対象インスタンス
    //
    // @return
    // 一致判定 : bool
    //
    // ===================================================
    /*-----インスタンス照合メソッド-----*/
    public boolean matches(SyntaxDeclaration pDeclaration) {
        /* 宣言変数の照合結果を返す */
        return m_pMathOperand.matches(pDeclaration.m_pMathOperand);
    }

}
