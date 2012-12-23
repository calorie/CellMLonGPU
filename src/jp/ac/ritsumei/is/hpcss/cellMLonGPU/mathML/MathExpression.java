package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.util.Stack;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML数式クラス
 */
public class MathExpression {

    /* 式のルートとなる要素 */
    MathFactor m_pRootFactor;

    /* 現在オペランドを探している演算子を保持するスタック */
    Stack<MathOperator> m_stackCurOperator;

    /* 関数構築用ポインタ */
    MathOperator m_pCurFuncOperator; // 現在構築中の関数演算子へのポインタ
    MathOperator m_pFuncParentOperand; // 関数演算子の親に位置する演算子へのポインタ

    /* 変数リスト保持ベクタ */
    Vector<MathOperand> m_vecVariables;

    /*-----コンストラクタ-----*/
    public MathExpression() {
        m_stackCurOperator = new Stack<MathOperator>();
        m_vecVariables = new Vector<MathOperand>();
        m_pRootFactor = null;
        m_pCurFuncOperator = null;
        m_pFuncParentOperand = null;
    }

    public MathExpression(MathFactor pRootFactor) {
        this();
        m_pRootFactor = pRootFactor;
    }

    /*-----計算式構築メソッド-----*/

    // ========================================================
    // addOperator
    // 演算子追加メソッド
    //
    // @arg
    // MathOperator* pOperator : 追加する演算子
    //
    // ========================================================
    public void addOperator(MathOperator pOperator) {
        /* 開始演算子設定 */
        if (m_stackCurOperator.empty()) {
            m_stackCurOperator.push(pOperator);
            m_pRootFactor = m_stackCurOperator.peek();
        } else {
            /* 関数構築中の場合 */
            if (m_pCurFuncOperator != null
                    && m_stackCurOperator.peek() == m_pFuncParentOperand) {
                /* 関数引数として要素追加 */
                m_pCurFuncOperator.addFactor(pOperator);
            }
            /* 通常の演算子追加 */
            else {
                /* 演算子に演算子追加 */
                m_stackCurOperator.peek().addFactor(pOperator);
            }

            /* スタックに追加した演算子をプッシュ */
            m_stackCurOperator.push(pOperator);
        }
    }

    // ========================================================
    // addOperand
    // オペランド追加メソッド
    //
    // @arg
    // MathOperand* pOperand : 追加するオペランド
    //
    // @throw
    // MathException
    //
    // ========================================================
    public void addOperand(MathOperand pOperand) throws MathException {
        /* 1変数のみの式を構成する場合 */
        if (m_pRootFactor == null) {
            m_pRootFactor = pOperand;
        }

        /* 例外処理 */
        if (m_stackCurOperator.empty()) {
            throw new MathException("MathExpression", "addOperand",
                    "no operator written before this operand");
        }

        /* 関数構築中の場合 */
        if (m_pCurFuncOperator != null
                && m_stackCurOperator.peek() == m_pFuncParentOperand) {
            /* 関数引数として要素追加 */
            m_pCurFuncOperator.addFactor(pOperand);
        }

        /* 関数演算子fnの場合 */
        else if (m_stackCurOperator.peek().matches(eMathOperator.MOP_FN)) {

            /* 変数オペランドMath_ci以外を受理しない */
            if (pOperand.matches(eMathOperand.MOPD_CI)) {
                ((Math_fn) m_stackCurOperator.peek())
                        .setFuncOperand((Math_ci) pOperand);
            } else {
                throw new MathException("MathExpression", "addOperand",
                        "can't use constant number for function operand");
            }
        }

        /* その他の演算子 */
        else {
            /* 演算子にオペランド追加 */
            m_stackCurOperator.peek().addFactor(pOperand);
        }
    }

    // ========================================================
    // addVariable
    // 変数追加メソッド
    //
    // @arg
    // MathOperand* pOperand : 追加するオペランド
    //
    // ========================================================
    public void addVariable(MathOperand pOperand) throws MathException {
        /* 重複チェック */
        boolean bDuplicate = false;

        for (MathOperand it : m_vecVariables) {

            if (it.matches(pOperand)) {
                bDuplicate = true;
                break;
            }
        }

        /* 重複しない場合 */
        if (!bDuplicate) {

            /* 変数リストに追加 */
            m_vecVariables.add(pOperand);
        }

        /* オペランドを追加 */
        this.addOperand(pOperand);
    }

    // ========================================================
    // breakOperator
    // 演算子範囲終了メソッド
    //
    // @arg
    // MathOperator* pOperator : 追加する演算子
    //
    // @throw
    // MathException
    //
    // ========================================================
    public void breakOperator(MathOperator pOperator) throws MathException {
        /* 例外処理 */
        if (m_stackCurOperator.empty()) {
            throw new MathException("MathExpression", "breakOperator",
                    "operator stack is empty");
        }

        /* 一致する演算子までpop-up */
        while (!m_stackCurOperator.peek().matches(pOperator)) {

            /* 1つpop-up */
            m_stackCurOperator.pop();

            /* 例外処理 */
            if (m_stackCurOperator.empty()) {
                throw new MathException("MathExpression", "breakOperator",
                        "start operator tag not found");
            }
        }

        /* 関数演算子の場合,自身と親演算子のポインタを保存する */
        if (m_stackCurOperator.peek().matches(eMathOperator.MOP_FN)) {

            /* 構築中の関数演算子を保存 */
            m_pCurFuncOperator = m_stackCurOperator.peek();

            /* 一致した演算子をpop-up */
            m_stackCurOperator.pop();

            /* 親演算子を保存 */
            m_pFuncParentOperand = m_stackCurOperator.peek();
        }

        /* operandを持たないoperatorはpop-upしない */
        else if (!m_stackCurOperator.peek().hasFactor()) {
            return;
        }

        else {

            /* 関数引数適用範囲の終了判定 */
            if (m_stackCurOperator.peek() == m_pFuncParentOperand) {
                m_pCurFuncOperator = null;
                m_pFuncParentOperand = null;
            }

            /* 一致した演算子をpop-up */
            m_stackCurOperator.pop();
        }
    }

    /*-----状態取得メソッド-----*/

    // ========================================================
    // isConstructing
    // 計算式構築状態取得メソッド
    //
    // @return
    // 計算式構築状態 : bool
    //
    // ========================================================
    public boolean isConstructing() {
        /* スタックに演算子が残ってなければ非構築状態 */
        if (m_stackCurOperator.empty()) {
            return false;
        }

        return true;
    }

    // ========================================================
    // getVariable
    // 変数取得メソッド
    //
    // @arg
    // int dVariableId : 変数id
    //
    // @return
    // 引数指定idの変数へのポインタ : MathOperand
    //
    // ========================================================
    /*-----変数取得-----*/
    public MathOperand getVariable(int dVariableId) {
        return m_vecVariables.get(dVariableId);
    }

    // ========================================================
    // getVariableCount
    // 数式中の変数の数を取得
    //
    // @return
    // 変数の数 : int
    //
    // ========================================================
    public int getVariableCount() {
        return m_vecVariables.size();
    }

    // ========================================================
    // createCopy
    // 数式複製メソッド
    //
    // @return
    // 複製数式インスタンス : MathExpression
    //
    // ========================================================
    /*-----数式複製メソッド-----*/
    public MathExpression createCopy() throws MathException {
        return new MathExpression(m_pRootFactor.createCopy());
    }

    /*-----数式置換メソッド-----*/

    // ========================================================
    // replace
    // 置換メソッド
    //
    // @arg
    // MathOperand* pOldOperand : 置換対象のオペランド
    // MathFactor* pNewFactor : 置換後の数式
    //
    // @throws
    // MathExpression
    //
    // ========================================================
    public void replace(MathOperand pOldOperand, MathFactor pNewFactor)
            throws MathException {
        /* ルートが演算子の場合 */
        if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {

            /* 関数の場合 */
            if (((MathOperator) m_pRootFactor).matches(eMathOperator.MOP_FN)) {

                /* 関数名の照合 */
                if (((Math_fn) m_pRootFactor).matches(pOldOperand)) {
                    m_pRootFactor = pNewFactor;
                }
            }
            /* その他の演算子 */
            else {
                ((MathOperator) m_pRootFactor).replace(pOldOperand, pNewFactor);
            }
        }
        /* オペランドの場合 */
        else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
            m_pRootFactor = pNewFactor;
        }
        /* 例外の要素 */
        else {
            throw new MathException("MathExpression", "replace",
                    "invalid root factor");
        }
    }

    // ========================================================
    // replace
    // 置換メソッド
    //
    // @arg
    // Math_fn* pOldFunction : 置換対象のオペランド
    // MathFactor* pNewFactor : 置換後の数式
    //
    // @throws
    // MathExpression
    //
    // ========================================================
    public void replace(Math_fn pOldFunction, MathFactor pNewFactor)
            throws MathException {
        /* ルートが演算子の場合 */
        if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {

            /* 関数の場合 */
            if (((MathOperator) m_pRootFactor).matches(eMathOperator.MOP_FN)) {

                /* 関数名の照合 */
                if (((Math_fn) m_pRootFactor).matches(pOldFunction)) {
                    m_pRootFactor = pNewFactor;
                }
            }
            /* その他の演算子 */
            else {
                /* 数式ツリーを探索する */
                ((MathOperator) m_pRootFactor)
                        .replace(pOldFunction, pNewFactor);
            }
        }
        /* オペランドの場合 */
        else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
        }
        /* 例外の要素 */
        else {
            throw new MathException("MathExpression", "replace",
                    "invalid root factor");
        }
    }

    // ========================================================
    // searchFunction
    // 置換指定ファクタ取得メソッド
    //
    // @arg
    // MathOperand* pSearchOperand : 検索関数オペランド
    // vector<Math_fn*>* vecDstFunctions : 検索結果取得先ポインタ
    //
    // ========================================================
    /*-----関数検索メソッド-----*/
    public void searchFunction(MathOperand pSearchOperand,
            Vector<Math_fn> pvecDstFunctions) {
        /* ルートが演算子の場合 */
        if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
            /* 検索開始 */
            ((MathOperator) m_pRootFactor).searchFunction(pSearchOperand,
                    pvecDstFunctions);
        }
    }

    /*-----式取得-----*/

    // ========================================================
    // getLeftExpression
    // 　左辺式取得メソッド
    //
    // @return
    // 左辺式 : MathExpression*
    //
    // ========================================================
    public MathExpression getLeftExpression() {
        /* ルート演算子がない場合は取得不能 */
        if (m_pRootFactor == null
                || !m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
            return null;
        }

        /* 左辺式ルート要素取得 */
        MathFactor pLeftRootFactor = ((MathOperator) m_pRootFactor)
                .getLeftExpression();

        /* エラー処理 */
        if (pLeftRootFactor == null) {
            return null;
        }

        /* 新しい数式インスタンス生成 */
        MathExpression pNewExpression = new MathExpression();

        /* ルート要素の設定 */
        pNewExpression.m_pRootFactor = pLeftRootFactor;

        return pNewExpression;
    }

    // ========================================================
    // getRightExpression
    // 　右辺式取得メソッド
    //
    // @return
    // 右辺式 : MathExpression*
    //
    // ========================================================
    public MathExpression getRightExpression() {
        /* ルート演算子がない場合は取得不能 */
        if (m_pRootFactor == null
                || !m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
            return null;
        }

        /* 右辺式ルート要素取得 */
        MathFactor pRightRootFactor = ((MathOperator) m_pRootFactor)
                .getRightExpression();

        /* エラー処理 */
        if (pRightRootFactor == null) {
            return null;
        }

        /* 新しい数式インスタンス生成 */
        MathExpression pNewExpression = new MathExpression();

        /* オペランドの場合 */
        if (pRightRootFactor.matches(eMathMLClassification.MML_OPERAND)) {
            /* 右辺変数をルートに */
            pNewExpression.m_pRootFactor = (MathOperand) pRightRootFactor;
        }
        /* 演算子の場合 */
        else {
            /* 右辺式をルートに */
            pNewExpression.m_pRootFactor = (MathOperator) pRightRootFactor;
        }

        return pNewExpression;
    }

    // ========================================================
    // getRootFactor
    // 　ルート要素取得メソッド
    //
    // @return
    // ルート要素 : MathFactor*
    //
    // ========================================================
    public MathFactor getRootFactor() {
        return m_pRootFactor;
    }

    // ========================================================
    // getFirstVariable
    // 式中第一変数取得メソッド
    //
    // @return
    // 式のはじめの変数 : Math_ci*
    //
    // ========================================================
    public Math_ci getFirstVariable() {
        /* ルートが存在しない場合は取得不可 */
        if (m_pRootFactor == null) {
            return null;
        }

        /* 演算子の場合 */
        if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
            return ((MathOperator) m_pRootFactor).getFirstVariable();
        }
        /* オペランドの場合 */
        else if (m_pRootFactor.matches(eMathMLClassification.MML_OPERAND)) {

            /* Math_ciインスタンスの場合 */
            if (((MathOperand) m_pRootFactor).matches(eMathOperand.MOPD_CI)) {
                return (Math_ci) m_pRootFactor;
            }
        }

        return null;
    }

    // ========================================================
    // matches
    // 数式比較メソッド
    //
    // @arg
    // MathExpression* pExpression : 比較対象の式
    //
    // @return
    // 比較結果 : bool
    //
    // ========================================================
    /*-----数式比較-----*/
    public boolean matches(MathExpression pExpression) {
        return m_pRootFactor.matchesExpression(pExpression.m_pRootFactor);
    }

    // ========================================================
    // calculate
    // 演算命令メソッド
    //
    // @throws
    // MathException
    //
    // ========================================================
    /*-----演算命令-----*/
    public void calculate() throws MathException {
        /* 通常の式の場合のみ演算命令が可能 */
        if (m_pRootFactor.matches(eMathMLClassification.MML_OPERATOR)) {
            ((MathOperator) m_pRootFactor).calculate();
        }
        /* 例外 */
        else {
            throw new MathException("MathExpression", "calculate",
                    "can't calculate this expression");
        }
    }

    // ========================================================
    // toLegalString
    // 文字列変換メソッド
    //
    // @return
    // 計算式文字列
    //
    // ========================================================
    /*-----文字列変換-----*/
    public String toLegalString() throws MathException {
        return m_pRootFactor.toLegalString();
    }

}
