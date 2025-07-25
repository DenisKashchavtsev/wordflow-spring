dependencies {

    // 🔌 JPA (Hibernate) — используется только как провайдер, без генерации таблиц
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    // 🌐 REST-контроллеры
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // 🛠️ Flyway для миграций
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-postgresql'

    // 💾 PostgreSQL драйвер
    runtimeOnly 'org.postgresql:postgresql'

    // ⚙️ DevTools для горячей перезагрузки (не для продакшена)
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    // ✍️ Lombok (для генерации геттеров/сеттеров/конструкторов и т.п.)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // ✅ Тестирование
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
