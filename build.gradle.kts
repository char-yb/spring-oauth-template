plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    kotlin("plugin.jpa") version "1.9.25"
    jacoco
}

group = "com.social"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Apple Login
    implementation("org.bouncycastle:bcpkix-jdk18on:1.72")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

val jacocoExcludePatterns =
    listOf(
        "**/*Application*",
        "**/*Config/*",
        "**/resources/**",
        "**/config/**",
        "**/global/**",
        "**/infra/**",
        "**/test/**",
        "**/*Constants/*",
    )

val jacocoDir = layout.buildDirectory.dir("reports/")

tasks.jacocoTestReport<JacocoReport> {
    dependsOn("test")
    executionData(fileTree(jacocoDir.get()).include("jacoco/*.exec"))
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }

    finalizedBy("jacocoTestCoverageVerification")
}

tasks.jacocoTestCoverageVerification<JacocoCoverageVerification> {
    violationRules {
        rule {
            // 'element'가 없으면 프로젝트의 전체 파일을 합친 값을 기준으로 한다.
            limit {
                // 'counter'를 지정하지 않으면 default는 'INSTRUCTION'
                // 'value'를 지정하지 않으면 default는 'COVEREDRATIO'
                minimum = "0.0".toBigDecimal()
            }
        }

        rule {
            // 룰을 간단히 켜고 끌 수 있다.
            enabled = true

            // 룰을 체크할 단위는 클래스 단위
            element = "CLASS"

//            // 브랜치 커버리지를 최소한 90% 만족시켜야 한다.
//            limit {
//                counter = "BRANCH"
//                value = "COVEREDRATIO"
//                minimum = "0.90".toBigDecimal()
//            }
//
//            // 라인 커버리지를 최소한 80% 만족시켜야 한다.
//            limit {
//                counter = "LINE"
//                value = "COVEREDRATIO"
//                minimum = "0.30".toBigDecimal()
//            }

            // 빈 줄을 제외한 코드의 라인수를 최대 200라인으로 제한한다.
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }

            // 커버리지 체크를 제외할 클래스들
            excludes = jacocoExcludePatterns
        }
    }
}

// scripts 경로의 pre-commit hook 등록
tasks.register("addGitPreCommitHook", DefaultTask::class) {
    group = "setup"
    description = "Install git hooks"
    doLast {
        val hooksDir = project.file(".git/hooks")
        val scriptDir = project.file("scripts")
        val preCommit = scriptDir.resolve("pre-commit")
        preCommit.copyTo(hooksDir.resolve("pre-commit"), overwrite = true)
        hooksDir.resolve("pre-commit").setExecutable(true)
    }
}

// compileKotlin가 addGitPreCommitHook에 의존하도록 설정
tasks.named("compileKotlin") {
    dependsOn("addGitPreCommitHook")
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage"

    dependsOn(
        ":test",
        ":jacocoTestReport",
        ":jacocoTestCoverageVerification",
    )

    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}
