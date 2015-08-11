package edu.uiowa.webapp;

/*
 * #%L
 * Protogen
 * %%
 * Copyright (C) 2009 - 2015 University of Iowa Institute for Clinical and Translational Science (ICTS)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Relationship extends ClayElement {

	Entity sourceEntity = null;
	String sourceEntityName = null; // necessary for foreign key references occurring prior to target table declaration
	Entity targetEntity = null;
	Hashtable<String,String> attributeMap = new Hashtable<String,String>();

	enum CardinalityEnum {
		ONE_TO_ONE, ONE_TO_MANY, ONE, MANY
	};

	private static final Log log = LogFactory.getLog(Relationship.class);

	CardinalityEnum relationshipCardinality = CardinalityEnum.ONE_TO_MANY;
	CardinalityEnum sourceEntityCardinality = CardinalityEnum.ONE;
	CardinalityEnum targetEntityCardinality = CardinalityEnum.MANY;

	public Entity getSourceEntity() {
		return sourceEntity;
	}

	public void setSourceEntity(Entity sourceEntity) {
        log.debug("setting source entity: " + sourceEntity);
		this.sourceEntity = sourceEntity;
	}

	public String getSourceEntityName() {
        return sourceEntityName;
    }

    public void setSourceEntityName(String sourceEntityName) {
        this.sourceEntityName = sourceEntityName;
    }

    public Entity getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(Entity targetEntity) {
		this.targetEntity = targetEntity;
	}

	public CardinalityEnum getRelationshipCardinality() {
		return relationshipCardinality;
	}

	public void setRelationshipCardinality(
			CardinalityEnum relationshipCardinality) {
		this.relationshipCardinality = relationshipCardinality;
	}

	public CardinalityEnum getSourceEntityCardinality() {
		return sourceEntityCardinality;
	}

	public void setSourceEntityCardinality(CardinalityEnum sourceEntityCardinality) {
		this.sourceEntityCardinality = sourceEntityCardinality;
	}

	public CardinalityEnum getTargetEntityCardinality() {
		return targetEntityCardinality;
	}

	public void setTargetEntityCardinality(CardinalityEnum targetEntityCardinality) {
		this.targetEntityCardinality = targetEntityCardinality;
	}
	
	public void setForeignReferencedAttributeMapping(String foreignAttribute, String referencedAttribute) {
	    log.debug(sourceEntity + " " + referencedAttribute + " --> " + targetEntity + " " + foreignAttribute);
	    attributeMap.put(referencedAttribute, foreignAttribute);
	//TODO    if (!referencedAttribute.equals(foreignAttribute)) targetEntity.getAttributeByLabel(foreignAttribute).setForeignAttribute(sourceEntity.getAttributeByLabel(referencedAttribute));
	}
	
	public String getForeignReferencedAttribute(String referencedAttribute) {
	    return attributeMap.get(referencedAttribute);
	}

    public void dump() {
        log.debug("\t\tsource entity: " + sourceEntity + "\ttarget entity: " + targetEntity + "\tuid: " + uid);
        log.debug("\t\tsource entity: " + (sourceEntity == null ? null : sourceEntity.getLabel()) + "\ttarget entity: " + (targetEntity == null ? null : targetEntity.getLabel()) + "\tuid: " + uid);
    }

	@Override
	public String toString() {
		return "Relationship [sourceEntity=" + sourceEntity
				+ ", sourceEntityName=" + sourceEntityName + ", targetEntity="
				+ targetEntity + "]";
	}
    
}
