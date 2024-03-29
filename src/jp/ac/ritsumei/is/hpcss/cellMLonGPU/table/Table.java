package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

import java.util.HashMap;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;

/**
 * table基底クラス
 */
public class Table<T> {

    /*-----テーブル名-----*/
    private String m_strName;

    /*-----テーブル要素-----*/
    private HashMap<String, T> m_mapElements;

    /*-----親へのポインタ-----*/
    // 使用していないので削除
    // private Table m_pParentTable;

    /*-----コンストラクタ-----*/
    public Table(String strTableName) {
        m_strName = strTableName;
        // m_pParentTable = null;
        m_mapElements = new HashMap<String, T>();
    }

    /*-----名前取得-----*/
    public String getName() {
        /* 名前を返す */
        return m_strName;
    }

    /*-----テーブルへの挿入-----*/
    protected void insert(String strTableName, T newElement) {
        /* 値の挿入 */
        m_mapElements.put(strTableName, newElement);
    }

    /*-----名前から値の取得-----*/
    protected T find(String strTableName) throws TableException {
        /* 名前から検索する */
        T v = m_mapElements.get(strTableName);

        /* 見つからなかった場合 */
        if (v == null) {
            throw new TableException("Table", "find", "can't find table: "
                    + strTableName);
        }

        /* 見つかった要素を返す */
        return v;
    }

    /*-----値の置換-----*/
    protected void replace(String strTableName, T repElement) {
        /* 以前の値の削除と新たな値の挿入 */
        m_mapElements.put(strTableName, repElement);
    }

}
