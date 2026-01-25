plugins {
    id("java")
    id("application")
}

group = "com.github.semyonkim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
	// Default entry point (can be overridden when running)
	mainClass.set("basics.HelloWorld")
}

tasks.test {
    useJUnitPlatform()
}