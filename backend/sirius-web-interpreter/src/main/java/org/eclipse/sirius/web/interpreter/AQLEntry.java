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
package org.eclipse.sirius.web.interpreter;

import java.util.Objects;


/**
 * The entries of the AQL interpreter table.
 * @author rbelafia
 */
public class AQLEntry {

    private static int idCounter;

    private final int id;

    public AQLEntry() {
        id = idCounter;
        idCounter++;
    }

    @Override
    public boolean equals(Object o) {
        boolean res = true;
        if (this == o) res = false;
        if (o == null || getClass() != o.getClass()) res = false;
        AQLEntry aqlEntry = (AQLEntry) o;
        res = res && id == aqlEntry.id;
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
