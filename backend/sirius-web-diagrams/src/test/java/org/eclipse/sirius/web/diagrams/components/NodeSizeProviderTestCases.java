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

import org.eclipse.sirius.web.diagrams.LineStyle;
import org.eclipse.sirius.web.diagrams.RectangularNodeStyle;
import org.eclipse.sirius.web.diagrams.Size;
import org.junit.Test;

/**
 * Test cases for {@link NodeSizeProvider}.
 *
 * @author fbarbin
 */
public class NodeSizeProviderTestCases {

    private static final int HEIGHT_70 = 70;

    private static final int WIDTH_150 = 150;

    private static final int WIDTH_50 = 50;

    private static final int HEIGHT_20 = 20;

    @Test
    public void testNodeSize() {
        NodeSizeProvider nodeSizeProvider = new NodeSizeProvider();
        Size size = nodeSizeProvider.getSize(null, List.of());
        assertThat(size).extracting(Size::getHeight).isEqualTo(Double.valueOf(HEIGHT_70));
        assertThat(size).extracting(Size::getWidth).isEqualTo(Double.valueOf(WIDTH_150));
    }

    @Test
    public void testRectangularNodeStyleSize() {
        NodeSizeProvider nodeSizeProvider = new NodeSizeProvider();
        Size size = nodeSizeProvider.getSize(this.getRectangularNodeStyle(WIDTH_50, HEIGHT_20), List.of());
        assertThat(size).extracting(Size::getHeight).isEqualTo(Double.valueOf(HEIGHT_20));
        assertThat(size).extracting(Size::getWidth).isEqualTo(Double.valueOf(WIDTH_50));
    }

    @Test
    public void testRectangularNodeStyleNoWidthSize() {
        NodeSizeProvider nodeSizeProvider = new NodeSizeProvider();
        Size size = nodeSizeProvider.getSize(this.getRectangularNodeStyle(0, HEIGHT_20), List.of());
        assertThat(size).extracting(Size::getHeight).isEqualTo(Double.valueOf(HEIGHT_20));
        assertThat(size).extracting(Size::getWidth).isEqualTo(Double.valueOf(WIDTH_150));
    }

    @Test
    public void testRectangularNodeStyleNoHeightSize() {
        NodeSizeProvider nodeSizeProvider = new NodeSizeProvider();
        Size size = nodeSizeProvider.getSize(this.getRectangularNodeStyle(WIDTH_50, 0), List.of());
        assertThat(size).extracting(Size::getHeight).isEqualTo(Double.valueOf(HEIGHT_70));
        assertThat(size).extracting(Size::getWidth).isEqualTo(Double.valueOf(WIDTH_50));
    }

    private RectangularNodeStyle getRectangularNodeStyle(int width, int height) {
        // @formatter:off
        return RectangularNodeStyle.newRectangularNodeStyle()
                .borderColor("#000000") //$NON-NLS-1$
                .borderSize(1)
                .borderStyle(LineStyle.Solid)
                .height(height)
                .width(width)
                .color("#FFFFFF") //$NON-NLS-1$
                .build();
        // @formatter:on
    }
}
