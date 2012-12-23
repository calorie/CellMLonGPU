package jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;

/**
 * TecML定義系ヘッダ
 */
public class TecMLDefinition {

    // ========================================================
    // DEFINE
    // ========================================================
    /* TecMLタグ文字列定義 */
    public static final String TECML_TAG_STR_TECML = "tecml";
    public static final String TECML_TAG_STR_INPUTVAR = "inputvar";
    public static final String TECML_TAG_STR_OUTPUTVAR = "outputvar";
    public static final String TECML_TAG_STR_VARIABLE = "variable";
    public static final String TECML_TAG_STR_FUNCTION = "function";
    public static final String TECML_TAG_STR_ARGUMENT = "argument";
    public static final String TECML_TAG_STR_MATH = "math";

    /* TecML変数型文字列定義 */
    public static final String TECML_VARTYPE_STR_DELTATIMEVAR = "deltatimevar";
    public static final String TECML_VARTYPE_STR_TIMEVAR = "timevar";
    public static final String TECML_VARTYPE_STR_DIFFVAR = "diffvar";
    public static final String TECML_VARTYPE_STR_DERIVATIVEVAR = "derivativevar";
    public static final String TECML_VARTYPE_STR_ARITHVAR = "arithvar";
    public static final String TECML_VARTYPE_STR_CONSTVAR = "constvar";

    /* TecML関数型文字列定義 */
    public static final String TECML_FUNCTYPE_STR_NONDIFF = "nondiffequ";
    public static final String TECML_FUNCTYPE_STR_DIFF = "diffequ";

    // ========================================================
    // ENUM
    // ========================================================

    // -------------------------------------TecML中で使用されるタグ種類
    public enum eTecMLTag {
        TTAG_TECML(TECML_TAG_STR_TECML), TTAG_INPUTVAR(TECML_TAG_STR_INPUTVAR), TTAG_OUTPUTVAR(
                TECML_TAG_STR_OUTPUTVAR), TTAG_VARIABLE(TECML_TAG_STR_VARIABLE), TTAG_FUNCTION(
                TECML_TAG_STR_FUNCTION), TTAG_ARGUMENT(TECML_TAG_STR_ARGUMENT), TTAG_MATH(
                TECML_TAG_STR_MATH), ;
        private final String operatorStr;

        private eTecMLTag(String operatorstr) {
            operatorStr = operatorstr;
        }

        private String getOperatorStr() {
            return operatorStr;
        }
    };

    // -------------------------------------TecML中変数で使用される型
    public enum eTecMLVarType {
        TVAR_TYPE_DELTATIMEVAR(TECML_VARTYPE_STR_DELTATIMEVAR), TVAR_TYPE_TIMEVAR(
                TECML_VARTYPE_STR_TIMEVAR), TVAR_TYPE_DIFFVAR(
                TECML_VARTYPE_STR_DIFFVAR), TVAR_TYPE_DERIVATIVEVAR(
                TECML_VARTYPE_STR_DERIVATIVEVAR), TVAR_TYPE_ARITHVAR(
                TECML_VARTYPE_STR_ARITHVAR), TVAR_TYPE_CONSTVAR(
                TECML_VARTYPE_STR_CONSTVAR), ;
        private final String operatorStr;

        private eTecMLVarType(String operatorstr) {
            operatorStr = operatorstr;
        }

        private String getOperatorStr() {
            return operatorStr;
        }
    };

    // -------------------------------------TecML中関数で使用される型
    public enum eTecMLFuncType {
        TFUNC_TYPE_NONDIFF(TECML_FUNCTYPE_STR_NONDIFF), TFUNC_TYPE_DIFF(
                TECML_FUNCTYPE_STR_DIFF), ;
        private final String operatorStr;

        private eTecMLFuncType(String operatorstr) {
            operatorStr = operatorstr;
        }

        private String getOperatorStr() {
            return operatorStr;
        }
    };

    // ========================================================
    // PROTOTYPE
    // ========================================================

    // ========================================================
    // getTecMLTagId
    // TecMLタグid取得
    //
    // @arg
    // string strTag : タグ文字列
    //
    // @return
    // TecMLタグid : eTecMLTag
    //
    // @throws
    // TecMLException
    //
    // ========================================================
    public static eTecMLTag getTecMLTagId(String strTag) throws TecMLException {
        /* 演算子と比較 */
        for (eTecMLTag t : eTecMLTag.values()) {
            if (t.getOperatorStr().equals(strTag)) {
                return t;
            }
        }

        /* 見つからなければ例外 */
        throw new TecMLException("", "getTecMLTagId", "invalid TecML tag : "
                + strTag);
    }

    // ========================================================
    // getTecMLVarType
    // TecML変数型id取得
    //
    // @arg
    // string strTag : タグ文字列
    //
    // @return
    // TecMLタグid : eTecMLVarType
    //
    // @throws
    // TecMLException
    //
    // ========================================================
    public static eTecMLVarType getTecMLVarType(String strTag)
            throws TecMLException {
        /* 演算子と比較 */
        for (eTecMLVarType t : eTecMLVarType.values()) {
            if (t.getOperatorStr().equals(strTag)) {
                return t;
            }
        }

        /* 見つからなければ例外 */
        throw new TecMLException("", "getTecMLVarType",
                "invalid TecML variable type : " + strTag);
    }

    // ========================================================
    // getTecMLFuncType
    // TecML関数型id取得
    //
    // @arg
    // string strTag : タグ文字列
    //
    // @return
    // TecMLタグid : eTecMLFuncType
    //
    // @throws
    // TecMLException
    //
    // ========================================================
    public static eTecMLFuncType getTecMLFuncType(String strTag)
            throws TecMLException {
        /* 演算子と比較 */
        for (eTecMLFuncType t : eTecMLFuncType.values()) {
            if (t.getOperatorStr().equals(strTag)) {
                return t;
            }
        }

        /* 見つからなければ例外 */
        throw new TecMLException("", "getTecMLVarType",
                "invalid TecML variable type : " + strTag);
    }

}
