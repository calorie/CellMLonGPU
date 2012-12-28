package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * 構文基底クラス
 */
@SuppressWarnings("serial")
public class Exception extends java.lang.Exception {

    /* 例外情報文字列 */
    protected String m_strClassName;
    protected String m_strFunctionName;
    protected String m_strMessage;

    /*-----コンストラクタ-----*/
    public Exception(String strClassName, String strFunctionName,
            String strMessage) {
        m_strClassName = strClassName;
        m_strFunctionName = strFunctionName;
        m_strMessage = strMessage;
    }

    /*-----メッセージ取得-----*/
    public String getMessage() {
        return "Exception In [" + m_strClassName + "." + m_strFunctionName
                + "] " + m_strMessage;
    }

}
