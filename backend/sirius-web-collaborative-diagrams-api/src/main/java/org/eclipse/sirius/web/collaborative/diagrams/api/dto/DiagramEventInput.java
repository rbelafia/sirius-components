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
package org.eclipse.sirius.web.collaborative.diagrams.api.dto;

import java.text.MessageFormat;
import java.util.UUID;

import org.eclipse.sirius.web.annotations.graphql.GraphQLField;
import org.eclipse.sirius.web.annotations.graphql.GraphQLID;
import org.eclipse.sirius.web.annotations.graphql.GraphQLInputObjectType;
import org.eclipse.sirius.web.annotations.graphql.GraphQLNonNull;
import org.eclipse.sirius.web.core.api.IInput;

/**
 * The input of the diagram event subscription.
 *
 * @author sbegaudeau
 */
@GraphQLInputObjectType
public final class DiagramEventInput implements IInput {
    private UUID projectId;

    private UUID diagramId;

    @GraphQLID
    @GraphQLField
    @GraphQLNonNull
    public UUID getProjectId() {
        return this.projectId;
    }

    @GraphQLID
    @GraphQLField
    @GraphQLNonNull
    public UUID getDiagramId() {
        return this.diagramId;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'projectId: {1}, diagramId: {2}'}'"; //$NON-NLS-1$
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.projectId, this.diagramId);
    }
}
