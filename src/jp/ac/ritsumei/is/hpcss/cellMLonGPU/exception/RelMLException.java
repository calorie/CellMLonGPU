package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * RelML例外クラス
 */
@SuppressWarnings("serial")
public class RelMLException extends Exception {

    /*-----コンストラクタ-----*/
    public RelMLException(String strClassName, String strFunctionName,
            String strMessage) {
        super(strClassName, strFunctionName, strMessage);
    }

    /*-----メッセージ取得-----*/
    public String getMessage() {
        return "RelMLException In [" + m_strClassName + "." + m_strFunctionName
                + "] " + m_strMessage;
    }

}
