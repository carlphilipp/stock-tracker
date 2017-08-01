# Stock Tracker 3.0.0
<pre>


     _______.___________.  ______     ______  __  ___    .___________..______          ___       ______  __  ___  _______ .______      
    /       |           | /  __  \   /      ||  |/  /    |           ||   _  \        /   \     /      ||  |/  / |   ____||   _  \     
   |   (----`---|  |----`|  |  |  | |  ,----'|  '  /     `---|  |----`|  |_)  |      /  ^  \   |  ,----'|  '  /  |  |__   |  |_)  |    
    \   \       |  |     |  |  |  | |  |     |    <          |  |     |      /      /  /_\  \  |  |     |    <   |   __|  |      /     
.----)   |      |  |     |  `--'  | |  `----.|  .  \         |  |     |  |\  \----./  _____  \ |  `----.|  .  \  |  |____ |  |\  \----.
|_______/       |__|      \______/   \______||__|\__\        |__|     | _| `._____/__/     \__\ \______||__|\__\ |_______|| _| `._____|
                                                                                                                                       


</pre>

A web application to track your personal investments with minimum maintenance.

Calculate your overall performance across all your investments.

## Prerequisite
- Java 1.8.0_131
- Tomcat 8.5.16
- MySQL 5.7.19

## Architecture
Two "microservices"
- website: Spring Boot application that is deployed as a `.war` in a Tomcat container. Tomcat is needed for a better JSP support.
- config-server: Spring Boot/Spring Cloud configuration server. Contains the configurations that will be consumed by the website.

<pre>
                                                                                                 ------               _____
                                                                                               /      \ ___\     ___/    ___
                                                                                            --/-  ___  /    \/  /  /    /   \  
 ________________________                 __________________             |                   /     /           \__     //_     \    
|                        |    REST           |                  |            |                  /                     \   / ___     |
|     config-server            |<---------->|      website     |<-----------|------------>     |           ___       \/+--/        /
|________________________|                |__________________|            |                   \__           \       \           /
                                                                      |                      \__           WEB      |          /
                                                                                             \     /____      /  /       |   /
                                                                                              _____/         ___       \/  /\
                                                                                                   \__      /      /    |    |
                                                                                                      \____/   \       /   //
                                                                                                           \    / 
                                                                                                            \__/
                                                                                                            
</pre>
## Usage
The first step would be to create your config file in `config-server/src/main/resources/config`. There is a `website.yml` already there, you need to create a `website-dev.yml` and `website-prod.yml`, containing your own configurations. The suffix is the spring profile name.

- Compile both projects`./gradlew clean build` or `./gradlew clean build -Pprod` for production.
- Start `config-server` using `java -jar config-server/build/libs/config-server.jar`.
- Deploy `website` artifact (found in `website/build/libs/`) in Tomcat and start the server specifying the Spring profile for example `spring.profiles.active=prod`.

The config server will be deployed on port 8888 and you can verify you obtain the config by going on `http://localhost:8888/website/default`.
The site should be available at `http://localhost:8080` or `http://localhost:8080/stock-tracker` depending on how you deployed it.
To check what was the config obtained by the website (if the security management is not enabled), you can try that URL `http://localhost:8080/env`.
