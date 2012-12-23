package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * XML例外クラス
 */
@SuppressWarnings("serial")
public class XMLException extends Exception {

    /*-----コンストラクタ-----*/
    public XMLException(String strClassName, String strFunctionName,
            String strMessage) {
        super(strClassName, strFunctionName, strMessage);
    }

    /*-----メッセージ取得-----*/
    public String getMessage() {
        return "XMLException In [" + m_strClassName + "." + m_strFunctionName
                + "] " + m_strMessage;
    }

}
