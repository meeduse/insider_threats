/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis.paths;

import de.prob.statespace.State;
import de.prob.statespace.Transition;
import java.util.Map;

/**
 * Creates a path of transitions from the root state to another.
 * @author Aurélien Pepin
 */
public class PathFromRoot extends Path {

    public PathFromRoot(State dest, Map<State, Transition> transitions) {
        super(null, dest, transitions);
    }
    
}
