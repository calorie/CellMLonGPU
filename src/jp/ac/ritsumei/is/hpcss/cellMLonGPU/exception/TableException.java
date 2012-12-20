package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * Table関連例外クラス
 */
@SuppressWarnings("serial")
public class TableException extends Exception {

	/*-----コンストラクタ-----*/
	public TableException(String strClassName, String strFunctionName, String strMessage) {
		super(strClassName, strFunctionName, strMessage);
	}

	/*-----メッセージ取得-----*/
	public String getMessage() {
		return "TableException In [" + m_strClassName + "." + m_strFunctionName + "] " + m_strMessage;
	}

}
