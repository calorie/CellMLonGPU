package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathSepType;

/**
 * MathML解析クラス
 */
public class MathMLAnalyzer extends XMLAnalyzer {

	/*数式ベクタ*/
	Vector<MathExpression> m_vecMathExpression;

	/*現在解析中の数式*/
	MathExpression m_pCurMathExpression;

	/*sep登録用一時変数*/
	boolean m_bUseSep;
	eMathSepType m_sepType;
	String m_strSepUsedValue;

	/*登録待ちオペランド種別*/
	protected eMathOperand m_NextOperandKind;

	/*-----コンストラクタ-----*/
	public MathMLAnalyzer() {
		m_pCurMathExpression = null;
		m_bUseSep = false;
		m_vecMathExpression = new Vector<MathExpression>();
		m_strSepUsedValue = "";
	}

	//========================================================
	//addNewExpression
	// 数式追加メソッド
	//
	//========================================================
	/*-----数式操作メソッド-----*/
	public void addNewExpression() {
		/*数式追加*/
		MathExpression pNewExpression = new MathExpression();
		m_vecMathExpression.add(pNewExpression);

		/*現在の数式を変更*/
		m_pCurMathExpression = pNewExpression;
	}

	//========================================================
	//findTagStart
	// 開始タグ解析メソッド
	//
	//@arg
	// string			strTag		: 開始タグ名
	// XMLAttribute*	pXMLAttr	: 属性クラスインスタンス
	//
	//@throws
	// MathException
	//
	//========================================================
	/*-----解析メソッド-----*/
	public void findTagStart(String strTag, XMLAttribute pXMLAttr)
	throws MathException, XMLException, RelMLException, CellMLException, TecMLException {
		eMathMLClassification tagKind;

		/*タグの種類を特定*/
		try {
			tagKind = MathMLDefinition.specifyMathMLClassification(strTag);
		}
		catch (MathException e) {
			System.err.println(e.getMessage());
			throw new MathException("MathMLAnalyzer","findTagStart",
					"can't specify MathML tag [" + strTag + "]");
		}

		/*種類ごとの処理*/
		switch(tagKind){

			//-----------------------------------演算子の解析
		case MML_OPERATOR:

			/*新しい計算式*/
			if(m_pCurMathExpression==null || !m_pCurMathExpression.isConstructing()){
				this.addNewExpression();
			}

			/*演算子を追加*/
			m_pCurMathExpression.addOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId(strTag)));

			break;

			//-----------------------------------被演算子の解析
		case MML_OPERAND:

			/*sep typeの処理*/
			try {
				/*typeを取得*/
				String strSepType =
					pXMLAttr.getValue(MathMLDefinition.MATHML_ATTR_STR_TYPE);

				/*type idの取得*/
				m_sepType = MathMLDefinition.getMathSepTypeId(strSepType);
				m_bUseSep = true;
			}
			catch (Exception e){
				/*通常はtypeが無い*/
			}

			/*この後に来るオペランドの種類を記録*/
			m_NextOperandKind = MathMLDefinition.getMathOperandId(strTag);
			break;

			//-----------------------------------補助的要素の解析
		case MML_OPTIONAL:

			/*種類ごとの処理*/
			switch(MathMLDefinition.getMathOptionalId()){

				//-------------------------------sepを利用する
			case MOPT_SEP:
				break;

			default:
				/*例外処理*/
				throw new MathException("MathMLAnalyzer","findTagStart",
						"unknown MathML tag found [" + strTag + "]");
			}
			break;

			//-----------------------------------未定義の種別
		default:
			/*例外処理*/
			throw new MathException("MathMLAnalyzer","findTagStart",
					"unknown MathML tag found [" + strTag + "]");
		}
	}

	//========================================================
	//findTagEnd
	// 終了タグ解析メソッド
	//
	//@arg
	// string	strTag	: 終了タグ名
	//
	//@throws
	// MathException
	//
	//========================================================
	public void findTagEnd(String strTag)
	throws MathException, RelMLException, CellMLException {
		eMathMLClassification tagKind;

		/*タグの種類を特定*/
		try{
			tagKind = MathMLDefinition.specifyMathMLClassification(strTag);
		}
		catch(MathException e){
			System.err.println(e.getMessage());
			throw new MathException("MathMLAnalyzer","findTagEnd",
					"can't specify MathML tag [" + strTag + "]");
		}

		/*種類ごとの処理*/
		switch(tagKind){

			//-----------------------------------演算子の解析
		case MML_OPERATOR:

			/*オペレータの終端タグ*/
			m_pCurMathExpression.breakOperator(
					MathFactory.createOperator(MathMLDefinition.getMathOperatorId()));

			break;

			//-----------------------------------被演算子の解析
		case MML_OPERAND:
			break;

			//-----------------------------------被演算子の解析
		case MML_OPTIONAL:
			break;

			//-----------------------------------未定義の種別
		default:
			/*例外処理*/
			throw new MathException("MathMLAnalyzer","findTagEnd",
					"unknown MathML tag found [" + strTag + "]");
		}
	}

	//========================================================
	//findText
	// 文字列解析メソッド
	//
	//@arg
	// string	strText	: 切り出された文字列
	//
	//@throws
	// MathException
	//
	//========================================================
	public void findText(String strText)
	throws MathException, CellMLException, TableException {
//		System.out.println("findText(" +strText+ ")");
		/*オペランドの追加*/
		switch(m_NextOperandKind){

			//-----------------------------------変数の登録
		case MOPD_CI:
			m_pCurMathExpression.addVariable(MathFactory.createOperand(eMathOperand.MOPD_CI,strText));
			break;

			//-----------------------------------定数の登録
		case MOPD_CN:

			/*sepを利用する場合*/
			if(m_bUseSep){

				/*sep第1定数*/
				if(m_strSepUsedValue.length()==0){
					m_strSepUsedValue = strText;
				}
				/*sep第2定数*/
				else{
					/*sep定数の登録*/
					m_pCurMathExpression.addOperand(MathFactory.createOperand(eMathOperand.MOPD_CN,
							"(double)"+m_strSepUsedValue,m_sepType,strText));

					/*sep用一時変数の初期化*/
					m_bUseSep = false;
					m_strSepUsedValue = "";
				}
			}
			/*通常の定数の登録*/
			else{
				m_pCurMathExpression.addOperand(MathFactory.createOperand(eMathOperand.MOPD_CN,
						"(double)"+strText));
			}
			break;

			//-----------------------------------未定義の種別
		default:
			/*例外処理*/
			throw new MathException("MathMLAnalyzer","findText",
					"invalid MathML operand kind");
		}
	}

	//========================================================
	//getExpression
	// 数式取得メソッド
	//
	//@arg
	// int	dExpressionId	: 数式id
	//
	//@return
	// 引数指定idの数式へのポインタ	: MathExpression*
	//
	//========================================================
	/*-----数式取得メソッド-----*/
	public MathExpression getExpression(int dExpressionId) {
		return m_vecMathExpression.get(dExpressionId);
	}

	//========================================================
	//getExpressionCount
	// 数式の数を取得
	//
	//@return
	// 解析した数式の数	: int
	//
	//========================================================
	public int getExpressionCount() {
		return m_vecMathExpression.size();
	}

	//========================================================
	//clearExpression
	// 数式クリアメソッド
	//
	//========================================================
	/*-----数式クリアメソッド-----*/
	public void clearExpression() {
		m_vecMathExpression.clear();
	}

	//========================================================
	//printExpressions
	// 数式標準出力メソッド
	//
	//========================================================
	/*-----数式標準出力メソッド-----*/
	public void printExpressions() throws MathException {
		/*すべての数式を出力*/
		for (MathExpression it: m_vecMathExpression) {

			/*数式標準出力*/
			System.out.println(it.toLegalString());

			//変数一覧表示（デバッグ用）
			int nVariableCount = it.getVariableCount();
			for (int j = 0; j < nVariableCount; j++) {
				System.out.println(it.getVariable(j).toLegalString());
			}

		}
	}

}
