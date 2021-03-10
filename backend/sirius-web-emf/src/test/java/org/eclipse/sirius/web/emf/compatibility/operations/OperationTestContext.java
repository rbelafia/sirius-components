/*******************************************************************************
 * Copyright (c) 2019, 2021 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.emf.compatibility.operations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreter;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Utility class for testing model operation handlers.
 *
 * @author lfasani
 */
public class OperationTestContext {

    public static final String CLASS1_NAME = "class1Name"; //$NON-NLS-1$

    public static final String ROOT_PACKAGE_NAME = "rootPackageName"; //$NON-NLS-1$

    private EPackage rootPackage;

    private EClass class1;

    private Map<String, Object> variables;

    private AQLInterpreter interpreter;

    private AQLInterpreterAPI interpreterAPI;

    private AQLEntry entry;

    public OperationTestContext() {
        // initialize the semantic model
        this.rootPackage = EcoreFactory.eINSTANCE.createEPackage();
        this.rootPackage.setName(ROOT_PACKAGE_NAME);
        this.class1 = EcoreFactory.eINSTANCE.createEClass();
        this.class1.setName(CLASS1_NAME);
        this.rootPackage.getEClassifiers().add(0, this.class1);

        this.interpreter = new AQLInterpreter();
        this.interpreterAPI = new AQLInterpreterAPI(interpreter);
        this.entry = interpreterAPI.initializeUser(List.of(ModelOperationServices.class), List.of(EcorePackage.eINSTANCE));

        this.variables = new HashMap<>();
        this.variables.put(VariableManager.SELF, this.rootPackage);
    }

    public EPackage getRootPackage() {
        return this.rootPackage;
    }

    public EClass getClass1() {
        return this.class1;
    }

    public Map<String, Object> getVariables() {
        return this.variables;
    }

    public AQLInterpreterAPI getInterpreter() {
        return this.interpreterAPI;
    }

    public AQLEntry getEntry() {return this.entry;}
}
