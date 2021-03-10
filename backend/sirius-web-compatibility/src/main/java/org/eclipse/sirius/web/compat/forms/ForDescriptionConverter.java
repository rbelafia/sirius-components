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
package org.eclipse.sirius.web.compat.forms;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.sirius.properties.DynamicMappingForDescription;
import org.eclipse.sirius.web.compat.api.IIdentifierProvider;
import org.eclipse.sirius.web.compat.api.IModelOperationHandlerSwitchProvider;
import org.eclipse.sirius.web.core.api.IObjectService;
import org.eclipse.sirius.web.forms.description.ForDescription;
import org.eclipse.sirius.web.forms.description.IfDescription;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * This class is used to convert a Sirius {@link DynamicMappingForDescription} to an Sirius Web {@link ForDescription}.
 *
 * @author fbarbin
 */
public class ForDescriptionConverter {

    private final AQLInterpreterAPI interpreterAPI;

    private final IObjectService objectService;

    private final IIdentifierProvider identifierProvider;

    private final IModelOperationHandlerSwitchProvider modelOperationHandlerSwitchProvider;

    private final AQLEntry entry;

    public ForDescriptionConverter(AQLInterpreterAPI interpreterAPI, IObjectService objectService, IIdentifierProvider identifierProvider,
                                   IModelOperationHandlerSwitchProvider modelOperationHandlerSwitchProvider, AQLEntry entry) {
        this.interpreterAPI = Objects.requireNonNull(interpreterAPI);
        this.objectService = Objects.requireNonNull(objectService);
        this.identifierProvider = Objects.requireNonNull(identifierProvider);
        this.modelOperationHandlerSwitchProvider = Objects.requireNonNull(modelOperationHandlerSwitchProvider);
        this.entry = entry;
    }

    public ForDescription convert(org.eclipse.sirius.properties.DynamicMappingForDescription siriusForDescription) {
        // @formatter:off
        Function<VariableManager, List<Object>> iterableProvider = (variableManager) -> {
            return this.interpreterAPI.evaluateExpression(variableManager.getVariables(), siriusForDescription.getIterableExpression(), entry).asObjects()
                    .orElse(Collections.emptyList());
        };
        IfDescriptionConverter converter = new IfDescriptionConverter(this.interpreterAPI, this.objectService, this.identifierProvider, this.modelOperationHandlerSwitchProvider, entry);
        List<IfDescription> ifDescriptions = siriusForDescription.getIfs().stream()
                .flatMap(ifDescription -> converter.convert(ifDescription).stream())
                .collect(Collectors.toUnmodifiableList());

       return ForDescription.newForDescription(this.identifierProvider.getIdentifier(siriusForDescription))
               .iterableProvider(iterableProvider)
               .iterator(siriusForDescription.getIterator())
               .ifDescriptions(ifDescriptions)
               .build();
        // @formatter:on

    }
}
