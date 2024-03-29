package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLDefinition;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.cellML.CellMLDefinition.eCellMLTag;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathExpression;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.Math_ci;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.ComponentTable;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.table.VariableTable;

/**
 * CellML解析クラス
 */
public class CellMLAnalyzer extends MathMLAnalyzer {

    public static final String CELLML_VAR_PREFIX_STR = "$";

    /* 数式解析中判定 */
    private boolean m_bMathParsing;

    /* 分類後変数ベクタ */
    Vector<Math_ci> m_vecTimeVar;

    public Vector<Math_ci> getM_vecTimeVar() {
        return m_vecTimeVar;
    }

    Vector<Math_ci> m_vecDiffVar;

    public Vector<Math_ci> getM_vecDiffVar() {
        return m_vecDiffVar;
    }

    Vector<Math_ci> m_vecArithVar;

    public Vector<Math_ci> getM_vecArithVar() {
        return m_vecArithVar;
    }

    Vector<Math_ci> m_vecConstVar;

    public Vector<Math_ci> getM_vecConstVar() {
        return m_vecConstVar;
    }

    /* 変数テーブル */
    ComponentTable m_pComponentTable;
    VariableTable m_pCurVariableTable;

    /* 分類後数式ベクタ */
    Vector<MathExpression> m_vecDiffExpression;

    public Vector<MathExpression> getM_vecDiffExpression() {
        return m_vecDiffExpression;
    }

    Vector<MathExpression> m_vecNonDiffExpression;

    public Vector<MathExpression> getM_vecNonDiffExpression() {
        return m_vecNonDiffExpression;
    }

    /*-----コンストラクタ-----*/
    public CellMLAnalyzer() {
        m_bMathParsing = false;
        m_pComponentTable = null;
        m_pCurVariableTable = null;

        m_vecTimeVar = new Vector<Math_ci>();
        m_vecDiffVar = new Vector<Math_ci>();
        m_vecArithVar = new Vector<Math_ci>();
        m_vecConstVar = new Vector<Math_ci>();
        m_vecDiffExpression = new Vector<MathExpression>();
        m_vecNonDiffExpression = new Vector<MathExpression>();
    }

    public CellMLAnalyzer(ComponentTable pComponentTable) {
        this();
        m_pComponentTable = pComponentTable;
    }

    // ========================================================
    // setComponentTable
    // ComponentTable設定メソッド
    //
    // @arg
    // ComponentTable* pComponentTable
    //
    // ========================================================
    /*-----変数テーブル設定メソッド-----*/
    public void setComponentTable(ComponentTable pComponentTable) {
        m_pComponentTable = pComponentTable;
    }

    /*-----解析メソッド-----*/

    // ========================================================
    // findTagStart
    // 開始タグ解析メソッド
    //
    // @arg
    // string strTag : 開始タグ名
    // XMLAttribute* pXMLAttr : 属性クラスインスタンス
    //
    // ========================================================
    public void findTagStart(String strTag, XMLAttribute pXMLAttr)
            throws CellMLException, MathException, XMLException,
            RelMLException, TecMLException {
        /* 変数テーブルを持たなければ例外 */
        if (m_pComponentTable == null) {
            throw new CellMLException("CellMLAnalyzer", "findTagStart",
                    "component table not found");
        }

        // -----------------------------------------------------
        // 数式部の解析
        // -----------------------------------------------------
        if (m_bMathParsing) {

            /* MathML解析器に投げる */
            super.findTagStart(strTag, pXMLAttr);
        }

        // -----------------------------------------------------
        // CellMLの解析
        // -----------------------------------------------------
        else {

            /* タグidの取得 */
            eCellMLTag tagId;

            try {
                tagId = CellMLDefinition.getCellMLTagId(strTag);
            } catch (CellMLException e) {
                /* 特定のタグ以外は無視する */
                return;
            }

            /* タグ種別ごとの処理 */
            switch (tagId) {

            // -----------------------------------数式解析開始
            case CTAG_MATH: {
                m_bMathParsing = true;
                m_NextOperandKind = null;
                break;
            }
            // -----------------------------------コンポーネント解析開始
            case CTAG_COMPONENT: {
                String strComponentName;

                try {
                    /* コンポーネント名を取得 */
                    strComponentName = pXMLAttr
                            .getValue(CellMLDefinition.CELLML_ATTR_STR_NAME);
                } catch (Exception e) {
                    throw new CellMLException("CellMLVariableAnalyzer",
                            "findTagStart", "component name not found");
                }

                try {
                    /* コンポーネントに対応する変数テーブルを取得 */
                    m_pCurVariableTable = m_pComponentTable
                            .searchTable(strComponentName);
                } catch (TableException e) {
                    System.err.println(e.getMessage());
                    throw new CellMLException("CellMLAnalyzer", "findTagStart",
                            "can't find variable table correspond to component");
                }

                break;
            }
            }
        }
    }

    // ========================================================
    // findTagEnd
    // 終了タグ解析メソッド
    //
    // @arg
    // string strTag : 終了タグ名
    //
    // ========================================================
    public void findTagEnd(String strTag) throws CellMLException,
            MathException, RelMLException {
        /* 変数テーブルを持たなければ例外 */
        if (m_pComponentTable == null) {
            throw new CellMLException("CellMLAnalyzer", "findTagEnd",
                    "component table not found");
        }

        // -----------------------------------------------------
        // 数式部の解析
        // -----------------------------------------------------
        if (m_bMathParsing) {

            /* 数式解析終了 */
            if (strTag == CellMLDefinition.CELLML_TAG_STR_MATH) {
                m_bMathParsing = false;
                return;
            }

            /* MathML解析器に投げる */
            super.findTagEnd(strTag);
        }

        // -----------------------------------------------------
        // CellMLの解析
        // -----------------------------------------------------
        else {

            /* タグidの取得 */
            eCellMLTag tagId;

            try {
                tagId = CellMLDefinition.getCellMLTagId(strTag);
            } catch (CellMLException e) {
                /* 特定のタグ以外は無視する */
                return;
            }

            /* タグ種別ごとの処理 */
            switch (tagId) {

            // -----------------------------------componentの解析終了
            case CTAG_COMPONENT: {
                /* 現在の変数テーブルをNULLに */
                m_pCurVariableTable = null;
                break;
            }

            }
        }
    }

    // ========================================================
    // findText
    // 文字列解析メソッド
    //
    // @arg
    // string strText : 切り出された文字列
    //
    // ========================================================
    public void findText(String strText) throws CellMLException, MathException,
            TableException {
        /* 変数テーブルを持たなければ例外 */
        if (m_pComponentTable == null) {
            throw new CellMLException("CellMLAnalyzer", "findText",
                    "component table not found");
        }

        // -----------------------------------------------------
        // 数式部の解析
        // -----------------------------------------------------
        if (m_bMathParsing && m_NextOperandKind != null) {
            /* 変数の場合,テーブルより完全名を取得 */
            if (m_NextOperandKind == eMathOperand.MOPD_CI) {
                strText = m_pCurVariableTable.getFullName(strText);
            }

            /* MathML解析器に投げる */
            super.findText(strText);
        }

        // -----------------------------------------------------
        // CellMLの解析
        // -----------------------------------------------------
        else {
        }
    }

    // ========================================================
    // applyRelML
    // RelML適用メソッド
    //
    // @arg
    // RelMLAnalyzer* pRelMLAnalyzer : RelML解析器インスタンス
    //
    // @throws
    // CellMLException
    //
    // ========================================================
    /*-----RelML適用メソッド-----*/
    public void applyRelML(RelMLAnalyzer pRelMLAnalyzer)
            throws CellMLException, RelMLException, MathException {
        /* 変数テーブルをRelMLに適用 */
        pRelMLAnalyzer.applyComponentTable(m_pComponentTable);

        /* 変数ベクタをコピー */
        m_vecTimeVar = pRelMLAnalyzer.m_vecTimeVar;
        m_vecConstVar = pRelMLAnalyzer.m_vecConstVar;

        // -------------------------------------------------
        // 数式の解析
        // -------------------------------------------------
        /* 数式数を取得 */
        int nExpressionNum = getExpressionCount();

        for (int i = 0; i < nExpressionNum; i++) {
            /* 数式取得 */
            MathExpression pMathExp = getExpression(i);

            /* 左辺式取得 */
            MathExpression pLeftExp = pMathExp.getLeftExpression();

            if (pLeftExp == null) {
                throw new CellMLException("CellMLAnalyzer", "applyRelML",
                        "failed to parse expression");
            }

            /* 左辺変数取得 */
            Math_ci pLeftVar = (Math_ci) pLeftExp.getFirstVariable();

            /* 左辺変数の型より式が微分式かを判別する */
            if (pRelMLAnalyzer.isDiffVar(pLeftVar)) {

                /* 微分変数ベクタに追加 */
                m_vecDiffVar.add(pLeftVar);

                /* 微分式として登録 */
                m_vecDiffExpression.add(pMathExp);
            } else {

                /* 通常変数ベクタに追加 */
                m_vecArithVar.add(pLeftVar);

                /* 非微分式として登録 */
                m_vecNonDiffExpression.add(pMathExp);
            }
        }

        /* 式の並べ替えを行う */
        this.sortExpressions();
        // System.out.println("sort m_vecNonDiffExpression");
        // for (int i = 0; i < m_vecNonDiffExpression.size(); i++) {
        // MathExpression it = m_vecNonDiffExpression.get(i);
        // System.out.println(i + "\t" + it.toLegalString());
        // }
        // System.out.println("sort m_vecArithVar");
        // for (int i = 0; i < m_vecArithVar.size(); i++) {
        // Math_ci it = m_vecArithVar.get(i);
        // System.out.println(i + "\t" + it.toLegalString());
        // }
        // printContents(); // debug
    }

    // ========================================================
    // printContents
    // 解析内容標準出力メソッド
    //
    // ========================================================
    /*-----解析結果表示メソッド-----*/
    public void printContents() throws MathException {
        /* 開始線出力 */
        System.out.println("[CellML]------------------------------------");

        /* 数式出力 */
        super.printExpressions();

        /* 改行 */
        System.out.println();
    }

    // ========================================================
    // sortExpressions
    // 計算式並べ替えメソッド
    //
    // @arg
    // vector<MathExpression*>* pvecExpressions : 並び替える数式ベクタ
    // vector<Math_ci*> pvecVariables : 未初期化変数リスト
    //
    // ========================================================
    /*-----数式ソートメソッド-----*/
    private void sortExpressions() throws MathException {
        Vector<MathExpression> pvecExpressions = m_vecNonDiffExpression;
        /* 並び替え後のベクタ */
        Vector<MathExpression> vecReorderedExpression = new Vector<MathExpression>();
        Vector<Math_ci> vecReorderedVariables = new Vector<Math_ci>();

        /* 未初期化変数ベクタ */
        Vector<Math_ci> vecUnInitializedVar = m_vecArithVar;

        /* 式を順番に調べていく */
        /* 新しいベクタにすべての式が入るまで繰り返し */
        while (pvecExpressions.size() > 0) {
            // System.out.println("pvecExpressions->size(): " +
            // pvecExpressions.size());

            Vector<MathExpression> newPvecExpressions = new Vector<MathExpression>();
            /**
             * VC++版と同じ並び順の出力を生成するためのフラグ. このフラグが行う処理はなくてもいい.
             */
            boolean removeFlagForSameAction_CPlusPlusVersion = false;
            for (MathExpression it : pvecExpressions) {
                if (removeFlagForSameAction_CPlusPlusVersion) {
                    removeFlagForSameAction_CPlusPlusVersion = false;
                    newPvecExpressions.add(it);
                    continue;
                }

                /* 式の取得 */
                MathExpression pExp = it;
                MathExpression pLeftExp = pExp.getLeftExpression();
                Math_ci pLeftVar = pLeftExp.getFirstVariable();

                /* 未初期化変数のチェック */
                boolean bUnInitialized = false;
                int nVariableNum = pExp.getVariableCount();
                // System.out.println(nVariableNum + "\t" + it.toLegalString());

                for (int i = 0; i < nVariableNum; i++) {

                    /* 変数取得 */
                    MathOperand pVariable = pExp.getVariable(i);

                    /* 左辺値と同じものは初期化済み扱い */
                    if (pVariable.toLegalString().equals(
                            pLeftVar.toLegalString())) {
                        continue;
                    }

                    /* 未初期化変数ベクタとの比較 */
                    for (Math_ci it2 : vecUnInitializedVar) {

                        /* 変数名が一致すれば未初期化 */
                        if (it2.toLegalString().equals(
                                pVariable.toLegalString())) {
                            bUnInitialized = true;
                            break;
                        }
                    }

                    if (bUnInitialized) {
                        break;
                    }
                }

                /* 未初期化の式は後回しにする */
                if (bUnInitialized) {
                    newPvecExpressions.add(it);
                    // System.out.println("UnInitialized");
                    continue;
                }
                /* 初期化済みの右辺式を持つ式 */
                else {
                    /* 式を新しいベクタに加える */
                    vecReorderedExpression.add(pExp);
                    vecReorderedVariables.add(pLeftVar);
                    // System.out.println("Initialized");
                    removeFlagForSameAction_CPlusPlusVersion = true;

                    /* 未初期化変数リストから左辺変数を削除 */
                    for (Math_ci it2 : vecUnInitializedVar) {

                        /* 一致する変数を削除 */
                        if (it2.toLegalString()
                                .equals(pLeftVar.toLegalString())) {
                            vecUnInitializedVar.remove(it2);
                            // System.out.println("removed " +
                            // it2.toLegalString());
                            break;
                        }
                    }

                    /* 元の式をベクタから削除 */
                    // pvecExpressions.remove(it);

                }
            }
            pvecExpressions = newPvecExpressions;
        }

        /* 新しいベクタを適用する */
        m_vecNonDiffExpression = vecReorderedExpression;
        m_vecArithVar = vecReorderedVariables;
    }

}
