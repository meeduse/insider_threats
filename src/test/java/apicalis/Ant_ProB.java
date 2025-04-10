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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Combination of ProB Api and Apicalis.
 * @author Aurélien Pepin
 */
public class Ant_ProB {

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
        // Load the state space
        StateSpace sspace = api.b_load("machines/rbac/RBAC_Model.mch");
        
        ClassicalBModel model = (ClassicalBModel) sspace.getModel();
        ClassicalBMachine machine = model.getMainMachine();
        
        /**
         * APICALIS ALGORITHM.
         */        
        List<Variable> variables = new ArrayList<>();
        variables.add(new ClassVariable("Customer", "{Bob,Paul}"));
        // variables.add(new ClassVariable("Account", "{cpt1,cpt2,cpt3}")); // ?
        variables.add(new ClassVariable("AccountOwner", "{(cpt1|->Bob),(cpt2|->Bob),(cpt3|->Paul)}", 2));
        
        AntColony colony = new AntColony(5, sspace.getRoot(), variables);
        colony.simulate();
    }
}
