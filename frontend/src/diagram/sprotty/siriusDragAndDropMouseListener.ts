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
import { Action, MoveMouseListener, Point, SModelElement, SNode } from 'sprotty';

export abstract class SiriusDragAndDropMouseListener extends MoveMouseListener {
  startResizePosition: Point | undefined;
  selector: String;

  public mouseDown(target: SModelElement, event: MouseEvent): Action[] {
    const actions: Action[] = super.mouseDown(target, event);
    this.initializeAction(target, event);
    return actions;
  }

  protected initializeAction(target: SModelElement, event: MouseEvent) {
    console.log('SiriusDragAndDropMouseListener');
    if (this.startDragPosition) {
      //if the click is perfomed on a resize selector, we switch from the move mode to resize mode.
      const selector = this.isResizeSelector(event);
      if (selector) {
        if (this.isSNode(target)) {
          this.selector = selector;
          this.startResizePosition = this.startDragPosition;
          //Deactivate the move
          this.startDragPosition = undefined;
        }
      }
    }
  }
  public mouseUp(target: SModelElement, event: MouseEvent): Action[] {
    this.startResizePosition = undefined;
    this.selector = undefined;
    return super.mouseUp(target, event);
  }

  private isResizeSelector(event: MouseEvent): String | undefined {
    const domTarget = event.target as Element;
    if (domTarget?.id?.startsWith('selectorGrip_resize')) {
      return domTarget.id;
    }
    return undefined;
  }

  protected isSNode(element: SModelElement): element is SNode {
    return element instanceof SNode;
  }
}
