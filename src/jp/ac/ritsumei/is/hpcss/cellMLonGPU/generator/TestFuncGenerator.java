package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_inc;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_minus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_neq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
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
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility.StringUtil;

/**
 * MPIメイン関数構文生成クラス
 * MPIProgramGeneratorクラスからメイン関数生成部を切り離したクラス
 */
public class TestFuncGenerator extends ProgramGenerator {

    //========================================================
    //DEFINE
    //========================================================
    private static final int BUFF_MAX_NUM = 100;
    private static final int TEST_MAX_NUM = 10;
    private static final String BUFF_MAX = "BUFF_MAX";
    private static final String TEST_MAX = "TEST_MAX";
    private static final String FILE_POINTER_NAME = "fp";
    private static final String HASH_ENTRY_NAME = "e";
    private static final String HASH_ENTRY_POINTER_NAME = "ep";
    private static final String OPTION_NAME = "op";
    private static final String NODE_NAME = "node";
    private static final String TEST_INIT_FUNC_NAME = "testInit";
    private static final String TEST_FUNC_NAME = "testCell";
    private static final String FILE_NAME = "fileName";
    private static final String STRLEN_FUNC_NAME = "strlen";
    private static final String DATA_PLACE_NAME = "\"data/node.dat\"";
    private static final String DATA_PLACE_NAME_EACH_NODE = "\"data/node%d.dat\"";
    private static final String EXIT_FUNC_NAME = "exit";
    private static final String EXIT_FAILURE_NAME = "EXIT_FAILURE";
    private static final String SPRINTF_FUNC_NAME = "sprintf";
    private static final String FPRINTF_FUNC_NAME = "fprintf";
    private static final String HCREATE_FUNC_NAME = "hcreate";
    private static final String STRDUP_FUNC_NAME = "strdup";
    private static final String HSEARCH_FUNC_NAME = "hsearch";
    private static final String FSEEK_FUNC_NAME = "fseek";
    private static final String SIZEOF_FUNC_NAME = "sizeof";
    private static final String FGETS_FUNC_NAME = "fgets";
    private static final String WRITE_MODE = "\'w\'";
    private static final String TEST_MODE = "\'t\'";
    private static final String FOPEN_FUNC_NAME = "fopen";
    private static final String TEST_NAME = "testName";
    private static final String CELL_NAME = "cell";
    private static final String DIFF_NAME = "diff";
    private static final String TAG_NAME = "tag";
    private static final String BUFF_NAME = "buff";
    private static final String CNT_NAME = "cnt";
    private static final String F_CELL_NAME = "f_cell";
    private static final String HASH_MODE_FIND = "FIND";
    private static final String HASH_MODE_ENTER = "ENTER";
    private static final String SEEK_SET = "SEEK_SET";

    private SyntaxProgram t_pSynProgram;

    /*共通変数*/
    protected Math_ci t_pDefinedBuffMaxVar;
    protected Math_ci t_pDefinedTestMaxVar;
    protected Math_ci t_pFilePointer;
    protected Math_ci t_pHashEntry;
    protected Math_ci t_pHashEntryPointer;
    protected Math_ci t_pOption;

    /*-----コンストラクタ-----*/
    public TestFuncGenerator(CellMLAnalyzer pCellMLAnalyzer,
            RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer, SyntaxProgram pSynProgram)
    throws MathException, CellMLException, RelMLException, TranslateException, SyntaxException {
        super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
        t_pSynProgram = pSynProgram;
        initialize();
        getSyntaxProgram();
    }

    //========================================================
    //initialize
    // 初期化メソッド
    //
    //========================================================
    /*-----初期化・終了処理メソッド-----*/
    protected void initialize() throws MathException {
        t_pDefinedBuffMaxVar =
            (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                               BUFF_MAX);
        t_pDefinedTestMaxVar =
                (Math_ci)MathFactory.createOperand(eMathOperand.MOPD_CI,
                                   TEST_MAX);
    }

    @Override
    public SyntaxProgram getSyntaxProgram() throws MathException,
            CellMLException, RelMLException, TranslateException,
            SyntaxException {

        /* includeの追加 */
        SyntaxPreprocessor pSynInclude1 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "string.h");
        SyntaxPreprocessor pSynInclude2 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_INCLUDE_ABS, "search.h");

        t_pSynProgram.addPreprocessor(pSynInclude1);
        t_pSynProgram.addPreprocessor(pSynInclude2);

        /* データ数定義defineの追加 */
        // BUFF_MAX 100
        String strElementNum = String.valueOf(BUFF_MAX_NUM);
        SyntaxPreprocessor pSynDefine1 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
                           t_pDefinedBuffMaxVar.toLegalString() + " "
                           + strElementNum);
        t_pSynProgram.addPreprocessor(pSynDefine1);
        // TEST_MAX 10
        strElementNum = String.valueOf(TEST_MAX_NUM);
        SyntaxPreprocessor pSynDefine2 =
            new SyntaxPreprocessor(ePreprocessorKind.PP_DEFINE,
                           t_pDefinedTestMaxVar.toLegalString() + " "
                           + strElementNum);
        t_pSynProgram.addPreprocessor(pSynDefine2);

        /* global変数追加 */
        // FILE *fp;
        t_pFilePointer = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, FILE_POINTER_NAME);
        SyntaxDataType pSynTypeFileForFilePointer = new SyntaxDataType(eDataType.DT_FILE, 1);
        SyntaxDeclaration pDecFilePointerVar = new SyntaxDeclaration(pSynTypeFileForFilePointer, t_pFilePointer);
        t_pSynProgram.addDeclaration(pDecFilePointerVar);
        // ENTRY e;
        t_pHashEntry = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, HASH_ENTRY_NAME);
        SyntaxDataType pSynTypeFileHashEntry = new SyntaxDataType(eDataType.DT_ENTRY, 0);
        SyntaxDeclaration pDecHashEntryVar = new SyntaxDeclaration(pSynTypeFileHashEntry, t_pHashEntry);
        t_pSynProgram.addDeclaration(pDecHashEntryVar);
        // ENTRY *ep;
        t_pHashEntryPointer = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, HASH_ENTRY_POINTER_NAME);
        SyntaxDataType pSynTypeFileHashEntryPointer = new SyntaxDataType(eDataType.DT_ENTRY, 1);
        SyntaxDeclaration pDecHashEntryPointerVar = new SyntaxDeclaration(pSynTypeFileHashEntryPointer, t_pHashEntryPointer);
        t_pSynProgram.addDeclaration(pDecHashEntryPointerVar);
        // char **op;
        t_pOption = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, OPTION_NAME);
        SyntaxDataType pSynTypeOption = new SyntaxDataType(eDataType.DT_CHAR, 0);
        SyntaxDeclaration pDecOptionVar = new SyntaxDeclaration(pSynTypeOption, t_pOption);
        t_pSynProgram.addDeclaration(pDecOptionVar);

        return t_pSynProgram;
    }

    //========================================================
    //getSyntaxTestInitFunction
    // メイン関数構文を生成し，返す
    //
    //@return
    // 関数構文インスタンス	: SyntaxFunction*
    //
    //@throws
    // MathException
    // SyntaxException
    // TranslateException
    //
    //========================================================
    /*-----テスト初期化プログラム構文取得メソッド-----*/
    public SyntaxFunction getSyntaxTestInitFunction()
    throws MathException, SyntaxException, TranslateException {

        // arg : char** argv
        Math_ci pArgvArgVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, PROG_VAR_STR_ARGV);
        SyntaxDeclaration pSynArgArgv = this.createArg(eDataType.DT_CHAR, 2, pArgvArgVar);
        // arg : int node
        Math_ci pNodeArgVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, NODE_NAME);
        SyntaxDeclaration pSynArgNode = this.createArg(eDataType.DT_INT, 0, pNodeArgVar);
        // create testInit function
        SyntaxDataType pSynReturnTypeTestInitFunc = new SyntaxDataType(eDataType.DT_VOID, 0);
        SyntaxFunction pSynTestInitFunc = this.createFunction(pSynReturnTypeTestInitFunc, TEST_INIT_FUNC_NAME, pSynArgArgv, pSynArgNode);
        t_pSynProgram.addFunction(pSynTestInitFunc);

        // char *fileName;
        Math_ci pTestFileNameVar = createCharVal(pSynTestInitFunc, FILE_NAME, 1, null);

        // fileName = (char*)malloc( sizeof(char) * (strlen( "data/node.dat" ) + sizeof(int)) );
        Math_ci pDataPlace = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, DATA_PLACE_NAME);
        Math_ci pStrlenDataPlace = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createStrlen(pDataPlace).toLegalStringWithNoSemicolon());
        createCharMalloc(pSynTestInitFunc, pTestFileNameVar, createPlus(pStrlenDataPlace, createSizeofChar()));

        //create if option not equal null
        Math_cn condNull = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "NULL");
        Math_neq pMathNeq = (Math_neq)MathFactory.createOperator(eMathOperator.MOP_NEQ);
        //create cond
        Math_ci pArgvArray = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, PROG_VAR_STR_ARGV);
        pArgvArray.addArrayIndexToBack(1);
        MathFactor pSynIfCond = createCondition(pArgvArray, condNull, pMathNeq);
        //create if
        SyntaxControl pSynIfOpNotNull = createIf(pSynIfCond);
        //add main func
        pSynTestInitFunc.addStatement(pSynIfOpNotNull);

        // *op++;
        Math_ci pArgvWithOnePointer = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, PROG_VAR_STR_ARGV);
        pArgvWithOnePointer.setPointerNum(1);
        createAssign(pSynIfOpNotNull, pArgvWithOnePointer, createPlus(pArgvWithOnePointer, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1")));

        //create if option equal hyphen
        Math_cn condHyphen = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "\'-\'");
        Math_eq pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        //create cond
        Math_ci pArgvWithTwoPointer = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, PROG_VAR_STR_ARGV);
        pArgvWithTwoPointer.setPointerNum(2);
        pSynIfCond = createCondition(pArgvWithTwoPointer, condHyphen, pMathEq);
        //create if
        SyntaxControl pSynIfOpEqualHyphen = createIf(pSynIfCond);
        //add if
        pSynIfOpNotNull.addStatement(pSynIfOpEqualHyphen);


        //create if equal t
        Math_cn condT= (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, TEST_MODE);
        //create cond
        pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        Math_plus pOptionWithOnePointerPlusOne = createPlus(pArgvWithOnePointer, (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "1"));
        Math_ci pOptionWithOnePointerPlusOnePointer = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, pOptionWithOnePointerPlusOne.toLegalString());
        pOptionWithOnePointerPlusOnePointer.setPointerNum(1);
        createAssign(pSynIfOpEqualHyphen, t_pOption, pOptionWithOnePointerPlusOnePointer);
        pSynIfCond = createCondition(t_pOption, condT, pMathEq);
        //create if
        SyntaxControl pSynIfOpEqualT= createIf(pSynIfCond);
        //add if
        pSynIfOpEqualHyphen.addStatement(pSynIfOpEqualT);
        // sprintf
        pSynIfOpEqualT.addStatement(createSprintf(pTestFileNameVar, pNodeArgVar));
        // fopen
        createAssign(pSynIfOpEqualT, t_pFilePointer, (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createFopen(pTestFileNameVar, "\"r\"").toLegalStringWithNoSemicolon()));

        //create elseif equal w
        Math_cn condW = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, WRITE_MODE);
        //create cond
        pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        pSynIfCond = createCondition(t_pOption, condW, pMathEq);
        //create elseif
        SyntaxControl pSynIfOpEqualW = createElseIf(pSynIfCond);
        //add if
        pSynIfOpEqualHyphen.addStatement(pSynIfOpEqualW);
        // sprintf
        pSynIfOpEqualW.addStatement(createSprintf(pTestFileNameVar, pNodeArgVar));
        // fopen
        createAssign(pSynIfOpEqualW, t_pFilePointer, (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createFopen(pTestFileNameVar, "\"w\"").toLegalStringWithNoSemicolon()));

        //create else
        SyntaxControl pSynElseOp = createElse();
        pSynIfOpEqualHyphen.addStatement(pSynElseOp);
        pSynElseOp.addStatement(createExit());

        // free( filename );
        pSynTestInitFunc.addStatement(createFree(pTestFileNameVar));

        return pSynTestInitFunc;
    }

    //========================================================
    //getSyntaxTestFunction
    // テスト関数構文を生成し，返す
    //
    //@return
    // 関数構文インスタンス	: SyntaxFunction*
    //
    //@throws
    // MathException
    // SyntaxException
    // TranslateException
    //
    //========================================================
    /*-----テストプログラム構文取得メソッド-----*/
    public SyntaxFunction getSyntaxTestFunction()
    throws MathException, SyntaxException, TranslateException {
        // arg : char* testNamea
        Math_ci pTestNameArgVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, TEST_NAME);
        SyntaxDeclaration pSynArgTestName = this.createArg(eDataType.DT_CHAR, 1, pTestNameArgVar);
        // arg : douvle cell 
        Math_ci pCellArgVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, CELL_NAME);
        SyntaxDeclaration pSynArgCell = this.createArg(eDataType.DT_DOUBLE, 0, pCellArgVar);
        // arg : douvle diff
        Math_ci pDiffArgVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, DIFF_NAME);
        SyntaxDeclaration pSynArgDiff = this.createArg(eDataType.DT_DOUBLE, 0, pDiffArgVar);
        // create testCell function
        SyntaxDataType pSynReturnTypeTestInitFunc = new SyntaxDataType(eDataType.DT_VOID, 0);
        SyntaxFunction pSynTestFunc = this.createFunction(pSynReturnTypeTestInitFunc, TEST_FUNC_NAME, pSynArgTestName, pSynArgCell, pSynArgDiff);
        t_pSynProgram.addFunction(pSynTestFunc);

        // char *tag;
        Math_ci pTagVar = createCharVal(pSynTestFunc, TAG_NAME, 1, null);
        // char buff[BUFF_MAX];
        Math_ci pBuffVar = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, BUFF_NAME);
        pBuffVar.addArrayIndexToBack(t_pDefinedBuffMaxVar);
        SyntaxDataType pSynTypeChar= new SyntaxDataType(
                eDataType.DT_CHAR, 0);
        SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeChar,
                pBuffVar);
        pSynTestFunc.addDeclaration(pDecVar);
        // int cnt=0;
        Math_ci pCntVar = createIntVal(pSynTestFunc, CNT_NAME, 0, 0, true);
        // double f_cell;
        Math_ci pFCellVar = createDoubleVal(pSynTestFunc, F_CELL_NAME, 0, 0, false);

        // tag = (char*)malloc( sizeof(char) * (strlen( BUFF_MAX ) - sizeof(double)) );
        createCharMalloc(pSynTestFunc, pTagVar, createMinus(t_pDefinedBuffMaxVar, createSizeofDouble()));

        //create if equal t
        Math_cn condT = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, TEST_MODE);
        //create cond
        Math_eq pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        MathFactor pSynIfCond = createCondition(t_pOption, condT, pMathEq);
        //create if
        SyntaxControl pSynIfOpEqualT= createIf(pSynIfCond);
        //add if
        pSynTestFunc.addStatement(pSynIfOpEqualT);
        // hcreate(TEST_NUM);
        pSynIfOpEqualT.addStatement(createHcreate(t_pDefinedTestMaxVar));
        // e.key = strdup(testName);
        createAssign(pSynIfOpEqualT, t_pHashEntry, (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createStrdup(pTestNameArgVar).toLegalStringWithNoSemicolon()));
        // ep = hsearch(e, FIND);
        createAssign(pSynIfOpEqualT, t_pHashEntryPointer, (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createHsearch(t_pHashEntry, HASH_MODE_FIND).toLegalStringWithNoSemicolon()));

        //create if ep not equal null
        Math_cn condEp = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "NULL");
        //create cond
        Math_neq pMathNeq = (Math_neq)MathFactory.createOperator(eMathOperator.MOP_NEQ);
        pSynIfCond = createCondition(t_pHashEntryPointer, condEp, pMathNeq);
        //create if
        SyntaxControl pSynIfEpEqualTrue = createIf(pSynIfCond);
        //add if
        pSynIfOpEqualT.addStatement(pSynIfEpEqualTrue);

        // (int)(ep->data) = (int)(ep->data) + 1;
        // cnt = (int)(ep->data);

        //create else 
        SyntaxControl pSynIfEpNotEqualTrue = createElse();
        //add else
        pSynIfOpEqualT.addStatement(pSynIfEpNotEqualTrue);
        // ep = hsearch(e, ENTER);
        createAssign(pSynIfEpNotEqualTrue, t_pHashEntryPointer, (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createHsearch(t_pHashEntry, HASH_MODE_ENTER).toLegalStringWithNoSemicolon()));

        // fseek
        pSynIfOpEqualT.addStatement(createFseek(t_pFilePointer));

        // while
        Math_cn condFgets = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "NULL");
        Math_ci pSizeofBuff = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createSizeof(pBuffVar).toLegalStringWithNoSemicolon());
        Math_ci pSynFgets = (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, createFgets(pBuffVar, pSizeofBuff, t_pFilePointer).toLegalStringWithNoSemicolon());
        MathFactor pSynWhileCond = createCondition(pSynFgets, condFgets, pMathNeq);
        SyntaxControl pSynWhile = createWhile(pSynWhileCond);
        pSynIfOpEqualT.addStatement(pSynWhile);

        //create elseif equal w
        Math_cn condW = (Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, WRITE_MODE);
        //create cond
        pMathEq = (Math_eq)MathFactory.createOperator(eMathOperator.MOP_EQ);
        MathFactor pSynElseIfCond = createCondition(t_pOption, condW, pMathEq);
        //create elseif
        SyntaxControl pSynElseIfOpEqualW = createElseIf(pSynElseIfCond);
        //add elseif
        pSynTestFunc.addStatement(pSynElseIfOpEqualW);
        // fprintf();
        pSynElseIfOpEqualW.addStatement(createFprintfCall(t_pFilePointer, "\"%lf%s\\n\"", pCellArgVar, pTestNameArgVar));

        // free( tag );
        pSynTestFunc.addStatement(createFree(pTagVar));

        return pSynTestFunc;
    }

    public SyntaxCallFunction createSizeof(Math_ci sizeofArg) throws MathException {
        SyntaxCallFunction pSynSizeof =
                new SyntaxCallFunction(SIZEOF_FUNC_NAME);
        pSynSizeof.addArgFactor(sizeofArg);

        return pSynSizeof;
    }
    public Math_ci createSizeofChar() throws MathException {
        return (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, "sizeof( char )");
    }
    public Math_ci createSizeofInt() throws MathException {
        return (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, "sizeof( int )");
    }
    public Math_ci createSizeofDouble() throws MathException {
        return (Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, "sizeof( double )");
    }
    private SyntaxCallFunction createStrlen(Math_ci pStr) {
        SyntaxCallFunction pSynStrlenCall =
            new SyntaxCallFunction(STRLEN_FUNC_NAME);
        pSynStrlenCall.addArgFactor(pStr);

        return pSynStrlenCall;
    }
    private SyntaxCallFunction createExit() throws MathException {
        SyntaxCallFunction pSynExitCall =
            new SyntaxCallFunction(EXIT_FUNC_NAME);
        pSynExitCall.addArgFactor((Math_ci) MathFactory.createOperand(
                eMathOperand.MOPD_CI, EXIT_FAILURE_NAME));

        return pSynExitCall;
    }
    private SyntaxCallFunction createSprintf(Math_ci fileNameBuff, Math_ci node) throws MathException {
        SyntaxCallFunction pSynSprintfCall =
                new SyntaxCallFunction(SPRINTF_FUNC_NAME);
        pSynSprintfCall.addArgFactor(fileNameBuff);
        pSynSprintfCall.addArgFactor((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, DATA_PLACE_NAME_EACH_NODE));
        pSynSprintfCall.addArgFactor(node);

        return pSynSprintfCall;
    }
    private SyntaxCallFunction createFprintfCall(Math_ci filePointer, String rowString, Math_ci... args) throws MathException {
        SyntaxCallFunction pSynFprintfCall =
                new SyntaxCallFunction(FPRINTF_FUNC_NAME);
        pSynFprintfCall.addArgFactor(filePointer);
        pSynFprintfCall.addArgFactor((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, rowString));
        for (int i=0; i < args.length; i++){
            pSynFprintfCall.addArgFactor(args[i]);
        }

        return pSynFprintfCall;
    }
    private SyntaxCallFunction createFopen(Math_ci fileName, String fileMode) throws MathException {
        SyntaxCallFunction pSynFopenCall =
                new SyntaxCallFunction(FOPEN_FUNC_NAME);
        pSynFopenCall.addArgFactor(fileName);
        pSynFopenCall.addArgFactor((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, fileMode));

        return pSynFopenCall;
    }
    private SyntaxCallFunction createHcreate(Math_ci testMaxNum) throws MathException {
        SyntaxCallFunction pSynHcreate =
                new SyntaxCallFunction(HCREATE_FUNC_NAME);
        pSynHcreate.addArgFactor(testMaxNum);

        return pSynHcreate;
    }
    private SyntaxCallFunction createStrdup(Math_ci testName) throws MathException {
        SyntaxCallFunction pSynStrdup =
                new SyntaxCallFunction(STRDUP_FUNC_NAME);
        pSynStrdup.addArgFactor(testName);

        return pSynStrdup;
    }
    private SyntaxCallFunction createHsearch(Math_ci entryVar, String hashMode) throws MathException {
        SyntaxCallFunction pSynHsearch =
                new SyntaxCallFunction(HSEARCH_FUNC_NAME);
        pSynHsearch.addArgFactor(entryVar);
        pSynHsearch.addArgFactor((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, hashMode));

        return pSynHsearch;
    }
    private SyntaxCallFunction createFseek(Math_ci filePointer) throws MathException {
        SyntaxCallFunction pSynHsearch =
                new SyntaxCallFunction(FSEEK_FUNC_NAME);
        pSynHsearch.addArgFactor(filePointer);
        pSynHsearch.addArgFactor((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, "0L"));
        pSynHsearch.addArgFactor((Math_cn)MathFactory.createOperand(eMathOperand.MOPD_CN, SEEK_SET));

        return pSynHsearch;
    }
    private SyntaxCallFunction createFgets(Math_ci buff, Math_ci buffSize, Math_ci filePointer) throws MathException {
        SyntaxCallFunction pSynFgets =
                new SyntaxCallFunction(FGETS_FUNC_NAME);
        pSynFgets.addArgFactor(buff);
        pSynFgets.addArgFactor(buffSize);
        pSynFgets.addArgFactor(filePointer);

        return pSynFgets;
    }
}
