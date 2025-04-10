/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis;

import de.prob.statespace.State;

/**
 * Represents an hunting site for an ant.
 * An hunting site encloses two things:
 *      - A state in the state space (s in S)
 *      - The number of fails associated with the state
 * 
 * @author Aurélien Pepin
 */
public class HuntingSite {
    
    /**
     * The state where the ant hunts.
     */
    private State site;
    
    /**
     * The number of fails in this state.
     */
    private int fails;

    
    /**
     * CONSTRUCTOR.
     * @param site  Initial site for the ant.
     */
    public HuntingSite(State site) {
        this.site = site;
        this.fails = 0;
    }
    
    /**
     * An hunting site should be forgotten if there were
     * too much fails for its exploration.
     * 
     * @param patience
     * @return True if the site should be forgotten.
     */
    public boolean shouldBeForgotten(int patience) {
        return this.fails >= patience;
    }
    
    /**
     * Add a fail for the hunting site.
     */
    public void addFail() {
        this.fails++;
    }
    
    /**
     * Reset fails if the exploration was successful.
     */
    public void resetFails() {
        this.fails = 0;
    }
    
    public State getSite() {
        return site;
    }

    public int getFails() {
        return fails;
    }

    public void setSite(State site) {
        this.site = site;
    }
}
