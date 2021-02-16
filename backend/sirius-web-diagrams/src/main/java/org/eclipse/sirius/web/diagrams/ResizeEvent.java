/*******************************************************************************
 * Copyright (c) 2021 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.web.diagrams;

import java.util.Set;
import java.util.UUID;

/**
 * Represent an immutable resize event.
 *
 * @author fbarbin
 */
public class ResizeEvent implements IDiagramElementEvent {

    private UUID nodeId;

    private Position positionDelta;

    private Size newSize;

    private Set<UUID> impactedChildren;

    public ResizeEvent(UUID nodeId, Position positionDelta, Size newSize, Set<UUID> impactedChildren) {
        this.nodeId = nodeId;
        this.positionDelta = positionDelta;
        this.newSize = newSize;
        this.impactedChildren = impactedChildren;
    }

    @Override
    public UUID getNodeId() {
        return this.nodeId;
    }

    public Position getPositionDelta() {
        return this.positionDelta;
    }

    public Size getNewSize() {
        return this.newSize;
    }

    public Set<UUID> getImpactedChildren() {
        return this.impactedChildren;
    }
}
