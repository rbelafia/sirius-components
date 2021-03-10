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
package org.eclipse.sirius.web.compat.diagrams;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.sirius.diagram.description.DescriptionFactory;
import org.eclipse.sirius.web.diagrams.description.DiagramDescription;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreter;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.representations.VariableManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of the computation of the label of a diagram.
 *
 * @author sbegaudeau
 */
public class DiagramLabelProviderTestCases {

    private AQLInterpreter interpreter;

    private AQLInterpreterAPI interpreterAPI;

    private AQLEntry entry;

    @Before
    public void setUp() throws Exception {
        interpreter = new AQLInterpreter();
        interpreterAPI = new AQLInterpreterAPI(interpreter);
        entry = interpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
    }

    @Test
    public void testUseDiagramNameFromUser() {
        org.eclipse.sirius.diagram.description.DiagramDescription diagramDescription = DescriptionFactory.eINSTANCE.createDiagramDescription();
        DiagramLabelProvider labelProvider = new DiagramLabelProvider(interpreterAPI, diagramDescription, entry);

        VariableManager variableManager = new VariableManager();
        String userProvidedDiagramName = "diagram name from the user"; //$NON-NLS-1$
        variableManager.put(DiagramDescription.LABEL, userProvidedDiagramName);
        String result = labelProvider.apply(variableManager);

        assertThat(result).isEqualTo(userProvidedDiagramName);
    }

    @Test
    public void testFallbackToTitleExpression() {
        org.eclipse.sirius.diagram.description.DiagramDescription diagramDescription = DescriptionFactory.eINSTANCE.createDiagramDescription();
        diagramDescription.setTitleExpression("aql:'NewLabel'"); //$NON-NLS-1$

        DiagramLabelProvider labelProvider = new DiagramLabelProvider(interpreterAPI, diagramDescription, entry);

        VariableManager variableManager = new VariableManager();
        String result = labelProvider.apply(variableManager);

        assertThat(result).isEqualTo("NewLabel"); //$NON-NLS-1$
    }

    @Test
    public void testUseDefaultTitleExpressionWithLabel() {
        org.eclipse.sirius.diagram.description.DiagramDescription diagramDescription = DescriptionFactory.eINSTANCE.createDiagramDescription();
        diagramDescription.setLabel("DefaultLabel"); //$NON-NLS-1$
        DiagramLabelProvider labelProvider = new DiagramLabelProvider(interpreterAPI, diagramDescription, entry);

        VariableManager variableManager = new VariableManager();
        String result = labelProvider.apply(variableManager);

        assertThat(result).isEqualTo("new DefaultLabel"); //$NON-NLS-1$
    }

    @Test
    public void testUseDefaultTitleExpressionWithName() {
        org.eclipse.sirius.diagram.description.DiagramDescription diagramDescription = DescriptionFactory.eINSTANCE.createDiagramDescription();
        diagramDescription.setName("DefaultName"); //$NON-NLS-1$
        DiagramLabelProvider labelProvider = new DiagramLabelProvider(interpreterAPI, diagramDescription, entry);

        VariableManager variableManager = new VariableManager();
        String result = labelProvider.apply(variableManager);

        assertThat(result).isEqualTo("new DefaultName"); //$NON-NLS-1$
    }
}
