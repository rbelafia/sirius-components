/*******************************************************************************
 * Copyright (c) 2021 Obeo.
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
package org.eclipse.sirius.web.compat.diagrams;

import java.util.List;
import java.util.Objects;

import org.eclipse.sirius.diagram.description.ConditionalEdgeStyleDescription;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.style.EdgeStyleDescription;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.Result;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Compute the proper style description to use for a edge mapping.
 *
 * @author sbegaudeau
 */
public class EdgeStyleDescriptionProvider {
    private final AQLInterpreterAPI interpreter;

    private final EdgeMapping edgeMapping;

    private final AQLEntry entry;

    public EdgeStyleDescriptionProvider(AQLInterpreterAPI interpreter, EdgeMapping edgeMapping, AQLEntry entry) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.edgeMapping = Objects.requireNonNull(edgeMapping);
        this.entry = entry;
    }

    public EdgeStyleDescription getEdgeStyleDescription(VariableManager variableManager) {
        EdgeStyleDescription styleDescription = this.edgeMapping.getStyle();
        List<ConditionalEdgeStyleDescription> conditionnalStyles = this.edgeMapping.getConditionnalStyles();
        for (ConditionalEdgeStyleDescription conditionalStyle : conditionnalStyles) {
            String predicateExpression = conditionalStyle.getPredicateExpression();
            Result result = this.interpreter.evaluateExpression(variableManager.getVariables(), predicateExpression, entry);
            boolean shouldUseStyle = result.asBoolean().orElse(Boolean.FALSE).booleanValue();
            if (shouldUseStyle) {
                styleDescription = conditionalStyle.getStyle();
                break;
            }
        }
        return styleDescription;
    }
}
