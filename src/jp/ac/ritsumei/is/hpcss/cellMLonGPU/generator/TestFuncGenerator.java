package jp.ac.ritsumei.is.hpcss.cellMLonGPU.generator;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.SyntaxException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TranslateException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathFactory;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_cn;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_eq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_leq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_lt;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_neq;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_plus;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.CellMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.RelMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser.TecMLAnalyzer;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxCallFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxControl;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDataType.eDataType;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxDeclaration;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxFunction;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxPreprocessor.ePreprocessorKind;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.syntax.SyntaxProgram;

/**
 * MPIメイン関数構文生成クラス MPIProgramGeneratorクラスからメイン関数生成部を切り離したクラス
 */
public class TestFuncGenerator extends ProgramGenerator {

    // ========================================================
    // DEFINE
    // ========================================================
    private static final int BUFF_MAX_NUM = 100;
    private static final int TEST_MAX_NUM = 20;
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE_NAME = "node";
    private static final String DATA_FILE_EXT = ".dat";
    private static final String SEPARATER = "/";
    private static final String WRITE_MODE = "w";
    private static final String TEST_MODE = "t";
    private static final String TEST_INIT_FUNC_NAME = "testInit";
    private static final String TEST_FUNC_NAME = "testCell";
    private static final String BUFF_MAX = "BUFF_MAX";
    private static final String TEST_MAX = "TEST_MAX";
    private static final String FILE_POINTER_NAME = "fp";
    private static final String HASH_ENTRY_NAME = "e";
    private static final String HASH_ENTRY_POINTER_NAME = "ep";
    private static final String OPTION_NAME = "op";
    private static final String NODE_NAME = "node";
    private static final String FILE_NAME = "fileName";
    private static final String TEST_NAME = "testName";
    private static final String CELL_NAME = "cell";
    private static final String DIFF_NAME = "diff";
    private static final String TAG_NAME = "tag";
    private static final String BUFF_NAME = "buff";
    private static final String CNT_NAME = "cnt";
    private static final String F_CELL_NAME = "f_cell";
    private static final String SUCCESS_MSG = "\\x1b[32m %s success\\n";
    private static final String FAIL_MSG = "\\x1b[31m %s fail | input=%lf : file=%lf\\n";
    private Math_cn MathCn0 = createMathCn("0");
    private Math_cn MathCn1 = createMathCn("1");
    private Math_cn MathCnNull = createMathCn("NULL");

    private SyntaxProgram t_pSynProgram;

    /* 共通変数 */
    protected Math_ci t_pDefinedBuffMaxVar;
    protected Math_ci t_pDefinedTestMaxVar;
    protected Math_ci t_pFilePointer;
    protected Math_ci t_pHashEntry;
    protected Math_ci t_pHashEntryPointer;
    protected Math_ci t_pOption;

    /*-----コンストラクタ-----*/
    public TestFuncGenerator(CellMLAnalyzer pCellMLAnalyzer,
            RelMLAnalyzer pRelMLAnalyzer, TecMLAnalyzer pTecMLAnalyzer,
            SyntaxProgram pSynProgram) throws MathException, CellMLException,
            RelMLException, TranslateException, SyntaxException {
        super(pCellMLAnalyzer, pRelMLAnalyzer, pTecMLAnalyzer);
        t_pSynProgram = pSynProgram;
        initialize();
        getSyntaxProgram();
    }

    // ========================================================
    // initialize
    // 初期化メソッド
    //
    // ========================================================
    /*-----初期化・終了処理メソッド-----*/
    protected void initialize() throws MathException {
        t_pDefinedBuffMaxVar = createMathCi(BUFF_MAX);
        t_pDefinedTestMaxVar = createMathCi(TEST_MAX);
    }

    @Override
    public SyntaxProgram getSyntaxProgram() throws MathException,
            CellMLException, RelMLException, TranslateException,
            SyntaxException {

        /* includeの追加 */
        SyntaxPreprocessor pSynInclude1 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_INCLUDE_ABS, "string.h");
        SyntaxPreprocessor pSynInclude2 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_INCLUDE_ABS, "search.h");
        SyntaxPreprocessor pSynInclude3 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_INCLUDE_ABS, "sys/stat.h");

        t_pSynProgram.addPreprocessor(pSynInclude1);
        t_pSynProgram.addPreprocessor(pSynInclude2);
        t_pSynProgram.addPreprocessor(pSynInclude3);

        /* データ数定義defineの追加 */
        // BUFF_MAX 100
        String strElementNum = String.valueOf(BUFF_MAX_NUM);
        SyntaxPreprocessor pSynDefine1 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_DEFINE,
                t_pDefinedBuffMaxVar.toLegalString() + " " + strElementNum);
        t_pSynProgram.addPreprocessor(pSynDefine1);

        // TEST_MAX 10
        strElementNum = String.valueOf(TEST_MAX_NUM);
        SyntaxPreprocessor pSynDefine2 = new SyntaxPreprocessor(
                ePreprocessorKind.PP_DEFINE,
                t_pDefinedTestMaxVar.toLegalString() + " " + strElementNum);
        t_pSynProgram.addPreprocessor(pSynDefine2);

        /* global変数追加 */
        // FILE *fp;
        t_pFilePointer = createMathCi(FILE_POINTER_NAME);
        SyntaxDataType pSynTypeFileForFilePointer = new SyntaxDataType(
                eDataType.DT_FILE, 1);
        SyntaxDeclaration pDecFilePointerVar = new SyntaxDeclaration(
                pSynTypeFileForFilePointer, t_pFilePointer);
        t_pSynProgram.addDeclaration(pDecFilePointerVar);

        // ENTRY e;
        t_pHashEntry = createMathCi(HASH_ENTRY_NAME);
        SyntaxDataType pSynTypeFileHashEntry = new SyntaxDataType(
                eDataType.DT_ENTRY, 0);
        SyntaxDeclaration pDecHashEntryVar = new SyntaxDeclaration(
                pSynTypeFileHashEntry, t_pHashEntry);
        t_pSynProgram.addDeclaration(pDecHashEntryVar);

        // ENTRY *ep;
        t_pHashEntryPointer = createMathCi(HASH_ENTRY_POINTER_NAME);
        SyntaxDataType pSynTypeFileHashEntryPointer = new SyntaxDataType(
                eDataType.DT_ENTRY, 1);
        SyntaxDeclaration pDecHashEntryPointerVar = new SyntaxDeclaration(
                pSynTypeFileHashEntryPointer, t_pHashEntryPointer);
        t_pSynProgram.addDeclaration(pDecHashEntryPointerVar);

        // char **op;
        t_pOption = createMathCi(OPTION_NAME);
        SyntaxDataType pSynTypeOption = new SyntaxDataType(eDataType.DT_CHAR, 0);
        SyntaxDeclaration pDecOptionVar = new SyntaxDeclaration(pSynTypeOption,
                t_pOption);
        t_pSynProgram.addDeclaration(pDecOptionVar);

        return t_pSynProgram;
    }

    // ========================================================
    // getSyntaxTestInitFunction
    // メイン関数構文を生成し，返す
    //
    // @return
    // 関数構文インスタンス : SyntaxFunction*
    //
    // @throws
    // MathException
    // SyntaxException
    // TranslateException
    //
    // ========================================================
    /*-----テスト初期化プログラム構文取得メソッド-----*/
    public SyntaxFunction getSyntaxTestInitFunction() throws MathException,
            SyntaxException, TranslateException {

        // arg : char** argv
        Math_ci pArgvArgVar = createMathCi(PROG_VAR_STR_ARGV);
        SyntaxDeclaration pSynArgArgv = this.createArg(eDataType.DT_CHAR, 2,
                pArgvArgVar);

        // arg : int node
        Math_ci pNodeArgVar = createMathCi(NODE_NAME);
        SyntaxDeclaration pSynArgNode = this.createArg(eDataType.DT_INT, 0,
                pNodeArgVar);

        // create testInit function
        SyntaxDataType pSynReturnTypeTestInitFunc = new SyntaxDataType(
                eDataType.DT_VOID, 0);
        SyntaxFunction pSynTestInitFunc = this.createFunction(
                pSynReturnTypeTestInitFunc, TEST_INIT_FUNC_NAME, pSynArgArgv,
                pSynArgNode);
        t_pSynProgram.addFunction(pSynTestInitFunc);

        // char *fileName;
        Math_ci pTestFileNameVar = createCharVal(pSynTestInitFunc, FILE_NAME,
                1, null);

        // fileName = (char*)malloc( sizeof(char) * (strlen( "data/node.dat" ) +
        // sizeof(int)) );
        Math_ci pDataPlace = createMathCi(createStr(
                DATA_DIR + SEPARATER + DATA_FILE_NAME + DATA_FILE_EXT)
                .toLegalString());
        Math_ci pStrlenDataPlace = createMathCi(createStrlen(pDataPlace)
                .toLegalStringWithNoSemicolon());
        createCharMalloc(pSynTestInitFunc, pTestFileNameVar,
                createPlus(pStrlenDataPlace, createSizeofChar()));

        // create if option not equal null
        Math_cn condNull = MathCnNull;
        Math_neq pMathNeq = (Math_neq) MathFactory
                .createOperator(eMathOperator.MOP_NEQ);

        // create cond
        Math_ci pArgvArray = createMathCi(PROG_VAR_STR_ARGV);
        pArgvArray.addArrayIndexToBack(1);

        // create if
        SyntaxControl pSynIfOpNotNull = createIf(createCondition(pArgvArray,
                condNull, pMathNeq));

        // add main func
        pSynTestInitFunc.addStatement(pSynIfOpNotNull);

        // *op++;
        Math_ci pArgvWithOnePointer = createMathCi(PROG_VAR_STR_ARGV);
        pArgvWithOnePointer.setPointerNum(1);
        createIncExp(pSynIfOpNotNull, pArgvWithOnePointer);

        // create if option equal hyphen
        Math_eq pMathEq = (Math_eq) MathFactory
                .createOperator(eMathOperator.MOP_EQ);

        // create cond
        Math_ci pArgvWithTwoPointer = createMathCi(PROG_VAR_STR_ARGV);
        pArgvWithTwoPointer.setPointerNum(2);

        // create if
        SyntaxControl pSynIfOpEqualHyphen = createIf(createCondition(
                pArgvWithTwoPointer, createMathCn(createChar("-")
                        .toLegalString()), pMathEq));

        // add if
        pSynIfOpNotNull.addStatement(pSynIfOpEqualHyphen);

        // create if equal t
        // create cond
        pMathEq = (Math_eq) MathFactory.createOperator(eMathOperator.MOP_EQ);
        Math_ci pOptionWithOnePointerPlusOnePointer = createMathCi(createPlus(
                pArgvWithOnePointer, MathCn1).toLegalString());
        pOptionWithOnePointerPlusOnePointer.setPointerNum(1);
        createAssign(pSynIfOpEqualHyphen, t_pOption,
                pOptionWithOnePointerPlusOnePointer);

        // create if
        SyntaxControl pSynIfOpEqualT = createIf(createCondition(t_pOption,
                createChar(TEST_MODE), pMathEq));

        // add if
        pSynIfOpEqualHyphen.addStatement(pSynIfOpEqualT);

        // sprintf
        pSynIfOpEqualT
                .addStatement(createSprintf(pTestFileNameVar, pNodeArgVar));

        // fopen
        createAssign(pSynIfOpEqualT, t_pFilePointer,
                createMathCi(createFopen(pTestFileNameVar, createStr("r"))
                        .toLegalStringWithNoSemicolon()));

        // create elseif equal w
        // create cond
        pMathEq = (Math_eq) MathFactory.createOperator(eMathOperator.MOP_EQ);

        // create elseif
        SyntaxControl pSynIfOpEqualW = createElseIf(createCondition(t_pOption,
                createChar(WRITE_MODE), pMathEq));

        // add if
        pSynIfOpEqualHyphen.addStatement(pSynIfOpEqualW);

        // mkdir
        pSynIfOpEqualW.addStatement(createMkdir(createStr("." + SEPARATER
                + DATA_DIR)));

        // sprintf
        pSynIfOpEqualW
                .addStatement(createSprintf(pTestFileNameVar, pNodeArgVar));

        // fopen
        createAssign(pSynIfOpEqualW, t_pFilePointer,
                createMathCi(createFopen(pTestFileNameVar, createStr("w"))
                        .toLegalStringWithNoSemicolon()));

        // create else
        SyntaxControl pSynElseOp = createElse();
        pSynIfOpEqualHyphen.addStatement(pSynElseOp);
        pSynElseOp.addStatement(createExit());

        // free( filename );
        pSynTestInitFunc.addStatement(createFree(pTestFileNameVar));

        return pSynTestInitFunc;
    }

    // ========================================================
    // getSyntaxTestFunction
    // テスト関数構文を生成し，返す
    //
    // @return
    // 関数構文インスタンス : SyntaxFunction*
    //
    // @throws
    // MathException
    // SyntaxException
    // TranslateException
    //
    // ========================================================
    /*-----テストプログラム構文取得メソッド-----*/
    public SyntaxFunction getSyntaxTestFunction() throws MathException,
            SyntaxException, TranslateException {

        // arg : char* testNamea
        Math_ci pTestNameArgVar = createMathCi(TEST_NAME);
        SyntaxDeclaration pSynArgTestName = createArg(eDataType.DT_CHAR, 1,
                pTestNameArgVar);

        // arg : douvle cell
        Math_ci pCellArgVar = createMathCi(CELL_NAME);
        SyntaxDeclaration pSynArgCell = createArg(eDataType.DT_DOUBLE, 0,
                pCellArgVar);

        // arg : douvle diff
        Math_ci pDiffArgVar = createMathCi(DIFF_NAME);
        SyntaxDeclaration pSynArgDiff = createArg(eDataType.DT_DOUBLE, 0,
                pDiffArgVar);

        // create testCell function
        SyntaxDataType pSynReturnTypeTestInitFunc = new SyntaxDataType(
                eDataType.DT_VOID, 0);
        SyntaxFunction pSynTestFunc = createFunction(
                pSynReturnTypeTestInitFunc, TEST_FUNC_NAME, pSynArgTestName,
                pSynArgCell, pSynArgDiff);
        t_pSynProgram.addFunction(pSynTestFunc);

        // char *tag;
        Math_ci pTagVar = createCharVal(pSynTestFunc, TAG_NAME, 1, null);

        // char buff[BUFF_MAX];
        Math_ci pBuffVar = createMathCi(BUFF_NAME);
        pBuffVar.addArrayIndexToBack(t_pDefinedBuffMaxVar);
        SyntaxDataType pSynTypeChar = new SyntaxDataType(eDataType.DT_CHAR, 0);
        SyntaxDeclaration pDecVar = new SyntaxDeclaration(pSynTypeChar,
                pBuffVar);
        pSynTestFunc.addDeclaration(pDecVar);

        // int cnt=0;
        Math_ci pCntVar = createIntVal(pSynTestFunc, CNT_NAME, 0, 0, true);

        // double f_cell;
        Math_ci pFCellVar = createDoubleVal(pSynTestFunc, F_CELL_NAME, 0, 0,
                false);

        // tag = (char*)malloc( sizeof(char) * (strlen( BUFF_MAX ) -
        // sizeof(double)) );
        createCharMalloc(pSynTestFunc, pTagVar,
                createMinus(t_pDefinedBuffMaxVar, createSizeofDouble()));

        // create if equal t
        // create cond
        Math_eq pMathEq = (Math_eq) MathFactory
                .createOperator(eMathOperator.MOP_EQ);

        // create if
        SyntaxControl pSynIfOpEqualT = createIf(createCondition(t_pOption,
                createChar(TEST_MODE), pMathEq));

        // add if
        pSynTestFunc.addStatement(pSynIfOpEqualT);

        // hcreate(TEST_NUM);
        pSynIfOpEqualT.addStatement(createHcreate(t_pDefinedTestMaxVar));

        // e.key = strdup(testName);
        Math_ci t_pHashEntryWithKey = createMathCi(HASH_ENTRY_NAME);
        t_pHashEntryWithKey.addMemberVar(createMathCi("key"));
        createAssign(pSynIfOpEqualT, t_pHashEntryWithKey,
                createMathCi(createStrdup(pTestNameArgVar)
                        .toLegalStringWithNoSemicolon()));

        // ep = hsearch(e, FIND);
        createAssign(pSynIfOpEqualT, t_pHashEntryPointer,
                createMathCi(createHsearch(t_pHashEntry, "FIND")
                        .toLegalStringWithNoSemicolon()));

        // create if ep not equal null
        // create cond
        Math_neq pMathNeq = (Math_neq) MathFactory
                .createOperator(eMathOperator.MOP_NEQ);

        // create if
        SyntaxControl pSynIfEpEqualTrue = createIf(createCondition(
                t_pHashEntryPointer, MathCnNull, pMathNeq));

        // add if
        pSynIfOpEqualT.addStatement(pSynIfEpEqualTrue);

        // cnt = (int)(ep->data)++;
        Math_ci t_pHashEntryPointerWithData = createMathCi(HASH_ENTRY_POINTER_NAME);
        Math_ci pStrData = createMathCi("data");
        t_pHashEntryPointerWithData.addMemberVar(pStrData);
        t_pHashEntryPointerWithData.setArrowOperaor();
        SyntaxDataType pSynIntType = new SyntaxDataType(eDataType.DT_INT, 0);
        t_pHashEntryPointerWithData.addCastDataType(pSynIntType);
        createAssign(pSynIfEpEqualTrue, pCntVar,
                createInc(t_pHashEntryPointerWithData));
        ;
        // create else
        SyntaxControl pSynIfEpNotEqualTrue = createElse();

        // add else
        pSynIfOpEqualT.addStatement(pSynIfEpNotEqualTrue);

        // e.data = (void *)1
        Math_ci t_pHashEntryWithData = createMathCi(HASH_ENTRY_NAME);
        t_pHashEntryWithData.addMemberVar(pStrData);
        SyntaxDataType pSynPVoidType = new SyntaxDataType(eDataType.DT_VOID, 1);
        Math_cn MathCn1WithVoidPointer = MathCn1;
        MathCn1WithVoidPointer.addCastDataType(pSynPVoidType);
        createAssign(pSynIfEpNotEqualTrue, t_pHashEntryWithData,
                MathCn1WithVoidPointer);

        // ep = hsearch(e, ENTER);
        createAssign(pSynIfEpNotEqualTrue, t_pHashEntryPointer,
                createMathCi(createHsearch(t_pHashEntry, "ENTER")
                        .toLegalStringWithNoSemicolon()));

        // fseek
        pSynIfOpEqualT.addStatement(createFseek(t_pFilePointer));

        // while
        Math_ci pBuffVarWithNoArrayIndex = createMathCi(BUFF_NAME);
        pMathNeq = (Math_neq) MathFactory.createOperator(eMathOperator.MOP_NEQ);
        SyntaxControl pSynWhile = createWhile(createCondition(
                createMathCi(createFgets(
                        pBuffVarWithNoArrayIndex,
                        createMathCi(createSizeof(pBuffVarWithNoArrayIndex)
                                .toLegalStringWithNoSemicolon()),
                        t_pFilePointer).toLegalStringWithNoSemicolon()),
                MathCnNull, pMathNeq));
        pSynIfOpEqualT.addStatement(pSynWhile);

        // sscanf
        Math_ci pFCellVarWithPointer = createMathCi(F_CELL_NAME);
        pFCellVarWithPointer.setPointerNum(-1);
        pSynWhile.addStatement(createSscanf(pBuffVarWithNoArrayIndex,
                createStr("%lf%s"), pFCellVarWithPointer, pTagVar));

        // create if for search testname
        // create cond
        pMathEq = (Math_eq) MathFactory.createOperator(eMathOperator.MOP_EQ);

        // create if
        SyntaxControl pSynIfTagEqualFilename = createIf(createCondition(
                createMathCi(createStrcmp(pTagVar, pTestNameArgVar)
                        .toLegalStringWithNoSemicolon()), MathCn0, pMathEq));

        // add if
        pSynWhile.addStatement(pSynIfTagEqualFilename);

        // create if for search testname
        // create cond
        pMathNeq = (Math_neq) MathFactory.createOperator(eMathOperator.MOP_NEQ);

        // create if
        SyntaxControl pSynIfCntNotEqual0 = createIf(createCondition(pCntVar,
                MathCn0, pMathNeq));

        // add if
        pSynIfTagEqualFilename.addStatement(pSynIfCntNotEqual0);

        // cnt--;
        createDecExp(pSynIfCntNotEqual0, pCntVar);

        // continue;
        createContinue(pSynIfCntNotEqual0);

        // create if for assert cell
        // create if
        SyntaxControl pSynIfAssertCell = createIf(createCondition(
                createMathCi(createFabs(
                        createMathCi(createMinus(pCellArgVar, pFCellVar)
                                .toLegalString()))
                        .toLegalStringWithNoSemicolon()), pDiffArgVar,
                (Math_leq) MathFactory.createOperator(eMathOperator.MOP_LEQ)));

        // add if
        pSynIfTagEqualFilename.addStatement(pSynIfAssertCell);

        // create else
        SyntaxControl pSynElseAssertCell = createElse();

        // add else
        pSynIfTagEqualFilename.addStatement(pSynElseAssertCell);

        // success printf
        pSynIfAssertCell.addStatement(createPrintf(createStr(SUCCESS_MSG),
                pTestNameArgVar));

        // fail printf
        pSynElseAssertCell.addStatement(createPrintf(createStr(FAIL_MSG),
                pTestNameArgVar, pCellArgVar, pFCellVar));

        // break;
        createBreak(pSynIfTagEqualFilename);

        // create elseif equal w
        // create elseif
        SyntaxControl pSynElseIfOpEqualW = createElseIf(createCondition(
                t_pOption, createChar(WRITE_MODE),
                (Math_eq) MathFactory.createOperator(eMathOperator.MOP_EQ)));

        // add elseif
        pSynTestFunc.addStatement(pSynElseIfOpEqualW);

        // fprintf();
        pSynElseIfOpEqualW.addStatement(createFprintfCall(t_pFilePointer,
                createStr("%lf%s\\n"), pCellArgVar, pTestNameArgVar));

        // free( tag );
        pSynTestFunc.addStatement(createFree(pTagVar));

        return pSynTestFunc;
    }

    public Math_ci createMathCi(String str) throws MathException {
        return (Math_ci) MathFactory.createOperand(eMathOperand.MOPD_CI, str);
    }

    public Math_cn createMathCn(String str) throws MathException {
        return (Math_cn) MathFactory.createOperand(eMathOperand.MOPD_CN, str);
    }

    public SyntaxCallFunction createSizeof(Math_ci sizeofArg)
            throws MathException {
        SyntaxCallFunction pSynSizeof = new SyntaxCallFunction("sizeof");
        pSynSizeof.addArgFactor(sizeofArg);

        return pSynSizeof;
    }

    public Math_ci createSizeofChar() throws MathException {
        return createMathCi("sizeof( char )");
    }

    public Math_ci createSizeofInt() throws MathException {
        return createMathCi("sizeof( int )");
    }

    public Math_ci createSizeofDouble() throws MathException {
        return createMathCi("sizeof( double )");
    }

    private SyntaxCallFunction createStrlen(Math_ci pStr) {
        SyntaxCallFunction pSynStrlenCall = new SyntaxCallFunction("strlen");
        pSynStrlenCall.addArgFactor(pStr);

        return pSynStrlenCall;
    }

    private SyntaxCallFunction createExit() throws MathException {
        SyntaxCallFunction pSynExitCall = new SyntaxCallFunction("exit");
        pSynExitCall.addArgFactor(createMathCi("EXIT_FAILURE"));

        return pSynExitCall;
    }

    private SyntaxCallFunction createSprintf(Math_ci fileNameBuff, Math_ci node)
            throws MathException {
        SyntaxCallFunction pSynSprintfCall = new SyntaxCallFunction("sprintf");
        pSynSprintfCall.addArgFactor(fileNameBuff);
        pSynSprintfCall.addArgFactor(createStr(DATA_DIR + SEPARATER
                + DATA_FILE_NAME + "%d" + DATA_FILE_EXT));
        pSynSprintfCall.addArgFactor(node);

        return pSynSprintfCall;
    }

    private SyntaxCallFunction createFprintfCall(Math_ci filePointer,
            Math_cn rowString, Math_ci... args) throws MathException {
        SyntaxCallFunction pSynFprintfCall = new SyntaxCallFunction("fprintf");
        pSynFprintfCall.addArgFactor(filePointer);
        pSynFprintfCall.addArgFactor(rowString);
        for (int i = 0; i < args.length; i++) {
            pSynFprintfCall.addArgFactor(args[i]);
        }

        return pSynFprintfCall;
    }

    private SyntaxCallFunction createFopen(Math_ci fileName, Math_cn fileMode)
            throws MathException {
        SyntaxCallFunction pSynFopenCall = new SyntaxCallFunction("fopen");
        pSynFopenCall.addArgFactor(fileName);
        pSynFopenCall.addArgFactor(fileMode);

        return pSynFopenCall;
    }

    private SyntaxCallFunction createHcreate(Math_ci testMaxNum)
            throws MathException {
        SyntaxCallFunction pSynHcreate = new SyntaxCallFunction("hcreate");
        pSynHcreate.addArgFactor(testMaxNum);

        return pSynHcreate;
    }

    private SyntaxCallFunction createStrdup(Math_ci testName)
            throws MathException {
        SyntaxCallFunction pSynStrdup = new SyntaxCallFunction("strdup");
        pSynStrdup.addArgFactor(testName);

        return pSynStrdup;
    }

    private SyntaxCallFunction createHsearch(Math_ci entryVar, String hashMode)
            throws MathException {
        SyntaxCallFunction pSynHsearch = new SyntaxCallFunction("hsearch");
        pSynHsearch.addArgFactor(entryVar);
        pSynHsearch.addArgFactor(createMathCn(hashMode));

        return pSynHsearch;
    }

    private SyntaxCallFunction createFseek(Math_ci filePointer)
            throws MathException {
        SyntaxCallFunction pSynHsearch = new SyntaxCallFunction("fseek");
        pSynHsearch.addArgFactor(filePointer);
        pSynHsearch.addArgFactor(createMathCn("0L"));
        pSynHsearch.addArgFactor(createMathCn("SEEK_SET"));

        return pSynHsearch;
    }

    private SyntaxCallFunction createFgets(Math_ci buff, Math_ci buffSize,
            Math_ci filePointer) throws MathException {
        SyntaxCallFunction pSynFgets = new SyntaxCallFunction("fgets");
        pSynFgets.addArgFactor(buff);
        pSynFgets.addArgFactor(buffSize);
        pSynFgets.addArgFactor(filePointer);

        return pSynFgets;
    }

    private SyntaxCallFunction createSscanf(Math_ci buff, Math_cn rowString,
            Math_ci... args) throws MathException {
        SyntaxCallFunction pSynSscanfCall = new SyntaxCallFunction("sscanf");
        pSynSscanfCall.addArgFactor(buff);
        pSynSscanfCall.addArgFactor(rowString);
        for (int i = 0; i < args.length; i++) {
            pSynSscanfCall.addArgFactor(args[i]);
        }

        return pSynSscanfCall;
    }

    private SyntaxCallFunction createStrcmp(Math_ci str1, Math_ci str2)
            throws MathException {
        SyntaxCallFunction pSynStrcmp = new SyntaxCallFunction("strcmp");
        pSynStrcmp.addArgFactor(str1);
        pSynStrcmp.addArgFactor(str2);

        return pSynStrcmp;
    }

    private SyntaxCallFunction createFabs(Math_ci fabsArg) throws MathException {
        SyntaxCallFunction pSynFabs = new SyntaxCallFunction("fabs");
        pSynFabs.addArgFactor(fabsArg);

        return pSynFabs;
    }

    private SyntaxCallFunction createPrintf(Math_cn printString,
            Math_ci... args) throws MathException {
        SyntaxCallFunction pSynPrintfCall = new SyntaxCallFunction("printf");
        pSynPrintfCall.addArgFactor(printString);
        for (int i = 0; i < args.length; i++) {
            pSynPrintfCall.addArgFactor(args[i]);
        }

        return pSynPrintfCall;
    }

    private SyntaxCallFunction createMkdir(MathFactor dir) throws MathException {
        SyntaxCallFunction pSynMkdirCall = new SyntaxCallFunction("mkdir");
        pSynMkdirCall.addArgFactor(dir);
        pSynMkdirCall.addArgFactor(createMathCn("0777"));

        return pSynMkdirCall;
    }

    private Math_cn createStr(String str) throws MathException {
        return createMathCn("\"" + str + "\"");
    }

    private Math_cn createChar(String str) throws MathException {
        return createMathCn("\'" + str + "\'");
    }
}
