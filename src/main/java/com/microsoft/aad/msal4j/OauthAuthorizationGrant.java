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

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.JWTBearerGrant;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class OauthAuthorizationGrant extends MsalAuthorizationGrant {

    private AuthorizationGrant grant;
    private final Map<String, List<String>> params;


    OauthAuthorizationGrant(final AuthorizationGrant grant, Set<String> scopes) {
        this.grant = grant;
        this.params = convertScopesToParameters(scopes);
    }

    OauthAuthorizationGrant(final AuthorizationGrant grant,
                            final Map<String, List<String>> params) {
        this.grant = grant;

        this.params = initializeStandardParamaters();
        if(params != null){
            this.params.putAll(params);
        }
    }

    @Override
    public Map<String, List<String>> toParameters() {
        final Map<String, List<String>> outParams = new LinkedHashMap<>();
        outParams.putAll(params);
        outParams.putAll(grant.toParameters());

        return Collections.unmodifiableMap(outParams);
    }


    private Map<String, List<String>> convertScopesToParameters(Set<String> scopes){
        Map<String, List<String>> parameters = initializeStandardParamaters();

        String scopesStr = scopes != null ? String.join(" ", scopes) : null;
        if (!StringHelper.isBlank(scopesStr)) {
            String scopesStrParameter = COMMON_SCOPES_PARAM + SCOPES_DELIMITER + scopesStr;
            parameters.put(SCOPE_PARAM_NAME, Collections.singletonList(scopesStrParameter));
        }

        if(grant instanceof JWTBearerGrant){
            parameters.put("requested_token_use", Collections.singletonList("on_behalf_of"));
        }
        return parameters;
    }


    private Map<String, List<String>> initializeStandardParamaters(){
        Map<String, List<String>> parameters = new LinkedHashMap<>();
        parameters.put(SCOPE_PARAM_NAME, Collections.singletonList(COMMON_SCOPES_PARAM));

        return parameters;
    }

    AuthorizationGrant getAuthorizationGrant() {
        return this.grant;
    }

    Map<String, List<String>> getCustomParameters() {
        return params;
    }
}