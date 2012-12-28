package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;

/**
 * VariableTableクラス
 */
public class VariableTable extends Table<VariableReference> {

    // class tagVariableReference{
    // String strVariableName; //変数名
    // VariableTable pParentTable; //親コンポーネントの変数テーブル
    // tagVariableReference pConnection; //コネクション先参照構造体
    // String strInitValue; //初期値文字列
    // };

    /*-----コンストラクタ-----*/
    public VariableTable(String strTableName) {
        super(strTableName);
    }

    /*-----変数の挿入-----*/
    public void insert(String strVariableName, String strInitValue) {

        /* 構造体を構成 */
        VariableReference pVarRef = new VariableReference();
        pVarRef.strVariableName = strVariableName;
        pVarRef.pParentTable = this;
        pVarRef.pConnection = null;
        pVarRef.strInitValue = strInitValue;

        /* 値の挿入 */
        super.insert(strVariableName, pVarRef);
    }

    /*-----コネクションの設定-----*/
    public void setConnection(VariableTable pDstVariableTable,
            String strSrcVarName, String strDstVarName) throws TableException {

        /* コネクション元の変数参照構造体を取得 */
        VariableReference pVarSrcRef = find(strSrcVarName);

        /* コネクション先の変数参照構造体を取得 */
        VariableReference pVarDstRef = pDstVariableTable.find(strDstVarName);

        /* コネクション元の現在のコネクション先を更新する */
        while (pVarSrcRef != null) {

            /* コネクションを設定 */
            VariableReference pConnectVarRef = pVarSrcRef.pConnection;
            pVarSrcRef.pConnection = pVarDstRef;

            /* 初期値のコピー */
            if (pVarSrcRef.strInitValue != null
                    && pVarSrcRef.strInitValue.length() != 0
                    && (pVarDstRef.strInitValue == null || pVarDstRef.strInitValue
                            .length() == 0)) {
                pVarDstRef.strInitValue = pVarSrcRef.strInitValue;
            } else if ((pVarSrcRef.strInitValue == null || pVarSrcRef.strInitValue
                    .length() == 0)
                    && pVarDstRef.strInitValue != null
                    && pVarDstRef.strInitValue.length() != 0) {
                pVarSrcRef.strInitValue = pVarDstRef.strInitValue;
            }

            /* 次のコネクションへ */
            pVarSrcRef = pConnectVarRef;
        }

        /* 現在の変数参照とコネクション先の変数参照を入れ替える */
        // this->replace(strSrcVarName,varRef);
    }

    /*-----完全名の取得-----*/
    public String getFullName(String strName) throws TableException {
        /* 変数参照の取得 */
        VariableReference pVarRef = this.find(strName);

        /* コネクションを辿る */
        while (pVarRef.pConnection != null) {
            pVarRef = pVarRef.pConnection;
        }

        /* 完全名を返す */
        return pVarRef.pParentTable.getName() + "." + pVarRef.strVariableName;
    }

    /*-----初期値の取得-----*/
    public String getInitValue(String strName) throws TableException {
        /* 変数参照の取得 */
        VariableReference pVarRef = this.find(strName);
        /* 初期値チェック */
        if (pVarRef.strInitValue == null || pVarRef.strInitValue.length() == 0) {
            throw new TableException("VariableTable", "getInitValue",
                    "no initialize value found");
        }

        /* 初期値を返す */
        return pVarRef.strInitValue;
    }

}
