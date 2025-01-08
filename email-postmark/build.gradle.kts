plugins {
    id("io.micronaut.build.internal.email-module")
}

dependencies {
    api(libs.managed.postmark)
    implementation(libs.commons.io) // postmark contains a vulnerable version of commons-io
    api(projects.micronautEmail)
    implementation(mn.reactor)
    testImplementation(mn.micronaut.http)
    testImplementation(projects.testSuiteUtils)
}
