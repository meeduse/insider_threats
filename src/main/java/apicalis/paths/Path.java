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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a path of origins from one state to another.
 * @author Aurélien Pepin
 */
public class Path {

    /**
     * Source state, beginning of the path.
     * If null, indicates the root of the statespace.
     */
    private final State source;
    
    /**
     * Destination state, end of the path.
     * Shouldn't be null.
     */
    private final State dest;
    
    /**
     * Transitions' map (as in the colony).
     * Shouldn't be null.
     */
    private final Map<State, Transition> origins;
    
    /**
     * Ordered list of transitions from "source" to "dest".
     * Computed in the constructor.
     */
    private final List<Transition> transitions;
    
    /**
     * CONSTRUCTOR.
     * @param source
     * @param dest
     * @param origins 
     */
    public Path(State source, State dest, Map<State, Transition> origins) {
        if (dest == null || origins == null)
            throw new NullPointerException("Can't build the path for null states");
        
        this.source = source;
        this.dest = dest;
        this.origins = origins;
        
        this.transitions = new LinkedList<>();
        
        // Find origins from "source" to "dest".
        this.compute();
    }
    
    /**
     * First computed in the constructor.
     */
    private void compute() {
        State currState = this.dest;
        Transition currTransition = this.origins.get(currState);
        
        while (computeCondition(currState, currTransition)) {
            this.transitions.add(0, currTransition);
            
            currState = currTransition.getSource();
            currTransition = this.origins.get(currState);
        }
    }
    
    /**
     * If the source is null, transitions are added until the end.
     * Otherwise, we look if the source equals the current state.
     * 
     * @param currState
     * @param currTransition
     * @return 
     */
    private boolean computeCondition(State currState, Transition currTransition) {
        if (!origins.containsKey(currState))
            throw new UnsupportedOperationException("Can't build the path with missing states");
        
        if (source == null)
            return currTransition != null;
        
        return !source.equals(currState);
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (int i = 0; i < transitions.size(); ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            
            sb.append(transitions.get(i).getName()).append("(");
            sb.append(transitions.get(i).getParameterPredicate()).append(")");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Print the path in a readable format.
     */
    public void prettyPrint() {
        System.out.println("History: ");
        
        for (int i = 0; i < transitions.size(); ++i) {
            System.out.print("\t" + (i + 1) + ". " + transitions.get(i).getName());
            System.out.println("(" + transitions.get(i).getParameterPredicate() + ")");
        }
    }
}
