package com.szmsd.bas.domain;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author chenanze
 * @date 2020-07-13
 * @description
 */
public class Calculation {

    /**
     * @param
     * @return 是否为中文
     * @warn 公式
     */
    public static Object analyticalMathematicalFormula(String formula, String value) {
        //替换中英文括号
        formula = formula.replace("w", value);
        formula = formula.replace("（", "(");
        formula = formula.replace("）", ")");
        Object result = formula;
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            result = engine.eval(formula);//
        } catch (ScriptException e) {
            return result;
        }
        return result;
    }

}