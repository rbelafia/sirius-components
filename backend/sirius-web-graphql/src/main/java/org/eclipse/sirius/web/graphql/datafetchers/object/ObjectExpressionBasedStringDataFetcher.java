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
package org.eclipse.sirius.web.graphql.datafetchers.object;

import graphql.schema.DataFetchingEnvironment;
import org.eclipse.sirius.web.annotations.spring.graphql.QueryDataFetcher;
import org.eclipse.sirius.web.graphql.schema.ObjectTypeProvider;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.interpreter.Result;
import org.eclipse.sirius.web.representations.VariableManager;
import org.eclipse.sirius.web.spring.graphql.api.IDataFetcherWithFieldCoordinates;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Map;

/**
 * Computes the expression to get a String.
 * <p>
 * It will be used to fetch the data for the following GraphQL field:
 * </p>
 *
 * <pre>
 * type Object {
 *   expressionBasedString(expression: String!): String
 * }
 * </pre>
 *
 * @author hmarchadour
 */
@QueryDataFetcher(type = ObjectTypeProvider.TYPE, field = ObjectTypeProvider.EXPRESSION_BASED_STRING_FIELD)
public class ObjectExpressionBasedStringDataFetcher implements IDataFetcherWithFieldCoordinates<String> {

    private final AQLInterpreterAPI interpreterAPI;

    @Autowired
    public ObjectExpressionBasedStringDataFetcher(AQLInterpreterAPI interpreterAPI) {
        this.interpreterAPI = interpreterAPI;
    }

    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        Object object = environment.getSource();
        String expression = environment.getArgument(ObjectTypeProvider.EXPRESSION_ARGUMENT);
        AQLEntry entry = interpreterAPI.initializeUser(new ArrayList<>(), new ArrayList<>());
        Result result = interpreterAPI.evaluateExpression(Map.of(VariableManager.SELF, object), expression, entry);
        return result.asString().orElse(null);
    }

}
