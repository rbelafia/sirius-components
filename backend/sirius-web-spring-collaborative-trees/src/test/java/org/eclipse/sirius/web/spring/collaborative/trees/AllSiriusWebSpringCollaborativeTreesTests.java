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
package org.eclipse.sirius.web.spring.collaborative.trees;

import org.eclipse.sirius.web.spring.collaborative.trees.architecture.CodingRulesTestCases;
import org.eclipse.sirius.web.spring.collaborative.trees.architecture.SpringCodingRulesTestCases;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite of the project.
 *
 * @author sbegaudeau
 */
@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
    CodingRulesTestCases.class,
    SpringCodingRulesTestCases.class
})
//@formatter:on
public final class AllSiriusWebSpringCollaborativeTreesTests {
    private AllSiriusWebSpringCollaborativeTreesTests() {
        // Prevent instantiation
    }
}
