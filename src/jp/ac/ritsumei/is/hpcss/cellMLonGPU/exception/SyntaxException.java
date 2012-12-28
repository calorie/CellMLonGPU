package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * 構文例外クラス
 */
@SuppressWarnings("serial")
public class SyntaxException extends Exception {

    /*-----コンストラクタ-----*/
    public SyntaxException(String strClassName, String strFunctionName,
            String strMessage) {
        super(strClassName, strFunctionName, strMessage);
    }

    /*-----メッセージ取得-----*/
    public String getMessage() {
        return "SyntaxException In [" + m_strClassName + "."
                + m_strFunctionName + "] " + m_strMessage;
    }

}
