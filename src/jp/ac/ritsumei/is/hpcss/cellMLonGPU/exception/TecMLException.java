package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * TecML例外クラス
 */
@SuppressWarnings("serial")
public class TecMLException extends Exception {

	/*-----コンストラクタ-----*/
	public TecMLException(String strClassName, String strFunctionName, String strMessage) {
		super(strClassName, strFunctionName, strMessage);
	}

	/*-----メッセージ取得-----*/
	public String getMessage() {
		return "TecMLException In [" + m_strClassName + "." + m_strFunctionName + "] " + m_strMessage;
	}

}
