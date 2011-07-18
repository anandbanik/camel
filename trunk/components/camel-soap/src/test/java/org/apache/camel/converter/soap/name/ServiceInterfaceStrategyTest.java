/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.converter.soap.name;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import com.example.customerservice.CustomerService;
import com.example.customerservice.GetCustomersByName;
import com.example.customerservice.GetCustomersByNameResponse;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.dataformat.soap.name.ServiceInterfaceStrategy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInterfaceStrategyTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInterfaceStrategyTest.class);

    @Test
    public void testServiceInterfaceStrategyWithClient() {
        ServiceInterfaceStrategy strategy = new ServiceInterfaceStrategy(CustomerService.class, true);
        QName elName = strategy.findQNameForSoapActionOrType("", GetCustomersByName.class);
        Assert.assertEquals("http://customerservice.example.com/", elName.getNamespaceURI());
        Assert.assertEquals("getCustomersByName", elName.getLocalPart());

        QName elName2 = strategy.findQNameForSoapActionOrType("getCustomersByName", GetCustomersByName.class);
        Assert.assertEquals("http://customerservice.example.com/", elName2.getNamespaceURI());
        Assert.assertEquals("getCustomersByName", elName2.getLocalPart());

        // Tests the case where the soap action is found but the in type is null
        QName elName3 = strategy.findQNameForSoapActionOrType("http://customerservice.example.com/getAllCustomers",
                null);
        Assert.assertNull(elName3);

        try {
            elName = strategy.findQNameForSoapActionOrType("test", Class.class);
            Assert.fail();
        } catch (RuntimeCamelException e) {
            LOG.debug("Caught expected message: " + e.getMessage());
        }
    }

    @Test
    public void testServiceInterfaceStrategyWithServer() {
        ServiceInterfaceStrategy strategy = new ServiceInterfaceStrategy(CustomerService.class, false);

        // Tests the case where the action is not found but the type is
        QName elName = strategy.findQNameForSoapActionOrType("", GetCustomersByNameResponse.class);
        Assert.assertEquals("http://customerservice.example.com/", elName.getNamespaceURI());
        Assert.assertEquals("getCustomersByNameResponse", elName.getLocalPart());

        // Tests the case where the soap action is found
        QName elName2 = strategy.findQNameForSoapActionOrType("http://customerservice.example.com/getCustomersByName",
                GetCustomersByName.class);
        Assert.assertEquals("http://customerservice.example.com/", elName2.getNamespaceURI());
        Assert.assertEquals("getCustomersByNameResponse", elName2.getLocalPart());

        // this tests the case that the soap action as well as the type are not
        // found
        try {
            elName = strategy.findQNameForSoapActionOrType("test", Class.class);
            Assert.fail();
        } catch (RuntimeCamelException e) {
            LOG.debug("Caught expected message: " + e.getMessage());
        }
    }

    @Test
    public void testServiceInterfaceStrategyWithRequestWrapperAndClient() {
        ServiceInterfaceStrategy strategy = new ServiceInterfaceStrategy(
                com.example.customerservice2.CustomerService.class, true);
        QName elName = strategy.findQNameForSoapActionOrType("", com.example.customerservice2.GetCustomersByName.class);
        Assert.assertEquals("http://customerservice2.example.com/", elName.getNamespaceURI());
        Assert.assertEquals("getCustomersByName", elName.getLocalPart());

        try {
            elName = strategy.findQNameForSoapActionOrType("test", Class.class);
            Assert.fail();
        } catch (RuntimeCamelException e) {
            LOG.debug("Caught expected message: " + e.getMessage());
        }
    }

    @Test
    public void testWithNonWebservice() {
        try {
            new ServiceInterfaceStrategy(Object.class, true);
            Assert.fail("Should throw an exception for a class that is no webservice");
        } catch (IllegalArgumentException e) {
            LOG.debug("Caught expected message: " + e.getMessage());
        }
    }
}
