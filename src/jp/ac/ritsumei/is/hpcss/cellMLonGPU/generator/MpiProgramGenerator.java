package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_fn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * MPIプログラム構文生成クラス
 */
public class MpiProgramGenerator extends ProgramGenerator {

    //========================================================
    //DEFINE
    //========================================================
    private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
    private static final String COMPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";
    private static final String MASTER_PROCESS = "MASTER";
    private static final int MASTER_RANK = 0;
    private static final String MPI_FUNC_STR_MPIINIT = "MPI_Init";
    private static final String MPI_FUNC_STR_MPICOMMSIZE = "MPI_Comm_size";
    private static final String MPI_FUNC_STR_COMMRANK = "MPI_Comm_rank";
    private static final String MPI_FUNC_STR_FINALIZE = "MPI_Finalize";
    private static final String MPI_FUNC_STR_SEND = "MPI_Send";
    private static final String MPI_FUNC_STR_RECV = "MPI_Recv";
    private static final String VAR_STR_ARGC = "argc";
    private static final String VAR_STR_ARGV = "argv";
    private static final String MPI_COMM_WORLD = "MPI_COMM_WORLD";


    /*共通変数*/
    protected Math_ci m_pDefinedDataSizeVar;		//データ数として#defineされる定数
    protected Math_ci m_pDefinedDataSizeVar2;
    protected Math_ci m_pNodeDataNum;

    /*-----コンストラクタ-----*/
    public MpiProgramGenerator(CellMLAnalyzer pCellMLAnalyzer,
            RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
    throws MathException {
        super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
        m_pDefinedDataSizeVar = null;
        m_pDefinedDataSizeVar2 = null;
        initialize();
    }

    //========================================================
    //getSyntaxProgram
    // プログラム構文を生成し，返す
    //
    //@return
    // プログラム構文インスタンス	: SyntaxProgram*
    //
    //@throws
    // TranslateException
    //
    //========================================================
    /*-----プログラム構文取得メソッド-----*/
    public SyntaxProgram getSyntaxProgram()
    throws MathException, CellMLException, RelMLException, TranslateException, SyntaxException {

        //----------------------------------------------
        //プログラム生成のための前処理
        //----------------------------------------------
        /*CellMLにRelMLを適用*/
        m_pCellMLAnalyzer.applyRelML(m_pRelMLAnalyzer);

        //----------------------------------------------
        //プログラム構文の生成
        //----------------------------------------------
        /*プログラム構文生成*/
        SyntaxProgram pSynProgram = this.createNewProgram();

        /*プリプロセッサ構文生成・追加*/
        SyntaxPreprocessor pSynInclude1 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdio.h");
        SyntaxPreprocessor pSynInclude2 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "stdlib.h");
        SyntaxPreprocessor pSynInclude3 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "math.h");
        SyntaxPreprocessor pSynInclude4 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_REL, "mpi.h");
        pSynProgram.addPreprocessor(pSynInclude1);
        pSynProgram.addPreprocessor(pSynInclude2);
        pSynProgram.addPreprocessor(pSynInclude3);
        pSynProgram.addPreprocessor(pSynInclude4);

        /*データ数定義defineの追加*/
        String strElementNum = String.valueOf(m_unElementNum);
        SyntaxPreprocessor pSynDefine1 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
                           m_pDefinedDataSizeVar.toLegalString() + " "
                           + strElementNum);
        pSynProgram.addPreprocessor(pSynDefine1);

        strElementNum = String.valueOf(MASTER_RANK);
        SyntaxPreprocessor pSynDefine2 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
                           m_pDefinedDataSizeVar2.toLegalString() + " "
                           + strElementNum);
        pSynProgram.addPreprocessor(pSynDefine2);

        if ( m_isTestGenerate ) {
            TestFuncGenerator pTestFuncGenerator =
                    new TestFuncGenerator(m_pCellMLAnalyzer, m_pRelMLAnalyzer, m_pTecMLAnalyzer, pSynProgram);
           pTestFuncGenerator.setTestInitFunc(pTestFuncGenerator.getSyntaxTestInitFunction());
           pTestFuncGenerator.setTestFunc(pTestFuncGenerator.getSyntaxTestFunction());
        }

        //----------------------------------------------
        //メイン関数生成
        //----------------------------------------------
        /*メイン関数の生成*/
        MpiMainFuncGenerator pMpiMainFuncGenerator =
            new MpiMainFuncGenerator(m_pCellMLAnalyzer, m_pRelMLAnalyzer, m_pTecMLAnalyzer);
        pMpiMainFuncGenerator.setElementNum(m_unElementNum);
        pMpiMainFuncGenerator.setTimeParam(m_dStartTime, m_dEndTime, m_dDeltaTime);
        SyntaxFunction pSynMainFunc = pMpiMainFuncGenerator.getSyntaxMainFunction();
        pSynProgram.addFunction(pSynMainFunc);

        /*プログラム構文を返す*/
        return pSynProgram;
    }

    //========================================================
    //initialize
    // 初期化メソッド
    //
    //========================================================
    /*-----初期化・終了処理メソッド-----*/
    protected void initialize() throws MathException {
        m_pDefinedDataSizeVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                               COMPROG_DEFINE_DATANUM_NAME);
        m_pDefinedDataSizeVar2 =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   MASTER_PROCESS);
    }

    //========================================================
    //createExpressions
    // 計算式部を生成し，ベクタを返す
    //
    //@return
    // 計算式ベクタ
    //
    //@throws
    // TranslateException
    //
    //========================================================
    /*-----計算式生成メソッド-----*/
    protected Vector<SyntaxExpression> createExpressions(Math_ci pMpiDataNumIndexVar)
    throws TranslateException, MathException {
        //---------------------------------------------
        //式生成のための前処理
        //---------------------------------------------
        /*ベクタを初期化*/
        Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();
        m_pNodeDataNum = pMpiDataNumIndexVar;

        //---------------------------------------------
        //式の追加
        //---------------------------------------------
        /*数式数を取得*/
        int nExpressionNum = m_pTecMLAnalyzer.getExpressionCount();

        for (int i = 0; i < nExpressionNum; i++) {

            /*数式の複製を取得*/
            MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);

            /*左辺式・右辺式取得*/
            MathExpression pLeftExp = pMathExp.getLeftExpression();
            MathExpression pRightExp = pMathExp.getRightExpression();

            if (pLeftExp == null || pRightExp == null) {
                throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
                                 "failed to parse expression");
            }

            /*左辺変数取得*/
            MathOperand pLeftVar = (MathOperand)pLeftExp.getFirstVariable();

            //-------------------------------------------
            //左辺式ごとに数式の追加
            //-------------------------------------------
            /*微係数変数*/
            if (m_pTecMLAnalyzer.isDerivativeVar(pLeftVar)) {

                /*微分式の数を取得*/
                int nDiffExpNum = m_pCellMLAnalyzer.getM_vecDiffExpression().size();

                /*数式の出力*/
                for (int j = 0; j < nDiffExpNum; j++) {

                    /*代入文の形成*/
                    Math_assign pMathAssign =
                        (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
                    pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
                    pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());

                    /*新たな計算式を生成*/
                    MathExpression pNewExp = new MathExpression(pMathAssign);

                    /*TecML変数に添え字を付加*/
                    this.addIndexToTecMLVariables(pNewExp, j);

                    /*微分式インスタンスのコピー取得*/
                    MathExpression pDiffExpression =
                        m_pCellMLAnalyzer.getM_vecDiffExpression().get(j).createCopy();

                    /*微分関数の展開*/
                    this.expandDiffFunction(pNewExp, pDiffExpression);

                    /*数式ベクタに追加*/
                    SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
                    vecExpressions.add(pSyntaxExp);
                }

            }

            /*微分変数*/
            else if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {

                /*微分式の数を取得*/
                int nDiffVarNum = m_pCellMLAnalyzer.getM_vecDiffVar().size();

                /*数式の出力*/
                for (int j = 0; j < nDiffVarNum; j++) {

                    /*代入文の形成*/
                    Math_assign pMathAssign =
                        (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
                    pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
                    pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());

                    /*新たな計算式を生成*/
                    MathExpression pNewExp = new MathExpression(pMathAssign);

                    /*添え字の付加*/
                    this.addIndexToTecMLVariables(pNewExp, j);

                    /*数式ベクタに追加*/
                    SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
                    vecExpressions.add(pSyntaxExp);
                }
            }

            /*通常変数*/

            else if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
                /*微分式の数を取得*/
                int nNonDiffExpNum = m_pCellMLAnalyzer.getM_vecNonDiffExpression().size();

                /*数式の出力*/
                for (int j = 0; j < nNonDiffExpNum; j++) {

                    /*代入文の形成*/
                    Math_assign pMathAssign =
                        (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
                    pMathAssign.addFactor(pLeftExp.createCopy().getRootFactor());
                    pMathAssign.addFactor(pRightExp.createCopy().getRootFactor());

                    /*新たな計算式を生成*/
                    MathExpression pNewExp = new MathExpression(pMathAssign);

                    /*TecML変数に添え字を付加*/
                    this.addIndexToTecMLVariables(pNewExp, j);

                    /*微分式インスタンスのコピー取得*/
                    MathExpression pNonDiffExpression =
                        m_pCellMLAnalyzer.getM_vecNonDiffExpression().get(j).createCopy();

                    /*微分関数の展開*/
                    this.expandNonDiffFunction(pNewExp, pNonDiffExpression);

                    /*数式ベクタに追加*/
                    SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
                    vecExpressions.add(pSyntaxExp);
                }
            }


            /*定数変数*/
            else if (m_pTecMLAnalyzer.isConstVar(pLeftVar)) {
            }

        }

        //---------------------------------------------
        //出力変数から入力変数への代入式の追加
        // (TecMLには記述されていない式を追加する)
        //---------------------------------------------
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
            /*代入式の構成*/
            Math_assign pMathAssign =
                (Math_assign)MathFactory.createOperator(eMathOperator.MOP_ASSIGN);
            pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pInputVar().createCopy());
            pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pOutputVar().createCopy());
            MathExpression pMathExp = new MathExpression(pMathAssign);

            /*添え字の追加*/
            this.addIndexToTecMLVariables(pMathExp, i);

            /*数式ベクタに追加*/
            SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathExp);
            vecExpressions.add(pSyntaxExp);
        }

        return vecExpressions;
    }

    /*-----関数展開・変数置換メソッド-----*/

    //========================================================
    //expandDiffFunction
    // 微分関数展開メソッド
    //
    //@arg
    // MathExpression*	pExpression	: 数式インスタンス
    // MathExpression*	pDiffExpression	: 微分式インスタンス
    //
    //========================================================
    protected void expandDiffFunction(MathExpression pExpression,
            MathExpression pDiffExpression)
    throws MathException, TranslateException {
        /*展開関数の検索*/
        Vector<Math_fn> vecFunctions = new Vector<Math_fn>();

        pExpression.searchFunction(m_pTecMLAnalyzer.getM_pDiffFuncVar(), vecFunctions);

        /*検索結果のすべての関数を展開*/
        int nFunctionNum = vecFunctions.size();

        for (int i = 0; i < nFunctionNum; i++) {

            /*置換対象の関数*/
            Math_fn pFunction = (Math_fn)vecFunctions.get(i).createCopy();

            /*関数の置換*/
            pExpression.replace(pFunction, pDiffExpression.getRightExpression().getRootFactor());

            /*関数引数型ごとのidを取得*/
            HashMap<eTecMLVarType, Integer> ati = m_pTecMLAnalyzer.getDiffFuncArgTypeIdx();

            if (ati.size() == 0) {
                throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
                 "failed to get arguments index of differential function ");
            }

            /*変数の置換*/
            this.replaceFunctionVariables(pExpression, pFunction, ati);
        }
    }

    //========================================================
    //expandDiffFunction
    // 非微分関数展開メソッド
    //
    //@arg
    // MathExpression*	pExpression			: 数式インスタンス
    // MathExpression*	pNonDiffExpression	: 非微分式インスタンス
    //
    //========================================================
    protected void expandNonDiffFunction(MathExpression pExpression,
            MathExpression pNonDiffExpression)
    throws MathException, TranslateException {
        /*展開関数の検索*/
        Vector<Math_fn> vecFunctions = new Vector<Math_fn>();

        pExpression.searchFunction(m_pTecMLAnalyzer.getM_pNonDiffFuncVar(), vecFunctions);

        /*検索結果のすべての関数を展開*/
        int nFunctionNum = vecFunctions.size();

        for (int i = 0; i < nFunctionNum; i++) {

            /*置換対象の関数*/
            Math_fn pFunction = (Math_fn)vecFunctions.get(i).createCopy();

            /*関数の置換*/
            pExpression.replace(pFunction, pNonDiffExpression.getRightExpression().getRootFactor());

            /*関数引数型ごとのidを取得*/
            HashMap<eTecMLVarType, Integer> ati = m_pTecMLAnalyzer.getDiffFuncArgTypeIdx();

            if (ati.size() == 0) {
                throw new TranslateException("SyntaxProgram","CommonProgramGenerator",
                 "failed to get arguments index of differential function ");
            }

            /*変数の置換*/
            this.replaceFunctionVariables(pExpression, pFunction, ati);
        }
    }

    //========================================================
    //replaceFunctionVariables
    // 関数中変数の置換メソッド
    //
    //@arg
    // MathExpression*	pExpression	: 数式インスタンス
    // Math_fn*		pFunction		: 置換関数
    // int			nTimeVarArgIdx	: 微分変数引数インデックス
    // int			nTimeArgIdx		: 時間変数引数インデックス
    // int			nVarArgIdx		: 通常変数引数インデックス
    // int			nConstVarArgIdx	: 定数引数インデックス
    //
    //========================================================
    private void replaceFunctionVariables(MathExpression pExpression, Math_fn pFunction,
              HashMap<eTecMLVarType, Integer> ati)
    throws MathException {
        /*関数引数型ごとのidを取得*/
        int nTimeArgIdx = 0;
        int nTimeVarArgIdx = 0;
        int nVarArgIdx = 0;
        int nConstVarArgIdx = 0;

        if (ati.containsKey(eTecMLVarType.TVAR_TYPE_TIMEVAR)) {
            nTimeArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_TIMEVAR);
        }
        if (ati.containsKey(eTecMLVarType.TVAR_TYPE_DIFFVAR)) {
            nTimeVarArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_DIFFVAR);
        }
        if (ati.containsKey(eTecMLVarType.TVAR_TYPE_ARITHVAR)) {
            nVarArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_ARITHVAR);
        }
        if (ati.containsKey(eTecMLVarType.TVAR_TYPE_CONSTVAR)) {
            nConstVarArgIdx = ati.get(eTecMLVarType.TVAR_TYPE_CONSTVAR);
        }

        /*時間変数の置換*/
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecTimeVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        pFunction.getArgumentsVector().get(nTimeArgIdx).toLegalString());

            /*配列インデックスを追加*/
            //pArgVar->addArrayIndexToBack(i);
            //時間にインデックスを付けず，共通の変数として扱う

            /*置換*/
            pExpression.replace(m_pCellMLAnalyzer.getM_vecTimeVar().get(i), pArgVar);
        }

        /*微分変数の置換*/
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        pFunction.getArgumentsVector().get(nTimeVarArgIdx).toLegalString());

            /*配列インデックスを作成*/
            Math_ci pTmpIndex =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   String.valueOf(i));
            Math_ci pLoopIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus =
                (Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pNodeDataNum);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /*配列インデックスを追加*/
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /*置換*/
            pExpression.replace(m_pCellMLAnalyzer.getM_vecDiffVar().get(i),pArgVar);
        }

        /*通常変数の置換*/
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        pFunction.getArgumentsVector().get(nVarArgIdx).toLegalString());

            /*配列インデックスを作成*/
            Math_ci pTmpIndex =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   String.valueOf(i));
            Math_ci pLoopIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus =
                (Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pNodeDataNum);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /*配列インデックスを追加*/
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /*置換*/
            pExpression.replace(m_pCellMLAnalyzer.getM_vecArithVar().get(i),pArgVar);
        }

        /*定数の置換*/
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecConstVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        pFunction.getArgumentsVector().get(nConstVarArgIdx).toLegalString());

            /*配列インデックスを追加*/
            pArgVar.addArrayIndexToBack(i);

            /*置換*/
            pExpression.replace(m_pCellMLAnalyzer.getM_vecConstVar().get(i),pArgVar);
        }
    }

    //========================================================
    //addIndexToTecMLVariables
    // TecML変数へのインデックス追加メソッド
    //
    //@arg
    // MathExpression*	pExpression	: 数式インスタンス
    // int	nIndex	: 付加するインデックス
    //
    //========================================================
    protected void addIndexToTecMLVariables(MathExpression pExpression, int nIndex)
    throws MathException {
        /*微分変数の置換*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                   m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());


            /*配列インデックスを作成*/
            Math_ci pTmpIndex =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   String.valueOf(nIndex));
            Math_ci pLoopIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus =
                (Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pNodeDataNum);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /*配列インデックスを追加*/
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /*置換*/
            pExpression.replace(m_pTecMLAnalyzer.getM_vecDiffVar().get(i),pArgVar);
        }

        /*微係数変数の置換*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

            /*配列インデックスを作成*/
            Math_ci pTmpIndex =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   String.valueOf(nIndex));
            Math_ci pLoopIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus =
                (Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pNodeDataNum);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /*配列インデックスを追加*/
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /*置換*/
            pExpression.replace(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i),pArgVar);
        }

        /*通常変数の置換*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /*引数変数のコピーを取得*/
            Math_ci pArgVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

            /*配列インデックスを作成*/
            Math_ci pTmpIndex =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   String.valueOf(nIndex));
            Math_ci pLoopIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus =
                (Math_plus)MathFactory.createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pNodeDataNum);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /*配列インデックスを追加*/
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /*置換*/
            pExpression.replace(m_pTecMLAnalyzer.getM_vecArithVar().get(i),pArgVar);
        }
    }

    /**
     * MPI_Init
     * @return
     * @throws MathException
     *
     * @author Yuu Shigetani
     */

    protected SyntaxCallFunction createMpiInit() throws MathException {
        /*関数呼び出しインスタンス生成*/
        SyntaxCallFunction pSynMpiInitCall =
            new SyntaxCallFunction(MPI_FUNC_STR_MPIINIT);

        /*引数の生成*/
        Math_ci pArgcVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    "&" + VAR_STR_ARGC);
        Math_ci pArgvVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                               "&" + VAR_STR_ARGV);

        /*引数の追加*/
        pSynMpiInitCall.addArgFactor(pArgcVar);
        pSynMpiInitCall.addArgFactor(pArgvVar);

        /*関数呼び出しインスタンスを戻す*/
        return pSynMpiInitCall;
    }

    /**
     * MPI_Comm_size
     * @param pMpiCommSizeIndexVar
     * @return
     * @throws MathException
     *
     * @author Yuu Shigetani
     */
    protected SyntaxCallFunction createMpiCommSize(Math_ci pMpiCommSizeIndexVar) throws MathException {
        /*関数呼び出しインスタンス生成*/
        SyntaxCallFunction pSynMpiCommSizeCall =
            new SyntaxCallFunction(MPI_FUNC_STR_MPICOMMSIZE);

        /*引数の生成*/
        Math_ci pArgcVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    MPI_COMM_WORLD);
        pMpiCommSizeIndexVar.setPointerNum(-1);
        Math_ci pArgvVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    pMpiCommSizeIndexVar.toLegalString());

        /*引数の追加*/
        pSynMpiCommSizeCall.addArgFactor(pArgcVar);
        pSynMpiCommSizeCall.addArgFactor(pArgvVar);

        pMpiCommSizeIndexVar.setPointerNum(0);

        /*関数呼び出しインスタンスを戻す*/
        return pSynMpiCommSizeCall;
    }

    /**
     * MPI_Comm_rank
     * @param pMpiCommRankIndexVar
     * @return
     * @throws MathException
     *
     * @author Yuu Shigetani
     */
    protected SyntaxCallFunction createMpiCommRank(Math_ci pMpiCommRankIndexVar) throws MathException {
        /*関数呼び出しインスタンス生成*/
        SyntaxCallFunction pSynMpiCommSizeCall =
            new SyntaxCallFunction(MPI_FUNC_STR_COMMRANK);

        /*引数の生成*/
        Math_ci pArgcVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    MPI_COMM_WORLD);
        pMpiCommRankIndexVar.setPointerNum(-1);
        Math_ci pArgvVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    pMpiCommRankIndexVar.toLegalString());

        /*引数の追加*/
        pSynMpiCommSizeCall.addArgFactor(pArgcVar);
        pSynMpiCommSizeCall.addArgFactor(pArgvVar);

        pMpiCommRankIndexVar.setPointerNum(0);

        /*関数呼び出しインスタンスを戻す*/
        return pSynMpiCommSizeCall;
    }

    /**
     * MPI_Finalize
     * @return
     *
     * @author Yuu Shigetani
     */
    protected SyntaxCallFunction createMpiFinalize() {
        /*関数呼び出しインスタンス生成*/
        SyntaxCallFunction pSynMpiCommSizeCall =
            new SyntaxCallFunction(MPI_FUNC_STR_FINALIZE);

        /*関数呼び出しインスタンスを戻す*/
        return pSynMpiCommSizeCall;
    }

    /**
     * MPI_Send
     * @param pMpiSendData
     * @param pMpiDataNum
     * @param pMpiDataType
     * @param pMpiSendNode
     * @param pMpiTag
     * @return
     * @throws MathException
     *
     * @author Yuu Shigetani
     */
    protected SyntaxCallFunction createMpiSend(Math_ci pMpiSendData,
            Math_ci pMpiDataNum, Math_ci pMpiDataType, Math_ci pMpiSendNode,
            Math_ci pMpiTag) throws MathException {
        /*関数呼び出しインスタンス生成*/
        SyntaxCallFunction pSynMpiCommSizeCall =
            new SyntaxCallFunction(MPI_FUNC_STR_SEND);

        pMpiSendData.setPointerNum(-1);
        Math_ci pArgSendData =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    pMpiSendData.toLegalString());
        Math_ci pArgCommWorld =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        MPI_COMM_WORLD);

        /*引数の追加*/
        pSynMpiCommSizeCall.addArgFactor(pArgSendData);
        pSynMpiCommSizeCall.addArgFactor(pMpiDataNum);
        pSynMpiCommSizeCall.addArgFactor(pMpiDataType);
        pSynMpiCommSizeCall.addArgFactor(pMpiSendNode);
        pSynMpiCommSizeCall.addArgFactor(pMpiTag);
        pSynMpiCommSizeCall.addArgFactor(pArgCommWorld);

        pArgSendData.setPointerNum(0);

        /*関数呼び出しインスタンスを戻す*/
        return pSynMpiCommSizeCall;
    }

    /**
     * MPI_Recv
     * @param pMpiRecvData
     * @param pMpiDataNum
     * @param pMpiDataType
     * @param pMpiRecvNode
     * @param pMpiTag
     * @param pMpiRecvStatus
     * @return
     * @throws MathException
     *
     * @author Yuu Shigetani
     */
    protected SyntaxCallFunction createMpiRecv(Math_ci pMpiRecvData,
            Math_ci pMpiDataNum, Math_ci pMpiDataType, Math_ci pMpiRecvNode,
            Math_ci pMpiTag, Math_ci pMpiRecvStatus) throws MathException {
        /*関数呼び出しインスタンス生成*/
        SyntaxCallFunction pSynMpiCommSizeCall =
            new SyntaxCallFunction(MPI_FUNC_STR_RECV);

        pMpiRecvData.setPointerNum(-1);
        Math_ci pArgRecvData =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    pMpiRecvData.toLegalString());
        pMpiRecvStatus.setPointerNum(-1);
        Math_ci pArgRecvStatus =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    pMpiRecvStatus.toLegalString());
        Math_ci pArgCommWorld =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        MPI_COMM_WORLD);

        /*引数の追加*/
        pSynMpiCommSizeCall.addArgFactor(pArgRecvData);
        pSynMpiCommSizeCall.addArgFactor(pMpiDataNum);
        pSynMpiCommSizeCall.addArgFactor(pMpiDataType);
        pSynMpiCommSizeCall.addArgFactor(pMpiRecvNode);
        pSynMpiCommSizeCall.addArgFactor(pMpiTag);
        pSynMpiCommSizeCall.addArgFactor(pArgCommWorld);
        pSynMpiCommSizeCall.addArgFactor(pArgRecvStatus);

        pArgRecvData.setPointerNum(0);
        pArgRecvStatus.setPointerNum(0);

        /*関数呼び出しインスタンスを戻す*/
        return pSynMpiCommSizeCall;
    }

}
