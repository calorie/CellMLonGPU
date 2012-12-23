package jp.ac.ritsumei.is.hpcss.cellMLonGPU.table;

public class VariableReference {

    String strVariableName; // 変数名
    VariableTable pParentTable; // 親コンポーネントの変数テーブル
    VariableReference pConnection; // コネクション先参照構造体
    String strInitValue; // 初期値文字列

}
