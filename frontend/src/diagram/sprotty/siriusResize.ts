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
import { decorate, inject } from 'inversify';
import {
  Action,
  CommandExecutionContext,
  CommandReturn,
  Dimension,
  findParentByFeature,
  isViewport,
  MergeableCommand,
  Point,
  SModelElement,
  SNode,
  TYPES,
} from 'sprotty';
import { SiriusDragAndDropMouseListener } from './siriusDragAndDropMouseListener';

export class ResizeAction implements Action {
  kind = SiriusResizeCommand.KIND;
  constructor(public readonly resize: ElementResize, public readonly finished: boolean = false) {}
}
export interface ElementResize {
  elementId: string;
  newPosition: Point;
  newSize: Dimension;
}
export class SiriusResizeCommand extends MergeableCommand {
  static readonly KIND = 'resize';

  constructor(protected readonly action: ResizeAction) {
    super();
  }

  execute(context: CommandExecutionContext): CommandReturn {
    const index = context.root.index;
    const elementResize: ElementResize = this.action.resize;
    const element: SModelElement = index.getById(elementResize.elementId);
    if (this.isNode(element)) {
      const delta: Point = {
        x: elementResize.newPosition.x - element.position.x,
        y: elementResize.newPosition.y - element.position.y,
      };
      this.updateChildrenRelativePosition(element, delta);
      element.size = elementResize.newSize;
      element.position = elementResize.newPosition;
    }
    return context.root;
  }

  private updateChildrenRelativePosition(element: SNode, delta: Point) {
    element.children.forEach((child) => {
      if (this.isNode(child)) {
        child.position = {
          x: child.position.x - delta.x,
          y: child.position.y - delta.y,
        };
      }
    });
  }

  private isNode(element: SModelElement): element is SNode {
    return element instanceof SNode;
  }
  undo(context: CommandExecutionContext): CommandReturn {
    return context.root;
  }

  redo(context: CommandExecutionContext): CommandReturn {
    return context.root;
  }
}
export class SiriusResizeMouseListener extends SiriusDragAndDropMouseListener {
  intialTarget: SNode;
  startResizePosition: Point | undefined;
  startingPosition: Point;
  startingSize: Dimension;
  selector: String;
  childrenLimits: [Point | undefined, Point | undefined];

  initializeAction(target: SModelElement, event: MouseEvent) {
    console.log('SiriusResizeMouseListener');
    super.initializeAction(target, event);
    //We are in the resize mode.
    if (this.startResizePosition && this.isSNode(target)) {
      //We keep the initial mouse event target since it might change during the resize.
      this.intialTarget = target;
      //We keep the initial target size since it will change during the resize.
      this.startingSize = target.size;
      //We keep the initial position since it might change during the resize.
      this.startingPosition = target.position;
      //We compute the children limits at the begining of the resize since relative positions will change during the resize.
      this.childrenLimits = this.getChildrenLimits(this.intialTarget);
    }
  }

  public mouseMove(target: SModelElement, event: MouseEvent): Action[] {
    //If we are in the resize mode we return a resize action.
    if (this.startResizePosition) {
      let result: Action[] = [];
      const action = this.getResizeAction(event, false);
      if (action) {
        result.push(action);
      }
      return result;
    } else {
      return super.mouseMove(target, event);
    }
  }
  public mouseUp(target: SModelElement, event: MouseEvent): Action[] {
    if (this.startResizePosition) {
      const result: Action[] = [];
      const action = this.getResizeAction(event, true);
      if (action) {
        result.push(action);
      }
      this.startResizePosition = undefined;
      this.intialTarget = undefined;
      this.startingPosition = undefined;
      this.startingSize = undefined;
      super.mouseUp(target, event);
      return result;
    } else {
      return super.mouseUp(target, event);
    }
  }
  protected getResizeAction(event: MouseEvent, isFinished: boolean): ResizeAction | undefined {
    if (!this.startResizePosition) return undefined;
    const viewport = findParentByFeature(this.intialTarget, isViewport);
    const zoom = viewport ? viewport.zoom : 1;
    const delta = {
      x: (event.pageX - this.startResizePosition.x) / zoom,
      y: (event.pageY - this.startResizePosition.y) / zoom,
    };
    const resizeElement = this.computeElementResize(delta);
    if (resizeElement) {
      return new ResizeAction(resizeElement, isFinished);
    }
    return undefined;
  }
  private computeElementResize(delta: Point): ElementResize {
    const elementId = this.intialTarget.id;
    let previousPosition = {
      x: this.intialTarget.position.x,
      y: this.intialTarget.position.y,
    };
    let previousSize = {
      width: this.intialTarget.size.width,
      height: this.intialTarget.size.height,
    };

    if (this.selector === 'selectorGrip_resize_s') {
      [previousSize, previousPosition] = this.handleSouth(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_e') {
      [previousSize, previousPosition] = this.handleEast(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_w') {
      [previousSize, previousPosition] = this.handleWest(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_n') {
      [previousSize, previousPosition] = this.handleNorth(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_nw') {
      [previousSize, previousPosition] = this.handleNorth(previousSize, previousPosition, delta);
      [previousSize, previousPosition] = this.handleWest(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_ne') {
      [previousSize, previousPosition] = this.handleNorth(previousSize, previousPosition, delta);
      [previousSize, previousPosition] = this.handleEast(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_se') {
      [previousSize, previousPosition] = this.handleSouth(previousSize, previousPosition, delta);
      [previousSize, previousPosition] = this.handleEast(previousSize, previousPosition, delta);
    } else if (this.selector === 'selectorGrip_resize_sw') {
      [previousSize, previousPosition] = this.handleSouth(previousSize, previousPosition, delta);
      [previousSize, previousPosition] = this.handleWest(previousSize, previousPosition, delta);
    }
    return {
      elementId,
      newPosition: previousPosition,
      newSize: previousSize,
    };
  }

  private getChildrenLimits(element: SNode): [Point | undefined, Point | undefined] {
    let minTopLeft = undefined;
    let maxBottomRight = undefined;
    element.children.forEach((child) => {
      if (this.isSNode(child)) {
        if (!minTopLeft) {
          minTopLeft = {
            x: child.position.x,
            y: child.position.y,
          };
        } else {
          minTopLeft.x = Math.min(minTopLeft.x, child.position.x);
          minTopLeft.y = Math.min(minTopLeft.y, child.position.y);
        }
        const childBottomRight = {
          x: child.position.x + child.size.width,
          y: child.position.y + child.size.height,
        };
        if (!maxBottomRight) {
          maxBottomRight = childBottomRight;
        } else {
          maxBottomRight.x = Math.max(childBottomRight.x, maxBottomRight.x);
          maxBottomRight.y = Math.max(childBottomRight.y, maxBottomRight.y);
        }
      }
    });
    console.log(minTopLeft?.x + ' ' + minTopLeft?.y);
    return [minTopLeft, maxBottomRight];
  }

  private handleNorth(previousSize: Dimension, previousPosition: Point, delta: Point): [Dimension, Point] {
    const [topLeftLimit] = this.childrenLimits;
    let validDelta = delta;
    //if the height is reduced and a child limit has been computed, we compute the valid delta
    if (topLeftLimit && delta.y > 0) {
      validDelta = {
        x: delta.x,
        y: Math.min(delta.y, topLeftLimit.y),
      };
    }
    const newSize = {
      width: previousSize.width,
      height: this.startingSize.height - validDelta.y,
    };
    const newPosition = {
      x: previousPosition.x,
      y: this.startingPosition.y + validDelta.y,
    };
    return [newSize, newPosition];
  }
  private handleSouth(previousSize: Dimension, previousPosition: Point, delta: Point): [Dimension, Point] {
    const buttomRightLimit = this.childrenLimits[1];
    let validDelta = delta;
    //if the height is reduced and a child limit has been computed, we compute the valid delta
    if (buttomRightLimit && delta.y < 0) {
      const bottomMaxDelta = buttomRightLimit.y - this.startingSize.height;
      validDelta = {
        x: delta.x,
        y: Math.max(delta.y, bottomMaxDelta),
      };
    }
    const newSize = {
      width: previousSize.width,
      height: this.startingSize.height + validDelta.y,
    };
    return [newSize, previousPosition];
  }
  private handleEast(previousSize: Dimension, previousPosition: Point, delta: Point): [Dimension, Point] {
    const buttomRightLimit = this.childrenLimits[1];
    let validDelta = delta;
    //if the width is reduced and a child limit has been computed, we compute the valid delta
    if (buttomRightLimit && delta.x < 0) {
      const eastMaxDelta = buttomRightLimit.x - this.startingSize.width;
      validDelta = {
        x: Math.max(delta.x, eastMaxDelta),
        y: delta.y,
      };
    }
    const newSize = {
      width: this.startingSize.width + validDelta.x,
      height: previousSize.height,
    };
    return [newSize, previousPosition];
  }
  private handleWest(previousSize: Dimension, previousPosition: Point, delta: Point): [Dimension, Point] {
    const [topLeftLimit] = this.childrenLimits;
    let validDelta = delta;
    //if the width is reduced and a child limit has been computed, we compute the valid delta
    if (topLeftLimit && delta.x > 0) {
      validDelta = {
        x: Math.min(delta.x, topLeftLimit.x),
        y: delta.y,
      };
    }
    const newSize = {
      width: this.startingSize.width - validDelta.x,
      height: previousSize.height,
    };
    const newPosition = {
      x: this.startingPosition.x + validDelta.x,
      y: previousPosition.y,
    };
    return [newSize, newPosition];
  }
}

decorate(inject(TYPES.Action), SiriusResizeCommand, 0);
