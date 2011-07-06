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
package org.spiffyui.hellospiffyauth.client;

import org.spiffyui.client.JSONUtil;
import org.spiffyui.client.JSUtil;
import org.spiffyui.client.MainFooter;
import org.spiffyui.client.MainHeader;
import org.spiffyui.client.MessageUtil;
import org.spiffyui.client.rest.RESTCallback;
import org.spiffyui.client.rest.RESTException;
import org.spiffyui.client.rest.RESTLoginCallBack;
import org.spiffyui.client.rest.RESTObjectCallBack;
import org.spiffyui.client.rest.RESTility;
import org.spiffyui.client.widgets.LongMessage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * This class is the main entry point for our GWT module.
 */
 public class Index implements EntryPoint, ClickHandler, KeyPressHandler 
 {
     private static final Strings STRINGS = (Strings) GWT.create(Strings.class);

     private static Index g_index;
     private TextBox m_text = new TextBox();
     private LongMessage m_longMessage = new LongMessage("longMsgPanel");

     /**
      * The Index page constructor
      */
     public Index()
     {
         g_index = this;
     }


     @Override
     public void onModuleLoad()
     {
         /*
            This is where we load our module and create our dynamic controls.  The MainHeader
            displays our title bar at the top of our page.
          */
         final MainHeader header = new MainHeader();
         header.setHeaderTitle("Hello Spiffy Auth!");
         Anchor logout = new Anchor("Logout", "#");
         logout.getElement().setId("header_logout");
         header.setLogout(logout);
         if (!Index.userLoggedIn()) {
             logout.setVisible(false);
             header.setWelcomeString("");            
         } else {
             /*
              * The SampleAuthServer combines the username and a UUID as the token
              */
             String token = RESTility.getUserToken();
             JSUtil.println(RESTility.getUsername());
             String name = token.substring(0, token.indexOf('-'));
             
             header.setWelcomeString("Welcome " + name);
         }
         logout.addClickHandler(new ClickHandler() {
             public void onClick(ClickEvent event)
             {
                 event.preventDefault();
                 doLogout();
             }
         });
         /*
            The main footer shows our message at the bottom of the page.
          */
         MainFooter footer = new MainFooter();
         footer.setFooterString("HelloSpiffyAuth was built with the <a href=\"http://www.spiffyui.org\">Spiffy UI Framework</a>");

         /*
            This HTMLPanel holds most of our content.
            MainPanel_html was built in the HTMLProps task from MainPanel.html, which allows you to use large passages of html
            without having to string escape them.
          */
         HTMLPanel panel = new HTMLPanel(STRINGS.MainPanel_html())
         {
             @Override
             public void onLoad()
             {
                 super.onLoad();
                 /*
                    Let's set focus into the text field when the page first loads
                  */
                 m_text.setFocus(true);
             }
         };

         RootPanel.get("mainContent").add(panel);

         /*
            These dynamic controls add interactivity to our page.
          */
         panel.add(m_longMessage, "longMsg");
         panel.add(m_text, "nameField");
         final Button button = new Button("Submit");
         panel.add(button, "submitButton");

         button.addClickHandler(this);
         m_text.addKeyPressHandler(this);

         RESTility.addLoginListener(new RESTLoginCallBack() {

             @Override
             public void onLoginSuccess()
             {
                 if (RESTility.getUserToken() == null) {
                     return;
                 }
                 header.setWelcomeString("Welcome " + RESTility.getUsername());
                 JSUtil.bounce("#" + MainHeader.HEADER_ACTIONS_BLOCK, 5, 500, 30);
                 JSUtil.show("#header_logout", "fast");
             }

             @Override
             public void loginPrompt()
             {
                 //do nothing
             }
         });
     }

     @Override
     public void onClick(ClickEvent event)
     {
         sendRequest();
     }

     @Override
     public void onKeyPress(KeyPressEvent event)
     {
         /*
            We want to submit the request if the user pressed enter
          */
         if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
             sendRequest();
         }
     }

     /**
      * Send the REST request to the server and read the response back.
      */
     private void sendRequest()
     {
         String q = m_text.getValue().trim();
         if (q.equals("")) {
             MessageUtil.showWarning("Please enter your name in the text field.", false);
             return;
         }
         RESTility.callREST("simple/" + q, new RESTCallback() {

             @Override
             public void onSuccess(JSONValue val)
             {
                 showSuccessMessage(val);
             }

             @Override
             public void onError(int statusCode, String errorResponse)
             {
                 MessageUtil.showError("Error.  Status Code: " + statusCode + " " + errorResponse);
             }

             @Override
             public void onError(RESTException e)
             {
                 MessageUtil.showError(e.getReason());
             }
         });

     }

     /**
      * Show the successful message result of our REST call.
      * 
      * @param val    the value containing the JSON response from the server
      */
     private void showSuccessMessage(JSONValue val)
     {
         JSONObject obj = val.isObject();
         String name = JSONUtil.getStringValue(obj, "user");
         String browser = JSONUtil.getStringValue(obj, "userAgent");
         String server = JSONUtil.getStringValue(obj, "serverInfo");

         String message = "Hello, " + name + "!  <br/>You are using " + browser + ".<br/>The server is " + server + ".";
         m_longMessage.setHTML(message);
     }

     /**
      * returns whether the  user is logged in or not
      * @return true if the user is logged in (browser cookie is there)
      */
     private static boolean userLoggedIn()
     {         
         String userToken = RESTility.getUserToken();
         if ((userToken == null) || (userToken.length() <= 0)) {
             return false;
         }
         return true;
     }

     /**
      * Logout of the application
      */
     private static void doLogout()
     {
         RESTility.getAuthProvider().logout(new RESTObjectCallBack<String>() {
             public void success(String message)
             {
                 Window.Location.reload();
             }

             public void error(String message)
             {
                 Window.Location.reload();
             }

             public void error(RESTException e)
             {
                 MessageUtil.showFatalError(e.getReason());
             }
         });
     }
 }
