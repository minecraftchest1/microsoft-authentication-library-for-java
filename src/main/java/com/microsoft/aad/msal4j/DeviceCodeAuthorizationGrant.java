// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.microsoft.aad.msal4j;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for device code grant.
 */
class DeviceCodeAuthorizationGrant extends AbstractMsalAuthorizationGrant {
    private final static String GRANT_TYPE = "device_code";

    private final DeviceCode deviceCode;
    private final String scopes;
    private String correlationId;

    /**
     *  Create a new device code grant object from a device code and a resource.
     *
     * @param scopes    The resource for which the device code was acquired.
     */
    DeviceCodeAuthorizationGrant(DeviceCode deviceCode, final String scopes) {
        this.deviceCode = deviceCode;
        this.correlationId = deviceCode.correlationId();
        this.scopes = scopes;
    }

    /**
     * Converts the device code grant to a map of HTTP paramters.
     *
     * @return The map with HTTP parameters.
     */
    @Override
    public Map<String, List<String>> toParameters() {
        final Map<String, List<String>> outParams = new LinkedHashMap<>();
        outParams.put(SCOPE_PARAM_NAME, Collections.singletonList(COMMON_SCOPES_PARAM + SCOPES_DELIMITER + scopes));
        outParams.put("grant_type", Collections.singletonList(GRANT_TYPE));
        outParams.put("device_code", Collections.singletonList(deviceCode.deviceCode()));
        outParams.put("client_info", Collections.singletonList("1"));

        return outParams;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
