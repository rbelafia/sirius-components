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
package org.eclipse.sirius.web.diagrams.components;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.eclipse.sirius.web.components.Element;
import org.eclipse.sirius.web.diagrams.INodeStyle;
import org.eclipse.sirius.web.diagrams.ImageNodeStyle;
import org.eclipse.sirius.web.diagrams.Position;
import org.eclipse.sirius.web.diagrams.Size;
import org.eclipse.sirius.web.diagrams.elements.NodeElementProps;
import org.junit.Test;

/**
 * Test cases for {@link EdgeRoutingPointsProvider}.
 *
 * @author fbarbin
 */
public class EdgeRoutingPointsProviderTestCases {

    @Test
    public void testEdgeRoutingPoints() {
        EdgeRoutingPointsProvider edgeRoutingPointsProvider = new EdgeRoutingPointsProvider();

        Position firstPosition = Position.at(0, 0);
        Position secondPosition = Position.at(200, 200);

        Element sourceElement = this.createNodeElement(firstPosition);
        Element targetElement = this.createNodeElement(secondPosition);

        List<Position> routingPoints = edgeRoutingPointsProvider.getRoutingPoints(sourceElement, targetElement);
        assertThat(routingPoints).hasSize(2);

        Position firstRoutingPoint = routingPoints.get(0);
        assertThat(firstRoutingPoint).extracting(Position::getX).isEqualTo(50.0);
        assertThat(firstRoutingPoint).extracting(Position::getY).isEqualTo(25.0);

        Position secondRoutingPoint = routingPoints.get(1);
        assertThat(secondRoutingPoint).extracting(Position::getX).isEqualTo(250.0);
        assertThat(secondRoutingPoint).extracting(Position::getY).isEqualTo(225.0);
    }

    private Element createNodeElement(Position position) {
        Size size = Size.of(100, 50);

        // @formatter:off
        INodeStyle style = ImageNodeStyle.newImageNodeStyle()
                .imageURL("") //$NON-NLS-1$
                .scalingFactor(42)
                .build();

        NodeElementProps nodeElementProps = NodeElementProps.newNodeElementProps(UUID.randomUUID())
                .type("type") //$NON-NLS-1$
                .targetObjectId("targetObjectId") //$NON-NLS-1$
                .targetObjectKind("targetObjectKind") //$NON-NLS-1$
                .targetObjectLabel("targetObjectLabel") //$NON-NLS-1$
                .descriptionId(UUID.randomUUID())
                .style(style)
                .position(position)
                .size(size)
                .absolutePosition(position)
                .children(List.of())
                .build();
        // @formatter:on
        return new Element(NodeElementProps.TYPE, nodeElementProps);
    }

}
