
package com.liferay.prototype.analytics.data.binding.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "elementId",
    "referrers",
    "entityId",
    "entityType"
})
public class Properties {

    @JsonProperty("elementId")
    private String elementId;
    @JsonProperty("referrers")
    private List<Object> referrers = new ArrayList<Object>();
    @JsonProperty("entityId")
    private String entityId;
    @JsonProperty("entityType")
    private String entityType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The elementId
     */
    @JsonProperty("elementId")
    public String getElementId() {
        return elementId;
    }

    /**
     * 
     * @param elementId
     *     The elementId
     */
    @JsonProperty("elementId")
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 
     * @return
     *     The referrers
     */
    @JsonProperty("referrers")
    public List<Object> getReferrers() {
        return referrers;
    }

    /**
     * 
     * @param referrers
     *     The referrers
     */
    @JsonProperty("referrers")
    public void setReferrers(List<Object> referrers) {
        this.referrers = referrers;
    }

    /**
     * 
     * @return
     *     The entityId
     */
    @JsonProperty("entityId")
    public String getEntityId() {
        return entityId;
    }

    /**
     * 
     * @param entityId
     *     The entityId
     */
    @JsonProperty("entityId")
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * 
     * @return
     *     The entityType
     */
    @JsonProperty("entityType")
    public String getEntityType() {
        return entityType;
    }

    /**
     * 
     * @param entityType
     *     The entityType
     */
    @JsonProperty("entityType")
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(elementId).append(referrers).append(entityId).append(entityType).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Properties) == false) {
            return false;
        }
        Properties rhs = ((Properties) other);
        return new EqualsBuilder().append(elementId, rhs.elementId).append(referrers, rhs.referrers).append(entityId, rhs.entityId).append(entityType, rhs.entityType).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
