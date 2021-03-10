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
package org.eclipse.sirius.web.compat.services.diagrams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.sirius.business.api.query.IdentifiedElementQuery;
import org.eclipse.sirius.diagram.business.internal.metamodel.description.spec.LayerSpec;
import org.eclipse.sirius.diagram.description.AbstractNodeMapping;
import org.eclipse.sirius.diagram.description.AdditionalLayer;
import org.eclipse.sirius.diagram.description.ContainerMapping;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.Layer;
import org.eclipse.sirius.diagram.description.tool.ContainerCreationDescription;
import org.eclipse.sirius.diagram.description.tool.EdgeCreationDescription;
import org.eclipse.sirius.diagram.description.tool.NodeCreationDescription;
import org.eclipse.sirius.diagram.description.tool.ToolGroup;
import org.eclipse.sirius.viewpoint.description.tool.AbstractToolDescription;
import org.eclipse.sirius.viewpoint.description.tool.InitEdgeCreationOperation;
import org.eclipse.sirius.viewpoint.description.tool.InitialNodeCreationOperation;
import org.eclipse.sirius.viewpoint.description.tool.InitialOperation;
import org.eclipse.sirius.viewpoint.description.tool.ToolDescription;
import org.eclipse.sirius.web.compat.api.IAQLInterpreterFactory;
import org.eclipse.sirius.web.compat.api.IIdentifierProvider;
import org.eclipse.sirius.web.compat.api.IModelOperationHandlerSwitchProvider;
import org.eclipse.sirius.web.compat.api.IToolImageProviderFactory;
import org.eclipse.sirius.web.diagrams.description.EdgeDescription;
import org.eclipse.sirius.web.diagrams.description.NodeDescription;
import org.eclipse.sirius.web.diagrams.tools.CreateEdgeTool;
import org.eclipse.sirius.web.diagrams.tools.CreateNodeTool;
import org.eclipse.sirius.web.diagrams.tools.EdgeCandidate;
import org.eclipse.sirius.web.diagrams.tools.ITool;
import org.eclipse.sirius.web.diagrams.tools.ToolSection;
import org.eclipse.sirius.web.interpreter.AQLEntry;
import org.eclipse.sirius.web.interpreter.AQLInterpreterAPI;
import org.eclipse.sirius.web.representations.Status;
import org.eclipse.sirius.web.representations.VariableManager;
import org.springframework.stereotype.Service;

/**
 * This class is used to compute the relevant list of tools for a diagram.
 *
 * @author sbegaudeau
 */
@Service
public class ToolProvider implements IToolProvider {

    private final IAQLInterpreterFactory interpreterFactory;

    private final IIdentifierProvider identifierProvider;

    private final IModelOperationHandlerSwitchProvider modelOperationHandlerSwitchProvider;

    private final IToolImageProviderFactory toolImageProviderFactory;

    private final AQLInterpreterAPI aqlInterpreterAPI;

    public ToolProvider(IIdentifierProvider identifierProvider, IAQLInterpreterFactory interpreterFactory, IModelOperationHandlerSwitchProvider modelOperationHandlerSwitchProvider,
                        IToolImageProviderFactory toolImageProviderFactory, AQLInterpreterAPI aqlInterpreterAPI) {
        this.interpreterFactory = Objects.requireNonNull(interpreterFactory);
        this.identifierProvider = Objects.requireNonNull(identifierProvider);
        this.modelOperationHandlerSwitchProvider = Objects.requireNonNull(modelOperationHandlerSwitchProvider);
        this.toolImageProviderFactory = Objects.requireNonNull(toolImageProviderFactory);
        this.aqlInterpreterAPI = Objects.requireNonNull(aqlInterpreterAPI);
    }

    @Override
    public List<ToolSection> getToolSections(Map<UUID, NodeDescription> id2NodeDescriptions, List<EdgeDescription> edgeDescriptions, DiagramDescription siriusDiagramDescription, List<Layer> layers) {

        List<ToolSection> result = new ArrayList<>();
        // @formatter:off
        var siriusToolSections = layers
            .stream()
            .flatMap(layer -> layer.getToolSections().stream())
            .filter(org.eclipse.sirius.diagram.description.tool.ToolSection.class::isInstance)
            .map(org.eclipse.sirius.diagram.description.tool.ToolSection.class::cast)
            .collect(Collectors.toList());
        // @formatter:on
        AQLEntry entry = this.interpreterFactory.create(siriusDiagramDescription);
        for (var siriusToolSection : siriusToolSections) {
            // @formatter:off
            List<ITool> tools = this.getToolDescriptions(siriusToolSection).stream()
                    .filter(this::isSupported)
                    .map(toolDescription -> this.convertTool(id2NodeDescriptions, siriusDiagramDescription, toolDescription, this.aqlInterpreterAPI, entry))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
            // @formatter:on
            if (!tools.isEmpty()) {
                ToolSection toolSection = this.convertToolSection(siriusToolSection, tools);
                result.add(toolSection);
            }
        }
        return result;
    }

    private boolean isSupported(AbstractToolDescription toolDescription) {
        boolean isSupported = toolDescription instanceof NodeCreationDescription;
        isSupported = isSupported || toolDescription instanceof ContainerCreationDescription;
        isSupported = isSupported || toolDescription instanceof EdgeCreationDescription;
        isSupported = isSupported || toolDescription instanceof ToolDescription;
        return isSupported;
    }

    private ToolSection convertToolSection(org.eclipse.sirius.diagram.description.tool.ToolSection siriusToolSection, List<ITool> tools) {
        String toolSectionLabel = Optional.ofNullable(siriusToolSection.getLabel()).orElse(siriusToolSection.getName());
        // @formatter:off
        return ToolSection.newToolSection(toolSectionLabel)
                .tools(tools)
                .label(toolSectionLabel)
                .imageURL(this.getImagePathFromIconPath(siriusToolSection).orElse("")) //$NON-NLS-1$
                .build();
        // @formatter:on
    }

    private List<AbstractToolDescription> getToolDescriptions(org.eclipse.sirius.diagram.description.tool.ToolSection toolSection) {
        //@formatter:off
        return Stream.concat(
                    toolSection.getOwnedTools().stream(),
                    toolSection.getReusedTools().stream()
                )
                .flatMap(toolEntry -> {
                    if (toolEntry instanceof ToolGroup) {
                        return ((ToolGroup) toolEntry).getTools().stream();
                    }
                    return Stream.of(toolEntry);
                })
                .filter(AbstractToolDescription.class::isInstance)
                .map(AbstractToolDescription.class::cast)
                .collect(Collectors.toList());

        //@formatter:on
    }

    private List<NodeDescription> getParentNodeDescriptions(List<? extends AbstractNodeMapping> nodeMappings, Map<UUID, NodeDescription> id2NodeDescriptions) {
        //@formatter:off
        return nodeMappings.stream()
                .map(AbstractNodeMapping::eContainer)
                .filter(AbstractNodeMapping.class::isInstance)
                .map(AbstractNodeMapping.class::cast)
                .map(this.identifierProvider::getIdentifier)
                .map(UUID::fromString)
                .map(id2NodeDescriptions::get)
                .collect(Collectors.toList());
        //@formatter:on
    }

    private boolean atLeastOneRootMapping(List<? extends AbstractNodeMapping> nodeMappings) {
        //@formatter:off
        return nodeMappings.stream()
                .map(AbstractNodeMapping::eContainer)
                .anyMatch(LayerSpec.class::isInstance);
        //@formatter:on
    }

    private Optional<ITool> convertTool(Map<UUID, NodeDescription> id2NodeDescriptions, org.eclipse.sirius.diagram.description.DiagramDescription siriusDiagramDescription,
                                        AbstractToolDescription siriusTool, AQLInterpreterAPI interpreter, AQLEntry entry) {
        Optional<ITool> result = Optional.empty();
        if (siriusTool instanceof NodeCreationDescription) {
            NodeCreationDescription nodeCreationTool = (NodeCreationDescription) siriusTool;
            result = Optional.of(this.convertNodeCreationDescription(id2NodeDescriptions, interpreter, entry, nodeCreationTool));
        } else if (siriusTool instanceof ContainerCreationDescription) {
            ContainerCreationDescription containerCreationDescription = (ContainerCreationDescription) siriusTool;
            result = Optional.of(this.convertContainerCreationDescription(id2NodeDescriptions, interpreter, entry, containerCreationDescription));
        } else if (siriusTool instanceof org.eclipse.sirius.viewpoint.description.tool.ToolDescription) {
            org.eclipse.sirius.viewpoint.description.tool.ToolDescription toolDescription = (org.eclipse.sirius.viewpoint.description.tool.ToolDescription) siriusTool;
            result = Optional.of(this.convertToolDescription(id2NodeDescriptions, interpreter, entry, siriusDiagramDescription, toolDescription));
        } else if (siriusTool instanceof EdgeCreationDescription) {
            EdgeCreationDescription edgeCreationDescription = (EdgeCreationDescription) siriusTool;
            result = Optional.of(this.convertEdgeCreationDescription(id2NodeDescriptions, interpreter, entry, edgeCreationDescription));
        }

        return result;
    }

    private CreateNodeTool convertNodeCreationDescription(Map<UUID, NodeDescription> id2NodeDescriptions, AQLInterpreterAPI interpreter, AQLEntry entry, NodeCreationDescription nodeCreationTool) {
        String id = this.identifierProvider.getIdentifier(nodeCreationTool);
        String label = new IdentifiedElementQuery(nodeCreationTool).getLabel();
        String imagePath = this.toolImageProviderFactory.getToolImageProvider(nodeCreationTool).get();
        List<NodeDescription> targetDescriptions = this.getParentNodeDescriptions(nodeCreationTool.getNodeMappings(), id2NodeDescriptions);
        // @formatter:off
        return CreateNodeTool.newCreateNodeTool(id)
                .label(label)
                .imageURL(imagePath)
                .handler(this.createNodeCreationHandler(interpreter, entry, nodeCreationTool))
                .targetDescriptions(targetDescriptions)
                .appliesToDiagramRoot(this.atLeastOneRootMapping(nodeCreationTool.getNodeMappings()))
                .build();
        // @formatter:on
    }

    private CreateNodeTool convertContainerCreationDescription(Map<UUID, NodeDescription> id2NodeDescriptions, AQLInterpreterAPI interpreter, AQLEntry entry, ContainerCreationDescription containerCreationDescription) {
        String id = this.identifierProvider.getIdentifier(containerCreationDescription);
        String label = new IdentifiedElementQuery(containerCreationDescription).getLabel();
        String imagePath = this.toolImageProviderFactory.getToolImageProvider(containerCreationDescription).get();
        List<NodeDescription> targetDescriptions = this.getParentNodeDescriptions(containerCreationDescription.getContainerMappings(), id2NodeDescriptions);
        // @formatter:off
        return CreateNodeTool.newCreateNodeTool(id)
                .label(label)
                .imageURL(imagePath)
                .handler(this.createContainerCreationHandler(interpreter, entry, containerCreationDescription))
                .targetDescriptions(targetDescriptions)
                .appliesToDiagramRoot(this.atLeastOneRootMapping(containerCreationDescription.getContainerMappings()))
                .build();
        // @formatter:on
    }

    private CreateNodeTool convertToolDescription(Map<UUID, NodeDescription> id2NodeDescriptions, AQLInterpreterAPI interpreter, AQLEntry entry, DiagramDescription siriusDiagramDescription,
                                                  ToolDescription toolDescription) {
        String id = this.identifierProvider.getIdentifier(toolDescription);
        String label = new IdentifiedElementQuery(toolDescription).getLabel();
        String imagePath = this.toolImageProviderFactory.getToolImageProvider(toolDescription).get();

        List<DiagramElementMapping> mappings = this.getAllDiagramElementMappings(siriusDiagramDescription);

       // @formatter:off
        List<String> targetDescriptionIds = mappings.stream()
                .map(this.identifierProvider::getIdentifier)
                .collect(Collectors.toList());
        List<NodeDescription> targetDescriptions = targetDescriptionIds.stream()
                .map(UUID::fromString)
                .map(id2NodeDescriptions::get)
                .collect(Collectors.toList());
        return CreateNodeTool.newCreateNodeTool(id)
                .label(label)
                .imageURL(imagePath)
                .handler(this.createGenericToolHandler(interpreter, entry, toolDescription))
                .targetDescriptions(targetDescriptions)
                .appliesToDiagramRoot(true)
                .build();
        // @formatter:on
    }

    private List<DiagramElementMapping> getAllDiagramElementMappings(DiagramDescription siriusDiagramDescription) {
        List<DiagramElementMapping> mappings = new ArrayList<>(siriusDiagramDescription.getDefaultLayer().getNodeMappings());
        mappings.addAll(siriusDiagramDescription.getDefaultLayer().getContainerMappings());
        mappings.addAll(this.getAllSubMappings(siriusDiagramDescription.getDefaultLayer().getContainerMappings()));
        for (AdditionalLayer additionalLayer : siriusDiagramDescription.getAdditionalLayers()) {
            mappings.addAll(additionalLayer.getNodeMappings());
            mappings.addAll(additionalLayer.getContainerMappings());
            mappings.addAll(this.getAllSubMappings(additionalLayer.getContainerMappings()));
        }
        return mappings;
    }

    private Collection<? extends DiagramElementMapping> getAllSubMappings(List<ContainerMapping> containerMappings) {
        List<DiagramElementMapping> result = new ArrayList<>();
        for (ContainerMapping containerMapping : containerMappings) {
            result.addAll(containerMapping.getSubNodeMappings());
            result.addAll(containerMapping.getBorderedNodeMappings());
            result.addAll(containerMapping.getSubContainerMappings());
            result.addAll(this.getAllSubMappings(containerMapping.getSubContainerMappings()));
        }
        return result;
    }

    private CreateEdgeTool convertEdgeCreationDescription(Map<UUID, NodeDescription> id2NodeDescriptions, AQLInterpreterAPI interpreter, AQLEntry entry, EdgeCreationDescription edgeCreationDescription) {
        String id = this.identifierProvider.getIdentifier(edgeCreationDescription);
        String label = new IdentifiedElementQuery(edgeCreationDescription).getLabel();
        String imagePath = this.toolImageProviderFactory.getToolImageProvider(edgeCreationDescription).get();
        // @formatter:off
        List<EdgeCandidate> edgeCandidates = new ArrayList<>();
        for (EdgeMapping edgeMapping : edgeCreationDescription.getEdgeMappings()) {
            List<NodeDescription> sources = edgeMapping.getSourceMapping().stream()
                    .filter(AbstractNodeMapping.class::isInstance)
                    .map(this.identifierProvider::getIdentifier)
                    .map(UUID::fromString)
                    .map(id2NodeDescriptions::get)
                    .collect(Collectors.toList());
            List<NodeDescription> targets = edgeMapping.getTargetMapping().stream()
                    .filter(AbstractNodeMapping.class::isInstance)
                    .map(this.identifierProvider::getIdentifier)
                    .map(UUID::fromString)
                    .map(id2NodeDescriptions::get)
                    .collect(Collectors.toList());
            EdgeCandidate edgeCandidate = EdgeCandidate.newEdgeCandidate()
                .sources(sources)
                .targets(targets)
                .build();
            edgeCandidates.add(edgeCandidate);
        }
        return CreateEdgeTool.newCreateEdgeTool(id)
                .label(label)
                .imageURL(imagePath)
                .handler(this.createEdgeCreationDescription(interpreter, entry, edgeCreationDescription))
                .edgeCandidates(edgeCandidates)
                .build();
        // @formatter:on
    }

    private Function<VariableManager, Status> createContainerCreationHandler(AQLInterpreterAPI interpreter, AQLEntry entry, ContainerCreationDescription toolDescription) {
        if (toolDescription != null) {
            InitialNodeCreationOperation initialOperation = toolDescription.getInitialOperation();
            return variableManager -> {
                Map<String, Object> variables = variableManager.getVariables();
                var modelOperationHandlerSwitch = this.modelOperationHandlerSwitchProvider.getModelOperationHandlerSwitch(interpreter, entry);
                return modelOperationHandlerSwitch.apply(initialOperation.getFirstModelOperations()).map(handler -> {
                    return handler.handle(variables);
                }).orElse(Status.ERROR);
            };
        } else {
            return variableManager -> Status.OK;
        }
    }

    private Function<VariableManager, Status> createNodeCreationHandler(AQLInterpreterAPI interpreter, AQLEntry entry, NodeCreationDescription toolDescription) {
        if (toolDescription != null) {
            InitialNodeCreationOperation initialOperation = toolDescription.getInitialOperation();
            return variableManager -> {
                Map<String, Object> variables = variableManager.getVariables();
                var modelOperationHandlerSwitch = this.modelOperationHandlerSwitchProvider.getModelOperationHandlerSwitch(interpreter, entry);
                return modelOperationHandlerSwitch.apply(initialOperation.getFirstModelOperations()).map(handler -> {
                    return handler.handle(variables);
                }).orElse(Status.ERROR);
            };
        } else {
            return variableManager -> Status.OK;
        }
    }

    private Function<VariableManager, Status> createGenericToolHandler(AQLInterpreterAPI interpreter, AQLEntry entry, org.eclipse.sirius.viewpoint.description.tool.ToolDescription toolDescription) {
        if (toolDescription != null) {
            InitialOperation initialOperation = toolDescription.getInitialOperation();
            return variableManager -> {
                Map<String, Object> variables = variableManager.getVariables();
                var modelOperationHandlerSwitch = this.modelOperationHandlerSwitchProvider.getModelOperationHandlerSwitch(interpreter, entry);
                return modelOperationHandlerSwitch.apply(initialOperation.getFirstModelOperations()).map(handler -> {
                    return handler.handle(variables);
                }).orElse(Status.ERROR);
            };
        } else {
            return variableManager -> Status.OK;
        }
    }

    private Function<VariableManager, Status> createEdgeCreationDescription(AQLInterpreterAPI interpreter, AQLEntry entry, EdgeCreationDescription edgeCreationDescription) {
        if (edgeCreationDescription != null) {
            InitEdgeCreationOperation initialOperation = edgeCreationDescription.getInitialOperation();
            return variableManager -> {
                Map<String, Object> variables = variableManager.getVariables();
                var modelOperationHandlerSwitch = this.modelOperationHandlerSwitchProvider.getModelOperationHandlerSwitch(interpreter, entry);
                return modelOperationHandlerSwitch.apply(initialOperation.getFirstModelOperations()).map(handler -> {
                    return handler.handle(variables);
                }).orElse(Status.ERROR);
            };
        } else {
            return variableManager -> Status.OK;
        }
    }

    private Optional<String> getImagePathFromIconPath(org.eclipse.sirius.diagram.description.tool.ToolSection toolSection) {
        var optionalIconPath = Optional.ofNullable(toolSection.getIcon());

        // @formatter:off
        return optionalIconPath
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(iconPath -> !iconPath.isBlank())
                .map(this::normalize);
        // @formatter:on
    }

    private String normalize(String iconPath) {
        String path = iconPath;
        if (!iconPath.startsWith("/")) { //$NON-NLS-1$
            path = "/" + iconPath; //$NON-NLS-1$
        }

        int index = path.indexOf('/', 1);
        if (index != -1) {
            path = path.substring(index);
        }
        return path;
    }
}
