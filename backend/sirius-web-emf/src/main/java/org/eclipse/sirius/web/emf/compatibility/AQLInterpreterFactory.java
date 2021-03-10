/*******************************************************************************
 * Copyright (c) 2019, 2021 Obeo.
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
package org.eclipse.sirius.web.emf.compatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.properties.ViewExtensionDescription;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.sirius.viewpoint.description.JavaExtension;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.sirius.web.compat.api.IAQLInterpreterFactory;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is used to create a new AQL interpreter using all the Java classes defined in a viewpoint.
 *
 * @author sbegaudeau
 */
@Service
public class AQLInterpreterFactory implements IAQLInterpreterFactory {

    private final Logger logger = LoggerFactory.getLogger(AQLInterpreterFactory.class);

    private final AQLInterpreterAPI aqlInterpreterAPI;

    @Autowired
    public AQLInterpreterFactory(AQLInterpreterAPI aqlInterpreterAPI) {
        this.aqlInterpreterAPI = aqlInterpreterAPI;
    }

    @Override
    public AQLEntry create(DiagramDescription diagramDescription) {
        // @formatter:off
        var javaClasses = Optional.of(diagramDescription.eContainer())
                .filter(Viewpoint.class::isInstance)
                .map(Viewpoint.class::cast)
                .map(this::getJavaServices)
                .orElse(new ArrayList<>());
        // @formatter:on

        List<EPackage> ePackages = diagramDescription.getMetamodel();
        return aqlInterpreterAPI.initializeUser(javaClasses, ePackages);
    }

    @Override
    public AQLEntry create(ViewExtensionDescription viewExtensionDescription) {
        // @formatter:off
        List<Viewpoint> viewpoints = Optional.of(viewExtensionDescription.eContainer())
                .filter(Group.class::isInstance)
                .map(Group.class::cast)
                .map(Group::getOwnedViewpoints)
                .orElse(new BasicEList<>());

        var javaClasses = viewpoints.stream()
                .map(this::getJavaServices)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
        // @formatter:on

        List<EPackage> ePackages = viewExtensionDescription.getMetamodels();

        return aqlInterpreterAPI.initializeUser(javaClasses, ePackages);
    }

    private List<Class<?>> getJavaServices(Viewpoint viewpoint) {
        List<Class<?>> classes = new ArrayList<>();

        // @formatter:off
        List<String> qualifiedNames = viewpoint.getOwnedJavaExtensions().stream()
                .map(JavaExtension::getQualifiedClassName)
                .collect(Collectors.toList());
        // @formatter:on

        for (String qualifiedName : qualifiedNames) {
            try {
                Class<?> aClass = Class.forName(qualifiedName);
                classes.add(aClass);
            } catch (ClassNotFoundException exception) {
                this.logger.error(exception.getMessage(), exception);
            }
        }

        return classes;
    }
}
