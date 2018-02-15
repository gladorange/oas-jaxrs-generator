# oas-jaxrs-generator

This is a maven plugin which allows you to generate OpenApiSpecification v3 (Also known as Swagger) for JAX-RS resources during your build. 
It allows to generate multiples *.yaml files for different resource packages.

Example of usage:

Add plugin section to your build:
```
 <build>
        <plugins>
          <plugin>
            <groupId>ru.gladorange</groupId>
            <artifactId>oas-gen-maven-plugin</artifactId>
            <version>1.0</version>
            <configuration>
              <resources>
                <resource>
                  <packages>ups.auth</packages>
                  <filename>authentication.yaml</filename>
                </resource>
                <resource>
                  <packages>ups.discovery</packages>
                  <filename>discovery</filename>
                </resource>
                <resource>
                  <packages>ups.jc</packages>
                  <filename>join-meeting</filename>
                </resource>
                <resource>
                  <packages>ups.meetmgmt</packages>
                  <filename>meeting-management</filename>
                </resource>
              </resources>
            </configuration>
            <executions>
              <execution>
                <phase>process-classes</phase>
                <goals>
                  <goal>generate</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
```
