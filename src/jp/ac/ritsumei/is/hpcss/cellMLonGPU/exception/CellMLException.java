package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * CellML例外クラス
 */
@SuppressWarnings("serial")
public class CellMLException extends Exception {

    /*-----コンストラクタ-----*/
    public CellMLException(String strClassName, String strFunctionName,
            String strMessage) {
        super(strClassName, strFunctionName, strMessage);
    }

    /*-----メッセージ取得-----*/
    public String getMessage() {
        return "CellMLException In [" + m_strClassName + "."
                + m_strFunctionName + "] " + m_strMessage;
    }

}
