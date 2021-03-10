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
package org.eclipse.sirius.web.compat.diagrams;

import java.util.Objects;
import java.util.function.Function;

import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.style.EdgeStyleDescription;
import org.eclipse.sirius.web.diagrams.ArrowStyle;
import org.eclipse.sirius.web.diagrams.EdgeStyle;
import org.eclipse.sirius.web.diagrams.LineStyle;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Used to compute the style using the definition of an edge mapping.
 *
 * @author sbegaudeau
 */
public class EdgeMappingStyleProvider implements Function<VariableManager, EdgeStyle> {

    private AQLInterpreterAPI interpreter;

    private EdgeMapping edgeMapping;

    private final AQLEntry entry;

    public EdgeMappingStyleProvider(AQLInterpreterAPI interpreter, EdgeMapping edgeMapping, AQLEntry entry) {
        this.interpreter = Objects.requireNonNull(interpreter);
        this.edgeMapping = Objects.requireNonNull(edgeMapping);
        this.entry = entry;
    }

    @Override
    public EdgeStyle apply(VariableManager variableManager) {
        EdgeStyleDescription edgeStyleDescription = new EdgeStyleDescriptionProvider(this.interpreter, this.edgeMapping, entry).getEdgeStyleDescription(variableManager);
        return this.getEdgeStyle(variableManager, edgeStyleDescription);
    }

    private EdgeStyle getEdgeStyle(VariableManager variableManager, EdgeStyleDescription style) {
        ColorDescriptionConverter colorDescriptionConverter = new ColorDescriptionConverter(this.interpreter, variableManager, this.entry);
        LineStyleConverter lineStyleConverter = new LineStyleConverter();
        ArrowStyleConverter arrowStyleConverter = new ArrowStyleConverter();

        int size = 1;
        LineStyle lineStyle = lineStyleConverter.getStyle(style.getLineStyle());
        ArrowStyle sourceArrow = arrowStyleConverter.getStyle(style.getSourceArrow());
        ArrowStyle targetArrow = arrowStyleConverter.getStyle(style.getTargetArrow());
        String color = colorDescriptionConverter.convert(style.getStrokeColor());

        // @formatter:off
        return EdgeStyle.newEdgeStyle()
                .size(size)
                .lineStyle(lineStyle)
                .sourceArrow(sourceArrow)
                .targetArrow(targetArrow)
                .color(color)
                .build();
        // @formatter:on
    }

}
