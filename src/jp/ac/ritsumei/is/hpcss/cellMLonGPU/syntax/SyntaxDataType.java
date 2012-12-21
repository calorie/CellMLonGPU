package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;

/**
 * データ型構文クラス
 */
public class SyntaxDataType {

    //========================================================
    //ENUM
    //========================================================

    //------------------------------------------データ型列挙
    public enum eDataType {
        DT_VOID		("void"),
        DT_CHAR		("char"),
        DT_UCHAR	("unsigned char"),
        DT_SHORT	("short"),
        DT_USHORT	("unsigned short"),
        DT_INT		("int"),
        DT_UINT		("unsigned int"),
        DT_FLOAT	("float"),
        DT_DOUBLE	("double"),
        DT_DIM3		("dim3"),
        DT_MPISTA   ("MPI_Status"),
        DT_FILE     ("FILE"),
        DT_ENTRY    ("ENTRY"),
            ;
        private final String operatorStr;
        private eDataType(String operatorstr) {
            operatorStr = operatorstr;
        }
        private String getOperatorStr() {
            return operatorStr;
        }
    };

    /*データ型*/
    protected eDataType m_dataType;

    /*ポインタの数*/
    protected int m_byPointerNum;

    /*配列要素数ベクタ*/
    protected Vector<MathFactor> m_vecArrayElementFactor;

    /*-----コンストラクタ-----*/
    public SyntaxDataType(eDataType dataType, int byPointerNum) {
        m_dataType = dataType;
        m_byPointerNum = byPointerNum;
        m_vecArrayElementFactor = new Vector<MathFactor>();
    }

    /*-----配列要素追加メソッド-----*/

    //===================================================
    //addArrayElementFactor
    //	配列要素追加メソッド（整数引数オーバーロード）
    //
    //@arg
    //	unsigned long	ulElementNum	: 配列要素数
    //
    //===================================================
    public void addArrayElementFactor(long ulElementNum)
    throws MathException {
        /*要素数をMath_cnに変換*/
        Math_cn pTmpConst =
            (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                    String.valueOf(ulElementNum));

        /*ベクタに配列要素数を追加*/
        m_vecArrayElementFactor.add(pTmpConst);
    }

    //===================================================
    //addArrayElementFactor
    //	配列要素追加メソッド
    //
    //@arg
    //	MathFactor* pFactor	: 追加要素ファクタ
    //
    //===================================================
    public void addArrayElementFactor(MathFactor pFactor) {
        /*ベクタに配列要素数を追加*/
        m_vecArrayElementFactor.add(pFactor);
    }

    /*-----文字列変換メソッド-----*/

    //===================================================
    //toLegalString
    //	文字列型変換メソッド
    //
    //@return
    //	文字列型表現	: string
    //
    //@throws
    // SyntaxException
    //
    //===================================================
    public String toLegalString() {
        /*境界値チェック*/
        //throw new SyntaxException("SyntaxDataType","toLegalString",
        //			  "undefined data type set");

        /*基本のデータ型文字列*/
        String strPresentText = m_dataType.getOperatorStr();

        /*ポインタ演算子付加*/
        for (int i = 0; i < m_byPointerNum; i++) {
            strPresentText += "*";
        }

        return strPresentText;
    }

    //===================================================
    //toStringSuffix
    //	接尾文字列変換メソッド
    //
    //@return
    //	文字列型表現	: string
    //
    //===================================================
    public String toStringSuffix()
    throws MathException {
        /*結果文字列*/
        String strPresentText = "";

        //------------------------------------------
        //配列要素を文字列変換していく
        //------------------------------------------
        /*配列要素を順次追加*/
        for (MathFactor it: m_vecArrayElementFactor) {

            /*NULLは空要素として扱う*/
            if(it == null){
                strPresentText += "[]";
            }
            else {
                /*要素追加*/
                strPresentText += "[" + it.toLegalString() + "]";
            }
        }

        return strPresentText;
    }

}
