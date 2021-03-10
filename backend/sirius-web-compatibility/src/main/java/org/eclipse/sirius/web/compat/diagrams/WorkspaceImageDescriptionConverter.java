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

import java.util.Objects;

import org.eclipse.sirius.diagram.description.style.WorkspaceImageDescription;
import org.eclipse.sirius.viewpoint.description.EAttributeCustomization;
import org.eclipse.sirius.web.diagrams.ImageNodeStyle;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.Result;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Used to convert workspace image description into image node style.
 *
 * @author sbegaudeau
 */
public class WorkspaceImageDescriptionConverter {

    private static final String WORKSPACE_PATH = "workspacePath"; //$NON-NLS-1$

    private static final int DEFAULT_SCALING_FACTOR = 1;

    private final AQLInterpreterAPI interpreterAPI;

    private final VariableManager variableManager;

    private final WorkspaceImageDescription workspaceImageDescription;

    private final EAttributeCustomizationProvider eAttributeCustomizationProvider;

    private final AQLEntry entry;

    public WorkspaceImageDescriptionConverter(AQLInterpreterAPI interpreterAPI, VariableManager variableManager, WorkspaceImageDescription workspaceImageDescription, AQLEntry entry) {
        this.interpreterAPI = Objects.requireNonNull(interpreterAPI);
        this.variableManager = Objects.requireNonNull(variableManager);
        this.workspaceImageDescription = Objects.requireNonNull(workspaceImageDescription);
        this.entry = Objects.requireNonNull(entry);
        this.eAttributeCustomizationProvider = new EAttributeCustomizationProvider(interpreterAPI, variableManager, entry);
    }

    public ImageNodeStyle convert() {
        // @formatter:off
        String workspacePath = this.eAttributeCustomizationProvider.getEAttributeCustomization(this.workspaceImageDescription, WORKSPACE_PATH)
                .map(EAttributeCustomization::getValue)
                .orElse(this.workspaceImageDescription.getWorkspacePath());
        // @formatter:on

        Result scalingFactorResult = this.interpreterAPI.evaluateExpression(this.variableManager.getVariables(), this.workspaceImageDescription.getSizeComputationExpression(), this.entry);
        int scalingFactor = scalingFactorResult.asInt().orElse(DEFAULT_SCALING_FACTOR);

        // @formatter:off
        return ImageNodeStyle.newImageNodeStyle()
                .imageURL(workspacePath.substring(workspacePath.indexOf('/', 1)))
                .scalingFactor(scalingFactor)
                .build();
        // @formatter:on
    }

}
