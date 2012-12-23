package jp.ac.ritsumei.is.hpcss.cellMLonGPU.relML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;

/**
 * XMLパーサ基底クラス
 */
public class RelMLDefinition {

    // ========================================================
    // DEFINE
    // ========================================================
    /* TecMLタグ文字列定義 */
    public static final String RELML_TAG_STR_RELML = "relml";
    public static final String RELML_TAG_STR_TECML = "tecml";
    public static final String RELML_TAG_STR_CELLML = "cellml";
    public static final String RELML_TAG_STR_VARIABLE = "variable";
    public static final String RELML_TAG_STR_DIFFEQU = "diffequ";
    public static final String RELML_TAG_STR_COMPONENT = "component";
    public static final String RELML_TAG_STR_MATH = "math";

    /* RelML変数型文字列定義 */
    public static final String RELML_VARTYPE_STR_TIMEVAR = "timevar";
    public static final String RELML_VARTYPE_STR_DIFFVAR = "diffvar";
    public static final String RELML_VARTYPE_STR_ARITHVAR = "arithvar";
    public static final String RELML_VARTYPE_STR_CONSTVAR = "constvar";

    // ========================================================
    // ENUM
    // ========================================================

    // -------------------------------------TecML中で使用されるタグ種類
    public enum eRelMLTag {
        RTAG_RELML(RELML_TAG_STR_RELML), RTAG_TECML(RELML_TAG_STR_TECML), RTAG_CELLML(
                RELML_TAG_STR_CELLML), RTAG_VARIABLE(RELML_TAG_STR_VARIABLE), RTAG_DIFFEQU(
                RELML_TAG_STR_DIFFEQU), RTAG_COMPONENT(RELML_TAG_STR_COMPONENT), RTAG_MATH(
                RELML_TAG_STR_MATH), ;
        private final String operatorStr;

        private eRelMLTag(String operatorstr) {
            operatorStr = operatorstr;
        }

        private String getOperatorStr() {
            return operatorStr;
        }
    };

    // -------------------------------------TecML中変数で使用される型
    public enum eRelMLVarType {
        RVAR_TYPE_TIMEVAR(RELML_VARTYPE_STR_TIMEVAR), RVAR_TYPE_DIFFVAR(
                RELML_VARTYPE_STR_DIFFVAR), RVAR_TYPE_ARITHVAR(
                RELML_VARTYPE_STR_ARITHVAR), RVAR_TYPE_CONSTVAR(
                RELML_VARTYPE_STR_CONSTVAR), ;
        private final String operatorStr;

        private eRelMLVarType(String operatorstr) {
            operatorStr = operatorstr;
        }

        private String getOperatorStr() {
            return operatorStr;
        }
    };

    // ========================================================
    // getRelMLTagId
    // RelMLタグid取得
    //
    // @arg
    // string strTag : タグ文字列
    //
    // @return
    // RelMLタグid : eRelMLTag
    //
    // @throws
    // RelMLException
    //
    // ========================================================
    public static eRelMLTag getRelMLTagId(String strTag) throws RelMLException {
        /* 演算子と比較 */
        for (eRelMLTag t : eRelMLTag.values()) {
            if (t.getOperatorStr().equals(strTag)) {
                return t;
            }
        }

        /* 見つからなければ例外 */
        throw new RelMLException("", "getRelMLTagId", "invalid RelML tag : "
                + strTag);
    }

    // ========================================================
    // getRelMLVarType
    // RelML変数型id取得
    //
    // @arg
    // string strType : タイプ文字列
    //
    // @return
    // RelML変数型タイプid : eRelMLVarType
    //
    // @throws
    // RelMLException
    //
    // ========================================================
    public static eRelMLVarType getRelMLVarType(String strType)
            throws RelMLException {
        /* 演算子と比較 */
        for (eRelMLVarType t : eRelMLVarType.values()) {
            if (t.getOperatorStr().equals(strType)) {
                return t;
            }
        }

        /* 見つからなければ例外 */
        throw new RelMLException("", "getRelMLVarType",
                "invalid RelML variable type : " + strType);
    }

}
