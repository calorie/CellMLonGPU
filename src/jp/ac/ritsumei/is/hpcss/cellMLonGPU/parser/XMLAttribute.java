package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;

/**
 * XML属性格納クラス
 */
public class XMLAttribute {

    /* 属性名と値文字列 */
    Vector<String> m_vecAttribute;
    Vector<String> m_vecValue;

    /*-----コンストラクタ-----*/
    public XMLAttribute() {
        m_vecAttribute = new Vector<String>();
        m_vecValue = new Vector<String>();
    }

    // ========================================================
    // addAttribute
    // 属性格納メソッド
    //
    // @arg
    // string strAttribute : 格納する属性
    // string strValue : 格納する値
    //
    // ========================================================
    /*-----属性格納メソッド-----*/
    public void addAttribute(String strAttribute, String strValue) {
        m_vecAttribute.add(strAttribute);
        m_vecValue.add(strValue);
    }

    // ========================================================
    // setAttribute
    // 属性取得メソッド
    //
    // @arg
    // string strAttribute : 取得したい属性
    //
    // @return
    // 引数に指定した属性の値 : string
    //
    // ========================================================
    /*-----属性取得メソッド-----*/
    public String getValue(String strAttribute) throws XMLException {
        /* 目的の属性を捜索 */
        int index = m_vecAttribute.indexOf(strAttribute);
        if (index != -1) {
            return m_vecValue.get(index);
        }

        throw new XMLException("XMLAttribute", "getValue",
                "attribute not found");
    }

}
