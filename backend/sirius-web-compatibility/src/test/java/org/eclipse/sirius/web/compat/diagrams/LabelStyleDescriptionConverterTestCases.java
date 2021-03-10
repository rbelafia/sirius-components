/*******************************************************************************
 * Copyright (c) 2021 Obeo.
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
import java.util.function.Function;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.sirius.diagram.description.style.ShapeContainerStyleDescription;
import org.eclipse.sirius.diagram.description.style.StyleFactory;
import org.eclipse.sirius.web.diagrams.description.LabelStyleDescription;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreter;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.representations.VariableManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests of the transformation of Sirius label style.
 *
 * @author arichard
 */
public class LabelStyleDescriptionConverterTestCases {

    private AQLInterpreter interpreter;

    private AQLInterpreterAPI interpreterAPI;

    private AQLEntry entry;

    @Before
    public void setUp() {
        interpreter = new AQLInterpreter();
        interpreterAPI = new AQLInterpreterAPI(interpreter);
        entry = interpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
    }

    @Test
    public void testConvertIconPath() {
        String pluginName = "my.sirius.plugin"; //$NON-NLS-1$
        String iconPath = "/my/icon/path/MyIcon.gif"; //$NON-NLS-1$
        String iconFullPath = pluginName + iconPath;

        ShapeContainerStyleDescription styleDescription = StyleFactory.eINSTANCE.createShapeContainerStyleDescription();
        styleDescription.setIconPath(iconFullPath);

        VariableManager variableManager = new VariableManager();

        LabelStyleDescriptionConverter labelStyleDescriptionConverter = new LabelStyleDescriptionConverter(interpreterAPI, new NoOpObjectService(), entry);
        LabelStyleDescription labelStyleDescriptionConverted = labelStyleDescriptionConverter.convert(styleDescription);

        assertThat(labelStyleDescriptionConverted).isNotNull();

        Function<VariableManager, String> iconURLProvider = labelStyleDescriptionConverted.getIconURLProvider();
        assertThat(iconURLProvider.apply(variableManager)).isEqualTo(iconPath);
    }
}
