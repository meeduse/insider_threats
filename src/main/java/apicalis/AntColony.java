/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis;

import apicalis.paths.PathFromRoot;
import apicalis.solutions.Observer;
import apicalis.solutions.PartialSolution;
import apicalis.variables.Variable;
import de.prob.statespace.State;
import de.prob.statespace.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Modelling of the colony of Pachycondyla Apicalis.
 * TODO: Description.
 * 
 * @author Aurélien Pepin
 */
public class AntColony implements Observer {
    
    /**
     * Position of the nest of the colony.
     */
    private State nest;
    
    /**
     * List of hunting ants in the colony.
     */
    private final List<Ant> ants;
    
    /**
     * General amplitude for the colony.
     * Should be in [0; 1].
     */
    private float amplitude;
    
    /**
     * Global best solution.
     * Merging all local solutions from ants.
     */
    private PartialSolution bestSolution;
    
    /**
     * Final searched values.
     * Should be valid according to the B syntax.
     */
    private final List<Variable> finalValues;
    
    /**
     * Associates a state with its coming transition.
     * Useful to build a trace of states.
     * 
     * Example. Root --T--> State1 is stored
     * in the map with the following entries:
     *      - (Root, null)
     *      - (State1, T)
     */
    private final Map<State, Transition> origins;
    
    /**
     * Keeps a set of states in which it is forbidden to move back.
     * These states are the root and all states accessibles
     * in one transition from the root, as they "uninitialize" the B machine.
     */
    private final Set<State> forbiddenReturns;
    
    /**
     * Keeps a table of evaluations (memoization).
     * Speed up a lot the execution of the algorithm.
     */
    private final Map<State, Float> evals;
    
    /**
     * Keeps a set of explored states.
     * Useful to measure the performance of the algorithm.
     */
    private final Set<State> exploredStates;
    
    /**
     * Constants for ants parameters.
     */
    private final int LOCAL_PATIENCE = 5;
    private final int LOCAL_AMPLITUDE = 3;
    private final int ANT_MEMORY = 3;
    
    // From the thesis (p. 128)
    private final int GLOBAL_PATIENCE = /* 2 * */ (LOCAL_PATIENCE + 1) * ANT_MEMORY;
    private final int GLOBAL_AMPLITUDE = 15;
    
    // The number of evaluations is >= than the number of states
    private final int MAX_NUMBER_OF_EVALUATIONS =1000;
    private final int MAX_NUMBER_OF_STATES = 500;
    
    // True if ants are allowed to move back
    private final boolean ANT_BACK = true;
    
    // True if variables can have different weights
    private final boolean WEIGHTING = true;
    
    // True if the progression of the algorithm should be printed
    private final boolean VERBOSE = true;
    
    // True if the algorithm should stop from the perfect solution
    private final boolean IMMEDIATE_STOP = true;
            
    /**
     * Performance measurement.
     */
    public long numberOfEvaluations = 0;
    public long numberOfStates = 0;
    public long numberOfTransition = 0;

    // Timestamp marking the start time of the simulation 
    public long start;
    // Set of unique transitions in the current colony
    public HashSet<Transition> uniqueTransitions = new HashSet<>();     

   
        
    /**
     * CONSTRUCTOR. Initialize a list of n ants.
     * @param n     Number of ants
     * @param root  Root of the state space, initial position of the nest
     * @param finalValues  Set of state values (variables, relations) in B.
     */
    public AntColony(int n, State root, List<Variable> finalValues) {
        this.nest = root;
        this.ants = new ArrayList<>();
        this.createAnts(n);
        
        this.finalValues = finalValues;
        this.evals = new HashMap<>();
        this.exploredStates = new HashSet<>();
        
        // Origin parameters
        this.origins = new HashMap<>();
        this.origins.put(root, null);
        
        this.forbiddenReturns = new HashSet<>();
        this.initForbiddenReturns(root);
        
    }

    public void set_time(long start){
        this.start=start;
    }
    
    /**
     * CONSTRUCTOR/INITIALIZATION. Initialize ants.
     * @param n     Number of ants
     */
    private void createAnts(int n) {
        if (n < 1)
            throw new IllegalArgumentException("Bad number of ants");
        
        for (int i = 0; i < n; i++) {
            this.ants.add(new Ant(LOCAL_PATIENCE, LOCAL_AMPLITUDE, ANT_MEMORY, this));
        }
    }
    
    
    /**
     * ALGORITHM. Simulate a colony of Apicalis ants.
     * Ants are well initialized before this method call.
     */
    public void simulate() {
        // The initial site of the nest is the root of the state space.
        int T = 1;
        
        while (numberOfEvaluations < MAX_NUMBER_OF_EVALUATIONS && (this.bestSolution == null || this.bestSolution.getScore() > 0) ) {
            // Local behaviour of ants
            for (Ant a : ants) a.search();
            
            // TODO. If the nest should be moved
            if (T % GLOBAL_PATIENCE == 0) {
                this.nest = this.getBestSolution();
                this.printProgression();

                for (Ant a : ants)
                    a.emptyMemory();
            }
            
            T++;
        }
        this.printProgression();
        if (!(this.bestSolution == null)) {
            System.out.println("End of the algorithm. Best solution: " + this.bestSolution.getScore());
        }
        System.out.println("Total number of explored states: " + numberOfStates);
    }
    
    
    /**
     * If the score 0.0 is found, the colony is notified.
     * @param solution  A solution whose score is 0.0
     */
    @Override
    public void stopStimulation(PartialSolution solution) {
        this.bestSolution = solution;
        this.fillOrigins(solution.getState(), this.origins.get(solution.getState())); 
        this.nest = solution.getState(); 
        this.printProgression();
        System.out.println("End of the algorithm. Best solution: " + this.bestSolution.getScore());
        System.out.println("Total number of transitions : " +    numberOfTransition);
        System.out.println("Total number of transitions (unique) : " + this.uniqueTransitions.size());
        System.out.println("Total number of explored states : " + numberOfStates);
        System.out.println("Total number of evaluations : " + numberOfEvaluations);
        long end = System.currentTimeMillis();
        System.out.printf("Total duration (main): %.2f seconds\n", (end - start) / 1000.0);
        System.exit(0);
    }
    
  
    /**
     * OPERATOR. Neighborhood operator (O_explo).
     * @param s The first point, already visited.    
     * @param amplitude Maximum number of followed transitions. 
     * @return  A new point in the neighborhood of s.
     */
    public State opExplo(State s, int amplitude) {
        if (s == null || amplitude < 1)
            return null;
        
        // Number of random transitions to follow
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int toFollow = rand.nextInt(amplitude);
        
        State randState = randomStateFrom(s, rand);
        
        while (toFollow > 0) {
            randState = randomStateFrom(randState, rand);
            toFollow--;
        }
        
        return randState;
    }
    private State randomStateFrom(State s, ThreadLocalRandom rand) {
        List<Transition> transitions = new ArrayList<>();
        for (Transition t : s.getOutTransitions()) {
            String name = t.getName().toLowerCase(); 
            if (!name.contains("launchattack") && !name.contains("secure_initiateattack")) {
                transitions.add(t);
            }
        }
        State randState = null;
        Transition randTransition=null;
    
        if (transitions.size() > 0) {
            int randIndex = rand.nextInt(transitions.size() + (ANT_BACK ? 1 : 0));
    
            if (randIndex < transitions.size() || forbiddenReturns.contains(s)) {
                randTransition = transitions.get(Math.min(randIndex, transitions.size() - 1));
                randState = randTransition.getDestination();
                numberOfTransition++;
                this.fillOrigins(randState, randTransition);
            } else {
                randTransition = this.origins.get(s);
                randState = randTransition.getSource();
                numberOfTransition++;
            }
        }
        uniqueTransitions.add(randTransition);
        return randState;
    }
    /**
     * EVALUATION FONCTION in [0, 1].
     * Indicates the quality of an hunting site.
     *      - 0: perfect
     *      - 1: very bad
     * 
     * @param state
     * @return The "quality" of the hunting site.
     */
    public float f(State state) {
        numberOfEvaluations++;
        markStateAsExplored(state);
        
        if (state == null)
            return 1;
        
        if (!evals.containsKey(state)) {
            float similarityMean = 0;
            float sumOfWeights = 0;

            // Compute each similarity measure for each interesting variable
            for (Variable var : finalValues) {
                similarityMean += var.evaluate(state) * (WEIGHTING ? var.getWeight() : 1);
                sumOfWeights += (WEIGHTING ? var.getWeight() : 1);
            }

            float result = (sumOfWeights == 0) ? 1 : (similarityMean / sumOfWeights);
            evals.put(state, result);
        }
        
        System.out.println(numberOfEvaluations + ". EVALUATION: " + evals.get(state));
        return evals.get(state);
    }
    
    /**
     * EVALUATION FONCTION in [0, 1].
     * Shortcut for the state evaluation fonction.
     * 
     * @param hSite
     * @return The "quality" of the hunting site.
     */
    public float f(HuntingSite hSite) {
        return this.f(hSite.getSite());
    }
    
    /**
     * Get the best (global) solution among ants' best (local) solutions.
     * @return The state associated with the best evaluation score.
     */
    public State getBestSolution() {
        State bestState = null;
        float bestScore = Float.MAX_VALUE;
        PartialSolution bestFromAnts = null;
        
        for (Ant a : ants) {
            if (a.getBestSolution().getScore() < bestScore) {
                bestScore = a.getBestSolution().getScore();
                bestState = a.getBestSolution().getState();
                
                bestFromAnts = a.getBestSolution();
            }
        }
        
        if (this.bestSolution != null && this.bestSolution.getScore() < bestFromAnts.getScore()) {
            return this.bestSolution.getState();
        }
        
        this.bestSolution = bestFromAnts;
        return bestState;
    }
    
    /**
     * Associate a transition with its end state.
     * Useful to build a full path from the root to a state.
     * 
     * @param state
     * @param transition 
     */
    public void fillOrigins(State state, Transition transition) {
        if (this.origins.containsKey(state))
            return;
        
        this.origins.put(state, transition);
    }
    
    /**
     * Gathers states from which it is impossible to move back in a set.
     * @param root Root of the statespace
     */
    private void initForbiddenReturns(State root) {
        this.forbiddenReturns.add(root);
        
        for (Transition t : root.getOutTransitions()) {
            this.forbiddenReturns.add(t.getDestination());
        }
    }

    /**
     * Includes the state in the set of explored states and increase the counter.
     */
    private void markStateAsExplored(State state) {
        if (exploredStates.contains(state))
            return;
        numberOfStates++;
        exploredStates.add(state);
    }
    
    /**
     * When the nest moves, print the interesting variables and their values.
     * Doesn't print if the VERBOSE flag equals to False.
     */
    private void printProgression() {
        if (!VERBOSE) return;
        
        // Print the score of the best current solution
        if (this.bestSolution == null) {
            System.out.println("\n== Mouvement du nid (score : ???) ==");
            return; // ou un affichage plus adapté
          } else {
            System.out.println(
              "\n== Mouvement du nid (score : " + this.bestSolution.getScore() + ") =="
            );
          }
          
        // Print the result of each variable
        for (Variable variable : finalValues) {
            System.out.println(" - " + variable.getIdentifier() + ": " + this.bestSolution.getState().eval(variable.getIdentifier()));
        }
        
        // Print the path
        new PathFromRoot(nest, origins).prettyPrint();
        System.out.println("===========\n");
    }
    
    public State getNest() {
        return nest;
    }

    public int getSiteAmplitude() {
        return GLOBAL_AMPLITUDE;
    }
}
