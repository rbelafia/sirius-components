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
package org.eclipse.sirius.web.compat.utils;

import java.util.Objects;
import java.util.function.Function;

import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.Result;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Utility class used to provide a boolean value from expression.
 *
 * @author fbarbin
 */
public class BooleanValueProvider implements Function<VariableManager, Boolean> {
    private final AQLInterpreterAPI interpreter;

    private final String expression;

    private final AQLEntry entry;

    public BooleanValueProvider(AQLInterpreterAPI interpreter, String expression, AQLEntry entry) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.expression = Objects.requireNonNull(expression);
        this.entry = entry;
    }

    @Override
    public Boolean apply(VariableManager variableManager) {
        if (!this.expression.isBlank()) {
            Result result = this.interpreter.evaluateExpression(variableManager.getVariables(), this.expression, entry);
            return result.asBoolean().orElse(Boolean.FALSE);
        }
        return Boolean.FALSE;
    }
}
