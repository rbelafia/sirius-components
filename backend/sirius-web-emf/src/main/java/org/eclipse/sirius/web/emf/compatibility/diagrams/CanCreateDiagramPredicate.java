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
package org.eclipse.sirius.web.emf.compatibility.diagrams;

import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.web.emf.compatibility.DomainClassPredicate;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.Result;
import org.eclipse.sirius.web.representations.IRepresentationDescription;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Predicate to test the ability to create a diagram according to the given {@Link VariableManager}.
 *
 * @author hmarchadour
 */
public class CanCreateDiagramPredicate implements Predicate<VariableManager> {

    private final DiagramDescription diagramDescription;

    private final AQLInterpreterAPI interpreter;

    private final AQLEntry entry;

    public CanCreateDiagramPredicate(DiagramDescription diagramDescription, AQLInterpreterAPI interpreter, AQLEntry entry) {
        this.diagramDescription = diagramDescription;
        this.interpreter = interpreter;
        this.entry = entry;
    }

    @Override
    public boolean test(VariableManager variableManager) {
        boolean result = false;

        String domainClass = this.diagramDescription.getDomainClass();

        // @formatter:off
        Optional<EObject> optionalEObject = Optional.ofNullable(variableManager.getVariables().get(IRepresentationDescription.CLASS))
                .filter(EClass.class::isInstance)
                .map(EClass.class::cast)
                .map(EcoreUtil::create)
                .filter(new DomainClassPredicate(domainClass));
        // @formatter:on

        if (optionalEObject.isPresent()) {
            String preconditionExpression = this.diagramDescription.getPreconditionExpression();
            if (preconditionExpression != null && !preconditionExpression.isBlank()) {
                Result preconditionResult = this.interpreter.evaluateExpression(variableManager.getVariables(), preconditionExpression, entry);
                result = preconditionResult.asBoolean().orElse(false);
            } else {
                result = true;
            }
        }

        return result;
    }

}
