package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_and;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_assign;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_divide;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_minus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_remainder;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_times;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * MPIメイン関数構文生成クラス
 * MPIProgramGeneratorクラスからメイン関数生成部を切り離したクラス
 */
public class MpiMainFuncGenerator extends MpiProgramGenerator {

    //========================================================
    //DEFINE
    //========================================================
    private static final String COMPROG_LOOP_INDEX_NAME1 = "__i";
    private static final String COMPROG_LOOP_INDEX_NAME2 = "__j";
    private static final String ALL_XO = "all_xo";
    private static final String TMP_XO = "tmp_xo";
    private static final String END_TIME = "end_time";
    private static final double END_TIME_NUM = 400.0;
    private static final String COMM_SIZE = "comm_size";
    private static final String NODE = "node";
    private static final String TAG = "tag";
    private static final int TAG_NUM = 0;
    private static final String WORKER = "worker";
    private static final String RECV_STATUS = "recv_status";
    private static final String MASTER_DATA_NUM = "__MASTER_DATA_NUM";
    private static final String WORKER_DATA_NUM = "__WORKER_DATA_NUM";
    private static final String COMM_SIZE_INDEX = "3";
    private static final String COMM_SIZE_MINUS_NUM = "1";
    private static final String MASTER_NODE_NUM = "0";
    private static final String WORKER_START_NUM = "1";
    private static final String ALLXO_TEST_NAME = "test_all_xo";
    private static final String MPI_DOUBLE = "MPI_DOUBLE";
    private static final String MPI_XO = "xo";
    private static final String START = "start";
    private static final String END = "end";
    private static final String TEST_DIFF = "0.0001";


    /*宣言変数ベクタ*/
    SyntaxDeclaration m_pSynHostInputVarDec;
    SyntaxDeclaration m_pSynHostOutputVarDec;
    SyntaxDeclaration m_pSynDevInputVarDec;
    SyntaxDeclaration m_pSynDevOutputVarDec;
    Vector<SyntaxDeclaration> m_vecSynHostTimeVarDec;
    Vector<SyntaxDeclaration> m_vecSynHostDiffVarDec;
    Vector<SyntaxDeclaration> m_vecSynHostVarDec;
    Vector<SyntaxDeclaration> m_vecSynHostConstVarDec;
    Vector<SyntaxDeclaration> m_vecSynDevTimeVarDec;
    Vector<SyntaxDeclaration> m_vecSynDevDiffVarDec;
    Vector<SyntaxDeclaration> m_vecSynDevVarDec;
    Vector<SyntaxDeclaration> m_vecSynDevConstVarDec;
    SyntaxDeclaration m_pSynTimeDec;
    SyntaxDeclaration m_pSynDeltaDec;
    SyntaxDeclaration m_pSynBlockDec;
    SyntaxDeclaration m_pSynGridDec;

    /*-----コンストラクタ-----*/
    public MpiMainFuncGenerator(CellMLAnalyzer pCellMLAnalyzer,
            RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer)
    throws MathException {
        super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
        m_vecSynHostTimeVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynHostDiffVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynHostVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynHostConstVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynDevTimeVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynDevDiffVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynDevVarDec = new Vector<SyntaxDeclaration>();
        m_vecSynDevConstVarDec = new Vector<SyntaxDeclaration>();
    }

    //========================================================
    //getSyntaxMainFunction
    // メイン関数構文を生成し，返す
    //
    //@return
    // 関数構文インスタンス	: SyntaxFunction*
    //
    //@throws
    // TranslateException
    //
    //========================================================
    /*-----プログラム構文取得メソッド-----*/
    public SyntaxFunction getSyntaxMainFunction()
    throws MathException, SyntaxException, TranslateException {
        //----------------------------------------------
        //メイン関数生成
        //----------------------------------------------
        /*メイン関数生成・追加*/
        SyntaxFunction pSynMainFunc = this.createMainFunction();

        //----------------------------------------------
        //宣言の追加
        //----------------------------------------------

        Math_ci pLoopIndexStartVar = createIntVal(pSynMainFunc, START, 0, 0, false);
        Math_ci pLoopIndexEndVar = createIntVal(pSynMainFunc, END, 0, 0, false);

        /*ループ変数インスタンス生成*/
        Math_ci pLoopIndexVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME1);

        /*ループ条件に使用した変数を関数に宣言追加*/
        {
            SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_INT, 0);
            SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeInt, pLoopIndexVar);
            pSynMainFunc.addDeclaration(pDecVar);
        }

        /*ループ変数インスタンス生成*/
        Math_ci pLoopIndexVar2 =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,COMPROG_LOOP_INDEX_NAME2);

        /*ループ条件に使用した変数を関数に宣言追加*/
        {
            SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_INT, 0);
            SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeInt, pLoopIndexVar2);
            pSynMainFunc.addDeclaration(pDecVar);
        }

        /*微分変数の宣言*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /*double型ポインタ配列構文生成*/
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

            /*宣言用変数の生成*/
            Math_ci pDecVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_vecDiffVar().get(i).toLegalString());

            /*宣言の生成*/
            SyntaxDeclaration pSynTimeVarDec =
                new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

            /*宣言の追加*/
            pSynMainFunc.addDeclaration(pSynTimeVarDec);
        }

        /*微係数変数の宣言*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /*double型ポインタ配列構文生成*/
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

            /*宣言用変数の生成*/
            Math_ci pDecVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i).toLegalString());

            /*宣言の生成*/
            SyntaxDeclaration pSynDiffVarDec =
                new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

            /*宣言の追加*/
            pSynMainFunc.addDeclaration(pSynDiffVarDec);
        }

        /*通常変数の宣言*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /*double型ポインタ配列構文生成*/
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

            /*宣言用変数の生成*/
            Math_ci pDecVar = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                    m_pTecMLAnalyzer.getM_vecArithVar().get(i).toLegalString());

            /*宣言の生成*/
            SyntaxDeclaration pSynVarDec =
                new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

            /*宣言の追加*/
            pSynMainFunc.addDeclaration(pSynVarDec);
        }

        /*定数の宣言*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

            /*double型ポインタ配列構文生成*/
            SyntaxDataType pSynTypePDoubleArray = new SyntaxDataType(eDataType.DT_DOUBLE, 1);

            /*宣言用変数の生成*/
            Math_ci pDecVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_vecConstVar().get(i).toLegalString());

            /*宣言の生成*/
            SyntaxDeclaration pSynConstVarDec =
                new SyntaxDeclaration(pSynTypePDoubleArray, pDecVar);

            /*宣言の追加*/
            pSynMainFunc.addDeclaration(pSynConstVarDec);
        }

        /*時間変数の宣言*/
        {

            /*double型構文生成*/
            SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

            /*宣言用変数の生成*/
            Math_ci pDecVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_pTimeVar().toLegalString());

            /*宣言の生成*/
            SyntaxDeclaration pSynTimeDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

            /*宣言の追加*/
            pSynMainFunc.addDeclaration(pSynTimeDec);
        }

        /*デルタ変数の宣言*/
        {
            /*double型構文生成*/
            SyntaxDataType pSynTypeDouble = new SyntaxDataType(eDataType.DT_DOUBLE, 0);

            /*宣言用変数の生成*/
            Math_ci pDecVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        m_pTecMLAnalyzer.getM_pDeltaVar().toLegalString());

            /*初期化式の生成*/
            Math_cn pConstDeltaVal =
                (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                        StringUtil.doubleToString(m_dDeltaTime));
            MathExpression pInitExpression = new MathExpression(pConstDeltaVal);

            /*宣言の生成*/
            SyntaxDeclaration pSynDeltaDec = new SyntaxDeclaration(pSynTypeDouble, pDecVar);

            /*初期化式の追加*/
            pSynDeltaDec.addInitExpression(pInitExpression);

            /*宣言の追加*/
            pSynMainFunc.addDeclaration(pSynDeltaDec);
        }

        //mpi用変数生成
        /*
         * Assign all nodes result
         * double* all_xo;
         */
        Math_ci pMpiAllXoIndexVar = createDoubleVal(pSynMainFunc, ALL_XO, 1, 0, false);

        /*
         * Assign all nodes result
         * double* tmp_xo;
         */
        Math_ci pMpiTmpXoIndexVar = createDoubleVal(pSynMainFunc, TMP_XO, 1, 0, false);

        /*
         * end time
         * double end_time = 400.0;
         */
        Math_ci pMpiEndTimeIndexVar = createDoubleVal(pSynMainFunc, END_TIME, 0, END_TIME_NUM, false);

        /*
         * Number of all nodes
         * int comm_size;
         */
        Math_ci pMpiCommSizeIndexVar = createIntVal(pSynMainFunc, COMM_SIZE, 0, 0, false);

        /*
         * Node number(Master:0, Worker:0~comm_size-1)
         * int node;
         */
        Math_ci pMpiNodeIndexVar = createIntVal(pSynMainFunc, NODE, 0, 0, false);

        /*
         * Tag for MPI_Send and MPI_Recv
         * int tag=0;
         */
        Math_ci pMpiTagIndexVar = createIntVal(pSynMainFunc, TAG, 0, TAG_NUM, true);


        /*
         * Node number of a worker
         * int worker;
         */
        Math_ci pMpiWorkerIndexVar = createIntVal(pSynMainFunc, WORKER, 0, 0, false);

        /*
         * MPI_Recv status
         * MPI_Status recv_status;
         */
        Math_ci pMpiRecvStatusIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,RECV_STATUS);

        /* 変数を関数に宣言追加 */
        {
            SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_MPISTA, 0);
            SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeInt, pMpiRecvStatusIndexVar);

            pSynMainFunc.addDeclaration(pDecVar);
        }

        /*
         * Master data size
         * int __MASTER_DATA_NUM;
         */
        Math_ci pMpiMasterDataNumIndexVar = createIntVal(pSynMainFunc, MASTER_DATA_NUM, 0, 0, false);

        /*
         * Worker data size
         * int __WORKER_DATA_NUM;
         */
        Math_ci pMpiWorkerDataNumIndexVar = createIntVal(pSynMainFunc, WORKER_DATA_NUM, 0, 0, false);

        /*
         * MPI initilize
         * MPI community size
         * Get node number of this node
         */
        pSynMainFunc.addStatement(this.createMpiInit());
        pSynMainFunc.addStatement(this.createMpiCommSize(pMpiCommSizeIndexVar));
        pSynMainFunc.addStatement(this.createMpiCommRank(pMpiNodeIndexVar));
        if ( m_isTestGenerate ) pSynMainFunc.addStatement(m_testInitFunc.callFunction((Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, PROG_VAR_STR_ARGV), pMpiNodeIndexVar));

        //create if for Decide data size
        Math_cn commSizeMax = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, COMM_SIZE_INDEX);
        Math_lt pMathLt = (Math_lt)MathFactory.createOperator(eMathOperator.MOP_LT);
        //create cond
        MathFactor pSynIfCond = createCondition(pMpiCommSizeIndexVar, commSizeMax, pMathLt);
        //create if
        SyntaxControl pSynIf = createIf(pSynIfCond);
        //add main func
        pSynMainFunc.addStatement(pSynIf);

        //if's element
        Math_divide pMathDivide = createDivide(m_pDefinedDataSizeVar, pMpiCommSizeIndexVar);
        Math_minus pMathMinus = createMinus(m_pDefinedDataSizeVar, pMpiMasterDataNumIndexVar);
        //add if
        createAssign(pSynIf, pMpiMasterDataNumIndexVar, pMathDivide);
        createAssign(pSynIf, pMpiWorkerDataNumIndexVar, pMathMinus);

        //create else
        SyntaxControl pSynElse = createElse();
        //add main func
        pSynMainFunc.addStatement(pSynElse);

        //else's elsemet
        Math_divide pMathDivideForElse = createDivide(m_pDefinedDataSizeVar, pMpiCommSizeIndexVar);
        Math_cn commSizeMinusNum = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, COMM_SIZE_MINUS_NUM);
        Math_minus pMathMinusForElse = createMinus(pMpiCommSizeIndexVar, commSizeMinusNum);
        Math_times pMathTimesForElse = createTimes(pMpiWorkerDataNumIndexVar, pMathMinusForElse);
        pMathMinusForElse = createMinus(m_pDefinedDataSizeVar, pMathTimesForElse);
        //add else
        createAssign(pSynElse, pMpiWorkerDataNumIndexVar, pMathDivideForElse);
        createAssign(pSynElse, pMpiMasterDataNumIndexVar, pMathMinusForElse);

        //start's element
        Math_cn nodeMinusNum = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1");
        Math_minus pMathMinusForStart = createMinus(pMpiNodeIndexVar, nodeMinusNum);
        Math_times pMathTimesForStart = createTimes(pMpiWorkerDataNumIndexVar, pMathMinusForStart);
        Math_plus pMathPlusForStart = createPlus(pMathTimesForStart, pMpiMasterDataNumIndexVar);
        //add main
        createAssign(pSynMainFunc, pLoopIndexStartVar, pMathPlusForStart);

        //end's element
        Math_plus pMathPlusForEnd = createPlus(pLoopIndexStartVar, pMpiWorkerDataNumIndexVar);
        //add main
        createAssign(pSynMainFunc, pLoopIndexEndVar, pMathPlusForEnd);

        //create if for master node
        Math_eq pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        Math_cn masterNodeNum = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, MASTER_NODE_NUM);
        //create condition
        MathFactor pSynIfForMasterNodeCond = createCondition(pMpiNodeIndexVar, masterNodeNum, pMathEq);
        //add cond to if
        SyntaxControl pSynIfForMasterNode = createIf(pSynIfForMasterNodeCond);
        pSynMainFunc.addStatement(pSynIfForMasterNode);

        //create else for slave node
        SyntaxControl pSynIfForSlaveNode = createElse();
        pSynMainFunc.addStatement(pSynIfForSlaveNode);


        //----------------------------------------------
        //malloc関数呼び出しの追加
        //----------------------------------------------
        /*微分変数へのmallocによるメモリ割り当て*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {

            /*データ数を表す数式を生成*/
            Math_times pMathTimesForMaster =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_times pMathTimesForSlave =
                    (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_cn pMathVarCount =
                (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                        String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));

            pMathTimesForMaster.addFactor(pMathVarCount);
            pMathTimesForSlave.addFactor(pMathVarCount);

            pMathTimesForMaster.addFactor(pMpiMasterDataNumIndexVar);
            pMathTimesForSlave.addFactor(m_pDefinedDataSizeVar);

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecDiffVar().get(i),
                    pMathTimesForMaster));
            pSynIfForSlaveNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecDiffVar().get(i),
                    pMathTimesForSlave));
        }

        // all_xo malloc
        {
        /*データ数を表す数式を生成*/
        Math_times pMathTimesForMaster =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

        Math_cn pMathVarCount =
            (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                    String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));

        pMathTimesForMaster.addFactor(pMathVarCount);

        pMathTimesForMaster.addFactor(m_pDefinedDataSizeVar);

        /*宣言の追加*/
        pSynIfForMasterNode.addStatement(createMalloc(pMpiAllXoIndexVar,
                pMathTimesForMaster));
        }

        // tmp_xo malloc
        {
        /*データ数を表す数式を生成*/
        Math_times pMathTimesForMaster =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

        Math_cn pMathVarCount =
            (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                    String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));

        pMathTimesForMaster.addFactor(pMathVarCount);

        pMathTimesForMaster.addFactor(pMpiWorkerDataNumIndexVar);

        /*宣言の追加*/
        pSynIfForMasterNode.addStatement(createMalloc(pMpiTmpXoIndexVar,
                pMathTimesForMaster));
        }

        /*一時変数へのmallocによるメモリ割り当て*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /*データ数を表す数式を生成*/
            Math_times pMathTimesForMaster =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_times pMathTimesForSlave =
                    (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

            Math_cn pMathVarCount =
                (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                        String.valueOf(m_pCellMLAnalyzer.getM_vecArithVar().size()));

            pMathTimesForMaster.addFactor(pMathVarCount);
            pMathTimesForSlave.addFactor(pMathVarCount);
            pMathTimesForMaster.addFactor(pMpiMasterDataNumIndexVar);
            pMathTimesForSlave.addFactor(m_pDefinedDataSizeVar);

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecArithVar().get(i),
                    pMathTimesForMaster));
            pSynIfForSlaveNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecArithVar().get(i),
                    pMathTimesForSlave));
        }
        /*微係数変数へのmallocによるメモリ割り当て*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /*データ数を表す数式を生成*/
            Math_times pMathTimesForMaster =
                (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);
            Math_times pMathTimesForSlave =
                    (Math_times)MathFactory.createOperator(eMathOperator.MOP_TIMES);

            Math_cn pMathVarCount =
                (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                        String.valueOf(m_pCellMLAnalyzer.getM_vecDiffVar().size()));

            pMathTimesForMaster.addFactor(pMathVarCount);
            pMathTimesForSlave.addFactor(pMathVarCount);
            pMathTimesForMaster.addFactor(pMpiMasterDataNumIndexVar);
            pMathTimesForSlave.addFactor(m_pDefinedDataSizeVar);

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i),
                    pMathTimesForMaster));
            pSynIfForSlaveNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i),
                    pMathTimesForSlave));
        }
        /*定数へのmallocによるメモリ割り当て*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

            /*データ数を表す変数を生成*/
            Math_cn pMathVarCount =
                (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                        String.valueOf(m_pCellMLAnalyzer.getM_vecConstVar().size()));

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecConstVar().get(i),
                    pMathVarCount));
            pSynIfForSlaveNode.addStatement(createMalloc(m_pTecMLAnalyzer.getM_vecConstVar().get(i),
                    pMathVarCount));
        }


        //----------------------------------------------
        //数式部分の追加
        //----------------------------------------------
        /*外側ループ構文生成・追加*/
        Math_ci pTimeVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pTimeVar().createCopy());
        Math_ci pDeltaVariale = (Math_ci)(m_pTecMLAnalyzer.getM_pDeltaVar().createCopy());

        SyntaxControl pSynFor1ForMaster = createSyntaxTimeLoop(m_dStartTime,
                m_dEndTime, pTimeVariale, pDeltaVariale);
        SyntaxControl pSynFor1ForSlave = createSyntaxTimeLoop(m_dStartTime,
                m_dEndTime, pTimeVariale, pDeltaVariale);
        pSynIfForMasterNode.addStatement(pSynFor1ForMaster);
        pSynIfForSlaveNode.addStatement(pSynFor1ForSlave);

        /*内側ループ構文生成・追加*/
        SyntaxControl pSynFor2ForMaster = createSyntaxDataNumLoop(pMpiMasterDataNumIndexVar, pLoopIndexVar);
        SyntaxControl pSynFor2ForSlave = createLoop(pLoopIndexStartVar,pLoopIndexEndVar, pLoopIndexVar);
        pSynFor1ForMaster.addStatement(pSynFor2ForMaster);
        pSynFor1ForSlave.addStatement(pSynFor2ForSlave);

        //----------------------------------------------
        //数式の生成と追加
        //----------------------------------------------
        /*数式を生成し，取得*/
        Vector<SyntaxExpression> vecExpressionsForMaster = this.createExpressions(pMpiMasterDataNumIndexVar);
        Vector<SyntaxExpression> vecExpressionsForSlave = this.createExpressions(pMpiWorkerDataNumIndexVar);

        /*ループ中に数式を追加*/
        int nExpressionNumForMaster = vecExpressionsForMaster.size();
        int nExpressionNumForSlave = vecExpressionsForSlave.size();

        for (int i = 0; i < nExpressionNumForMaster;i++) {

            /*数式の追加*/
            pSynFor2ForMaster.addStatement(vecExpressionsForMaster.get(i));
        }
        for (int i = 0; i < nExpressionNumForSlave;i++) {

            /*数式の追加*/
            pSynFor2ForSlave.addStatement(vecExpressionsForSlave.get(i));
        }

        for (int i = 0; i < m_pCellMLAnalyzer.getM_vecDiffVar().size(); i++) {
            //TODO
            Math_ci pMpiAllXoIndexVartes =
                    (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                            ALL_XO);
            Math_cn allxoLoopVar = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, ""+ i);

            //all_xo's []
            Math_times tmpTimes = createTimes(allxoLoopVar, m_pDefinedDataSizeVar);
            Math_plus allxo = createPlus(tmpTimes, pLoopIndexVar);

            //set [] to all_xo
            pMpiAllXoIndexVartes.setArrayIndexToBack(allxo);

            /*数式の複製を取得*/
            //MathExpression pMathExp = m_pCellMLAnalyzer.getM_vecDiffExpression().get(i);
            /*左辺式取得*/
            //MathExpression pLeftExp = pMathExp.getLeftExpression();
            //TODO
            Math_ci pMpixo =
                    (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                            MPI_XO);
            Math_times tmpTimes2 = createTimes(allxoLoopVar, pMpiMasterDataNumIndexVar);
            Math_plus xo = createPlus(tmpTimes2, pLoopIndexVar);

            //set [] to all_xo
            pMpixo.setArrayIndexToBack(xo);

            createAssign(pSynFor2ForMaster, pMpiAllXoIndexVartes, pMpixo);

        }

        //create recv loop
        Math_cn workerStartNum = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, WORKER_START_NUM);
        SyntaxControl recvLoop = createLoop(workerStartNum, pMpiCommSizeIndexVar, pMpiWorkerIndexVar);
        pSynFor1ForMaster.addStatement(recvLoop);


        Math_ci pMpiDouble =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        MPI_DOUBLE);
        //MPI_Recv
        //TODO
        Math_ci recvSize = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                            createTimes(pMpiWorkerDataNumIndexVar, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                                    ""+m_pCellMLAnalyzer.getM_vecDiffVar().size())).toLegalString());
        pMpiTmpXoIndexVar.setArrayIndexToBack((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));
        pMpiTmpXoIndexVar.setPointerNum(-1);
        pMpiRecvStatusIndexVar.setPointerNum(-1);
        SyntaxCallFunction pMpiRecv = createMpiRecv(pMpiTmpXoIndexVar, recvSize,
                pMpiDouble, pMpiWorkerIndexVar, pMpiTagIndexVar, pMpiRecvStatusIndexVar);
        recvLoop.addStatement(pMpiRecv);

        // __j = 0;
        createAssign(recvLoop, pLoopIndexVar2, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"));

        // create for loop
        SyntaxControl allxoLoop = createLoop((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"),
                recvSize, pLoopIndexVar);
        recvLoop.addStatement(allxoLoop);

        //all_xo's[]
        Math_plus allxo = createPlus(
                createPlus(
                        createTimes(
                                createMinus(pMpiWorkerIndexVar,
                                        (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1")),
                                        pMpiWorkerDataNumIndexVar),
                                        pMpiMasterDataNumIndexVar),
                                        pLoopIndexVar2);
        pMpiAllXoIndexVar.setArrayIndexToBack(allxo);
        pMpiTmpXoIndexVar.setPointerNum(0);
        pMpiTmpXoIndexVar.setArrayIndexToBack(pLoopIndexVar);
        createAssign(allxoLoop,
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pMpiAllXoIndexVar.toLegalString()),
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pMpiTmpXoIndexVar.toLegalString()));

        //create if for set allxoo
        pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        //create if
        SyntaxControl pSynAllxoIf = createIf(
                createCondition(
                        createRemainder(
                                createPlus(
                                        pLoopIndexVar,
                                        (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1")),
                                        pMpiWorkerDataNumIndexVar),
                                        (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"),
                                        pMathEq));
        //add allxo loop
        allxoLoop.addStatement(pSynAllxoIf);

        // j += __DATA_NUM - __WORKER_DATA_NUM
        createAssign(pSynAllxoIf,
                pLoopIndexVar2,
                createPlus(createMinus(m_pDefinedDataSizeVar, pMpiWorkerDataNumIndexVar), pLoopIndexVar2));

        // j++
        createAssign(allxoLoop, pLoopIndexVar2, createPlus(pLoopIndexVar2, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1")));

        // testCell call loop
        if ( m_isTestGenerate ) {
            //create test loop
            SyntaxControl outputTestLoop = createLoop(
                    (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0"),
                    (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                            createTimes(m_pDefinedDataSizeVar, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                                    ""+m_pCellMLAnalyzer.getM_vecDiffVar().size())).toLegalString()),
                                    pLoopIndexVar);
            //add test loop
            pSynFor1ForMaster.addStatement(outputTestLoop);

            pMpiAllXoIndexVar.setArrayIndexToBack(pLoopIndexVar);
            outputTestLoop.addStatement(m_testFunc.callFunction(
                    (Math_cn) MathFactory.createOperand(eMathOperand.MOPD_CN, ALLXO_TEST_NAME),
                    (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI, pMpiAllXoIndexVar.toLegalString()),
                    (Math_cn) MathFactory.createOperand(eMathOperand.MOPD_CN, TEST_DIFF)));
        }


        //MPI_Send
        //TODO
        Math_ci pMpixo =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                        MPI_XO);
        pMpixo.setArrayIndexToBack(pLoopIndexStartVar);
        pMpixo.setPointerNum(-1);

        Math_ci sendSize = (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                            createTimes(pMpiWorkerDataNumIndexVar, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN,
                                    ""+m_pCellMLAnalyzer.getM_vecDiffVar().size())).toLegalString());
        SyntaxCallFunction pMpiSend = createMpiSend(pMpixo, sendSize, pMpiDouble,
                m_pDefinedDataSizeVar2, pMpiTagIndexVar);

        pSynFor1ForSlave.addStatement(pMpiSend);

        //----------------------------------------------
        //free関数呼び出しの追加
        //----------------------------------------------
        /*微分変数のfree*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDiffVar().size(); i++) {
            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecDiffVar().get(i)));
            pSynIfForSlaveNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecDiffVar().get(i)));
        }
        /*一時変数のfree*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecArithVar().size(); i++) {

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecArithVar().get(i)));
            pSynIfForSlaveNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecArithVar().get(i)));
        }
        /*微係数変数のfree*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecDerivativeVar().size(); i++) {

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i)));
            pSynIfForSlaveNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecDerivativeVar().get(i)));
        }
        /*定数のfree*/
        for (int i = 0; i < m_pTecMLAnalyzer.getM_vecConstVar().size(); i++) {

            /*宣言の追加*/
            pSynIfForMasterNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecConstVar().get(i)));
            pSynIfForSlaveNode.addStatement(createFree(m_pTecMLAnalyzer.getM_vecConstVar().get(i)));
        }
        //free all_xo
        pMpiAllXoIndexVar.clearArrayIndex();
        pMpiAllXoIndexVar.setPointerNum(0);
        pSynIfForMasterNode.addStatement(createFree(pMpiAllXoIndexVar));

        //free tmp_xo
        pMpiTmpXoIndexVar.clearArrayIndex();
        pSynIfForMasterNode.addStatement(createFree(pMpiTmpXoIndexVar));

        //MPI_Finalize
        pSynMainFunc.addStatement(this.createMpiFinalize());

        pMpiRecvStatusIndexVar.setPointerNum(0);

        return pSynMainFunc;
    }

    /**
     * mpi value create
     * @param SyntaxFunction pSynMainFunc
     * @throws MathException
     *
     * @author Yuu Shigetani
     */
    public void mpiValCreateFunction(SyntaxFunction pSynMainFunc) throws MathException {
        /*
         * Assign all nodes result
         * double* all_xo;
         */
        createDoubleVal(pSynMainFunc, ALL_XO, 1, 0, false);

        /*
         * end time
         * double end_time = 400.0;
         */
        createDoubleVal(pSynMainFunc, END_TIME, 0, END_TIME_NUM, false);

        /*
         * Node number(Master:0, Worker:0~comm_size-1)
         * int node;
         */
        createIntVal(pSynMainFunc, NODE, 0, 0, false);

        /*
         * Tag for MPI_Send and MPI_Recv
         * int tag=0;
         */
        createIntVal(pSynMainFunc, TAG, 0, TAG_NUM, true);

        /*
         * Node number of a worker
         * int worker;
         */
        createIntVal(pSynMainFunc, WORKER, 0, 0, false);

        /*
         * MPI_Recv status
         * MPI_Status recv_status;
         */
        Math_ci pMpiIndexVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,RECV_STATUS);

        /* 変数を関数に宣言追加 */
        {
            SyntaxDataType pSynTypeInt = new SyntaxDataType(eDataType.DT_MPISTA, 0);
            SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeInt, pMpiIndexVar);

            pSynMainFunc.addDeclaration(pDecVar);
        }

        /*
         * Master data size
         * int __MASTER_DATA_NUM;
         */
        createIntVal(pSynMainFunc, MASTER_DATA_NUM, 0, 0, false);

        /*
         * Worker data size
         * int __WORKER_DATA_NUM;
         */
        createIntVal(pSynMainFunc, WORKER_DATA_NUM, 0, 0, false);

    }

}
