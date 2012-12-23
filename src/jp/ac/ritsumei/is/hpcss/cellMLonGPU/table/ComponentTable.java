package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;

/**
 * ComponentTableクラス
 */
public class ComponentTable extends Table<VariableTable> {

    /*-----コンストラクタ-----*/
    public ComponentTable(String strTableName) {
        super(strTableName);
    }

    /*-----変数テーブルの挿入-----*/
    public void insert(VariableTable newElement) {
        /* 値の挿入 */
        super.insert(newElement.getName(), newElement);
    }

    /*-----コネクションの設定-----*/
    public void setConnection(String strSrcCompName, String strDstCompName,
            String strSrcVarName, String strDstVarName) throws TableException {
        /* 変数テーブルの取得 */
        VariableTable pSrcVariableTable = searchTable(strSrcCompName); // コネクションを設定するコンポーネントの変数テーブル
        VariableTable pDstVariableTable = searchTable(strDstCompName); // コネクション先のコンポーネントの変数テーブル

        /* コネクション設定 */
        pSrcVariableTable.setConnection(pDstVariableTable, strSrcVarName,
                strDstVarName);
    }

    /*-----変数テーブルの取得-----*/
    public VariableTable searchTable(String strTableName) throws TableException {
        return find(strTableName);
    }

}
