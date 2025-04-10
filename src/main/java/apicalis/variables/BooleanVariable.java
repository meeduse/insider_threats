/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis.variables;

import de.prob.statespace.State;

/**
 * Represents a boolean as variable in the B-method.
 * Syntax: varName = {TRUE, FALSE}.
 * 
 * @author Aurélien Pepin
 */
public class BooleanVariable extends Variable {

    public BooleanVariable(String identifier, boolean value) {
        super(identifier, value == true ? "TRUE" : "FALSE");
    }
    
    public BooleanVariable(String identifier, boolean value, double weight) {
        super(identifier, value == true ? "TRUE" : "FALSE", weight);
    }

    @Override
    public float evaluate(State state) {
        String res = identifier + " = " + value;
        return "TRUE".equals(state.eval(res).toString()) ? 0 : 1;
    }
}
