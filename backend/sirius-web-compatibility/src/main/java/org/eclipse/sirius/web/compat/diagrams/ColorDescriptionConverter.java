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
package org.eclipse.sirius.web.compat.diagrams;

import java.util.Map;
import java.util.Objects;

import org.eclipse.sirius.viewpoint.description.ColorDescription;
import org.eclipse.sirius.viewpoint.description.ComputedColor;
import org.eclipse.sirius.viewpoint.description.FixedColor;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Provides a unified color format from a Sirius {@link ColorDescription}.
 *
 * @author fbarbin
 */
public class ColorDescriptionConverter {

    private static final String DEFAULT_COLOR = "#000000"; //$NON-NLS-1$

    private final AQLInterpreterAPI interpreterAPI;
    private final AQLEntry entry;

    private final VariableManager variableManager;

    public ColorDescriptionConverter(AQLInterpreterAPI interpreterAPI, VariableManager variableManager, AQLEntry entry) {
        this.interpreterAPI = Objects.requireNonNull(interpreterAPI);
        this.variableManager = Objects.requireNonNull(variableManager);
        this.entry = Objects.requireNonNull(entry);
    }

    public String convert(ColorDescription colorDescription) {
        String value = DEFAULT_COLOR;
        if (colorDescription instanceof FixedColor) {
            FixedColor fixedColor = (FixedColor) colorDescription;
            value = this.toHex(fixedColor.getRed(), fixedColor.getGreen(), fixedColor.getBlue());
        } else if (colorDescription instanceof ComputedColor) {
            ComputedColor computedColor = (ComputedColor) colorDescription;

            Map<String, Object> variables = this.variableManager.getVariables();
            int red = this.interpreterAPI.evaluateExpression(variables, computedColor.getRed(), entry).asInt().orElse(0);
            int green = this.interpreterAPI.evaluateExpression(variables, computedColor.getGreen(), entry).asInt().orElse(0);
            int blue = this.interpreterAPI.evaluateExpression(variables, computedColor.getBlue(), entry).asInt().orElse(0);

            value = this.toHex(red, green, blue);
        }
        return value;
    }

    private String toHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b); //$NON-NLS-1$
    }
}
