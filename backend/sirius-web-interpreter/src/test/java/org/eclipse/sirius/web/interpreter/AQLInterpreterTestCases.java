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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that the AQLInterpreter can properly evaluate AQL expressions.
 *
 * @author hmarchadour
 */
public class AQLInterpreterTestCases {

    private static final String SELF = "self"; //$NON-NLS-1$
    AQLInterpreter interpreter;
    AQLInterpreterAPI aqlInterpreterAPI;

    @Before
    public void setUp() {
        interpreter = new AQLInterpreter();
        aqlInterpreterAPI = new AQLInterpreterAPI(interpreter);
    }

    @Test
    public void testNameFeatureExpression() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(SELF, EcorePackage.eINSTANCE.getEModelElement()), "feature:name", entry); //$NON-NLS-1$
        Optional<String> asString = result.asString();
        assertThat(asString).isPresent().hasValue(EcorePackage.eINSTANCE.getEModelElement().getName());
    }

    @Test
    public void testEContentsFeatureExpression() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(SELF, EcorePackage.eINSTANCE.getEModelElement()), "feature:eContents", entry); //$NON-NLS-1$
        Optional<List<Object>> asObjects = result.asObjects();
        assertThat(asObjects).isPresent();
        assertThat(asObjects.get()).contains(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS, EcorePackage.Literals.EMODEL_ELEMENT___GET_EANNOTATION__STRING);
    }

    @Test
    public void testEAllContentsFeatureExpression() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(SELF, EcorePackage.eINSTANCE.getEModelElement()), "feature:eAllContents", entry); //$NON-NLS-1$
        Optional<List<Object>> asObjects = result.asObjects();
        assertThat(asObjects).isPresent();
        assertThat(asObjects.get()).contains(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS, EcorePackage.Literals.EMODEL_ELEMENT___GET_EANNOTATION__STRING);
    }

    @Test
    public void testEContainerFeatureExpression() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(SELF, EcorePackage.eINSTANCE.getEModelElement()), "feature:eContainer", entry); //$NON-NLS-1$
        Optional<List<Object>> asObjects = result.asObjects();
        assertThat(asObjects).isPresent();
        assertThat(asObjects.get()).contains(EcorePackage.eINSTANCE);
    }

    @Test
    public void testEClassFeatureExpression() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(SELF, EcorePackage.eINSTANCE.getEModelElement()), "feature:eClass", entry); //$NON-NLS-1$
        Optional<List<Object>> asObjects = result.asObjects();
        assertThat(asObjects).isPresent();
        assertThat(asObjects.get()).contains(EcorePackage.Literals.ECLASS);
    }

    @Test
    public void testECrossReferencesFeatureExpression() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(SELF, EcorePackage.eINSTANCE.getEEnumLiteral()), "feature:eCrossReferences", entry); //$NON-NLS-1$
        Optional<List<Object>> asObjects = result.asObjects();
        assertThat(asObjects).isPresent();
        assertThat(asObjects.get()).contains(EcorePackage.Literals.EENUM_LITERAL__EENUM);
    }

    @Test
    public void testBrokenEPackage() {
        EPackage broken = EcoreFactory.eINSTANCE.createEPackage();
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(broken));
        assertThat(entry).isNotNull();
    }

    @Test
    public void testNullExpressionEvaluatesToTrue() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = aqlInterpreterAPI.evaluateExpression(Map.of(), null, entry);
        assertThat(result).isNotNull();
        assertThat(result.asBoolean()).contains(Boolean.TRUE);
    }

    @Test
    public void testEmptyExpressionEvaluatesToTrue() {
        AQLEntry entry = aqlInterpreterAPI.initializeUser(List.of(), List.of(EcorePackage.eINSTANCE));
        Result result = interpreter.evaluateExpression(Map.of(), "", entry); //$NON-NLS-1$
        assertThat(result).isNotNull();
        assertThat(result.asBoolean()).contains(Boolean.TRUE);
    }

}
