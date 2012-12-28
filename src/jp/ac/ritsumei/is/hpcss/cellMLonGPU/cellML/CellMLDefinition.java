package jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;

/**
 * CellML定義系ヘッダ
 */
public class CellMLDefinition {

    // ========================================================
    // DEFINE
    // ========================================================
    /* CellMLタグ定義 */
    public static final String CELLML_TAG_STR_MATH = "math";
    public static final String CELLML_TAG_STR_COMPONENT = "component";
    public static final String CELLML_TAG_STR_VARIABLE = "variable";
    public static final String CELLML_TAG_STR_CONNECTION = "connection";
    public static final String CELLML_TAG_STR_MAP_COMPONENTS = "map_components";
    public static final String CELLML_TAG_STR_MAP_VARIABLES = "map_variables";

    /* CellMLタグ属性定義 */
    public static final String CELLML_ATTR_STR_NAME = "name";
    public static final String CELLML_ATTR_STR_INITIAL_VALUE = "initial_value";
    public static final String CELLML_ATTR_STR_COMPONENT_1 = "component_1";
    public static final String CELLML_ATTR_STR_COMPONENT_2 = "component_2";
    public static final String CELLML_ATTR_STR_VARIABLE1 = "variable_1";
    public static final String CELLML_ATTR_STR_VARIABLE2 = "variable_2";

    // ========================================================
    // ENUM
    // ========================================================

    // -------------------------------------CellML中で使用されるタグ種類
    public enum eCellMLTag {
        CTAG_MATH(CELLML_TAG_STR_MATH), CTAG_COMPONENT(CELLML_TAG_STR_COMPONENT), CTAG_VARIABLE(
                CELLML_TAG_STR_VARIABLE), CTAG_CONNECTION(
                CELLML_TAG_STR_CONNECTION), CTAG_MAP_COMPONENTS(
                CELLML_TAG_STR_MAP_COMPONENTS), CTAG_MAP_VARIABLES(
                CELLML_TAG_STR_MAP_VARIABLES), ;
        private final String operatorStr;

        private eCellMLTag(String operatorstr) {
            operatorStr = operatorstr;
        }

        private String getOperatorStr() {
            return operatorStr;
        }
    };

    // -------------------------------------CellML中変数で使用される型
    public enum eCellMLAttribute {
        CATTR_NAME, CATTR_INITIAL_VALUE, CATTR_COMPONENT_1, CATTR_COMPONENT_2, CATTR_VARIABLE_1, CATTR_VARIABLE_2,
    };

    // ========================================================
    // getCellMLTagId
    // CellMLタグid取得
    //
    // @arg
    // string strTag : タグ文字列
    //
    // @return
    // CellMLタグid : eCellMLTag
    //
    // @throws
    // CellMLException
    //
    // ========================================================
    public static eCellMLTag getCellMLTagId(String strTag)
            throws CellMLException {
        /* 演算子と比較 */
        for (eCellMLTag t : eCellMLTag.values()) {
            if (t.getOperatorStr().equals(strTag)) {
                return t;
            }
        }

        /* 見つからなければ例外 */
        throw new CellMLException("", "getCellMLTagId", "invalid CellML tag : "
                + strTag);
    }

}
