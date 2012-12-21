package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathSepType;

/**
 * Math要素インスタンス生成クラス
 */
public class MathFactory {

	/*-----コンストラクタ-----*/
	public MathFactory() {
	}

	/*-----生成系メソッド-----*/

	//========================================================
	//createOperator
	// タグ文字列よりオペレータインスタンスに変換
	//
	//@arg
	// eMathOperator	operatorId	: 演算子列挙型id
	//
	//@return
	// オペレータインスタンス	: MathOperator
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperator createOperator(eMathOperator operatorId)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operatorId) {

		case MOP_APPLY:
			return new Math_apply();
		case MOP_DIVIDE:
			return new Math_divide();
		case MOP_EQ:
			return new Math_eq();
		case MOP_NEQ:
			return new Math_neq();
		case MOP_MINUS:
			return new Math_minus();
		case MOP_PLUS:
			return new Math_plus();
		case MOP_TIMES:
			return new Math_times();
		case MOP_EXP:
			return new Math_exp();
		case MOP_LOG:
			return new Math_log();
		case MOP_POWER:
			return new Math_power();
		case MOP_SIN:
			return new Math_sin();
		case MOP_COS:
			return new Math_cos();
		case MOP_TAN:
			return new Math_tan();
		case MOP_DIFF:
			return new Math_diff();
		case MOP_BVAR:
			return new Math_bvar();
		case MOP_LT:
			return new Math_lt();
		case MOP_LEQ:
			return new Math_leq();
		case MOP_GT:
			return new Math_gt();
		case MOP_GEQ:
			return new Math_geq();
		case MOP_AND:
			return new Math_and();
		case MOP_OR:
			return new Math_or();
		case MOP_INC:
			return new Math_inc();
		case MOP_DEC:
			return new Math_dec();
		case MOP_ASSIGN:
			return new Math_assign();
		case MOP_FN:
			return new Math_fn();
		case MOP_LN:
			return new Math_ln();
		case MOP_ROOT:
			return new Math_root();
		case MOP_FLOOR:
			return new Math_floor();
		case MOP_CEIL:
			return new Math_ceil();
		case MOP_PIECEWISE:
			return new Math_piecewise();
		case MOP_PIECE:
			return new Math_piece();
		case MOP_OTHERWISE:
			return new Math_otherwise();

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperator",
						"Invalid Operator id");
		}
	}

	//========================================================
	//createOperand
	// タグ文字列よりオペランドインスタンスに変換
	//
	//@arg
	// eMathOperand	operandId	: オペランド列挙型id
	// string		strName		: オペランドの名前
	// double		dInitValue	: 初期値
	//
	//@return
	// オペランドインスタンス	: MathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperand createOperand(eMathOperand operandId,
			String strName, double dInitValue)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operandId) {

		case MOPD_CI:
			return new Math_ci(strName,dInitValue);
		case MOPD_CN:
			return new Math_cn(strName);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperand",
						"Invalid Operand id");
		}
	}

	//========================================================
	//createOperand
	// タグ文字列よりオペランドインスタンスに変換
	// (オーバーロード : 初期値無し)
	//
	//@arg
	// eMathOperand	operandId	: オペランド列挙型id
	// string		strName		: オペランドの名前
	//
	//@return
	// オペランドインスタンス	: MathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperand createOperand(eMathOperand operandId, String strName)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operandId) {

		case MOPD_CI:
			return new Math_ci(strName);
		case MOPD_CN:
			return new Math_cn(strName);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperand",
						"Invalid Operand id");
		}
	}

	//========================================================
	//createOperand
	// タグ文字列よりオペランドインスタンスに変換
	// (オーバーロード : sepタイプ指定)
	//
	//@arg
	// eMathOperand	operandId	: オペランド列挙型id
	// string		strName		: オペランドの名前
	// eMathSepType	sepType		: sepタイプ
	// string		strSepValue	: sepに使う値
	//
	//@return
	// オペランドインスタンス	: MathOperand
	//
	//@throws
	// MathException
	//
	//========================================================
	public static MathOperand createOperand(eMathOperand operandId,
			String strName, eMathSepType sepType, String strSepValue)
	throws MathException {
		/*対応するインスタンスを返す*/
		switch (operandId) {

		case MOPD_CI:
			return new Math_ci(strName);
		case MOPD_CN:
			return new Math_cn(strName,sepType,strSepValue);

			/*例外*/
		default:
			throw new MathException("MathFactory","createOperand",
						"Invalid Operand id");
		}
	}

}
