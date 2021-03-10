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
package org.eclipse.sirius.web.emf.compatibility.modeloperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.sirius.viewpoint.description.tool.ChangeContext;
import org.eclipse.sirius.viewpoint.description.tool.ModelOperation;
import org.eclipse.sirius.web.compat.api.IModelOperationHandler;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.representations.Status;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Handles the change context model operation.
 *
 * @author sbegaudeau
 */
public class ChangeContextOperationHandler implements IModelOperationHandler {

    private final AQLInterpreterAPI interpreter;

    private final ChildModelOperationHandler childModelOperationHandler;

    private final ChangeContext changeContext;

    private final AQLEntry entry;

    public ChangeContextOperationHandler(AQLInterpreterAPI interpreter, ChildModelOperationHandler childModelOperationHandler, ChangeContext changeContext, AQLEntry entry) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.childModelOperationHandler = Objects.requireNonNull(childModelOperationHandler);
        this.changeContext = Objects.requireNonNull(changeContext);
        this.entry = entry;
    }

    @Override
    public Status handle(Map<String, Object> variables) {
        String browseExpression = this.changeContext.getBrowseExpression();
        Map<String, Object> childVariables = new HashMap<>(variables);
        if (browseExpression != null && !browseExpression.isBlank()) {
            Optional<Object> optionalObject = this.interpreter.evaluateExpression(variables, browseExpression, entry).asObject();
            optionalObject.ifPresent(object -> childVariables.put(VariableManager.SELF, object));
        }

        List<ModelOperation> subModelOperations = this.changeContext.getSubModelOperations();
        return this.childModelOperationHandler.handle(this.interpreter, childVariables, subModelOperations, entry);
    }

}
