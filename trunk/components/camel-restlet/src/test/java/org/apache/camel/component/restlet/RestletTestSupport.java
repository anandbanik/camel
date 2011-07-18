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
package org.apache.camel.component.restlet;

import java.io.IOException;

import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;

/**
 *
 * @version 
 */
public abstract class RestletTestSupport extends CamelTestSupport {
    public static int portNum;
    
    @BeforeClass
    public static void initializePortNum() {
        portNum = AvailablePortFinder.getNextAvailable();
    }
    
    public HttpResponse doExecute(HttpUriRequest method) throws Exception {
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(method);
            response.setEntity(new BufferedHttpEntity(response.getEntity()));
            return response;
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    public static void assertHttpResponse(HttpResponse response, int expectedStatusCode,
                                          String expectedContentType) throws ParseException, IOException {
        assertHttpResponse(response, expectedStatusCode, expectedContentType, null);
    }

    public static void assertHttpResponse(HttpResponse response, int expectedStatusCode,
                                          String expectedContentType, String expectedBody)
        throws ParseException, IOException {
        assertEquals(expectedStatusCode, response.getStatusLine().getStatusCode());
        assertTrue(response.getFirstHeader("Content-Type").getValue().startsWith(expectedContentType));
        if (expectedBody != null) {
            assertEquals(expectedBody, EntityUtils.toString(response.getEntity()));
        }
    }
}
