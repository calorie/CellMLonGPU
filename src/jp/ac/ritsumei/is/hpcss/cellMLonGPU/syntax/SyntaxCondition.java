package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;

/**
 * 条件文構文クラス
 */
public class SyntaxCondition extends Syntax {

	/*条件式*/
	protected MathExpression m_pCondExpression;

	/*forループ用初期化式 for( m_pInitExpression ; m_pCondExpression ; m_pReInitExpression) */
	protected MathExpression m_pInitExpression;
	protected MathExpression m_pReInitExpression;

	/*-----コンストラクタ-----*/
	public SyntaxCondition(MathExpression pCondExpression) {
		super(eSyntaxClassification.SYN_CONDITION);
		m_pCondExpression = pCondExpression;
		m_pInitExpression = null;
		m_pReInitExpression = null;
	}

	//===================================================
	//setInitExpression
	//	forループ用初期化式設定メソッド
	//
	//@arg
	// MathExpression*	pInitExpression		: 初期化式
	// MathExpression*	pReInitExpression	: 再初期化式
	//
	//===================================================
	/*-----初期化式設定メソッド-----*/
	public void setInitExpression(MathExpression pInitExpression, MathExpression pReInitExpression) {
		/*式を設定*/
		m_pInitExpression = pInitExpression;
		m_pReInitExpression = pReInitExpression;
	}

	/*-----文字列変換メソッド-----*/

	//===================================================
	//toLegalString
	//	文字列型変換メソッド
	//
	//@return
	//	文字列型表現	: string
	//
	//===================================================
	public String toLegalString() throws MathException {
		/*条件文のみ返却*/
		return m_pCondExpression.toLegalString();
	}

	//===================================================
	//toStringFor
	//	for文用文字列型変換メソッド
	//
	//@return
	//	文字列型表現	: string
	//
	//===================================================
	public String toStringFor() throws MathException {
		/*結果文字列*/
		String strPresentText = "";

		/*初期化式追加*/
		if (m_pInitExpression != null) {
			strPresentText += m_pInitExpression.toLegalString();
		}

		strPresentText += ";";

		/*条件式追加*/
		if (m_pCondExpression != null) {
			strPresentText += m_pCondExpression.toLegalString();
		}

		strPresentText += ";";

		/*再初期化式追加*/
		if (m_pReInitExpression != null) {
			strPresentText += m_pReInitExpression.toLegalString();
		}

		return strPresentText;
	}

}
