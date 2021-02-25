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
package org.eclipse.sirius.web.services.api.modelers;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.web.annotations.graphql.GraphQLField;
import org.eclipse.sirius.web.annotations.graphql.GraphQLID;
import org.eclipse.sirius.web.annotations.graphql.GraphQLNonNull;
import org.eclipse.sirius.web.annotations.graphql.GraphQLObjectType;
import org.eclipse.sirius.web.core.api.IPayload;

/**
 * Represent the result returned when renaming a modeler through the GraphQL API.
 *
 * @author pcdavid
 */
@GraphQLObjectType
public final class RenameModelerSuccessPayload implements IPayload {

    private final UUID id;

    private final Modeler modeler;

    public RenameModelerSuccessPayload(UUID id, Modeler modeler) {
        this.id = Objects.requireNonNull(id);
        this.modeler = Objects.requireNonNull(modeler);
    }

    @Override
    @GraphQLID
    @GraphQLField
    @GraphQLNonNull
    public UUID getId() {
        return this.id;
    }

    @GraphQLField
    @GraphQLNonNull
    public Modeler getModeler() {
        return this.modeler;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, modeler: '{'id: {2}, name: {3}'}''}'"; //$NON-NLS-1$
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.modeler.getId(), this.modeler.getName());
    }
}
