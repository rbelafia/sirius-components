/*******************************************************************************
 * Copyright (c) 2019, 2021 Obeo and others.
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
package org.eclipse.sirius.web.diagrams.components;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.eclipse.sirius.web.components.IProps;
import org.eclipse.sirius.web.diagrams.Diagram;
import org.eclipse.sirius.web.diagrams.Position;
import org.eclipse.sirius.web.diagrams.ViewCreationRequest;
import org.eclipse.sirius.web.diagrams.description.DiagramDescription;
import org.eclipse.sirius.web.diagrams.utils.Pair;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * The properties of a diagram component.
 *
 * @author sbegaudeau
 */
public final class DiagramComponentProps implements IProps {
    private VariableManager variableManager;

    private DiagramDescription diagramDescription;

    private Optional<Diagram> previousDiagram;

    private List<ViewCreationRequest> viewCreationRequests;

    private Pair<UUID, Position> movedElementIdToNewPositionPair;

    private Set<UUID> allMovedElementIds;

    private Position startingPosition;

    private DiagramComponentProps() {
        // Prevent instantiation
    }

    public VariableManager getVariableManager() {
        return this.variableManager;
    }

    public DiagramDescription getDiagramDescription() {
        return this.diagramDescription;
    }

    public Optional<Diagram> getPreviousDiagram() {
        return this.previousDiagram;
    }

    public List<ViewCreationRequest> getViewCreationRequests() {
        return this.viewCreationRequests;
    }

    public Pair<UUID, Position> getMovedElementIdToNewPositionPair() {
        return this.movedElementIdToNewPositionPair;
    }

    public Set<UUID> getAllMovedElementIds() {
        return this.allMovedElementIds;
    }

    public Position getStartingPosition() {
        return this.startingPosition;
    }

    public static Builder newDiagramComponentProps() {
        return new Builder();
    }

    /**
     * The Builder to create a new {@link DiagramComponentProps}.
     *
     * @author fbarbin
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public static final class Builder {
        private VariableManager variableManager;

        private DiagramDescription diagramDescription;

        private Optional<Diagram> previousDiagram;

        private List<ViewCreationRequest> viewCreationRequests;

        private Pair<UUID, Position> movedElementIdToNewPositionPair;

        private Set<UUID> allMovedElementIds;

        private Position startingPosition;

        public Builder variableManager(VariableManager variableManager) {
            this.variableManager = Objects.requireNonNull(variableManager);
            return this;
        }

        public Builder diagramDescription(DiagramDescription diagramDescription) {
            this.diagramDescription = Objects.requireNonNull(diagramDescription);
            return this;
        }

        public Builder previousDiagram(Optional<Diagram> previousDiagram) {
            this.previousDiagram = Objects.requireNonNull(previousDiagram);
            return this;
        }

        public Builder viewCreationRequests(List<ViewCreationRequest> viewCreationRequests) {
            this.viewCreationRequests = Objects.requireNonNull(viewCreationRequests);
            return this;
        }

        public Builder movedElementIdToNewPositionPair(Pair<UUID, Position> movedElementIdToNewPositionPair) {
            this.movedElementIdToNewPositionPair = Objects.requireNonNull(movedElementIdToNewPositionPair);
            return this;
        }

        public Builder allMovedElementIds(Set<UUID> allMovedElementIds) {
            this.allMovedElementIds = Objects.requireNonNull(allMovedElementIds);
            return this;
        }

        public Builder startingPosition(Position startingPosition) {
            this.startingPosition = Objects.requireNonNull(startingPosition);
            return this;
        }

        public DiagramComponentProps build() {
            DiagramComponentProps diagramComponentProps = new DiagramComponentProps();
            diagramComponentProps.variableManager = Objects.requireNonNull(this.variableManager);
            diagramComponentProps.diagramDescription = Objects.requireNonNull(this.diagramDescription);
            diagramComponentProps.previousDiagram = Objects.requireNonNull(this.previousDiagram);
            diagramComponentProps.viewCreationRequests = List.copyOf(Objects.requireNonNull(this.viewCreationRequests));
            diagramComponentProps.movedElementIdToNewPositionPair = this.movedElementIdToNewPositionPair;
            diagramComponentProps.allMovedElementIds = Set.copyOf(Objects.requireNonNull(this.allMovedElementIds));
            diagramComponentProps.startingPosition = this.startingPosition;
            return diagramComponentProps;
        }
    }

}
