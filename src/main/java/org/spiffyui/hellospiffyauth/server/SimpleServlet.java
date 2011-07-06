/*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.spiffyui.hellospiffyauth.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet returns the user and browser and server info.
 */
public class SimpleServlet extends HttpServlet
{

    private static final long serialVersionUID = -1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        StringBuffer buff = new StringBuffer();
        String origURL = request.getRequestURL().toString(); //this is http://host:port/simple/name
        String servletPath = request.getServletPath().substring(1); //this is /simple/name
        String authServerURL = origURL.replace(servletPath, "authserver");

        //check for Authorization request header, if it is not there, then need to login
        String authToken = request.getHeader("Authorization");
        if (authToken == null || authToken.length() <= 0) {
            response.setHeader("WWW-Authenticate", "X-OPAQUE uri=\"" + authServerURL + "\", signOffUri=\"" + "\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            buff.append(generateFault("Sender", "NoAuthHeader", ""));
        } else {
            String user = request.getPathInfo();
            if (user.startsWith("/")) {
                user = user.substring(1);
            }
            String serverInfo = getServletContext().getServerInfo();
            String userAgent = request.getHeader("User-Agent");
            
            response.setContentType("application/json");
            
            /*
             We just need to return some simple JSON for this REST call.
             */
            buff.append("{");
            buff.append("\"user\": \"" + user + "\"");
            buff.append(",");
            buff.append("\"userAgent\": \"" + userAgent + "\"");
            buff.append(",");
            buff.append("\"serverInfo\": \"" + serverInfo + "\"");
            buff.append("}");
        }
        out.println(buff.toString());
    }

    private String generateFault(String code, String subcode, String reason)
    {
        StringBuffer buff = new StringBuffer();

        buff.append("{\"Fault\":{\"Code\":{\"Value\":\"").
                append(code).
                append("\",\"Subcode\":{\"Value\":\"").
                append(subcode).
                append("\"}},\"Reason\":{\"Text\":\"").
                append(reason).
                append("\"}}}");
        return buff.toString();
    }

}
