/*******************************************************************************
 * Copyright (c) 2024 Akram Idani [0000−0003−2267−3639], Aurélien Pepin, and Mariem Triki
 * Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package apicalis;

import apicalis.variables.ClassVariable;
import apicalis.variables.Variable;
import de.prob.Main;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.Api;
import de.prob.scripting.ModelTranslationError;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;
import de.prob.statespace.State;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the algorithm on many parameters combinations.
 * @author Aurélien Pepin
 */
public class Tests_ProB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Instance of the API to work with
        Api api = Main.getInjector().getInstance(Api.class);
        System.out.println("LOADING ProB: " + api.getVersion());
        
        // Work with the API
        try {
            apicalisProB(api);
        } catch (IOException | ModelTranslationError ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        
        System.exit(0);
    }

    private static void apicalisProB(Api api) throws IOException, ModelTranslationError {
        long start = System.currentTimeMillis();
        // Load the state space
        StateSpace sspace = api.b_load("machines/rbac/RBAC_Model.mch");
        
        ClassicalBModel model = (ClassicalBModel) sspace.getModel();
        ClassicalBMachine machine = model.getMainMachine();
        
        /**
         * APICALIS ALGORITHM.
         */        
        List<Variable> variables = new ArrayList<>();
        // Final state: A target state that satisfies all the desired conditions or variable assignments.
        // It represents the goal of the simulation, where the system reaches a configuration that we're looking for
        variables.add(new ClassVariable("Customer", "{Paul,Martin,Bob}"));
        //variables.add(new ClassVariable("Account", "{cpt1,cpt2,cpt3,cpt4}"));
        variables.add(new ClassVariable("AccountOwner", "{(cpt1|->Bob),(cpt4|->Bob),(cpt2|->Martin),(cpt3|->Paul)}",2));
        
        State initState = sspace.getRoot();
        //Number of ants 
        List<Integer> antsNumbers = Arrays.asList(5);
        AntColony colony;
        //initial state
        Transition init = initState.findTransition("$initialise_machine");
        if (init != null) {
            initState = init.getDestination();
        }
        State current = initState.perform("setPermissions");
        current = current.perform("secure_InitiateAttack");

        for (Integer i : antsNumbers) {
            System.out.println("--------------------------------------------");
            System.out.println("----------- Number of ants: " + i + "\t------------");
            colony = new AntColony(i, current, variables);
            //setting time for time measurement 
            colony.set_time(start);
            colony.simulate();
            
        }
        
    }
}
