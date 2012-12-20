package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;

/**
 * CUDA初期化カーネル構文生成クラス
 * CudaProgramGeneratorクラスから初期化カーネル生成部を切り離したクラス
 */
public class CudaInitKernelGenerator extends CudaProgramGenerator {

	public static final String CUPROG_INIT_KERNEL_NAME = "__init_kernel";

	/*-----コンストラクタ-----*/
	public CudaInitKernelGenerator(CellMLAnalyzer pCellMLAnalyzer,
			RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
	throws MathException {
		super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
	}

	//========================================================
	//getSyntaxInitKernel
	// 初期化カーネルを生成し，返す
	//
	//@return
	// 関数構文インスタンス	: SyntaxFunction*
	//
	//@throws
	// TranslateException
	//
	//========================================================
	/*-----プログラム構文取得メソッド-----*/
	public SyntaxFunction getSyntaxInitKernel()
	throws MathException {
		//----------------------------------------------
		//カーネル生成
		//----------------------------------------------
		/*カーネル生成*/
		SyntaxFunction  pSynInitKernel = this.createKernel(CUPROG_INIT_KERNEL_NAME);

		//----------------------------------------------
		//宣言の追加
		//----------------------------------------------
		/*入力出力変数の追加*/
		{
			/*型構文生成*/
			SyntaxDataType pSynTypePDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

			/*宣言用変数の生成*/
			Math_ci pDecVar =
				(Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
								   m_pTecMLAnalyzer.getM_pInputVar().toLegalString());

			/*宣言の生成*/
			SyntaxDeclaration pSynInputVarDec =
				new SyntaxDeclaration(pSynTypePDouble, pDecVar);

			/*カーネル引数宣言の追加*/
			pSynInitKernel.addParam(pSynInputVarDec);
		}


		//----------------------------------------------
		//初期化文追加
		//----------------------------------------------



		/*生成したカーネルインスタンスを返す*/
		return pSynInitKernel;
	}

}
