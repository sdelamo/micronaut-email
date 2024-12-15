plugins {
    java
}
dependencies {
    testAnnotationProcessor(platform(mn.micronaut.core.bom))
    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(mnLogging.logback.classic)
    testImplementation(projects.micronautEmailTemplate)
    testImplementation(mnHibernateValidator.micronaut.hibernate.validator)
    testImplementation("org.hibernate:hibernate-validator:8.0.2.Final") // remove when https://github.com/micronaut-projects/micronaut-hibernate-validator/pull/333 gets merged

}
tasks.withType<Test> {
    useJUnitPlatform()
}

