/*******************************************************************************
 * Copyright (c) 2019, 2020 Obeo.
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
package org.eclipse.sirius.web.interpreter;

import org.eclipse.emf.ecore.EPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * The API for the AQL Interpreter service.
 * @author rbelafia
 */
@Controller
public class AQLInterpreterAPI {

    private final AQLInterpreter interpreter;

    @Autowired
    public AQLInterpreterAPI(AQLInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Result evaluateExpression(Map<String, Object> variables, String expressionBody, AQLEntry entry) {
        return this.interpreter.evaluateExpression(variables, expressionBody, entry);
    }

    public AQLEntry initializeUser(List<Class<?>> classes, List<EPackage> ePackages) {
        return this.interpreter.initializeUser(classes, ePackages);
    }


}
