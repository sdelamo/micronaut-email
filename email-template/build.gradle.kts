plugins {
    id("io.micronaut.build.internal.email-module")
}
dependencies {
    annotationProcessor(mn.micronaut.validation)
    implementation(mn.micronaut.validation)
    api(mn.micronaut.views.core)
    api(projects.email)
}
