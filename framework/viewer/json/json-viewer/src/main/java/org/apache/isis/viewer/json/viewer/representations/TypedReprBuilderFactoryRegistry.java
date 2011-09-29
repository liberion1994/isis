/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.json.viewer.representations;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.isis.viewer.json.applib.RepresentationType;
import org.apache.isis.viewer.json.viewer.resources.domainobjects.DomainObjectReprBuilderFactory;
import org.apache.isis.viewer.json.viewer.resources.user.UserReprBuilderFactory;

import com.google.common.collect.Maps;

public class TypedReprBuilderFactoryRegistry {

    private final Map<MediaType, TypedReprBuilderFactory> factoryByReprType = Maps.newHashMap();
    
    public TypedReprBuilderFactoryRegistry() {
        registerDefaults();
    }
    
    private void registerDefaults() {
        register(new DomainObjectReprBuilderFactory());
        register(new UserReprBuilderFactory());
    }

    public void register(TypedReprBuilderFactory factory) {
        final RepresentationType representationType = factory.getRepresentationType();
        factoryByReprType.put(representationType.getMediaType(), factory);
    }
    
    public TypedReprBuilderFactory find(MediaType mediaType) {
        return factoryByReprType.get(mediaType);
    }

    public TypedReprBuilderFactory find(RepresentationType representationType) {
        return find(representationType.getMediaType());
    }

}
