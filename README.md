# KarmaAPI
 KarmaAPI source code...

 [Please take a look at the license before using the source code, legal actions could be performed if you break any condition](http://karmaconfigs.ml/license/)

## Maven

```xml
<build>
    <finalName>MyAwesomePlugin</finalName>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>X.X.X</version>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>ml.karmaconfigs.api</pattern>
                        <shadedPattern>change.this</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

<dependency>
    <groupId>ml.karmaconfigs</groupId>
    <artifactId>KarmaAPI-{Bukkit/Bungee/Common/Bundle/Velocity}</artifactId>
    <version>1.3.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```
