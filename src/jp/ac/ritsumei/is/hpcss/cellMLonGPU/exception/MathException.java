package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * MathML例外クラス
 */
@SuppressWarnings("serial")
public class MathException extends Exception {

    /*-----コンストラクタ-----*/
    public MathException(String strClassName, String strFunctionName,
            String strMessage) {
        super(strClassName, strFunctionName, strMessage);
    }

    /*-----メッセージ取得-----*/
    public String getMessage() {
        return "MathException In [" + m_strClassName + "." + m_strFunctionName
                + "] " + m_strMessage;
    }

}
