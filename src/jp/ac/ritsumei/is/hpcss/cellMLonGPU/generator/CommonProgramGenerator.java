package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.HashMap;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.tecML.TecMLDefinition.eTecMLVarType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * 逐次プログラム構文生成クラス
 */
public class CommonProgramGenerator extends ProgramGenerator {

    // ========================================================
    // DEFINE
    // ========================================================
    private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
    private static final String COMPROG_DEFINE_DATANUM_NAME = "__DATA_NUM";

    /* 共通変数 */
    protected Math_ci m_pDefinedDataSizeVar; // データ数として#defineされる定数

    /*-----コンストラクタ-----*/
    public CommonProgramGenerator(CellMLAnalyzer pCellMLAnalyzer,
            RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
            throws MathException {
        super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
        m_pDefinedDataSizeVar = null;
        initialize();
    }

    // ========================================================
    // getSyntaxProgram
    // プログラム構文を生成し，返す
    //
    // @return
    // プログラム構文インスタンス : SyntaxProgram*
    //
    // @throws
    // TranslateException
    //
    // ========================================================
    /*-----プログラム構文取得メソッド-----*/
    public SyntaxProgram getSyntaxProgram() throws MathException,
            CellMLException, RelMLException, TranslateException {

        // ----------------------------------------------
        // プログラム生成のための前処理
        // ----------------------------------------------
        /* CellMLにRelMLを適用 */
        m_pCellMLAnalyzer.applyRelML(m_pRelMLAnalyzer);

        // ----------------------------------------------
        // プログラム構文の生成
        // ----------------------------------------------
        /* プログラム構文生成 */
        SyntaxProgram pSynProgram = this.createNewProgram();

        /* プリプロセッサ構文生成・追加 */
        SyntaxPreprocessor pSynInclude1 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_INCLUDE_ABS, "stdio.h");
        SyntaxPreprocessor pSynInclude2 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_INCLUDE_ABS, "stdlib.h");
        SyntaxPreprocessor pSynInclude3 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_INCLUDE_ABS, "math.h");
        pSynProgram.addPreprocessor(pSynInclude1);
        pSynProgram.addPreprocessor(pSynInclude2);
        pSynProgram.addPreprocessor(pSynInclude3);

        /* データ数定義defineの追加 */
        String strElementNum = String.valueOf(m_unElementNum);
        SyntaxPreprocessor pSynDefine1 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_DEFINE,
                m_pDefinedDataSizeVar.toLegalString() + " " + strElementNum);
        pSynProgram.addPreprocessor(pSynDefine1);

        // ----------------------------------------------
        // メイン関数生成
        // ----------------------------------------------
        /* メイン関数生成・追加 */
        SyntaxFunction pSynMainFunc = this.createMainFunction();
        pSynProgram.addFunction(pSynMainFunc);

        // ----------------------------------------------
        // 宣言の追加
        // ----------------------------------------------
        /* ループ変数インスタンス生成 */
        Math_ci pLoopIndexVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, COMPROG_LOOP_INDEX_NAME1);

        /* ループ条件に使用した変数を関数に宣言追加 */
        {
            SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_INT, 0);
            SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeInt,
                    pLoopIndexVar);
            pSynMainFunc.addDeclaration(pDecVar);
        }

        /* 微分変数の宣言 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /* double型ポインタ配列構文生成 */
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(
                    eDataType.DT_DOUBLE, 1);

            /* 宣言用変数の生成 */
            Math_ci pDecVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_vecDiffVar()
                            .get(i).toLegalString());

            /* 宣言の生成 */
            SyntaxDeclaration pSynTimeVarDec = new SyntaxDeclaration(
                    pSynTypePDoubleArray, pDecVar);

            /* 宣言の追加 */
            pSynMainFunc.addDeclaration(pSynTimeVarDec);
        }

        /* 微係数変数の宣言 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /* double型ポインタ配列構文生成 */
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(
                    eDataType.DT_DOUBLE, 1);

            /* 宣言用変数の生成 */
            Math_ci pDecVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer
                            .getM_vecDerivativeVar().get(i).toLegalString());

            /* 宣言の生成 */
            SyntaxDeclaration pSynDiffVarDec = new SyntaxDeclaration(
                    pSynTypePDoubleArray, pDecVar);

            /* 宣言の追加 */
            pSynMainFunc.addDeclaration(pSynDiffVarDec);
        }

        /* 通常変数の宣言 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /* double型ポインタ配列構文生成 */
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(
                    eDataType.DT_DOUBLE, 1);

            /* 宣言用変数の生成 */
            Math_ci pDecVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_vecArithVar()
                            .get(i).toLegalString());

            /* 宣言の生成 */
            SyntaxDeclaration pSynVarDec = new SyntaxDeclaration(
                    pSynTypePDoubleArray, pDecVar);

            /* 宣言の追加 */
            pSynMainFunc.addDeclaration(pSynVarDec);
        }

        /* 定数の宣言 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

            /* double型ポインタ配列構文生成 */
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(
                    eDataType.DT_DOUBLE, 1);

            /* 宣言用変数の生成 */
            Math_ci pDecVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_vecConstVar()
                            .get(i).toLegalString());

            /* 宣言の生成 */
            SyntaxDeclaration pSynConstVarDec = new SyntaxDeclaration(
                    pSynTypePDoubleArray, pDecVar);

            /* 宣言の追加 */
            pSynMainFunc.addDeclaration(pSynConstVarDec);
        }

        /* 時間変数の宣言 */
        {

            /* double型構文生成 */
            SyntaxDataType pSynTypeDouble = new SyntaxDataType(
                    eDataType.DT_DOUBLE, 0);

            /* 宣言用変数の生成 */
            Math_ci pDecVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_pTimeVar()
                            .toLegalString());

            /* 宣言の生成 */
            SyntaxDeclaration pSynTimeDec = new SyntaxDeclaration(
                    pSynTypeDouble, pDecVar);

            /* 宣言の追加 */
            pSynMainFunc.addDeclaration(pSynTimeDec);
        }

        /* デルタ変数の宣言 */
        {
            /* double型構文生成 */
            SyntaxDataType pSynTypeDouble = new SyntaxDataType(
                    eDataType.DT_DOUBLE, 0);

            /* 宣言用変数の生成 */
            Math_ci pDecVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_pDeltaVar()
                            .toLegalString());

            /* 初期化式の生成 */
            Math_cn pConstDeltaVal = (Math_cn) MathFactory.createOperand(
                    eMathOperand.MOPD_CN,
                    StringUtil.doubleToString(m_dDeltaTime));
            MathExpression pInitExpression = new MathExpression(pConstDeltaVal);

            /* 宣言の生成 */
            SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(
                    pSynTypeDouble, pDecVar);

            /* 初期化式の追加 */
            pSynDeltaDec.addInitExpression(pInitExpression);

            /* 宣言の追加 */
            pSynMainFunc.addDeclaration(pSynDeltaDec);
        }

        // ----------------------------------------------
        // malloc関数呼び出しの追加
        // ----------------------------------------------
        /* 微分変数へのmallocによるメモリ割り当て */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /* データ数を表す数式を生成 */
            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_cn pMathVarCount = (Math_cn) MathFactory.createOperand(
                    eMathOperand.MOPD_CN,
                    String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));
            pMathTimes.addFactor(pMathVarCount);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer
                    .getM_vecDiffVar().get(i), pMathTimes));
        }
        /* 一時変数へのmallocによるメモリ割り当て */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /* データ数を表す数式を生成 */
            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_cn pMathVarCount = (Math_cn) MathFactory
                    .createOperand(eMathOperand.MOPD_CN, String
                            .valueOf(m_pCellMLAnalyzer.getM_vecArithVar()
                                    .size()));
            pMathTimes.addFactor(pMathVarCount);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer
                    .getM_vecArithVar().get(i), pMathTimes));
        }
        /* 微係数変数へのmallocによるメモリ割り当て */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /* データ数を表す数式を生成 */
            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_cn pMathVarCount = (Math_cn) MathFactory.createOperand(
                    eMathOperand.MOPD_CN,
                    String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));
            pMathTimes.addFactor(pMathVarCount);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer
                    .getM_vecDerivativeVar().get(i), pMathTimes));
        }
        /* 定数へのmallocによるメモリ割り当て */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

            /* データ数を表す変数を生成 */
            Math_cn pMathVarCount = (Math_cn) MathFactory
                    .createOperand(eMathOperand.MOPD_CN, String
                            .valueOf(m_pCellMLAnalyzer.getM_vecConstVar()
                                    .size()));

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createMalloc(m_pTecMLAnalyzer
                    .getM_vecConstVar().get(i), pMathVarCount));
        }

        // ----------------------------------------------
        // 数式部分の追加
        // ----------------------------------------------
        /* 外側ループ構文生成・追加 */
        Math_ci pTimeVariale = (Math_ci) (m_pTecMLAnalyzer.getM_pTimeVar()
                .createCopy());
        Math_ci pDeltaVariale = (Math_ci) (m_pTecMLAnalyzer.getM_pDeltaVar()
                .createCopy());

        SyntaxControl pSynFor1 = createSyntaxTimeLoop(m_dStartTime, m_dEndTime,
                pTimeVariale, pDeltaVariale);
        pSynMainFunc.addStatement(pSynFor1);

        /* 内側ループ構文生成・追加 */
        SyntaxControl pSynFor2 = createSyntaxDataNumLoop(m_pDefinedDataSizeVar,
                pLoopIndexVar);
        pSynFor1.addStatement(pSynFor2);

        // ----------------------------------------------
        // 数式の生成と追加
        // ----------------------------------------------
        /* 数式を生成し，取得 */
        Vector<SyntaxExpression> vecExpressions = this.createExpressions();

        /* ループ中に数式を追加 */
        int nExpressionNum = vecExpressions.size();

        for (int i = 0; i < nExpressionNum; i++) {

            /* 数式の追加 */
            pSynFor2.addStatement(vecExpressions.get(i));
        }

        // ----------------------------------------------
        // free関数呼び出しの追加
        // ----------------------------------------------
        /* 微分変数のfree */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer
                    .getM_vecDiffVar().get(i)));
        }
        /* 一時変数のfree */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer
                    .getM_vecArithVar().get(i)));
        }
        /* 微係数変数のfree */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer
                    .getM_vecDerivativeVar().get(i)));
        }
        /* 定数のfree */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

            /* 宣言の追加 */
            pSynMainFunc.addStatement(createFree(m_pTecMLAnalyzer
                    .getM_vecConstVar().get(i)));
        }

        /* プログラム構文を返す */
        return pSynProgram;
    }

    // ========================================================
    // initialize
    // 初期化メソッド
    //
    // ========================================================
    /*-----初期化・終了処理メソッド-----*/
    protected void initialize() throws MathException {
        m_pDefinedDataSizeVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, COMPROG_DEFINE_DATANUM_NAME);
    }

    // ========================================================
    // createExpressions
    // 計算式部を生成し，ベクタを返す
    //
    // @return
    // 計算式ベクタ
    //
    // @throws
    // TranslateException
    //
    // ========================================================
    /*-----計算式生成メソッド-----*/
    protected Vector<SyntaxExpression> createExpressions()
            throws TranslateException, MathException {
        // ---------------------------------------------
        // 式生成のための前処理
        // ---------------------------------------------
        /* ベクタを初期化 */
        Vector<SyntaxExpression> vecExpressions = new Vector<SyntaxExpression>();

        // ---------------------------------------------
        // 式の追加
        // ---------------------------------------------
        /* 数式数を取得 */
        int nExpressionNum = m_pTecMLAnalyzer.getExpressionCount();

        for (int i = 0; i < nExpressionNum; i++) {

            /* 数式の複製を取得 */
            MathExpression pMathExp = m_pTecMLAnalyzer.getExpression(i);

            /* 左辺式・右辺式取得 */
            MathExpression pLeftExp = pMathExp.getLeftExpression();
            MathExpression pRightExp = pMathExp.getRightExpression();

            if (pLeftExp == null || pRightExp == null) {
                throw new TranslateException("SyntaxProgram",
                        "CommonProgramGenerator", "failed to parse expression");
            }

            /* 左辺変数取得 */
            MathOperand pLeftVar = (MathOperand) pLeftExp.getFirstVariable();

            // -------------------------------------------
            // 左辺式ごとに数式の追加
            // -------------------------------------------
            /* 微係数変数 */
            if (m_pTecMLAnalyzer.isDerivativeVar(pLeftVar)) {

                /* 微分式の数を取得 */
                int nDiffExpNum = m_pCellMLAnalyzer.getM_vecDiffExpression()
                        .size();

                /* 数式の出力 */
                for (int j = 0; j < nDiffExpNum; j++) {

                    /* 代入文の形成 */
                    Math_assign pMathAssign = (Math_assign) MathFactory
                            .createOperator(eMathOperator.MOP_ASSIGN);
                    pMathAssign
                            .addFactor(pLeftExp.createCopy().getRootFactor());
                    pMathAssign.addFactor(pRightExp.createCopy()
                            .getRootFactor());

                    /* 新たな計算式を生成 */
                    MathExpression pNewExp = new MathExpression(pMathAssign);

                    /* TecML変数に添え字を付加 */
                    this.addIndexToTecMLVariables(pNewExp, j);

                    /* 微分式インスタンスのコピー取得 */
                    MathExpression pDiffExpression = m_pCellMLAnalyzer
                            .getM_vecDiffExpression().get(j).createCopy();

                    /* 微分関数の展開 */
                    this.expandDiffFunction(pNewExp, pDiffExpression);

                    /* 数式ベクタに追加 */
                    SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
                    vecExpressions.add(pSyntaxExp);
                }

            }

            /* 微分変数 */
            else if (m_pTecMLAnalyzer.isDiffVar(pLeftVar)) {

                /* 微分式の数を取得 */
                int nDiffVarNum = m_pCellMLAnalyzer.getM_vecDiffVar().size();

                /* 数式の出力 */
                for (int j = 0; j < nDiffVarNum; j++) {

                    /* 代入文の形成 */
                    Math_assign pMathAssign = (Math_assign) MathFactory
                            .createOperator(eMathOperator.MOP_ASSIGN);
                    pMathAssign
                            .addFactor(pLeftExp.createCopy().getRootFactor());
                    pMathAssign.addFactor(pRightExp.createCopy()
                            .getRootFactor());

                    /* 新たな計算式を生成 */
                    MathExpression pNewExp = new MathExpression(pMathAssign);

                    /* 添え字の付加 */
                    this.addIndexToTecMLVariables(pNewExp, j);

                    /* 数式ベクタに追加 */
                    SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
                    vecExpressions.add(pSyntaxExp);
                }
            }

            /* 通常変数 */
            else if (m_pTecMLAnalyzer.isArithVar(pLeftVar)) {
                /* 微分式の数を取得 */
                int nNonDiffExpNum = m_pCellMLAnalyzer
                        .getM_vecNonDiffExpression().size();

                /* 数式の出力 */
                for (int j = 0; j < nNonDiffExpNum; j++) {

                    /* 代入文の形成 */
                    Math_assign pMathAssign = (Math_assign) MathFactory
                            .createOperator(eMathOperator.MOP_ASSIGN);
                    pMathAssign
                            .addFactor(pLeftExp.createCopy().getRootFactor());
                    pMathAssign.addFactor(pRightExp.createCopy()
                            .getRootFactor());

                    /* 新たな計算式を生成 */
                    MathExpression pNewExp = new MathExpression(pMathAssign);

                    /* TecML変数に添え字を付加 */
                    this.addIndexToTecMLVariables(pNewExp, j);

                    /* 微分式インスタンスのコピー取得 */
                    MathExpression pNonDiffExpression = m_pCellMLAnalyzer
                            .getM_vecNonDiffExpression().get(j).createCopy();

                    /* 微分関数の展開 */
                    this.expandNonDiffFunction(pNewExp, pNonDiffExpression);

                    /* 数式ベクタに追加 */
                    SyntaxExpression pSyntaxExp = new SyntaxExpression(pNewExp);
                    vecExpressions.add(pSyntaxExp);
                }
            }

            /* 定数変数 */
            else if (m_pTecMLAnalyzer.isConstVar(pLeftVar)) {
            }

        }

        // ---------------------------------------------
        // 出力変数から入力変数への代入式の追加
        // (TecMLには記述されていない式を追加する)
        // ---------------------------------------------
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
            /* 代入式の構成 */
            Math_assign pMathAssign = (Math_assign) MathFactory
                    .createOperator(eMathOperator.MOP_ASSIGN);
            pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pInputVar()
                    .createCopy());
            pMathAssign.addFactor(m_pTecMLAnalyzer.getM_pOutputVar()
                    .createCopy());
            MathExpression pMathExp = new MathExpression(pMathAssign);

            /* 添え字の追加 */
            this.addIndexToTecMLVariables(pMathExp, i);

            /* 数式ベクタに追加 */
            SyntaxExpression pSyntaxExp = new SyntaxExpression(pMathExp);
            vecExpressions.add(pSyntaxExp);
        }

        return vecExpressions;
    }

    /*-----関数展開・変数置換メソッド-----*/

    // ========================================================
    // expandDiffFunction
    // 微分関数展開メソッド
    //
    // @arg
    // MathExpression* pExpression : 数式インスタンス
    // MathExpression* pDiffExpression : 微分式インスタンス
    //
    // ========================================================
    protected void expandDiffFunction(MathExpression pExpression,
            MathExpression pDiffExpression) throws MathException,
            TranslateException {
        /* 展開関数の検索 */
        Vector<Math_fn> vecFunctions = new Vector<Math_fn>();

        pExpression.searchFunction(m_pTecMLAnalyzer.getM_pDiffFuncVar(),
                vecFunctions);

        /* 検索結果のすべての関数を展開 */
        int nFunctionNum = vecFunctions.size();

        for (int i = 0; i < nFunctionNum; i++) {

            /* 置換対象の関数 */
            Math_fn pFunction = (Math_fn) vecFunctions.get(i).createCopy();

            /* 関数の置換 */
            pExpression.replace(pFunction, pDiffExpression.getRightExpression()
                    .getRootFactor());

            /* 関数引数型ごとのidを取得 */
            HashMap<eTecMLVarType, Integer> ati = m_pTecMLAnalyzer
                    .getDiffFuncArgTypeIdx();

            if (ati.size() == 0) {
                throw new TranslateException("SyntaxProgram",
                        "CommonProgramGenerator",
                        "failed to get arguments index of differential function ");
            }

            /* 変数の置換 */
            this.replaceFunctionVariables(pExpression, pFunction, ati);
        }
    }

    // ========================================================
    // expandDiffFunction
    // 非微分関数展開メソッド
    //
    // @arg
    // MathExpression* pExpression : 数式インスタンス
    // MathExpression* pNonDiffExpression : 非微分式インスタンス
    //
    // ========================================================
    protected void expandNonDiffFunction(MathExpression pExpression,
            MathExpression pNonDiffExpression) throws MathException,
            TranslateException {
        /* 展開関数の検索 */
        Vector<Math_fn> vecFunctions = new Vector<Math_fn>();

        pExpression.searchFunction(m_pTecMLAnalyzer.getM_pNonDiffFuncVar(),
                vecFunctions);

        /* 検索結果のすべての関数を展開 */
        int nFunctionNum = vecFunctions.size();

        for (int i = 0; i < nFunctionNum; i++) {

            /* 置換対象の関数 */
            Math_fn pFunction = (Math_fn) vecFunctions.get(i).createCopy();

            /* 関数の置換 */
            pExpression.replace(pFunction, pNonDiffExpression
                    .getRightExpression().getRootFactor());

            /* 関数引数型ごとのidを取得 */
            HashMap<eTecMLVarType, Integer> ati = m_pTecMLAnalyzer
                    .getDiffFuncArgTypeIdx();

            if (ati.size() == 0) {
                throw new TranslateException("SyntaxProgram",
                        "CommonProgramGenerator",
                        "failed to get arguments index of differential function ");
            }

            /* 変数の置換 */
            this.replaceFunctionVariables(pExpression, pFunction, ati);
        }
    }

    // ========================================================
    // replaceFunctionVariables
    // 関数中変数の置換メソッド
    //
    // @arg
    // MathExpression* pExpression : 数式インスタンス
    // Math_fn* pFunction : 置換関数
    // int nTimeVarArgIdx : 微分変数引数インデックス
    // int nTimeArgIdx : 時間変数引数インデックス
    // int nVarArgIdx : 通常変数引数インデックス
    // int nConstVarArgIdx : 定数引数インデックス
    //
    // ========================================================
    private void replaceFunctionVariables(MathExpression pExpression,
            Math_fn pFunction, HashMap<eTecMLVarType, Integer> ati)
            throws MathException {
        /* 関数引数型ごとのidを取得 */
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

        /* 時間変数の置換 */
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecTimeVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI,
                    pFunction.getArgumentsVector().get(nTimeArgIdx)
                            .toLegalString());

            /* 配列インデックスを追加 */
            // pArgVar->addArrayIndexToBack(i);
            // 時間にインデックスを付けず，共通の変数として扱う

            /* 置換 */
            pExpression.replace(m_pCellMLAnalyzer.getM_vecTimeVar().get(i),
                    pArgVar);
        }

        /* 微分変数の置換 */
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI,
                    pFunction.getArgumentsVector().get(nTimeVarArgIdx)
                            .toLegalString());

            /* 配列インデックスを作成 */
            Math_ci pTmpIndex = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, String.valueOf(i));
            Math_ci pLoopIndexVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus = (Math_plus) MathFactory
                    .createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /* 配列インデックスを追加 */
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /* 置換 */
            pExpression.replace(m_pCellMLAnalyzer.getM_vecDiffVar().get(i),
                    pArgVar);
        }

        /* 通常変数の置換 */
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecArithVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI,
                    pFunction.getArgumentsVector().get(nVarArgIdx)
                            .toLegalString());

            /* 配列インデックスを作成 */
            Math_ci pTmpIndex = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, String.valueOf(i));
            Math_ci pLoopIndexVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus = (Math_plus) MathFactory
                    .createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /* 配列インデックスを追加 */
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /* 置換 */
            pExpression.replace(m_pCellMLAnalyzer.getM_vecArithVar().get(i),
                    pArgVar);
        }

        /* 定数の置換 */
        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecConstVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI,
                    pFunction.getArgumentsVector().get(nConstVarArgIdx)
                            .toLegalString());

            /* 配列インデックスを追加 */
            pArgVar.addArrayIndexToBack(i);

            /* 置換 */
            pExpression.replace(m_pCellMLAnalyzer.getM_vecConstVar().get(i),
                    pArgVar);
        }
    }

    // ========================================================
    // addIndexToTecMLVariables
    // TecML変数へのインデックス追加メソッド
    //
    // @arg
    // MathExpression* pExpression : 数式インスタンス
    // int nIndex : 付加するインデックス
    //
    // ========================================================
    protected void addIndexToTecMLVariables(MathExpression pExpression,
            int nIndex) throws MathException {
        /* 微分変数の置換 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_vecDiffVar()
                            .get(i).toLegalString());

            /* 配列インデックスを作成 */
            Math_ci pTmpIndex = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, String.valueOf(nIndex));
            Math_ci pLoopIndexVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus = (Math_plus) MathFactory
                    .createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /* 配列インデックスを追加 */
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /* 置換 */
            pExpression.replace(m_pTecMLAnalyzer.getM_vecDiffVar().get(i),
                    pArgVar);
        }

        /* 微係数変数の置換 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer
                            .getM_vecDerivativeVar().get(i).toLegalString());

            /* 配列インデックスを作成 */
            Math_ci pTmpIndex = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, String.valueOf(nIndex));
            Math_ci pLoopIndexVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus = (Math_plus) MathFactory
                    .createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /* 配列インデックスを追加 */
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /* 置換 */
            pExpression.replace(
                    m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i), pArgVar);
        }

        /* 通常変数の置換 */
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /* 引数変数のコピーを取得 */
            Math_ci pArgVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, m_pTecMLAnalyzer.getM_vecArithVar()
                            .get(i).toLegalString());

            /* 配列インデックスを作成 */
            Math_ci pTmpIndex = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, String.valueOf(nIndex));
            Math_ci pLoopIndexVar = (Math_ci) MathFactory.createOperand(
                    eMathOperand.MOPD_CI, COMPROG_LOOP_INDEX_NAME1);

            Math_times pMathTimes = (Math_times) MathFactory
                    .createOperator(eMathOperator.MOP_TIMES);
            Math_plus pMathPlus = (Math_plus) MathFactory
                    .createOperator(eMathOperator.MOP_PLUS);

            pMathTimes.addFactor(pTmpIndex);
            pMathTimes.addFactor(m_pDefinedDataSizeVar);
            pMathPlus.addFactor(pMathTimes);
            pMathPlus.addFactor(pLoopIndexVar);

            MathFactor pIndexFactor = pMathPlus;

            /* 配列インデックスを追加 */
            pArgVar.addArrayIndexToBack(pIndexFactor);

            /* 置換 */
            pExpression.replace(m_pTecMLAnalyzer.getM_vecArithVar().get(i),
                    pArgVar);
        }
    }

}
