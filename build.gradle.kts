plugins {
    `java-library`
    `osgi`
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(group = "org.osgi", name = "org.osgi.service.component.annotations", version = "1.3.0")
    implementation(group = "org.osgi", name = "org.osgi.service.metatype.annotations", version = "1.3.0")
    implementation(group = "org.osgi", name = "org.osgi.annotation", version = "6.0.0")

    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.5")
    implementation(group = "org.apache.httpcomponents", name = "fluent-hc", version = "4.5.5")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.7")

    api("com.cognifide.aet:communication-api:3.2.2")
    api("com.cognifide.aet:jobs-api:3.2.2")
    api("com.cognifide.aet:datastorage-api:3.2.2")

    testImplementation("junit:junit:4.12")
}

tasks.withType<Jar>().configureEach {
    manifest {
        // the manifest of the default jar is of type OsgiManifest
        (manifest as? OsgiManifest)?.apply {
            instruction("Bundle-Vendor", "Maciej Laskowski")
            instruction("Bundle-Description", "AET Extension: lighthouse collector and comparator")
            instruction("Bundle-DocURL", "https://github.com/Skejven")
            instruction("Service-Component", "OSGI-INF/com.github.skejven.collector.LighthouseCollectorFactory.xml,OSGI-INF/com.github.skejven.comparator.LighthouseComparatorFactory.xml")
        }
    }
}