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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.ecore.extender.business.internal.accessor.ecore.EcoreIntrinsicExtender;
import org.eclipse.sirius.viewpoint.description.tool.ModelOperation;
import org.eclipse.sirius.viewpoint.description.tool.RemoveElement;
import org.eclipse.sirius.web.compat.api.IModelOperationHandler;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.representations.Status;
import org.eclipse.sirius.web.representations.VariableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle the {@link RemoveElement} model operation.
 *
 * @author lfasani
 */
public class RemoveElementOperationHandler implements IModelOperationHandler {

    private final AQLInterpreterAPI interpreter;

    private final ChildModelOperationHandler childModelOperationHandler;

    private final RemoveElement removeElementOperation;

    private final Logger logger = LoggerFactory.getLogger(CreateInstanceOperationHandler.class);

    private final AQLEntry entry;

    public RemoveElementOperationHandler(AQLInterpreterAPI interpreter, ChildModelOperationHandler childModelOperationHandler, RemoveElement removeElementOperation, AQLEntry entry) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.childModelOperationHandler = Objects.requireNonNull(childModelOperationHandler);
        this.removeElementOperation = Objects.requireNonNull(removeElementOperation);
        this.entry = entry;
    }

    @Override
    public Status handle(Map<String, Object> variables) {
        // @formatter:off
        Optional<EObject> optionalObjectToRemove = Optional.ofNullable(variables.get(VariableManager.SELF))
             .filter(EObject.class::isInstance)
             .map(EObject.class::cast);
        // @formatter:on

        if (optionalObjectToRemove.isPresent()) {
            EObject objectToRemove = optionalObjectToRemove.get();

            EcoreIntrinsicExtender ecoreIntrinsicExtender = new EcoreIntrinsicExtender();
            String containingFeatureName = ecoreIntrinsicExtender.eContainingFeatureName(objectToRemove);
            EObject container = ecoreIntrinsicExtender.eContainer(objectToRemove);
            Object removedElement = ecoreIntrinsicExtender.eRemove(container, containingFeatureName, objectToRemove);
            if (removedElement == null) {
                this.logger.error(MessageFormat.format("The removal of the object {0} from the instance {1} on the feature {2} failed.", objectToRemove, container, containingFeatureName)); //$NON-NLS-1$
            }
        }

        Map<String, Object> childVariables = new HashMap<>(variables);
        List<ModelOperation> subModelOperations = this.removeElementOperation.getSubModelOperations();
        return this.childModelOperationHandler.handle(this.interpreter, childVariables, subModelOperations, this.entry);
    }

}
