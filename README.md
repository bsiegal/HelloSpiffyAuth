[Hello Spiffy Auth](http://www.spiffyui.org) - GWT made simple
==================================================

This is an application created from the [Spiffy UI Framework](http://www.spiffyui.org) project creator, which builds a simple REST application with Apache Ant.  The SimpleServlet in this sample is secured, so in order to access its payload the user must log in.  A sample authentication server is also provided in the form of a simple authentication servlet.  For more details on the security scheme, visit [Spiffy UI's Authentication and Security](http://www.spiffyui.org/#!auth).

Building and Running HelloSpiffyAuth
--------------------------------------

This project is built with [Apache Ant](http://ant.apache.org/) using [Apache Ivy](http://ant.apache.org/ivy/).  Once you've installed Ant go to your project's root directory and run this command:

    <ANT HOME>/ant run
    
This will download Apache Ivy and the other the required libraries, build your project, and run it with an embedded Jetty web server.  It will then provide instructions for accessing the running application once the build is completed.  


Debugging through Eclipse
--------------------------------------

See [Spiffy UI's GWT Dev Mode page](http://www.spiffyui.org/#!hostedMode) for more information.    


License
--------------------------------------

Spiffy UI is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
