package com.cus.metime.search.domain;

import java.util.List;
import com.cus.metime.search.domain.KeyValue;

/**
 *
 * @author Handoyo
 */
public class SearchParameter extends Location {

    //search id (example : index/type/3)
    private Long id;
    //index (example : index)
    private String serviceName;
    //type (example : index/type)
    private String type;
    //list other params, it's body
    private List<KeyValue> parameters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<KeyValue> getParameters() {
        return parameters;
    }

    public void setParameters(List<KeyValue> parameters) {
        this.parameters = parameters;
    }

    public String toJsonParam() {
        String json = "{";
        for (int i = 0; i < parameters.size(); i++) {
            if (i == (parameters.size() - 1)) {
                json += parameters.get(i).toString();
            } else {
                json += (parameters.get(i).toString() + ",");
            }
        }
        if (getLatitude() != null || getLongitude() != null) {
            if (parameters.size() > 0) {
                json += ",";
            }
            json += "\"location\":" + toString();
        }
        json += "}";
        return json;
    }
}
