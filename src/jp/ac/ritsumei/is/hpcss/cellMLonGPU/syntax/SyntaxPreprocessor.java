package jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;

/**
 * プリプロセッサ構文クラス
 */
public class SyntaxPreprocessor extends Syntax {

	//========================================================
	//ENUM
	//========================================================

	//------------------------------------------プリプロセッサ分類列挙
	public enum ePreprocessorKind {
		PP_INCLUDE_ABS,		//include<> 絶対パスで探すほう
		PP_INCLUDE_REL,		//include"" 相対パスで探すほう
		PP_DEFINE,
	};

	/*プリプロセッサ種類*/
	protected ePreprocessorKind m_preprocessorKind;

	/*内容文字列*/
	protected String m_strContent;

	/*-----コンストラクタ-----*/
	public SyntaxPreprocessor(ePreprocessorKind preprocessorKind, String strContent) {
		super(eSyntaxClassification.SYN_PREPROCESSOR);
		m_preprocessorKind = preprocessorKind;
		m_strContent = strContent;
	}

	//===================================================
	//toLegalString
	//	文字列型変換メソッド
	//
	//@return
	//	文字列型表現	: string
	//
	//@throws
	// SyntaxPreprocessor
	//
	//===================================================
	/*-----文字列変換メソッド-----*/
	public String toLegalString()
	throws SyntaxException {
		/*プリプロセッサの種類ごとの処理*/
		switch (m_preprocessorKind) {

			/*include<絶対パス>*/
		case PP_INCLUDE_ABS:
			return "#include<" + m_strContent + ">";

			/*include"相対パス"*/
		case PP_INCLUDE_REL:
			return "#include\"" + m_strContent + "\"";

			/*define*/
		case PP_DEFINE:
			return "#define " + m_strContent;

			/*予期しない種類*/
		default:
			throw new SyntaxException("SyntaxPreprocessor","toLegalString",
						  "not declared preprocessor kind used");
		}
	}

	//===================================================
	//matches
	//	インスタンス照合メソッド
	//
	//@arg
	// SyntaxPreprocessor*	pPreprocessor	: 比較対象インスタンス
	//
	//@return
	//	一致判定	: bool
	//
	//===================================================
	/*-----インスタンス照合メソッド-----*/
	public boolean matches(SyntaxPreprocessor pPreprocessor) {
		/*プリプロセッサの種類と内容の一致を確認*/
		if (m_preprocessorKind == pPreprocessor.m_preprocessorKind &&
		    m_strContent == pPreprocessor.m_strContent ) {
			return true;
		}

		return false;
	}

}
