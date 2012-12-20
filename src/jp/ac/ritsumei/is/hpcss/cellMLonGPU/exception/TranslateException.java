package jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception;

/**
 * 変換処理例外クラス
 */
@SuppressWarnings("serial")
public class TranslateException extends Exception {

	/*-----コンストラクタ-----*/
	public TranslateException(String strClassName, String strFunctionName, String strMessage) {
		super(strClassName, strFunctionName, strMessage);
	}

	/*-----メッセージ取得-----*/
	public String getMessage() {
		return "TranslateException In [" + m_strClassName + "." + m_strFunctionName + "] " + m_strMessage;
	}

}
