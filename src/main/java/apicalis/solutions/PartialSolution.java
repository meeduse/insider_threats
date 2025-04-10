
/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis.solutions;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.statespace.State;

/**
 * Represents a partial (local) solution for an ant.
 * A partial solution is the best state found by an ant.
 * It is associated with the evaluation score (f).
 * 
 * @author Aurélien Pepin
 */
public class PartialSolution implements Comparable {
    private final State state;
    private final float score;

    public PartialSolution(State state, float score) {
        this.state = state;
        this.score = score;
    }

    @Override
    public int compareTo(Object t) {
        if (!(t instanceof PartialSolution))
            throw new UnsupportedOperationException("Impossible comparison.");
        
        // Using the score evaluation to compare solutions
        PartialSolution other = (PartialSolution) t;
        
        return (this.score == other.score) ? 0 : ((this.score < other.score) ? -1 : 1);
    }

    public State getState() {
        return state;
    }

    public float getScore() {
        return score;
    }
    
    public AbstractEvalResult getProperty(String name) {
        return state.eval(name);
    }
}
