/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis.solutions;

/**
 * Observer pattern: observable.
 * @author Aurélien Pepin
 */
public interface Observable {
    
    /**
     * Notify the colony.
     * @param solution A solution whose score is 0.0
     */
    public void notifyColony(PartialSolution solution);
}
