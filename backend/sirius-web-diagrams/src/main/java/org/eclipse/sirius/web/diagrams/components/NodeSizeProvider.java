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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.sirius.web.components.Element;
import org.eclipse.sirius.web.diagrams.INodeStyle;
import org.eclipse.sirius.web.diagrams.ImageNodeStyle;
import org.eclipse.sirius.web.diagrams.ImageNodeStyleSizeProvider;
import org.eclipse.sirius.web.diagrams.ImageSizeProvider;
import org.eclipse.sirius.web.diagrams.Node;
import org.eclipse.sirius.web.diagrams.ResizeEvent;
import org.eclipse.sirius.web.diagrams.Size;

/**
 * Provides the Size to apply to a new node.
 *
 * @author fbarbin
 */
public class NodeSizeProvider {

    private ResizeEvent resizeEvent;

    /**
     * Default constructor.
     *
     * @param resizeEvent
     *            the resize event. Can be null.
     */
    public NodeSizeProvider(ResizeEvent resizeEvent) {
        this.resizeEvent = resizeEvent;
    }

    public Size getSize(UUID elementID, Optional<Node> optionalPreviousNode, INodeStyle style, List<Element> childElements) {
        Size size;
        if (this.resizeEvent != null && this.resizeEvent.getNodeId().equals(elementID)) {
            size = this.resizeEvent.getNewSize();

        } else if (optionalPreviousNode.isPresent()) {
            return optionalPreviousNode.get().getSize();
        } else if (style instanceof ImageNodeStyle) {
            size = new ImageNodeStyleSizeProvider(new ImageSizeProvider()).getSize((ImageNodeStyle) style);
        } else {
            // @formatter:off
            size = Size.newSize()
                    .width(150)
                    .height(70)
                    .build();
            // @formatter:on
        }
        return size;
    }
}
