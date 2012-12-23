package jp.ac.ritsumei.is.hpcss.cellMLonGPU.parser;

import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.CellMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.MathException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.RelMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TableException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.TecMLException;
import jp.ac.ritsumei.is.hpcss.cellMLonGPU.exception.XMLException;

/**
 * XML解析基底クラス
 */
public abstract class XMLAnalyzer {

    /*-----コンストラクタ-----*/
    public XMLAnalyzer() {
    }

    /*-----解析関連仮想メソッド-----*/
    abstract public void findTagStart(String strTag, XMLAttribute pXMLAttr)
            throws MathException, XMLException, RelMLException,
            CellMLException, TecMLException;

    abstract public void findTagEnd(String strTag) throws MathException,
            RelMLException, CellMLException;

    abstract public void findText(String strText) throws MathException,
            CellMLException, TableException;
}
