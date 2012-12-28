package jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML;

import java.util.Stack;
import java.util.Vector;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathMLClassification;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperand;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.mathML.MathMLDefinition.eMathOperator;

/**
 * MathML演算子クラス
 */
public abstract class MathOperator extends MathFactor {

    /* 被演算要素ベクタ */
    protected Vector<MathFactor> m_vecFactor;

    /* 演算子種類 */
    protected eMathOperator m_operatorKind;

    /* 有効判定 */
    protected boolean m_bEnable;

    /* 必要被演算要素個数 */
    protected int m_unMinFactorNum;

    /*-----コンストラクタ-----*/
    public MathOperator(String strPresentText, eMathOperator operatorKind,
            int unMinFactorNum) {
        super(strPresentText, eMathMLClassification.MML_OPERATOR);
        m_vecFactor = new Vector<MathFactor>();
        m_operatorKind = operatorKind;
        m_bEnable = true;
        m_unMinFactorNum = unMinFactorNum;
    }

    /*-----デストラクタ-----*/
    // ~MathOperator(void){

    // /*イテレータ取得*/
    // vector<MathFactor*>::iterator it = m_vecFactor.begin();

    // /*ベクタ中のインスタンス全解放*/
    // for(it=m_vecFactor.begin();it!=m_vecFactor.end();it++){
    // SAFE_DELETE(*it);
    // }
    // }

    // ========================================================
    // getValue
    // 要素値取得メソッド
    //
    // @return
    // 演算結果
    //
    // ========================================================
    /*-----演算結果取得メソッド-----*/
    public double getValue() throws MathException {
        /* 演算結果を返す */
        return calculate();
    }

    // ========================================================
    // setValue
    // 値格納メソッド
    //
    // @arg
    // double dValue : 設定する値
    //
    // ========================================================
    /*-----値格納メソッド-----*/
    public void setValue(double dValue) throws MathException {
        throw new MathException("MathOperator", "setValue",
                "can't set value on operator");
    }

    // ========================================================
    // addFactor
    // 要素追加メソッド
    //
    // @arg
    // MathFactor* pFactor : 追加要素
    //
    // ========================================================
    /*-----要素追加メソッド-----*/
    public void addFactor(MathFactor pFactor) {
        /* ベクタへの追加 */
        m_vecFactor.add(pFactor);
    }

    // ========================================================
    // setEnable
    // インスタンス有効判定設定メソッド
    //
    // @arg
    // bool bEnable : 有効無効フラグ
    //
    // ========================================================
    /*-----有効状態設定取得メソッド-----*/
    public void setEnable(boolean bEnable) {
        m_bEnable = bEnable;
    }

    // ========================================================
    // isEnable
    // インスタンス有効判定メソッド
    //
    // @return
    // 有効判定 : bool
    //
    // ========================================================
    public boolean isEnable() {
        return m_bEnable;
    }

    // ========================================================
    // hasFactor
    // 要素所持判定メソッド
    //
    // @return
    // 要素所持判定 : bool
    //
    // ========================================================
    /*-----要素所持判定メソッド-----*/
    public boolean hasFactor() {
        return m_vecFactor.size() > 0;
    }

    // ========================================================
    // createCopy
    // 数式複製メソッド
    //
    // @return
    // 複製演算子インスタンス : MathFactor
    //
    // ========================================================
    /*-----数式複製メソッド-----*/
    public MathFactor createCopy() throws MathException {
        /* 関数の場合 */
        if (this.matches(eMathOperator.MOP_FN)) {
            return ((Math_fn) this).createCopy();
        }

        /* その他の演算子 */
        else {

            /* 演算子の複製 */
            MathOperator newOperator = MathFactory
                    .createOperator(m_operatorKind);

            /* すべての子要素を複製 */
            for (MathFactor it : m_vecFactor) {
                newOperator.addFactor(it.createCopy());
            }

            return newOperator;
        }
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
    public void replace(MathOperand pOldOperand, MathFactor pNewFactor) {
        /* 関数の場合(ここで一致する場合は置換対象ではない． */
        // if(this->matches(MOP_FN)){
        // return;
        // }

        /* すべての要素を調べる */
        for (int i = 0; i < m_vecFactor.size(); i++) {
            MathFactor it = m_vecFactor.get(i);

            /* オペランドの場合 */
            if (it.matches(eMathMLClassification.MML_OPERAND)) {

                /* オペランドの置換 */
                if (((MathOperand) it).matches(pOldOperand)) {
                    m_vecFactor.set(i, pNewFactor);
                }
            }
            /* オペレータの場合 */
            else if (it.matches(eMathMLClassification.MML_OPERATOR)) {
                /* 関数の場合 */
                if (((MathOperator) it).matches(eMathOperator.MOP_FN)) {

                    /* 関数の置換 */
                    if (((Math_fn) it).matches(pOldOperand)) {
                        m_vecFactor.set(i, pNewFactor);
                    }
                }

                /* その他の場合 */
                else {
                    /* 再帰呼び出し */
                    ((MathOperator) it).replace(pOldOperand, pNewFactor);
                }
            }
        }
    }

    // ========================================================
    // replace
    // 置換メソッド(関数置換用オーバーロード)
    // こちらは引数の一致まで検査
    //
    // @arg
    // Math_fn* pOldFunction : 置換対象の関数
    // MathFactor* pNewFactor : 置換後の数式
    //
    // @throws
    // MathExpression
    //
    // ========================================================
    public void replace(Math_fn pOldFunction, MathFactor pNewFactor) {
        /* 関数の場合(ここで一致する場合は置換対象ではない． */
        // if(this->matches(MOP_FN)){
        // return;
        // }

        /* すべての要素を調べる */
        for (int i = 0; i < m_vecFactor.size(); i++) {
            MathFactor it = m_vecFactor.get(i);

            /* オペレータの場合 */
            if (it.matches(eMathMLClassification.MML_OPERATOR)) {

                /* 関数の場合 */
                if (((MathOperator) it).matches(eMathOperator.MOP_FN)) {

                    /* 関数の置換 */
                    if (((Math_fn) it).matches(pOldFunction)) {
                        m_vecFactor.set(i, pNewFactor);
                    }
                }

                /* その他の場合 */
                else {
                    /* 再帰呼び出し */
                    ((MathOperator) it).replace(pOldFunction, pNewFactor);
                }
            }
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
    /*-----関数探索メソッド-----*/
    public void searchFunction(MathOperand pSearchOperand,
            Vector<Math_fn> pvecDstFunctions) {
        /* 検索関数に一致する場合 */
        if (this.matches(eMathOperator.MOP_FN)
                && ((Math_fn) this).matches(pSearchOperand)) {
            pvecDstFunctions.add((Math_fn) this);
        }

        /* その他の演算子 */
        else {
            /* すべての要素を調べる */
            for (MathFactor it : m_vecFactor) {

                /* 演算子の場合 */
                if (it.matches(eMathMLClassification.MML_OPERATOR)) {

                    /* 再帰呼び出し */
                    ((MathOperator) it).searchFunction(pSearchOperand,
                            pvecDstFunctions);
                }
            }
        }
    }

    // ========================================================
    // searchUnknownVariable
    // 未知変数探索メソッド
    //
    // @arg
    // stack<OperatorTracks*> stcOperatorTracks : 数式ツリー探索時に発見した演算子へのポインタを
    // 保存しておくスタック
    // vector<MathOperand*> vecUnknownVar : 未知変数リストを保持するベクタ
    //
    // @return
    // 見つからない場合 / NULL
    // 未知変数発見時 / 未知変数インスタンス : MathOperand*
    //
    // ========================================================
    /*-----未知変数探索メソッド-----*/
    public MathOperand searchUnknownVariable(
            Stack<OperatorTracks> stcOperatorTracks,
            Vector<MathOperand> vecUnknownVar) {
        /* 四則演算子以外は探さない */
        if (!this.matches(eMathOperator.MOP_PLUS)
                && !this.matches(eMathOperator.MOP_MINUS)
                && !this.matches(eMathOperator.MOP_TIMES)
                && !this.matches(eMathOperator.MOP_DIVIDE)) {
            return null;
        }

        /* すべての要素を調べる */
        for (int nOperatorNum = 0; nOperatorNum < m_vecFactor.size(); nOperatorNum++) {
            MathFactor it1 = m_vecFactor.get(nOperatorNum);

            /* 演算子の場合 */
            if (it1.matches(eMathMLClassification.MML_OPERATOR)) {

                /* スタックに探索演算子情報を追加 */
                OperatorTracks tracks = new OperatorTracks();
                tracks.pMathOperator = this;
                tracks.unEdgeNumber = nOperatorNum;
                stcOperatorTracks.push(tracks);

                /* 再帰呼び出し */
                MathOperand pUnknownVar = ((MathOperator) it1)
                        .searchUnknownVariable(stcOperatorTracks, vecUnknownVar);

                /* 未知変数が見つかれば戻す */
                if (pUnknownVar != null) {
                    return pUnknownVar;
                }
            }
            /* 被演算子の場合 */
            else if (it1.matches(eMathMLClassification.MML_OPERAND)) {

                /* すべての未知変数を調べる */
                for (MathOperand it2 : vecUnknownVar) {

                    /* 未知変数との一致判定 */
                    if (it2.matches((MathOperand) it1)) {

                        /* 未知変数を戻す */
                        return (MathOperand) it1;
                    }
                }
            }
        }

        /* 未知変数を検出できなかった */
        return null;
    }

    // ========================================================
    // removeFactor
    // 数式要素削除メソッド
    //
    // @arg
    // unsigned int unFactorNum : 何番目の要素を削除するか
    //
    // @throws
    // MathException
    //
    // ========================================================
    /*-----ノード削除・修復メソッド-----*/
    public void removeFactor(int unFactorNum) throws MathException {
        /* 削除しようとした要素が存在しない場合 */
        if (unFactorNum >= m_vecFactor.size()) {

            /* 例外を投げる */
            throw new MathException("MathOperator", "removeFactor",
                    "missing factor is being removed");
        }

        /* 要素の削除 */
        m_vecFactor.remove(unFactorNum);

        /* 必要最低限の被演算要素があるか */
        if (m_vecFactor.size() < m_unMinFactorNum) {

            /* 最低限の被演算要素がなければ無効化 */
            this.setEnable(false);
        }
    }

    // ========================================================
    // restoreFactor
    // removeFactor後のの数式ツリー修復メソッド
    // 仮想メソッドであり，演算子によって処理が異なる
    //
    // @arg
    // unsigned int unRemovedFactorNum : 何番目の要素が削除されたか
    //
    // ========================================================
    public void restoreFactor(int unRemovedFactorNum) {
        /* すべての要素を調べる */
        for (MathFactor it : m_vecFactor) {

            /* 演算子をチェック */
            if (it.matches(eMathMLClassification.MML_OPERATOR)) {

                /* 有効でないノードを削除する */
                if (!((MathOperator) it).isEnable()) {

                    /* 子ノードの削除 */
                    m_vecFactor.remove(it);
                }
            }
        }
    }

    /*-----オブジェクト比較メソッド-----*/

    // ========================================================
    // matches
    // オブジェクト比較メソッド
    //
    // @return
    // 同一判定 : bool
    //
    // ========================================================
    public boolean matches(MathOperator pOperator) {
        return m_operatorKind == pOperator.m_operatorKind;
    }

    public boolean matches(eMathOperator operatorKind) {
        return m_operatorKind == operatorKind;
    }

    public boolean matches(MathOperand pOperand) {
        return false;
    }

    // ========================================================
    // matchesExpression
    // 数式比較メソッド
    //
    // @arg
    // const MathFactor *pFactor : 比較対照ファクター
    //
    // @return
    // 一致判定 : bool
    //
    // ========================================================
    /*-----数式比較メソッド-----*/
    public boolean matchesExpression(MathFactor pFactor) {
        /* 比較要素が演算子でなければ終了 */
        if (!pFactor.matches(eMathMLClassification.MML_OPERATOR)) {
            return false;
        }

        /* 要素ベクタサイズ比較 */
        if (((MathOperator) pFactor).m_vecFactor.size() != m_vecFactor.size()) {
            return false;
        }

        /* すべての要素を比較 */
        for (int i = 0; i < m_vecFactor.size(); i++) {
            MathFactor it1 = m_vecFactor.get(i);
            MathFactor it2 = ((MathOperator) pFactor).m_vecFactor.get(i);
            if (!it1.matchesExpression(it2)) {
                return false;
            }
        }

        return true;
    }

    // ========================================================
    // getLeftExpression
    // 左辺式取得メソッド
    //
    // @return
    // 左辺式 : MathFactor*
    //
    // ========================================================
    /*-----左辺式取得メソッド-----*/
    public MathFactor getLeftExpression() {
        /* =演算子の場合 */
        if (this.matches(eMathOperator.MOP_EQ)) {

            /* 左辺式を返す */
            return m_vecFactor.get(0);
        }
        /* その他の演算子の場合 */
        else {
            if (m_vecFactor.size() > 0) {

                /* その他の場合 */
                if (m_vecFactor.get(0).matches(
                        eMathMLClassification.MML_OPERATOR)) {
                    return ((MathOperator) m_vecFactor.get(0))
                            .getLeftExpression();
                }
                /* 演算子の場合 */
                else {
                    return null;
                }
            }
        }

        return null;
    }

    // ========================================================
    // getRightExpression
    // 右辺式取得メソッド
    //
    // @return
    // 右辺式 : MathFactor*
    //
    // ========================================================
    /*-----右辺式取得メソッド-----*/
    public MathFactor getRightExpression() {
        /* =演算子の場合 */
        if (this.matches(eMathOperator.MOP_EQ)) {

            /* 右辺式を返す */
            return m_vecFactor.get(1);
        }
        /* その他の演算子の場合 */
        else {
            if (m_vecFactor.size() > 0) {

                /* 演算子の場合 */
                if (m_vecFactor.get(0).matches(
                        eMathMLClassification.MML_OPERATOR)) {
                    return ((MathOperator) m_vecFactor.get(0))
                            .getRightExpression();
                }
                /* その他の場合 */
                else {
                    return null;
                }
            }
        }

        return null;
    }

    // ========================================================
    // getFirstVariable
    // 式中第一変数取得メソッド
    //
    // @return
    // 式のはじめの変数 : Math_ci*
    //
    // ========================================================
    /*-----第一変数取得メソッド-----*/
    public Math_ci getFirstVariable() {
        /* すべての要素を調べる */
        for (MathFactor it : m_vecFactor) {

            /* オペランドの場合 */
            if (it.matches(eMathMLClassification.MML_OPERAND)
                    && ((MathOperand) it).matches(eMathOperand.MOPD_CI)) {
                return (Math_ci) it;
            }

            /* オペレータの場合 */
            else if (it.matches(eMathMLClassification.MML_OPERATOR)) {

                /* diffの場合 */
                if (((MathOperator) it).matches(eMathOperator.MOP_DIFF)) {
                    return (Math_ci) (((MathOperator) it).m_vecFactor.get(1));
                }

                /* 再帰呼び出し */
                Math_ci pTmpVar = ((MathOperator) it).getFirstVariable();

                /* 結果が得られていれば返す */
                if (pTmpVar != null) {
                    return pTmpVar;
                }
            }
            /* その他の要素は現状調査する必要がない */
            else {
            }
        }

        /* 変数が見つからなかった */
        return null;
    }

    /*-----演算命令メソッド-----*/
    public abstract double calculate() throws MathException;

}
