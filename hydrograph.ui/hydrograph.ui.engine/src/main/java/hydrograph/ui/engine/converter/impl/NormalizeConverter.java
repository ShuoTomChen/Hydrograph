/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.engine.converter.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutputRecordCount;
import hydrograph.engine.jaxb.operationstypes.Normalize;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.converter.TransformConverter;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;
/**
 * Normalize converter
 * 
 * @author Bitwise
 */
public class NormalizeConverter extends TransformConverter {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(NormalizeConverter.class);
	private TransformMapping transformMapping;
	private List<BasicSchemaGridRow> schemaGridRows;
	ConverterHelper converterHelper;

	public NormalizeConverter(Component component) {
		super(component);
		this.baseComponent = new Normalize();
		this.component = component;
		this.properties = component.getProperties();
		transformMapping = (TransformMapping) properties.get(Constants.PARAM_OPERATION);
		converterHelper = new ConverterHelper(component);
		initSchemaGridRows();
	}

	
	private void initSchemaGridRows() {
		schemaGridRows = new LinkedList<>();
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) properties
				.get(Constants.SCHEMA_TO_PROPAGATE);
		if (schemaMap != null && schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null) {
			ComponentsOutputSchema componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
			List<GridRow> gridRows = componentsOutputSchema.getSchemaGridOutputFields(null);
			for (GridRow row : gridRows) {
				schemaGridRows.add((BasicSchemaGridRow) row.copy());
			}
		}
	}
	
	
	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for :{}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
        Normalize normalize = (Normalize) baseComponent;
		normalize.getOperationOrExpression().addAll(getOperations());
		if(transformMapping!=null && transformMapping.isExpression())
		normalize.setOutputRecordCount(getOutputRecordCountValue());
	}

	private TypeOutputRecordCount getOutputRecordCountValue() {
		TypeOutputRecordCount typeOutputRecordCount=new TypeOutputRecordCount();
		typeOutputRecordCount.setValue(transformMapping.getExpressionEditorData().getExpression());
		return typeOutputRecordCount;
	}


	@Override
	protected List<Object> getOperations() {
		return converterHelper.getOperationsOrExpression(transformMapping,schemaGridRows);
	}

	@Override
	protected List<TypeOperationsOutSocket> getOutSocket() {
		return converterHelper.getOutSocket(transformMapping,schemaGridRows);
	}

	@Override
	public List<TypeBaseInSocket> getInSocket() {
		return converterHelper.getInSocket();
	}

}