package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathSepType;

/**
 * MathML定数被演算子cnクラス
 */
public class Math_cn extends MathOperand {

	eMathSepType m_sepType;
	String m_strSepValue;

	/*-----コンストラクタ-----*/
	public Math_cn(String strValueString) {
		super(strValueString, eMathOperand.MOPD_CN);
		/*初期値設定*/
		try {
			if (strValueString.startsWith("(double)")) {
				m_dValue = Double.parseDouble(strValueString.substring(8));
			} else {
				m_dValue = Double.parseDouble(strValueString);
			}
		} catch (NumberFormatException e) {
			m_dValue = 0;
		}
		m_bInitFlag = true;
	}
	public Math_cn(String strValueString, eMathSepType sepType, String strSepValue) {
		super(strValueString, eMathOperand.MOPD_CN);
		m_sepType = sepType;
		m_strSepValue = strSepValue;
		/*初期値設定*/
		try {
			if (strValueString.startsWith("(double)")) {
				m_dValue = Double.parseDouble(strValueString.substring(8));
			} else {
				m_dValue = Double.parseDouble(strValueString);
			}
		} catch (NumberFormatException e) {
			m_dValue = 0;
		}
		m_bInitFlag = true;
	}

	/*-----値格納メソッド-----*/
	public void setValue(double dValue) throws MathException {
		throw new MathException("Math_cn","setValue",
				"can't set value on constant number");
	}

	/*-----数式複製メソッド-----*/
	public MathFactor createCopy() throws MathException {
		return MathFactory.createOperand(m_operandKind,m_strPresentText,
				m_sepType,m_strSepValue);
	}

	/*-----文字列変換メソッド-----*/
	public String toLegalString() throws MathException {

		/*sepが利用される場合*/
		if(m_strSepValue != null && m_strSepValue.length()!=0){

			/*sepタイプに応じた文字列化*/
			switch(m_sepType){

				//------------------------指数表記
				case MSEP_E_NOTATION:
					return m_strPresentText + "E" + m_strSepValue;

				//------------------------定義されないタイプ
				default:
					throw new MathException("Math_cn","toLegalString",
							"can't set value on constant number");
			}
		}

		/*sepのない場合*/
		else{
			return m_strPresentText;
		}
	}

}
